package de.hypercdn.ticat.api.endpoints.user

import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.sql.User
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserInfoHandler @Autowired constructor(
    val userRepository: UserRepository
) {

    // auth required
    @GetMapping("/user/info")
    fun getUserInfo(): UserJson{
        return UserJson(userRepository.findById(User.currentAuthUUID()).orElse(null))
            .includeAll()
    }

}