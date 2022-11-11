package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.BoardMember
import de.hypercdn.ticat.api.entities.sql.joinkeys.BoardMemberId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardMemberRepository : JpaRepository<BoardMember, BoardMemberId>{
}