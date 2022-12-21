package de.hypercdn.ticat.api.entities.sql.entities

import de.hypercdn.ticat.api.entities.sql.shared.EntityHint
import jakarta.persistence.*
import jakarta.persistence.Table
import lombok.NoArgsConstructor
import org.hibernate.annotations.*
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.*

@Entity
@IdClass(Member.Key::class)
@Table(name = "board_members")
@DynamicInsert
@DynamicUpdate
class Member : EntityHint {

    @NoArgsConstructor
    class Key(
        var userUUID: UUID,
        var boardId: String
    ) : Serializable

    @Id
    @Column(
        name = "user_uuid",
        nullable = false,
        updatable = false
    )
    lateinit var userUUID: UUID

    @PrimaryKeyJoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_uuid", referencedColumnName = "user_uuid",
        insertable = false, updatable = false
    )
    lateinit var user: User

    @Id
    @Column(
        name = "board_id",
        nullable = false,
        updatable = false
    )
    lateinit var boardId: String

    @PrimaryKeyJoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "board_id", referencedColumnName = "board_id",
        insertable = false, updatable = false
    )
    lateinit var board: Board

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
        name = "status",
        nullable = false
    )
    @ColumnDefault("BLOCKED")
    @Enumerated(EnumType.STRING)
    lateinit var status: MembershipStatus

    @Column(
        name = "can_view",
        nullable = false
    )
    @ColumnDefault("true")
    var canView: Boolean = true

    @Column(
        name = "can_use",
        nullable = false
    )
    @ColumnDefault("true")
    var canUse: Boolean = true

    @Column(
        name = "can_manage",
        nullable = false
    )
    @ColumnDefault("false")
    var canManage: Boolean = false

    @Column(
        name = "can_administrate",
        nullable = false
    )
    @ColumnDefault("false")
    var canAdministrate: Boolean = false

    enum class MembershipStatus {
        REQUESTED,
        OFFERED,
        GRANTED,
        BLOCKED
    }

    override fun asHint(): String {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "Member ${user.displayName} ($boardId, $userUUID)"
    }

}