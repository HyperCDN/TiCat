package de.hypercdn.ticat.api.endpoints.data.user

import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.sql.User
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class UserHandler @Autowired constructor(
    val userRepository: UserRepository
) {
    @GetMapping("/user/@")
    fun getSelfUserInfo(): UserJson {
        return UserJson(userRepository.findById(User.currentAuthUUID()).orElse(null))
            .includeAll()
    }

    @GetMapping("/user/{userUUID}")
    fun getUserInfo(
        @PathVariable("userUUID") userUUID: UUID,
    ): UserJson {
        val user = userRepository.findById(userUUID).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        return UserJson(user)
            .includeId()
            .includeFullName()
            .includeEmail()
            .includePermissions()
    }

}