package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.Board
import de.hypercdn.ticat.api.entities.sql.Member
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Member.Key>{

    @Query("""
        FROM Member member
        WHERE member.boardId = :#{#board.id}
            AND (member.status = 'GRANTED' OR :#{#anyStatus} = true)
    """)
    fun getMembersOf(
        @Param("board") board: Board,
        page: Pageable = PageRequest.of(0, 100),
        @Param("anyStatus") anyStatus: Boolean = false
    ): List<Member>

}