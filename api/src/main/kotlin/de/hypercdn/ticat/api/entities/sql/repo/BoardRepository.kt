package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.entities.Board
import de.hypercdn.ticat.api.entities.sql.entities.User
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BoardRepository : JpaRepository<Board, String> {

    @Query(
        """
        FROM Board board
        LEFT JOIN Member member ON (board.id = member.boardId)
        WHERE (:#{#user.isAdmin()} = true)
            OR (board.ownerUUID = :#{#user.uuid})
            OR (member.userUUID = :#{#user.uuid} AND member.status = 'GRANTED' AND member.canView = true)
            OR (board.visibility = 'LOGGED_IN_USER' AND :#{#authenticatedUser} = true)
            OR (board.visibility = 'ANYONE')
    """
    )
    fun getBoardsAvailableTo(
        @Param("user") user: User,
        page: Pageable = PageRequest.of(0, 50),
        @Param("authenticatedUser") authenticatedUser: Boolean = false
    ): List<Board>


}