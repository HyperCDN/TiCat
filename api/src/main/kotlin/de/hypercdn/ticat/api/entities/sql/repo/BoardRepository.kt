package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.Board
import de.hypercdn.ticat.api.entities.sql.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BoardRepository : JpaRepository<Board, String> {

    @Query("SELECT board FROM Board board WHERE board.ownerUUID = :#{#user.uuid}")
    fun ownedBy(@Param("user") user: User): List<Board>

    @Query("SELECT board FROM Board board LEFT JOIN BoardMember member ON board.id = member.boardId WHERE member.userUUID = :#{#user.uuid} AND NOT board.ownerUUID = :#{#user.uuid}")
    fun forMember(@Param("user") user: User): List<Board>

}