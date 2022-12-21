package de.hypercdn.ticat.api.entities.json.`in`

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.hypercdn.ticat.api.entities.sql.entities.Board

class BoardCreateJson {

    @JsonProperty(value = "id", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var id: String? = null

    @JsonProperty(value = "title", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var title: String? = null

    @JsonProperty(value = "description", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var description: String? = null

    @JsonProperty(value = "settings", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var settings: Settings? = null

    class Settings {

        @JsonProperty(value = "visibility", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var visibility: Board.Visibility? = null

        @JsonProperty(value = "accessMode", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var accessMode: Board.AccessMode? = null

    }

}