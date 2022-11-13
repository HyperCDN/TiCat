package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.Ticket
import de.hypercdn.ticat.api.entities.sql.joinkeys.TicketId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : JpaRepository<Ticket, TicketId> {
}