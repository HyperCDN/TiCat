package de.hypercdn.ticat.api.entities.json.out

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.hypercdn.ticat.api.entities.sql.entities.Ticket
import java.time.OffsetDateTime
import java.util.function.Supplier

class TicketJson(
    @JsonIgnore
    var ticket: Ticket? = null
) {

    @JsonProperty(value = "id", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var id: Int? = null

    @JsonProperty(value = "board", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var board: BoardJson? = null

    @JsonProperty(value = "creator", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var creator: UserJson? = null

    @JsonProperty(value = "title", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var title: String? = null

    @JsonProperty(value = "content", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var content: String? = null

    @JsonProperty(value = "assignee", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var assignee: UserJson? = null

    @JsonProperty(value = "properties", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var properties: Properties? = null

    class Properties {

        @JsonProperty(value = "category", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var category: Ticket.Category? = null

        @JsonProperty(value = "priority", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var priority: Ticket.Priority? = null

        @JsonProperty(value = "status", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var status: Ticket.Status? = null

    }

    @JsonProperty(value = "versionTimestamp", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var versionTimestamp: OffsetDateTime? = null

    fun includeId(skip: Boolean = false): TicketJson {
        if (skip) return this
        id = ticket?.id
        return this
    }

    fun includeBoard(skip: Boolean = false, boardSupplier: Supplier<BoardJson>? = null): TicketJson {
        if (skip) return this
        board = boardSupplier?.get() ?: BoardJson().apply {
            id = ticket?.boardId
        }
        return this
    }

    fun includeCreator(skip: Boolean = false, creatorSupplier: Supplier<UserJson>? = null): TicketJson {
        if (skip) return this
        creator = creatorSupplier?.get() ?: UserJson().apply {
            ticket?.creatorUUID
        }
        return this
    }

    fun includeTitle(skip: Boolean = false): TicketJson {
        if (skip) return this
        title = ticket?.title
        return this
    }

    fun includeContent(skip: Boolean = false): TicketJson {
        if (skip) return this
        content = ticket?.content
        return this
    }

    fun includeAssignee(skip: Boolean = false, assigneeSupplier: Supplier<UserJson>? = null): TicketJson {
        if (skip) return this
        assignee = assigneeSupplier?.get() ?: UserJson().apply {
            ticket?.creatorUUID
        }
        return this
    }

    fun includeProperties(skip: Boolean = false): TicketJson {
        if (skip) return this
        properties = Properties().apply {
            category = ticket?.category
            priority = ticket?.priority
            status = ticket?.status
        }
        return this
    }

    fun includeVersionTimestamp(skip: Boolean = false): TicketJson {
        if (skip) return this
        versionTimestamp = ticket?.modifiedAt
        return this
    }

}