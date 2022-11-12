package de.hypercdn.ticat.api.endpoints.board

import de.hypercdn.ticat.api.entities.json.out.BoardJson
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class BoardHandler @Autowired constructor(
    val boardRepository: BoardRepository,
    val userRepository: UserRepository
) {

    // optional auth
    @GetMapping("/board/:boardId")
    fun getBoard(@PathVariable("boardId") boardId: String): BoardJson {
        throw NotImplementedError()
    }

    // auth required
    @PostMapping("/board")
    fun createBoard(@RequestBody board: Any): BoardJson {
        throw NotImplementedError()
    }

    // auth required
    @PutMapping("/board")
    fun updateBoard(@RequestBody board: Any): BoardJson {
        throw NotImplementedError()
    }

    // auth required
    @DeleteMapping("/board/:boardId")
    fun deleteBoard(@PathVariable("boardId") boardId: String) {

    }





}