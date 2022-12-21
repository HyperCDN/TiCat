package de.hypercdn.ticat.api.endpoints.data.board

import de.hypercdn.ticat.api.entities.helper.getBoardIfExists
import de.hypercdn.ticat.api.entities.helper.getLoggedInOrFallbackWhenAllowed
import de.hypercdn.ticat.api.entities.helper.hasEffectiveManagementPower
import de.hypercdn.ticat.api.entities.json.`in`.BoardCreateJson
import de.hypercdn.ticat.api.entities.json.`in`.BoardUpdateJson
import de.hypercdn.ticat.api.entities.json.out.BoardJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.sql.entities.Board
import de.hypercdn.ticat.api.entities.sql.entities.Member
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.MemberRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class BoardManagement @Autowired constructor(
    val userRepository: UserRepository,
    val boardRepository: BoardRepository,
    val memberRepository: MemberRepository
) {

    @PostMapping("/board")
    fun createNewBoard(
        @RequestBody requestBody: BoardCreateJson
    ): BoardJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID = null)
        if (!selfUser.canBoardCreate)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)

        val newBoard = Board()
        newBoard.id = requestBody.id!!
        newBoard.title = requestBody.title!!
        requestBody.description?.let { newBoard.description = it }
        requestBody.settings?.let {
            it.visibility?.let { v -> newBoard.visibility = v }
            it.accessMode?.let { v -> newBoard.accessMode = v }
        }
        if (boardRepository.existsById(newBoard.id))
            throw ResponseStatusException(HttpStatus.CONFLICT)

        val newBoardSaved = boardRepository.save(newBoard)
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
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID = null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(Member.Key(selfUser.uuid, board.id)).orElse(null)
        if (!selfUser.isAdmin && selfMember?.hasEffectiveManagementPower() != true)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)

        requestBody.versionBaseTimestamp?.let { if (board.modifiedAt.isAfter(it)) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Update based on outdated entity") }

        requestBody.title?.let { board.title = it }
        requestBody.description?.let { board.description = it }
        requestBody.settings?.let {
            it.visibility?.let { v -> board.visibility = v }
            it.accessMode?.let { v -> board.accessMode = v }
        }

        val updatedBoardSaved = boardRepository.save(board)
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
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID = null)
        val board = boardRepository.getBoardIfExists(boardId)
        if (!selfUser.isAdmin && board.ownerUUID != selfUser.uuid)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        boardRepository.delete(board)
    }

}