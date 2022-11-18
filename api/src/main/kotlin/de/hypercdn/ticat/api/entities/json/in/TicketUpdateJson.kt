package de.hypercdn.ticat.api.entities.json.`in`

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.hypercdn.ticat.api.entities.sql.enums.TicketCategory
import de.hypercdn.ticat.api.entities.sql.enums.TicketPriority
import de.hypercdn.ticat.api.entities.sql.enums.TicketStatus
import java.util.*

class TicketUpdateJson {

    @JsonProperty(value = "title", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var title: String? = null

    @JsonProperty(value = "content", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var content: String? = null

    @JsonProperty(value = "assigneeUUID", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var assigneeUUID: UUID? = null

    @JsonProperty(value = "properties", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var properties: Properties? = null

    class Properties {

        @JsonProperty(value = "category", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var category: TicketCategory? = null

        @JsonProperty(value = "priority", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var priority: TicketPriority? = null

        @JsonProperty(value = "status", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var status: TicketStatus? = null

    }

}