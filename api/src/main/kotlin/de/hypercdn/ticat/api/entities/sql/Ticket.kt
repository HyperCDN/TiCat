package de.hypercdn.ticat.api.entities.sql

import de.hypercdn.ticat.api.entities.sql.enums.TicketCategory
import de.hypercdn.ticat.api.entities.sql.enums.TicketPriority
import de.hypercdn.ticat.api.entities.sql.enums.TicketStatus
import de.hypercdn.ticat.api.entities.sql.joinkeys.TicketId
import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import java.time.LocalDateTime
import java.util.*

@Entity
@IdClass(TicketId::class)
@Table(name = "tickets")
@DynamicInsert
@DynamicUpdate
class Ticket() {

    @Id
    @Column(
        name = "ticket_id",
        nullable = false,
        updatable = false
    )
    @Generated(GenerationTime.INSERT)
    var id: Int = -1

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
        name = "created_by",
        nullable = false,
        updatable = false
    )
    lateinit var creatorUUID: UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "user_uuid",
        insertable = false, updatable = false)
    lateinit var creator: User

    @Column(
        name = "category",
        nullable = false
    )
    @Enumerated(EnumType.STRING)
    lateinit var category: TicketCategory

    @Column(
        name = "priority",
        nullable = false
    )
    @ColumnDefault("NORMAL")
    @Enumerated(EnumType.STRING)
    var priority: TicketPriority = TicketPriority.NORMAL

    @Column(
        name = "status",
        nullable = false
    )
    @ColumnDefault("OPEN")
    @Enumerated(EnumType.STRING)
    var status: TicketStatus = TicketStatus.OPEN

    @Column(
        name = "title",
        nullable = false
    )
    lateinit var title: String

    @Column(
        name = "content",
        nullable = false
    )
    @ColumnDefault("")
    var content: String = ""

    @Column(
        name = "assignee",
        nullable = true
    )
    @ColumnDefault("NULL")
    var assigneeUUID: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee", referencedColumnName = "user_uuid",
        insertable = false, updatable = false)
    var assignee: User? = null

}