package de.hypercdn.ticat.api.endpoints.data.board

import de.hypercdn.ticat.api.entities.helper.*
import de.hypercdn.ticat.api.entities.json.`in`.BoardCreateJson
import de.hypercdn.ticat.api.entities.json.`in`.BoardUpdateJson
import de.hypercdn.ticat.api.entities.json.out.BoardJson
import de.hypercdn.ticat.api.entities.json.out.MemberJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.sql.entities.Audit
import de.hypercdn.ticat.api.entities.sql.entities.Board
import de.hypercdn.ticat.api.entities.sql.entities.Member
import de.hypercdn.ticat.api.entities.sql.entities.User
import de.hypercdn.ticat.api.entities.sql.repo.AuditLogRepository
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.MemberRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class BoardManagement @Autowired constructor(
    val userRepository: UserRepository,
    val boardRepository: BoardRepository,
    val memberRepository: MemberRepository,
    val auditLogRepository: AuditLogRepository
) {

    @PostMapping("/board")
    fun createNewBoard(
        @RequestBody requestBody: BoardCreateJson
    ): BoardJson {
        val selfUser = userRepository.getLoggedInOrFallbackElse403(fallbackUUID = null)
        if (!selfUser.canBoardCreate)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val newBoard = Board()
        newBoard.id = requestBody.id!!.uppercase()
        newBoard.title = requestBody.title!!
        requestBody.description?.let { newBoard.description = it }
        requestBody.settings?.let {
            it.visibility?.let { v -> newBoard.visibility = v }
            it.accessMode?.let { v -> newBoard.accessMode = v }
        }
        if (boardRepository.existsById(newBoard.id))
            throw ResponseStatusException(HttpStatus.CONFLICT)
        val newBoardSaved = boardRepository.save(newBoard)
        auditLogRepository.save(Audit.of(newBoardSaved, selfUser, Audit.Action.BOARD_CREATE))
        val owner = Member.newFor(newBoardSaved, selfUser, Member.MembershipStatus.GRANTED, MemberJson.Permissions.ALL)
        memberRepository.save(owner)
        auditLogRepository.save(Audit.of(owner, selfUser, Audit.Action.MEMBERSHIP_GRANT))
        val guest = Member.newFor(newBoardSaved, selfUser, if (newBoardSaved.visibility == Board.Visibility.ANYONE) Member.MembershipStatus.GRANTED else Member.MembershipStatus.BLOCKED, MemberJson.Permissions.MIN)
        memberRepository.save(guest)
        auditLogRepository.save(Audit.of(guest, selfUser, Audit.Action.MEMBERSHIP_GRANT))
        return BoardJson(newBoardSaved)
            .includeId()
            .includeTitle()
            .includeDescription()
            .includeOwner {
                UserJson(selfUser)
                    .includeId()
            }
            .includeSettings()
    }

    @PatchMapping("/board/{boardId}")
    fun updateBoard(
        @RequestBody requestBody: BoardUpdateJson,
        @PathVariable("boardId") boardId: String
    ): BoardJson {
        val selfUser = userRepository.getLoggedInOrFallbackElse403(fallbackUUID = null)
        val board = boardRepository.findByIdElseThrow404(boardId)
        val selfMember = memberRepository.findByIdOrNull(Member.Key(selfUser.uuid, board.id))
        if (!board.isManageableBy(selfUser, selfMember))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        requestBody.versionBaseTimestamp?.let {
            if (board.modifiedAt.isAfter(it))
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Update based on outdated entity")
        }
        requestBody.title?.let { board.title = it }
        requestBody.description?.let { board.description = it }
        requestBody.settings?.let {
            it.visibility?.let { v ->
                {
                    board.visibility = v
                    memberRepository.findByIdOrNull(Member.Key(User.GUEST_UUID, board.id))?.let { guest ->
                        guest.status = if (v == Board.Visibility.ANYONE) Member.MembershipStatus.GRANTED else Member.MembershipStatus.BLOCKED
                        memberRepository.save(guest)
                        auditLogRepository.save(Audit.of(guest, selfUser, Audit.Action.MEMBERSHIP_MODIFY))
                    }
                }
            }
            it.accessMode?.let { v -> board.accessMode = v }
        }
        val updatedBoardSaved = boardRepository.save(board)
        auditLogRepository.save(Audit.of(updatedBoardSaved, selfUser, Audit.Action.BOARD_MODIFY))
        return BoardJson(updatedBoardSaved)
            .includeId()
            .includeTitle()
            .includeDescription()
            .includeOwner {
                UserJson(board.owner)
                    .includeId()
                    .includeName()
            }
            .includeSettings()
    }

    @DeleteMapping("/board/{boardId}")
    fun deleteBoard(
        @PathVariable("boardId") boardId: String
    ) {
        val selfUser = userRepository.getLoggedInOrFallbackElse403(fallbackUUID = null)
        val board = boardRepository.findByIdElseThrow404(boardId)
        if (!board.isOwnedBy(selfUser))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        boardRepository.delete(board)
        auditLogRepository.save(Audit.of(board, selfUser, Audit.Action.BOARD_DELETE))
    }

}