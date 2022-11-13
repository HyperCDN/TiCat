package de.hypercdn.ticat.api.entities.sql

import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
@DynamicInsert
@DynamicUpdate
class User() {

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
    @Generated(GenerationTime.INSERT)
    @ColumnDefault("NOW()")
    lateinit var createdAt: LocalDateTime

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

    override fun toString(): String {
        return "$uuid is ($firstName, $lastName) as $displayName"
    }

    companion object{

        fun isAuthenticated(): Boolean {
            val context = SecurityContextHolder.getContext()
            val authentication = context.authentication
            return authentication != null && authentication.isAuthenticated
        }

        fun jwtAuthenticationToken(): JwtAuthenticationToken {
            val context = SecurityContextHolder.getContext()
            val authentication = context.authentication
            if (authentication is JwtAuthenticationToken){
                return authentication
            }
            throw IllegalAccessError("No authentication available or type missmatch")
        }
        fun currentAuthUUID(): UUID {
            return UUID.fromString(jwtAuthenticationToken().tokenAttributes["sub"] as String)
        }

        val SYSTEM_UUID: UUID = UUID.fromString("00000000-0000-4000-0000-000000000000")
        val DELETED_USER_UUID: UUID = UUID.fromString("00000000-0000-4000-0000-000000000001")
        val GUEST_UUID: UUID = UUID.fromString("00000000-0000-4000-0000-000000000002")

    }
}