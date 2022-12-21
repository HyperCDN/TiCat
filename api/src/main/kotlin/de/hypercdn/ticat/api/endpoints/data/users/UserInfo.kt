package de.hypercdn.ticat.api.endpoints.data.users

import de.hypercdn.ticat.api.entities.helper.getLoggedInOrFallbackWhenAllowed
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.json.out.helper.PagedData
import de.hypercdn.ticat.api.entities.sql.User
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import jakarta.validation.constraints.Size
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class UserInfo @Autowired constructor(
    val userRepository: UserRepository
) {

    @GetMapping("/users")
    @Validated
    fun getUserInfos(
        @RequestParam("user_uuids", required = true) @Size(min = 1, max = 100) userUUIDs: List<UUID>
    ): PagedData<UserJson> {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        val users = userRepository.findAllById(userUUIDs)
        val paged = PagedData<UserJson>(PageRequest.of(-1, userUUIDs.size))
        paged.entities = users.stream().map {
            UserJson(it)
                .includeId()
                .includeName()
                .includeFullName(skip = !User.isAuthenticated())
                .includeEmail(skip = !User.isAuthenticated())
                .includePermissions(skip = selfUser.uuid != it.uuid && !selfUser.isAdmin)
        }.toList()
        return paged
    }

    @GetMapping("/user/{userUUID}")
    fun getUserInfo(
        @PathVariable("userUUID") userUUID: UUID
    ): UserJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        val user = userRepository.findById(userUUID).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return UserJson(user)
            .includeId()
            .includeName()
            .includeFullName(skip = !User.isAuthenticated())
            .includeEmail(skip = !User.isAuthenticated())
            .includePermissions(skip = selfUser.uuid != user.uuid && !selfUser.isAdmin)

    }

}