package de.hypercdn.ticat.api.endpoints.board

import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BoardsHandler @Autowired constructor(
    val boardRepository: BoardRepository,
    val userRepository: UserRepository
) {

    // auth required
    @GetMapping("/boards/owned")
    fun listOwnedBoards(): List<Any> {
        return emptyList()
    }

    // auth required
    @GetMapping("/boards/memberof")
    fun listBoardsWhereMember(): List<Any> {
        return emptyList()
    }

    // optional auth
    @GetMapping("/boards/popular")
    fun listPopularPublic(): List<Any> {
        return emptyList()
    }

}