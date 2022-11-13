package de.hypercdn.ticat.api.endpoints.data.board

import de.hypercdn.ticat.api.entities.helper.getBoardIfExist
import de.hypercdn.ticat.api.entities.helper.getLoggedInOrFallbackWhenAllowed
import de.hypercdn.ticat.api.entities.json.`in`.board.BoardCreateJson
import de.hypercdn.ticat.api.entities.json.`in`.board.BoardUpdateJson
import de.hypercdn.ticat.api.entities.json.out.BoardJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.sql.Board
import de.hypercdn.ticat.api.entities.sql.enums.BoardMembershipStatus
import de.hypercdn.ticat.api.entities.sql.enums.BoardVisibility
import de.hypercdn.ticat.api.entities.sql.joinkeys.BoardMemberId
import de.hypercdn.ticat.api.entities.sql.repo.BoardMemberRepository
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
class BoardHandler @Autowired constructor(
    val userRepository: UserRepository,
    val memberRepository: BoardMemberRepository,
    val boardRepository: BoardRepository
) {

    @GetMapping("/board/{boardId}")
    fun getBoardData(
        @PathVariable("boardId") boardId: String,
        @RequestParam(value = "include_description", required = false, defaultValue = "true") includeDescription: Boolean,
        @RequestParam(value = "include_settings", required = false, defaultValue = "true") includeSettings: Boolean
    ): BoardJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        val board = boardRepository.getBoardIfExist(boardId.uppercase())
        val selfMember = memberRepository.findById(BoardMemberId(selfUser.uuid, boardId.uppercase())).orElse(null)

        if (
            (selfMember == null && board.visibility != BoardVisibility.ANYONE)
            || (selfMember != null && (selfMember.status != BoardMembershipStatus.GRANTED || !selfMember.canView))
        ){
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }

        return BoardJson(board)
            .includeId()
            .includeTitle()
            .includeDescription(!includeDescription)
            .includeOwner {
                UserJson(board.owner)
                    .includeId()
                    .includeName()
            }
            .includeSettings(!includeSettings)
    }

    @PostMapping("/board")
    fun createBoard(@RequestBody boardCreateJson: BoardCreateJson): BoardJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        if (!selfUser.canBoardCreate || boardRepository.existsById(boardCreateJson.id!!.uppercase())) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        val newBoard = Board()
        newBoard.id = boardCreateJson.id!!.uppercase()
        newBoard.title = boardCreateJson.title!!
        newBoard.description = boardCreateJson.description
        boardCreateJson.settings?.let {
            it.visibility?.let { v -> newBoard.visibility = v }
            it.accessMode?.let { v -> newBoard.accessMode = v }
        }

        val createdBoard = boardRepository.save(newBoard)

        return BoardJson(createdBoard)
            .includeId()
            .includeTitle()
            .includeDescription()
            .includeOwner {
                UserJson(selfUser)
                    .includeId()
                    .includeName()
            }
            .includeSettings()
    }

    @PutMapping("/board")
    fun updateBoard(@RequestBody boardUpdateJson: BoardUpdateJson): BoardJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        val board = boardRepository.getBoardIfExist(boardUpdateJson.id!!.uppercase())
        val selfMember = memberRepository.findById(BoardMemberId(selfUser.uuid, boardUpdateJson.id!!.uppercase())).orElse(null)

        if(board.ownerUUID != selfUser.uuid && !selfMember.canAdministrate) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        boardUpdateJson.title?.let { board.title = it }
        boardUpdateJson.description?.let { board.description = it }
        boardUpdateJson.settings?.let {
            it.visibility?.let { v -> board.visibility = v }
            it.accessMode?.let { v -> board.accessMode = v }
        }

        val updatedBoard = boardRepository.save(board)

        return BoardJson(updatedBoard)
            .includeId()
            .includeTitle()
            .includeDescription()
            .includeOwner {
                UserJson(selfUser)
                    .includeId()
                    .includeName()
            }
            .includeSettings()
    }

    @DeleteMapping
    fun deleteBoard(
        @PathVariable("boardId") boardId: String,
    ) {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        val board = boardRepository.getBoardIfExist(boardId)

        if (selfUser.uuid != board.ownerUUID) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        boardRepository.delete(board)
    }

}