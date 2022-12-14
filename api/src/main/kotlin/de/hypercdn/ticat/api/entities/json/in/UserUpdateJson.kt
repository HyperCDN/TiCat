package de.hypercdn.ticat.api.entities.json.`in`

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.OffsetDateTime

class UserUpdateJson {

    @JsonProperty(value = "permissions", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var permissions: Permissions? = null

    class Permissions {

        @JsonProperty(value = "canLogin", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var canLogin: Boolean? = null

        @JsonProperty(value = "canBoardCreate", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var canBoardCreate: Boolean? = null

        @JsonProperty(value = "canBoardJoin", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var canBoardJoin: Boolean? = null

    }

    @JsonProperty(value = "versionTimestamp", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var versionBaseTimestamp: OffsetDateTime? = null

}