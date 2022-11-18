package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.Board
import de.hypercdn.ticat.api.entities.sql.Member
import de.hypercdn.ticat.api.entities.sql.joinkeys.MemberId
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, MemberId>{

    @Query("""
        SELECT members
        FROM Member members
        WHERE members.boardId = :#{#board.id}
            AND (members.status = 'GRANTED' OR :#{#onlyGranted} = false)
    """)
    fun getMembersOf(
        @Param("board") board: Board,
        page: Pageable = PageRequest.of(0, 50),
        @Param("onlyGranted") onlyGranted: Boolean = true
    ): List<Member>

}