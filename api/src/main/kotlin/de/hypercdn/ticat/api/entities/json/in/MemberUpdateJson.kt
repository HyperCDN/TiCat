package de.hypercdn.ticat.api.entities.json.`in`

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.OffsetDateTime

class MemberUpdateJson {

    @JsonProperty(value = "block", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var block: Boolean? = null

    @JsonProperty(value = "permissions", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var permissions: Permissions? = null

    class Permissions {

        @JsonProperty(value = "canView", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var canView: Boolean? = null

        @JsonProperty(value = "canUse", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var canUse: Boolean? = null

        @JsonProperty(value = "canModify", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var canManage: Boolean? = null

        @JsonProperty(value = "canAdministrate", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var canAdministrate: Boolean? = null

    }

    @JsonProperty(value = "versionTimestamp", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var versionBaseTimestamp: OffsetDateTime? = null

}