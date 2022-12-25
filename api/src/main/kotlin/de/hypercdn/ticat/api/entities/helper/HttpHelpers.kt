package de.hypercdn.ticat.api.entities.helper

import de.hypercdn.ticat.api.entities.sql.entities.Board
import de.hypercdn.ticat.api.entities.sql.entities.Ticket
import de.hypercdn.ticat.api.entities.sql.entities.User
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.TicketRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Throws(ResponseStatusException::class)
fun UserRepository.getLoggedInOrFallbackElse403(fallbackUUID: UUID? = User.GUEST_UUID): User {
    val user = if (User.isAuthenticated()) {
        findByIdOrNull(User.currentAuthUUID())
    } else {
        if (fallbackUUID != null)
            findByIdOrNull(User.GUEST_UUID) else null
    }
    if (user == null || !user.canLogin)
        throw ResponseStatusException(HttpStatus.FORBIDDEN)
    return user
}

@Throws(ResponseStatusException::class)
fun BoardRepository.getBoardIfExistsElse404(boardId: String): Board {
    return findByIdOrNull(boardId.uppercase())
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
}

@Throws(ResponseStatusException::class)
fun TicketRepository.getTicketIfExistsElse404(ticketId: Ticket.Key): Ticket {
    return findByIdOrNull(ticketId)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
}

@Throws(ResponseStatusException::class)
fun UserRepository.getUserIfExistsElse404(userUUID: UUID): User {
    return findByIdOrNull(userUUID)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
}