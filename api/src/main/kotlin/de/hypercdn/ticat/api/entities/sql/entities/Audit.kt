package de.hypercdn.ticat.api.entities.sql.entities

import de.hypercdn.ticat.api.entities.sql.shared.EntityHint
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
        nullable = false,
        updatable = false
    )
    lateinit var actorUUID: UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "actor", referencedColumnName = "user_uuid",
        insertable = false, updatable = false
    )
    lateinit var actor: User

    @Column(
        name = "action",
        nullable = false,
        updatable = false
    )
    @Enumerated(EnumType.STRING)
    lateinit var action: Action

    @Column(
        name = "entity_hint",
        nullable = false,
        updatable = false
    )
    lateinit var entityHint: String

    enum class Action {
        USER_MODIFY,
        BOARD_CREATE, BOARD_MODIFY, BOARD_DELETE,
        TICKET_CREATE, TICKET_MODIFY, TICKET_DELETE,
        INVITE_CREATE, INVITE_ACCEPT, MEMBERSHIP_GRANT, MEMBERSHIP_MODIFY, MEMBERSHIP_DELETE
    }

    override fun toString(): String {
        return "Audit $id ($action on $entityHint)"
    }

    companion object {

        fun forEntity(entity: EntityHint, actor: User, action: Action): Audit {
            val audit = Audit()
            audit.entityHint = entity.asHint()
            audit.actorUUID = actor.uuid
            audit.action = action
            return audit
        }

    }


}