package de.hypercdn.ticat.server.data.sql.entities.messages

import com.fasterxml.jackson.annotation.JsonFilter
import com.fasterxml.jackson.annotation.JsonIgnore
import de.hypercdn.ticat.server.config.OMIT_UNINITIALIZED_LATEINIT_FIELDS_FILTER
import de.hypercdn.ticat.server.data.helper.CopyConstructable
import de.hypercdn.ticat.server.data.sql.entities.page.Page
import de.hypercdn.ticat.server.data.sql.entities.ticket.Ticket
import de.hypercdn.ticat.server.data.sql.entities.user.User
import de.hypercdn.ticat.server.data.sql.entities.workspace.Workspace
import jakarta.persistence.*
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "messages")
@DynamicInsert
@DynamicUpdate
@JsonFilter(OMIT_UNINITIALIZED_LATEINIT_FIELDS_FILTER)
class Message : CopyConstructable<Message> {

    companion object

    @Id
    @Column(
        name = "message_uuid",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
        name = "sender_uuid",
        nullable = false,
        updatable = false
    )
    lateinit var senderUUID: UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "sender_uuid",
        referencedColumnName = "user_uuid",
        insertable = false,
        updatable = false
    )
    @JsonIgnore
    lateinit var sender: User

    @Embedded
    var recipient: Recipient = Recipient()

    @Embeddable
    class Recipient : CopyConstructable<Recipient> {

        companion object

        @Column(
            name = "recipient_workspace_uuid"
        )
        @ColumnDefault("NULL")
        var workspaceUUID: UUID? = null

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(
            name = "recipient_workspace_uuid",
            referencedColumnName = "workspace_uuid",
            insertable = false,
            updatable = false
        )
        var workspace: Workspace? = null

        @Column(
            name = "recipient_page_uuid"
        )
        @ColumnDefault("NULL")
        var pageUUID: UUID? = null

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(
            name = "recipient_page_uuid",
            referencedColumnName = "page_uuid",
            insertable = false,
            updatable = false
        )
        @JsonIgnore
        var page: Page? = null

        @Column(
            name = "recipient_ticket_uuid"
        )
        @ColumnDefault("NULL")
        var ticketUUID: UUID? = null

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(
            name = "recipient_ticket_uuid",
            referencedColumnName = "ticket_uuid",
            insertable = false,
            updatable = false
        )
        @JsonIgnore
        var ticket: Ticket? = null

        @Column(
            name = "recipient_user_uuid"
        )
        @ColumnDefault("NULL")
        var userUUID: UUID? = null

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(
            name = "recipient_user_uuid",
            referencedColumnName = "user_uuid",
            insertable = false,
            updatable = false
        )
        var user: User? = null

        constructor()

        constructor(other: Recipient) {
            this.workspaceUUID = other.workspaceUUID
            this.pageUUID = other.pageUUID
            this.ticketUUID = other.ticketUUID
            this.userUUID = other.userUUID
        }

    }

    @Column(
        name = "parent_message_uuid",
        updatable = false
    )
    var parentMessageUUID: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "message_uuid",
        referencedColumnName = "parent_message_uuid",
        insertable = false,
        updatable = false
    )
    @JsonIgnore
    var parentMessage: Message? = null

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "parent_message_uuid",
        referencedColumnName = "message_uuid",
        insertable = false,
        updatable = false
    )
    @JsonIgnore
    var childMessages: List<Message>? = null

    @Column(
        name = "content",
        updatable = false
    )
    var content: String? = null

    @Embedded
    var settings: Settings = Settings()

    @Embeddable
    class Settings : CopyConstructable<Settings> {

        companion object

        @Column(
            name = "setting_status",
            nullable = false
        )
        @ColumnDefault("ACTIVE")
        @Enumerated(EnumType.STRING)
        var status: Status = Status.ACTIVE

        enum class Status {
            ACTIVE,
            ARCHIVED,
            DELETED
        }

        constructor()

        constructor(other: Settings) {
            this.status = other.status
        }

    }

    constructor()

    constructor(other: Message) {
        if (other::uuid.isInitialized)
            this.uuid = other.uuid
        if (other::createdAt.isInitialized)
            this.createdAt = other.createdAt
        if (other::modifiedAt.isInitialized)
            this.modifiedAt = other.modifiedAt
        if (other::senderUUID.isInitialized)
            this.senderUUID = other.senderUUID
//        this.recipient = Recipient(other.recipient)
        this.parentMessageUUID = other.parentMessageUUID
        this.content = other.content
        this.settings = Settings(other.settings)
    }

}