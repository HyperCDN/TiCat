package de.hypercdn.ticat.api.endpoints.auth

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
class JwtSyncEndpoints @Autowired constructor(
    @Value("\${spring.security.oauth2.resourceserver.jwt.role.admin}")
    val adminRole: String,
    @Value("\${spring.security.oauth2.resourceserver.jwt.role.user}")
    val userRole: String,
    val userRepository: UserRepository
) {

    @GetMapping("/auth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun authTest(){
        // endpoint to be used to verify that an auth token is valid
    }

    @PostMapping("/auth")
    fun updateUserDetailsFromJwtContents(): User {
        try {
            val authentication = User.jwtAuthenticationToken()
            val user = User()
            (authentication.tokenAttributes["sub"] as? String)?.let { user.uuid = UUID.fromString(it) }
            (authentication.tokenAttributes["given_name"] as? String)?.let { user.firstName = it }
            (authentication.tokenAttributes["family_name"] as? String)?.let { user.lastName = it }
            (authentication.tokenAttributes["preferred_username"] as? String)?.let { user.displayName = it }
            (authentication.tokenAttributes["email"] as? String)?.let { user.email = it }
            (authentication.tokenAttributes["realm_access"] as? Map<*, *> )?.let {
                (it["roles"] as? List<*>)?.let { v -> {
                    user.canLogin = v.contains(userRole)
                    user.isAdmin = v.contains(adminRole)
                }}
            }
            return userRepository.save(user)
        }catch (e: IllegalAccessException) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal auth failure")
        }
    }

}