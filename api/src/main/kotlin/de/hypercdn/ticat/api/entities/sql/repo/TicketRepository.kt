package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.entities.Board
import de.hypercdn.ticat.api.entities.sql.entities.Ticket
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : JpaRepository<Ticket, Ticket.Key> {

    @Query(
        """
        FROM Ticket ticket
        WHERE ticket.boardId = :#{#board.id}
    """
    )
    fun getTicketsOf(
        @Param("board") board: Board,
        page: Pageable = PageRequest.of(0, 100),
    ): List<Ticket>

}