package de.hypercdn.ticat.api.entities

import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import java.time.LocalDateTime
import java.util.UUID

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
        name = "is_disabled",
        nullable = false
    )
    @ColumnDefault("false")
    var isDisabled: Boolean = false

    @Column(
        name = "is_admin",
        nullable = false
    )
    @ColumnDefault("false")
    var isAdmin: Boolean = false

    override fun toString(): String {
        return "$uuid is ($firstName, $lastName) as $displayName"
    }
}