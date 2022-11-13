package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.User
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.awt.print.Pageable
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    @Query(
        """
            SELECT user
            FROM User user
            WHERE LOWER(user.displayName) like LOWER('%:#{#name}%')
                OR LOWER(user.firstName) like LOWER('%:#{#name}%')
                OR LOWER(user.lastName) like LOWER('%:#{#name}%')
        """
    )
    fun searchByName(
        @Param("name") name: String,
        @Param("limit") limit: Int,
        pageable: Pageable = PageRequest.of(0, 10) as Pageable
    ): List<User>
}