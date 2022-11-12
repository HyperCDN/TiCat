package de.hypercdn.ticat.api.endpoints.board

import de.hypercdn.ticat.api.entities.json.out.BoardJson
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BoardSearchHandler @Autowired constructor(
    val boardRepository: BoardRepository,
    val userRepository: UserRepository
) {

    // optional auth
    @GetMapping("/search/boards")
    fun searchForBoards(@RequestParam("qName") qName: String): List<BoardJson> {
        throw NotImplementedError()
    }

}