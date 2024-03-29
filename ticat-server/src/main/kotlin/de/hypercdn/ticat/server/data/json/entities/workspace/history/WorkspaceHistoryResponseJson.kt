package de.hypercdn.ticat.server.data.json.entities.workspace.history

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.hypercdn.ticat.server.data.json.entities.user.UserResponseJson
import de.hypercdn.ticat.server.data.json.entities.workspace.WorkspaceResponseJson
import de.hypercdn.ticat.server.data.sql.entities.workspace.Workspace
import java.time.OffsetDateTime
import java.util.UUID

class WorkspaceHistoryResponseJson {

    companion object

    @JsonProperty(value = "uuid", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var uuid: UUID? = null

    @JsonProperty(value = "workspace", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var workspace: WorkspaceResponseJson? = null

    @JsonProperty(value = "version", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var version: Int? = null

    @JsonProperty(value = "version_timestamp", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var versionTimestamp: OffsetDateTime? = null

    @JsonProperty(value = "editor", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var editor: UserResponseJson? = null

    @JsonProperty(value = "old_title", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var oldTitle: String? = null

    @JsonProperty(value = "old_description", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var oldDescription: String? = null

    @JsonProperty(value = "old_visibility", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var oldVisibility: Workspace.Settings.Visibility? = null

    @JsonProperty(value = "old_access_mode", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var oldAccessMode: Workspace.Settings.AccessMode? = null

    @JsonProperty(value = "old_status", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var oldStatus: Workspace.Settings.Status? = null

}