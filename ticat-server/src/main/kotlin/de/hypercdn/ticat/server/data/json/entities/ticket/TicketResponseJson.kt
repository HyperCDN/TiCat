package de.hypercdn.ticat.server.data.json.entities.ticket

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.hypercdn.ticat.server.data.json.entities.board.BoardResponseJson
import de.hypercdn.ticat.server.data.json.entities.board.stage.BoardStageResponseJson
import de.hypercdn.ticat.server.data.json.entities.page.PageResponseJson
import de.hypercdn.ticat.server.data.json.entities.ticket.history.TicketHistoryResponseJsonBuilder
import de.hypercdn.ticat.server.data.json.entities.user.UserResponseJson
import de.hypercdn.ticat.server.data.json.entities.workspace.history.WorkspaceHistoryResponseJson
import de.hypercdn.ticat.server.data.sql.entities.board.stage.BoardStage
import de.hypercdn.ticat.server.data.sql.entities.ticket.Ticket
import java.time.OffsetDateTime
import java.util.UUID

class TicketResponseJson(
    @JsonIgnore
    val ticket: Ticket? = null
) {

    companion object

    @JsonProperty(value = "uuid", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var uuid: UUID? = null

    @JsonProperty(value = "board", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var board: BoardResponseJson? = null

    @JsonProperty(value = "series_id", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var seriesId: Int? = null

    @JsonProperty(value = "version_timestamp", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var versionTimestamp: OffsetDateTime? = null

    @JsonProperty(value = "creator", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var creator: UserResponseJson? = null

    @JsonProperty(value = "editor", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var editor: UserResponseJson? = null

    @JsonProperty(value = "settings", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var settings: Settings? = null

    class Settings {

        @JsonProperty(value = "setting_status", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var status: Ticket.Settings.Status? = null

        @JsonProperty(value = "setting_board_stage_uuid", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var boardStageUUID: UUID? = null

        @JsonProperty(value = "setting_assignee_uuid", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var assigneeUUID: UUID? = null

    }

    @JsonProperty(value = "title", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var title: String? = null

    @JsonProperty(value = "content", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var content: String? = null

    @JsonProperty(value = "entity_history", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var history: List<TicketHistoryResponseJsonBuilder>? = null

}