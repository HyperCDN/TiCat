package de.hypercdn.ticat.api.entities.helper

import de.hypercdn.ticat.api.entities.sql.Board
import de.hypercdn.ticat.api.entities.sql.User
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Throws(ResponseStatusException::class)
fun UserRepository.getLoggedInOrFallbackWhenAllowed(): User {
    val user = if (User.isAuthenticated()) {
        findById(User.currentAuthUUID())
    } else {
        findById(User.GUEST_UUID)
    }.orElse(null)
    if (user == null || !user.canLogin) {
        throw ResponseStatusException(HttpStatus.FORBIDDEN)
    }
    return user
}

@Throws(ResponseStatusException::class)
fun BoardRepository.getBoardIfExist(boardId: String): Board {
    return findById(boardId.uppercase()).orElse(null)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
}