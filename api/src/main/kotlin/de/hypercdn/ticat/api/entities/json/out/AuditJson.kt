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

    @JsonProperty(value = "actor", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var actor: UserJson? = null

    @JsonProperty(value = "action", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var action: Audit.Action? = null

    @JsonProperty(value = "entityHint", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var entityHint: String? = null

    fun includeAll(skip: Boolean = false): AuditJson {
        if (skip) return this
        id = audit?.id
        actionTime = audit?.actionTime
        actor = UserJson(audit?.actor).includeId().includeName()
        action = audit?.action
        entityHint = audit?.entityHint
        return this
    }

}