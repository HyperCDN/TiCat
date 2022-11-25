package de.hypercdn.ticat.api.entities.json.out

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.hypercdn.ticat.api.entities.sql.Ticket
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

    fun includeId(skip: Boolean = false): TicketJson {
        if (skip) return this
        id = ticket?.id
        return this
    }

    fun includeBoard(skip: Boolean = false, boardSupplier: Supplier<BoardJson>? = null): TicketJson {
        if (skip) return this
        if (boardSupplier == null) {
            val tmp = BoardJson()
            tmp.id = ticket?.boardId
            board = tmp
        } else {
            board = boardSupplier.get()
        }
        return this
    }

    fun includeCreator(skip: Boolean = false, creatorSupplier: Supplier<UserJson>? = null): TicketJson {
        if (skip) return this
        if (creatorSupplier == null) {
            val tmp = UserJson()
            tmp.id = ticket?.creatorUUID
            assignee = tmp
        } else {
            assignee = creatorSupplier.get()
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
        if (assigneeSupplier == null) {
            val tmp = UserJson()
            tmp.id = ticket?.assigneeUUID
            assignee = tmp
        } else {
            assignee = assigneeSupplier.get()
        }
        return this
    }

    fun includeProperties(skip: Boolean = false): TicketJson {
        if (skip) return this
        val tmp = Properties()
        tmp.category = ticket?.category
        tmp.priority = ticket?.priority
        tmp.status = ticket?.status
        properties = tmp
        return this
    }

}