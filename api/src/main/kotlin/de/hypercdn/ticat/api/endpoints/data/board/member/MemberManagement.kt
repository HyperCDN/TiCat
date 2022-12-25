package de.hypercdn.ticat.api.endpoints.data.board.member

import de.hypercdn.ticat.api.entities.helper.*
import de.hypercdn.ticat.api.entities.json.`in`.MemberUpdateJson
import de.hypercdn.ticat.api.entities.json.out.MemberJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.sql.entities.Audit
import de.hypercdn.ticat.api.entities.sql.entities.Board
import de.hypercdn.ticat.api.entities.sql.entities.Member
import de.hypercdn.ticat.api.entities.sql.entities.User.Companion.INTERNAL_USER_UUIDS
import de.hypercdn.ticat.api.entities.sql.repo.AuditLogRepository
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.MemberRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class MemberManagement @Autowired constructor(
    val userRepository: UserRepository,
    val boardRepository: BoardRepository,
    val memberRepository: MemberRepository,
    val auditLogRepository: AuditLogRepository
) {

    @PostMapping("/invite/{boardId}")
    fun requestAccessOrAcceptInvite(
        @PathVariable("boardId") boardId: String,
    ) {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID = null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMembership = memberRepository.findById(Member.Key(selfUser.uuid, board.id)).orElse(null)
        if (selfMembership != null) { // invited
            if (selfMembership.status != Member.MembershipStatus.OFFERED) return
            selfMembership.apply {
                status = Member.MembershipStatus.GRANTED
                canView = true
            }
            memberRepository.save(selfMembership)
            auditLogRepository.save(Audit.of(selfMembership, selfUser, Audit.Action.INVITE_ACCEPT))
            return
        }
        if (!selfUser.isAdmin && (!selfUser.canBoardJoin || !board.isVisibleTo(selfUser))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        val membershipRequest = Member.newFor(
            board, selfUser, when (board.accessMode) {
                Board.AccessMode.PUBLIC_JOIN -> Member.MembershipStatus.GRANTED
                Board.AccessMode.MANUAL_VERIFY -> Member.MembershipStatus.REQUESTED
                else -> throw ResponseStatusException(HttpStatus.FORBIDDEN)
            }, MemberJson.Permissions.MIN
        )
        val savedMembershipRequest = memberRepository.save(membershipRequest)
        auditLogRepository.save(
            Audit.of(
                savedMembershipRequest, selfUser, when (savedMembershipRequest.status) {
                    Member.MembershipStatus.GRANTED -> Audit.Action.MEMBERSHIP_GRANT
                    Member.MembershipStatus.REQUESTED -> Audit.Action.INVITE_CREATE
                    else -> throw ResponseStatusException(HttpStatus.FORBIDDEN)
                }
            )
        )
    }

    @PostMapping("/invite/{boardId}/{userUUID}")
    fun inviteMemberOrGrantMembership(
        @PathVariable("boardId") boardId: String,
        @PathVariable("userUUID") userUUID: UUID,
    ): MemberJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID = null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(Member.Key(selfUser.uuid, board.id)).orElse(null)
        if (!selfUser.isAdmin && selfMember?.hasEffectiveManagementPower() != true) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        val invitedUser = userRepository.findById(userUUID).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        var invitedMember = memberRepository.findById(Member.Key(invitedUser.uuid, board.id)).orElse(null)
        if (invitedMember == null) {
            invitedMember = Member.newFor(board, invitedUser, Member.MembershipStatus.OFFERED, MemberJson.Permissions.MIN)
            invitedMember = memberRepository.save(invitedMember)
            auditLogRepository.save(Audit.of(invitedMember, selfUser, Audit.Action.INVITE_CREATE))
        } else if (invitedMember.status == Member.MembershipStatus.REQUESTED) {
            invitedMember.apply {
                status = Member.MembershipStatus.GRANTED
                canView = true
            }
            invitedMember = memberRepository.save(invitedMember)
            auditLogRepository.save(Audit.of(invitedMember, selfUser, Audit.Action.MEMBERSHIP_GRANT))
        }

        return MemberJson(invitedMember)
            .includeUser {
                UserJson(invitedUser)
                    .includeId()
                    .includeName()
            }
            .includeStatus()
            .includePermissions()
    }

    @PatchMapping("/member/{boardId}/{userUUID}")
    fun updateMembership(
        @RequestBody requestBody: MemberUpdateJson,
        @PathVariable("boardId") boardId: String,
        @PathVariable("userUUID") userUUID: UUID,
    ): MemberJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID = null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(Member.Key(selfUser.uuid, board.id)).orElse(null)
        if (!selfUser.isAdmin && selfMember?.hasEffectiveManagementPower() != true)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val member = memberRepository.findById(Member.Key(userUUID, board.id)).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        requestBody.versionBaseTimestamp?.let { if (member.modifiedAt.isAfter(it)) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Update based on outdated entity") }

        requestBody.block?.let { if (selfMember.hasEffectiveAdministrationPower() && it) member.status = Member.MembershipStatus.BLOCKED }
        requestBody.permissions?.let {
            it.canView?.let { v -> member.canView = v }
            it.canUse?.let { v -> member.canUse = v }
            it.canManage?.let { v -> member.canManage = v }
            it.canAdministrate?.let { v -> if (selfMember.hasEffectiveAdministrationPower()) member.canAdministrate = v }
        }

        val updatedMember = memberRepository.save(member)
        auditLogRepository.save(Audit.of(updatedMember, selfUser, Audit.Action.MEMBERSHIP_MODIFY))
        return MemberJson(updatedMember)
            .includeUser {
                UserJson(updatedMember.user)
                    .includeId()
                    .includeName()
            }
            .includeStatus()
            .includePermissions()
    }

    @DeleteMapping("/member/{boardId}/{userUUID}")
    fun deleteOrDenyMembership(
        @PathVariable("boardId") boardId: String,
        @PathVariable("userUUID") userUUID: UUID,
    ) {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(Member.Key(selfUser.uuid, board.id)).orElse(null)
        if (!selfUser.isAdmin && (selfMember?.hasEffectiveManagementPower() != true) || selfUser.uuid != userUUID)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        if (userUUID in INTERNAL_USER_UUIDS || userUUID == board.ownerUUID)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val member = memberRepository.findById(Member.Key(userUUID, board.id)).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (member.status != Member.MembershipStatus.BLOCKED) {
            memberRepository.delete(member)
            auditLogRepository.save(Audit.of(member, selfUser, Audit.Action.MEMBERSHIP_DELETE))
        }
    }

}