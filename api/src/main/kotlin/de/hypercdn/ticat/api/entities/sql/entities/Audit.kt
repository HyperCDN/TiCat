package de.hypercdn.ticat.api.entities.sql.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import java.time.OffsetDateTime
import java.util.*

class Audit {

    @Id
    @Column(
        name = "audit_id",
        nullable = false,
        updatable = false
    )
    @Generated(GenerationTime.INSERT)
    var id: Long = -1

    @Column(
        name = "action_time",
        nullable = false,
        updatable = false
    )
    @ColumnDefault("NOW()")
    @CreationTimestamp
    lateinit var actionTime: OffsetDateTime

    @Column(
        name = "actor",
        updatable = false
    )
    var actorUUID: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "actor",
        referencedColumnName = "user_uuid",
        insertable = false,
        updatable = false
    )
    var actor: User? = null

    @Column(
        name = "entity_hint_actor",
        updatable = false
    )
    var entityHintActor: String? = null

    @Column(
        name = "action",
        nullable = false,
        updatable = false
    )
    @Enumerated(EnumType.STRING)
    lateinit var action: Action

    @Column(
        name = "entity_reference_user",
        updatable = false
    )
    var entityReferenceUserUUID: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "entity_reference_user",
        referencedColumnName = "user_uuid",
        insertable = false,
        updatable = false
    )
    var entityReferenceUser: User? = null

    @Column(
        name = "entity_hint_user",
        updatable = false
    )
    var entityHintUser: String? = null

    @Column(
        name = "entity_reference_board",
        updatable = false
    )
    var entityReferenceBoardId: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "entity_reference_board",
        referencedColumnName = "board_id",
        insertable = false,
        updatable = false
    )
    var entityReferenceBoard: Board? = null

    @Column(
        name = "entity_hint_board",
        updatable = false
    )
    var entityHintBoard: String? = null

    @Column(
        name = "entity_reference_ticket",
        updatable = false
    )
    var entityReferenceTicketId: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        value = [
            JoinColumn(
                name = "entity_reference_board",
                referencedColumnName = "board_id",
                insertable = false,
                updatable = false
            ),
            JoinColumn(
                name = "entity_reference_ticket",
                referencedColumnName = "ticket_id",
                insertable = false,
                updatable = false
            )
        ]
    )
    var entityReferenceTicket: Ticket? = null

    @Column(
        name = "entity_hint_ticket",
        updatable = false
    )
    var entityHintTicket: String? = null

    enum class Action {
        USER_MODIFY,
        BOARD_CREATE, BOARD_MODIFY, BOARD_DELETE,
        TICKET_CREATE, TICKET_MODIFY, TICKET_DELETE,
        INVITE_CREATE, INVITE_ACCEPT, MEMBERSHIP_GRANT, MEMBERSHIP_MODIFY, MEMBERSHIP_DELETE
    }

    override fun toString(): String {
        return "Audit $id"
    }

    companion object {

        fun of(entity: Any, actor: User, action: Action): Audit {
            val audit = Audit()
            when (entity) {
                is User -> {
                    audit.entityReferenceUserUUID = entity.uuid
                    audit.entityHintUser = entity.displayName
                }

                is Board -> {
                    audit.entityReferenceBoardId = entity.id
                    audit.entityHintBoard = entity.id
                }

                is Member -> {
                    audit.entityReferenceUserUUID = entity.userUUID
                    audit.entityHintUser = entity.user.displayName
                    audit.entityReferenceBoardId = entity.boardId
                    audit.entityHintBoard = entity.board.id
                }

                is Ticket -> {
                    audit.entityReferenceBoardId = entity.boardId
                    audit.entityHintBoard = entity.board.id
                    audit.entityReferenceTicketId = entity.id
                    audit.entityHintTicket = entity.id.toString()
                }

                else -> throw IllegalArgumentException("Bad entity provided")
            }
            audit.actorUUID = actor.uuid
            audit.entityHintActor = actor.displayName
            audit.action = action
            return audit
        }

    }

}