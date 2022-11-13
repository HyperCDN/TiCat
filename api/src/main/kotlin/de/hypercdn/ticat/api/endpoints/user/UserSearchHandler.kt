package de.hypercdn.ticat.api.endpoints.user

import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class UserSearchHandler @Autowired constructor(
    val userRepository: UserRepository
) {

    // auth required
    @GetMapping("/search/users")
    fun searchForUser(
        @RequestParam(name = "uuid", required = false) uuid: UUID?,
        @RequestParam(name = "qName", required = false) qName: String?,
        @RequestParam(name = "email", required = false) email: String?
    ): List<UserJson> {
        throw NotImplementedError()
    }

}