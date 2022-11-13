package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.Board
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.awt.print.Pageable
import java.util.*

@Repository
interface BoardRepository : JpaRepository<Board, String> {

    @Query("""
        SELECT board
        FROM Board board
        WHERE board.ownerUUID = :#{#userUUID}
    """)
    fun findAllOwnedBy(
        @Param("userUUID") userUUID: UUID
    ): List<Board>

    @Query("""
        SELECT board
        FROM Board board
        LEFT JOIN BoardMember member ON board.id = member.boardId
        WHERE member.userUUID = :#{#userUUID}
            AND NOT board.ownerUUID = :#{#userUUID}
    """)
    fun findAllWhereIsMember(
        @Param("userUUID") userUUID: UUID
    ): List<Board>

    @Query("""
        SELECT board
        FROM Board board
        WHERE LOWER(board.id) like LOWER('%:#{#title}%')
            OR LOWER(board.title) like LOWER('%:#{#title}%')
    """)
    fun searchByTitle(
        @Param("title") title: String,
        pageable: Pageable = PageRequest.of(0, 10) as Pageable
    ): List<Board>

}