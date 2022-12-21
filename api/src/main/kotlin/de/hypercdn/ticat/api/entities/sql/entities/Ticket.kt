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
@IdClass(Ticket.Key::class)
@Table(name = "tickets")
@DynamicInsert
@DynamicUpdate
class Ticket : EntityHint {

    @NoArgsConstructor
    class Key(
        var id: Int,
        var boardId: String
    ) : Serializable

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
        name = "created_by",
        nullable = false,
        updatable = false
    )
    lateinit var creatorUUID: UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "created_by", referencedColumnName = "user_uuid",
        insertable = false, updatable = false
    )
    lateinit var creator: User

    @Column(
        name = "category",
        nullable = false
    )
    @Enumerated(EnumType.STRING)
    lateinit var category: Category

    @Column(
        name = "priority",
        nullable = false
    )
    @ColumnDefault("NORMAL")
    @Enumerated(EnumType.STRING)
    var priority: Priority = Priority.NORMAL

    @Column(
        name = "status",
        nullable = false
    )
    @ColumnDefault("OPEN")
    @Enumerated(EnumType.STRING)
    var status: Status = Status.OPEN

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
    @JoinColumn(
        name = "assignee", referencedColumnName = "user_uuid",
        insertable = false, updatable = false
    )
    var assignee: User? = null

    enum class Category {
        EPIC,
        BUG,
        STORY
    }

    enum class Priority {
        CRITICAL,
        HIGH,
        NORMAL,
        LOW,
        NONE
    }

    enum class Status {
        OPEN,
        CLOSED,
        DELETED
    }

    override fun asHint(): String {
        return "ticket#$boardId:$id"
    }

    override fun toString(): String {
        return "Ticket $id ($boardId)"
    }

}