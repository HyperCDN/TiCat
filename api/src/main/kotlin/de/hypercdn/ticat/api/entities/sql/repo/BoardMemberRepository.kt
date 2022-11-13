package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.BoardMember
import de.hypercdn.ticat.api.entities.sql.joinkeys.BoardMemberId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BoardMemberRepository : JpaRepository<BoardMember, BoardMemberId>{

    @Query("SELECT member FROM BoardMember member WHERE member.boardId = :#{#boardId}")
    fun findAllForBoard(@Param("boardId") boardId: String): List<BoardMember>
}