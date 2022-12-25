package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.entities.Audit
import de.hypercdn.ticat.api.entities.sql.entities.Board
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface AuditLogRepository : CrudRepository<Audit, Long> {

    @Query(
        """
        FROM Audit audit
    """
    )
    fun getAllPaged(
        page: Pageable = PageRequest.of(0, 100)
    ): List<Audit>

    @Query(
        """
        FROM Audit audit
        WHERE :#{#board} = audit.entityReferenceBoard
    """
    )
    fun getAllFor(
        @Param("board") board: Board,
        page: Pageable = PageRequest.of(0, 100)
    ): List<Audit>

}