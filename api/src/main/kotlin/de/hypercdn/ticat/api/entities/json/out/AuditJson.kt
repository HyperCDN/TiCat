package de.hypercdn.ticat.api.entities.json.out

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.hypercdn.ticat.api.entities.sql.entities.Audit
import java.time.OffsetDateTime
import java.util.*

class AuditJson(
    @JsonIgnore
    val audit: Audit? = null
) {

    @JsonProperty(value = "id", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var id: Long? = null

    @JsonProperty(value = "actionTime", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var actionTime: OffsetDateTime? = null

    @JsonProperty(value = "action", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var action: Audit.Action? = null

    @JsonProperty(value = "referenceEntities", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var referenceEntities: ReferenceEntities? = null

    class ReferenceEntities {

        @JsonProperty(value = "actor", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var actor: UserJson? = null

        @JsonProperty(value = "user", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var user: UserJson? = null

        @JsonProperty(value = "board", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var board: BoardJson? = null

        @JsonProperty(value = "ticket", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var ticket: TicketJson? = null

    }

    @JsonProperty(value = "entityHints", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var entityHints: EntityHints? = null

    class EntityHints {

        @JsonProperty(value = "actor", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var actor: String? = null

        @JsonProperty(value = "user", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var user: String? = null

        @JsonProperty(value = "board", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var board: String? = null

        @JsonProperty(value = "ticket", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var ticket: String? = null

    }

    fun includeBasic(skip: Boolean = false): AuditJson {
        if (skip) return this
        id = audit?.id
        actionTime = audit?.actionTime
        action = audit?.action
        entityHints = (entityHints ?: EntityHints()).apply {
            actor = audit?.entityHintActor
        }
        referenceEntities = (referenceEntities ?: ReferenceEntities()).apply {
            actor = UserJson(audit?.actor)
                .includeId()
                .includeName()
        }
        return this
    }

    fun includeEntityHints(skip: Boolean = false): AuditJson {
        if (skip) return this
        entityHints = (entityHints ?: EntityHints()).apply {
            actor = audit?.entityHintActor
            user = audit?.entityHintUser
            board = audit?.entityHintBoard
            ticket = audit?.entityHintTicket
        }
        return this
    }

    fun includeReferenceEntities(skip: Boolean = false): AuditJson {
        if (skip) return this
        referenceEntities = (referenceEntities ?: ReferenceEntities()).apply {
            audit?.actor?.let {
                actor = UserJson(audit.actor)
                    .includeId()
                    .includeName()
            }
            audit?.entityReferenceUser?.let {
                user = UserJson(audit.entityReferenceUser)
                    .includeId()
                    .includeName()
            }
            audit?.entityReferenceBoard?.let {
                board = BoardJson(audit.entityReferenceBoard)
                    .includeId()
                    .includeTitle()
            }
            audit?.entityReferenceTicket?.let {
                ticket = TicketJson(audit.entityReferenceTicket)
                    .includeId()
                    .includeTitle()
            }
        }
        return this
    }

}