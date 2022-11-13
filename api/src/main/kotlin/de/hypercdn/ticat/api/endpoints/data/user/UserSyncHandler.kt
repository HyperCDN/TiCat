package de.hypercdn.ticat.api.endpoints.data.user

import de.hypercdn.ticat.api.entities.sql.User
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class UserSyncHandler @Autowired constructor(
    @Value("\${spring.security.oauth2.resourceserver.jwt.role.admin}")
    val adminRole: String,

    @Value("\${spring.security.oauth2.resourceserver.jwt.role.user}")
    val userRole: String,

    val userRepository: UserRepository
) {

    @PostMapping("/user/sync")
    fun updateUserDetails(): User {
        try {
            val authentication = User.jwtAuthenticationToken()
            val user = User()
            user.uuid = UUID.fromString(authentication.tokenAttributes["sub"] as String)
            user.firstName = authentication.tokenAttributes["given_name"] as String
            user.lastName = authentication.tokenAttributes["family_name"] as String
            user.displayName = authentication.tokenAttributes["preferred_username"] as String
            user.email = authentication.tokenAttributes["email"] as? String
            user.canLogin = ((authentication.tokenAttributes["realm_access"] as Map<*, *> )["roles"] as List<*>).contains(userRole)
            user.isAdmin = ((authentication.tokenAttributes["realm_access"] as Map<*, *> )["roles"] as List<*>).contains(adminRole)
            return userRepository.save(user)
        }catch (e: IllegalAccessException) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal auth failure")
        }
    }

    @GetMapping("/user/test")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun authTest(){
        // endpoint to be used to verify that an auth token is valid
    }


}