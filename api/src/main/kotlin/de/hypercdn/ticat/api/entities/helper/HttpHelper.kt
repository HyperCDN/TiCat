package de.hypercdn.ticat.api.entities.helper

import de.hypercdn.ticat.api.entities.sql.Board
import de.hypercdn.ticat.api.entities.sql.Ticket
import de.hypercdn.ticat.api.entities.sql.User
import de.hypercdn.ticat.api.entities.sql.joinkeys.TicketId
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.TicketRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Throws(ResponseStatusException::class)
fun UserRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID: UUID? = User.GUEST_UUID): User {
    val user = if (User.isAuthenticated()) {
        findById(User.currentAuthUUID())
    } else {
        if (fallbackUUID != null) findById(User.GUEST_UUID) else Optional.empty()
    }.orElse(null)
    if (user == null || !user.canLogin)
        throw ResponseStatusException(HttpStatus.FORBIDDEN)
    return user
}

@Throws(ResponseStatusException::class)
fun BoardRepository.getBoardIfExists(boardId: String): Board {
    return findById(boardId.uppercase()).orElse(null)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
}

@Throws(ResponseStatusException::class)
fun TicketRepository.getTicketIfExists(ticketId: TicketId): Ticket {
    return findById(ticketId).orElse(null)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
}