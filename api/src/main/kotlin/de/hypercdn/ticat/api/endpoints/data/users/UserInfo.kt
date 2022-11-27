package de.hypercdn.ticat.api.endpoints.data.users

import de.hypercdn.ticat.api.entities.helper.getLoggedInOrFallbackWhenAllowed
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
class UserInfo @Autowired constructor(
    val userRepository: UserRepository
){

    @GetMapping("/u/{userUUID}")
    fun getUserInfo(
        @PathVariable("userUUID") userUUID: UUID
    ): UserJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        val user = userRepository.findById(userUUID).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return UserJson(user)
            .includeId()
            .includeName()
            .includeFullName(!User.isAuthenticated())
            .includeEmail(!User.isAuthenticated())
            .includePermissions(!selfUser.isAdmin)

    }

}