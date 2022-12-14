package de.hypercdn.ticat.api.endpoints.data.board.member

import de.hypercdn.ticat.api.entities.helper.*
import de.hypercdn.ticat.api.entities.json.`in`.MemberUpdateJson
import de.hypercdn.ticat.api.entities.json.out.MemberJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.sql.Board
import de.hypercdn.ticat.api.entities.sql.Member
import de.hypercdn.ticat.api.entities.sql.joinkeys.MemberId
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
    val memberRepository: MemberRepository
) {

    @PostMapping("/i/{boardId}")
    fun requestAccessOrAcceptInvite(
        @PathVariable("boardId") boardId: String,
    ) {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID = null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMembership = memberRepository.findById(MemberId(selfUser.uuid, board.id)).orElse(null)
        if (selfMembership != null) {
            if (selfMembership.status != Member.MembershipStatus.OFFERED) return
            selfMembership.status = Member.MembershipStatus.GRANTED
            selfMembership.canView = true
            memberRepository.save(selfMembership)
            return
        }
        if (!selfUser.isAdmin && (!selfUser.canBoardJoin || !board.isVisibleTo(selfUser))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        val membershipRequest = Member()
        membershipRequest.boardId = board.id
        membershipRequest.userUUID = selfUser.uuid
        membershipRequest.status = when (board.accessMode) {
            Board.AccessMode.PUBLIC_JOIN -> Member.MembershipStatus.GRANTED
            Board.AccessMode.MANUAL_VERIFY -> Member.MembershipStatus.REQUESTED
            else -> throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        membershipRequest.canView = true
        memberRepository.save(membershipRequest)
    }

    @PostMapping("/i/{boardId}/{userUUID}")
    fun inviteMemberOrAcceptRequest(
        @PathVariable("boardId") boardId: String,
        @PathVariable("userUUID") userUUID: UUID,
    ): MemberJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID = null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(MemberId(selfUser.uuid, board.id)).orElse(null)
        if (!selfUser.isAdmin && selfMember?.hasEffectiveManagementPower() != true ) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        val invitedUser = userRepository.findById(userUUID).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        var invitedMember = memberRepository.findById(MemberId(invitedUser.uuid, board.id)).orElse(null)
        if (invitedMember == null) {
            invitedMember = Member()
            invitedMember.boardId = board.id
            invitedMember.userUUID = invitedUser.uuid
            invitedMember.status = Member.MembershipStatus.OFFERED
            invitedMember = memberRepository.save(invitedMember)
        } else if (invitedMember.status == Member.MembershipStatus.REQUESTED){
            invitedMember.status = Member.MembershipStatus.GRANTED
            invitedMember.canView = true
            invitedMember = memberRepository.save(invitedMember)
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

    @PatchMapping("/m/{boardId}/{userUUID}")
    fun updateMembership(
        @RequestBody requestBody: MemberUpdateJson,
        @PathVariable("boardId") boardId: String,
        @PathVariable("userUUID") userUUID: UUID,
    ): MemberJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID = null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(MemberId(selfUser.uuid, board.id)).orElse(null)
        if (!selfUser.isAdmin && selfMember?.hasEffectiveManagementPower() != true)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val member = memberRepository.findById(MemberId(userUUID, board.id)).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        requestBody.versionBaseTimestamp?.let { if (member.updatedAt.isAfter(it)) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Update based on outdated entity") }

        requestBody.block?.let { if (selfMember.hasEffectiveAdministrationPower() && it) member.status = Member.MembershipStatus.BLOCKED }
        requestBody.permissions?.let {
            it.canView?.let { v -> member.canView = v }
            it.canUse?.let { v -> member.canUse = v }
            it.canManage?.let { v -> member.canManage = v }
            it.canAdministrate?.let { v -> if (selfMember.hasEffectiveAdministrationPower()) member.canAdministrate = v}
        }

        val updatedMember = memberRepository.save(member)
        return MemberJson(updatedMember)
            .includeUser {
                UserJson(updatedMember.user)
                    .includeId()
                    .includeName()
            }
            .includeStatus()
            .includePermissions()
    }

    @DeleteMapping("/m/{boardId}/{userUUID}")
    fun deleteOrDenyMembership(
        @PathVariable("boardId") boardId: String,
        @PathVariable("userUUID") userUUID: UUID,
    ) {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(MemberId(selfUser.uuid, board.id)).orElse(null)
        if (!selfUser.isAdmin && (selfMember?.hasEffectiveManagementPower() != true) || selfUser.uuid != userUUID)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val member = memberRepository.findById(MemberId(userUUID, board.id)).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (member.status != Member.MembershipStatus.BLOCKED){
            memberRepository.delete(member)
        }
    }

}