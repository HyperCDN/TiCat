package de.hypercdn.ticat.api.endpoints.data.board

import de.hypercdn.ticat.api.entities.helper.*
import de.hypercdn.ticat.api.entities.json.out.BoardJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.json.out.helper.PagedData
import de.hypercdn.ticat.api.entities.sql.entities.Member
import de.hypercdn.ticat.api.entities.sql.entities.User
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.MemberRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import jakarta.validation.constraints.Min
import org.hibernate.validator.constraints.Range
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class BoardInfo @Autowired constructor(
    val userRepository: UserRepository,
    val boardRepository: BoardRepository,
    val memberRepository: MemberRepository
) {

    @GetMapping("/boards")
    @Validated
    fun getAvailableBoards(
        @RequestParam("page", required = false, defaultValue = "0") @Min(0) page: Int,
        @RequestParam("chunkSize", required = false, defaultValue = "50") @Range(min = 1, max = 50) chunkSize: Int
    ): PagedData<BoardJson> {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        val pageRequest = PageRequest.of(page, chunkSize)
        val boards = boardRepository.getBoardsAvailableTo(selfUser)
        return PagedData<BoardJson>(pageRequest).apply {
            entities = boards.stream()
                .map {
                    BoardJson(it)
                        .includeId()
                        .includeTitle()
                        .includeOwner {
                            UserJson(it.owner)
                                .includeId()
                                .includeName()
                                .includeFullName(skip = !User.isAuthenticated())
                        }
                }
                .toList()
        }
    }

    @GetMapping("/board/{boardId}")
    fun getBoardInfo(
        @PathVariable("boardId") boardId: String
    ): BoardJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(Member.Key(selfUser.uuid, board.id)).orElse(null)
        if (!board.isVisibleTo(selfUser, selfMember))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        return BoardJson(board)
            .includeId()
            .includeTitle()
            .includeDescription()
            .includeSettings(skip = !(selfMember?.hasEffectiveManagementPower() ?: false))

    }

}