package de.hypercdn.ticat.api.endpoints.user

import de.hypercdn.ticat.api.entities.User
import de.hypercdn.ticat.api.entities.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpServerErrorException.InternalServerError
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
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
        val context = SecurityContextHolder.getContext()
        val authentication = context.authentication
        if (authentication is JwtAuthenticationToken){
            val user = User()
            user.uuid = UUID.fromString(authentication.tokenAttributes["sub"] as String)
            user.firstName = authentication.tokenAttributes["given_name"] as String
            user.lastName = authentication.tokenAttributes["family_name"] as String
            user.displayName = authentication.tokenAttributes["preferred_username"] as String
            user.email = authentication.tokenAttributes["email"] as? String
            user.isDisabled = ! ((authentication.tokenAttributes["realm_access"] as Map<*, *> )["roles"] as List<*>).contains(userRole)
            user.isAdmin = ((authentication.tokenAttributes["realm_access"] as Map<*, *> )["roles"] as List<*>).contains(adminRole)

//            val user = User(
//                UUID.fromString(authentication.tokenAttributes["sub"] as String),
//                LocalDateTime.now(),
//                authentication.tokenAttributes["given_name"] as String,
//                authentication.tokenAttributes["family_name"] as String,
//                authentication.tokenAttributes["preferred_username"] as String,
//                authentication.tokenAttributes["email"] as? String,
//                ! ((authentication.tokenAttributes["realm_access"] as Map<*, *> )["roles"] as List<*>).contains(userRole),
//                  ((authentication.tokenAttributes["realm_access"] as Map<*, *> )["roles"] as List<*>).contains(adminRole),
//            )

            return userRepository.save(user)
        }
        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal auth failure")
    }

    @GetMapping("/user/test")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun authTest(){
    }


}