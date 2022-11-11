package de.hypercdn.ticat.api.entities.sql

import de.hypercdn.ticat.api.entities.sql.enums.BoardMembershipStatus
import de.hypercdn.ticat.api.entities.sql.joinkeys.BoardMemberId
import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import java.time.LocalDateTime
import java.util.*

@Entity
@IdClass(BoardMemberId::class)
@Table(name = "board_members")
@DynamicInsert
@DynamicUpdate
class BoardMember() {

    @Id
    @Column(
        name = "user_uuid",
        nullable = false,
        updatable = false
    )
    lateinit var userUUID: UUID

    @PrimaryKeyJoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", referencedColumnName = "user_uuid",
        insertable = false, updatable = false)
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
    @JoinColumn(name = "board_id", referencedColumnName = "board_id",
        insertable = false, updatable = false)
    lateinit var board: Board

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    @Generated(GenerationTime.INSERT)
    @ColumnDefault("NOW()")
    lateinit var createdAt: LocalDateTime

    @Column(
        name = "status",
        nullable = false
    )
    @ColumnDefault("BLOCKED")
    @Enumerated(EnumType.STRING)
    lateinit var status: BoardMembershipStatus

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

}