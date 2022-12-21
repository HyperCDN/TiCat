package de.hypercdn.ticat.api.entities.sql.entities

import de.hypercdn.ticat.api.entities.sql.shared.EntityHint
import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "users")
@DynamicInsert
@DynamicUpdate
class User : EntityHint {

    @Id
    @Column(
        name = "user_uuid",
        nullable = false,
        updatable = false
    )
    lateinit var uuid: UUID

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    @ColumnDefault("NOW()")
    @CreationTimestamp
    lateinit var createdAt: OffsetDateTime

    @Column(
        name = "modified_at",
        nullable = false
    )
    @ColumnDefault("NOW()")
    @UpdateTimestamp
    lateinit var modifiedAt: OffsetDateTime

    @Column(
        name = "first_name",
        nullable = true
    )
    @ColumnDefault("NULL")
    var firstName: String? = null

    @Column(
        name = "last_name",
        nullable = true
    )
    @ColumnDefault("NULL")
    var lastName: String? = null

    @Column(
        name = "display_name",
        nullable = false
    )
    lateinit var displayName: String

    @Column(
        name = "email",
        nullable = true
    )
    @ColumnDefault("NULL")
    var email: String? = null

    @Column(
        name = "is_system",
        nullable = false
    )
    @ColumnDefault("false")
    var isSystem: Boolean = false

    @Column(
        name = "is_admin",
        nullable = false
    )
    @ColumnDefault("false")
    var isAdmin: Boolean = false

    @Column(
        name = "can_login",
        nullable = false
    )
    @ColumnDefault("true")
    var canLogin: Boolean = true

    @Column(
        name = "can_board_create",
        nullable = false
    )
    @ColumnDefault("false")
    var canBoardCreate: Boolean = false

    @Column(
        name = "can_board_join",
        nullable = false
    )
    @ColumnDefault("true")
    var canBoardJoin: Boolean = true
    override fun asHint(): String {
        return "user#$uuid"
    }

    override fun toString(): String {
        return "User $displayName ($uuid)"
    }

    companion object {

        fun isAuthenticated(): Boolean {
            val context = SecurityContextHolder.getContext()
            val authentication = context.authentication
            return authentication != null && authentication.isAuthenticated && authentication is JwtAuthenticationToken
        }

        fun jwtAuthenticationToken(): JwtAuthenticationToken {
            val context = SecurityContextHolder.getContext()
            val authentication = context.authentication
            if (authentication !is JwtAuthenticationToken) throw IllegalAccessError("No authentication available or type mismatch")
            return authentication
        }

        fun currentAuthUUID(): UUID {
            return UUID.fromString(jwtAuthenticationToken().tokenAttributes["sub"] as String)
        }

        val SYSTEM_UUID: UUID = UUID.fromString("00000000-0000-4000-0000-000000000000")
        val DELETED_USER_UUID: UUID = UUID.fromString("00000000-0000-4000-0000-000000000001")
        val GUEST_UUID: UUID = UUID.fromString("00000000-0000-4000-0000-000000000002")

    }
}