package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.Board
import de.hypercdn.ticat.api.entities.sql.Ticket
import de.hypercdn.ticat.api.entities.sql.joinkeys.TicketId
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : JpaRepository<Ticket, TicketId> {

    @Query("""
        FROM Ticket ticket
        WHERE ticket.boardId = :#{#board.id}
    """)
    fun getTicketsOf(
        @Param("board") board: Board,
        page: Pageable = PageRequest.of(0, 100),
    ): List<Ticket>

}