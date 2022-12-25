package de.hypercdn.ticat.api.endpoints.data.users

import de.hypercdn.ticat.api.entities.helper.findByIdElseThrow404
import de.hypercdn.ticat.api.entities.helper.getLoggedInOrFallbackElse403
import de.hypercdn.ticat.api.entities.json.`in`.UserUpdateJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.sql.entities.Audit
import de.hypercdn.ticat.api.entities.sql.repo.AuditLogRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class UserManagement @Autowired constructor(
    val userRepository: UserRepository,
    val auditLogRepository: AuditLogRepository
) {

    @PatchMapping("/user/{userUUID}")
    fun updateUserPermissions(
        @PathVariable("userUUID") userUUID: UUID,
        @RequestBody requestBody: UserUpdateJson
    ): UserJson {
        val selfUser = userRepository.getLoggedInOrFallbackElse403(fallbackUUID = null)
        if (!selfUser.isAdmin)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val user = userRepository.findByIdElseThrow404(userUUID)
        requestBody.versionBaseTimestamp?.let {
            if (user.modifiedAt.isAfter(it))
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Update based on outdated entity")
        }
        requestBody.permissions?.let {
            it.canLogin?.let { v -> user.canLogin = v }
            it.canBoardCreate?.let { v -> user.canBoardCreate = v }
            it.canBoardJoin?.let { v -> user.canBoardJoin = v }
        }
        val updatedUserSaved = userRepository.save(user)
        auditLogRepository.save(Audit.of(user, selfUser, Audit.Action.USER_MODIFY))
        return UserJson(updatedUserSaved)
            .includeAll()
    }

}