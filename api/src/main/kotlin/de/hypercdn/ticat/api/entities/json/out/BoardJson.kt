package de.hypercdn.ticat.api.entities.json.out

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.hypercdn.ticat.api.entities.sql.entities.Board
import java.time.OffsetDateTime
import java.util.function.Supplier

class BoardJson(
    @JsonIgnore
    val board: Board? = null
) {

    @JsonProperty(value = "id", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var id: String? = null

    @JsonProperty(value = "title", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var title: String? = null

    @JsonProperty(value = "description", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var description: String? = null

    @JsonProperty(value = "owner", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var owner: UserJson? = null

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

    @JsonProperty(value = "versionTimestamp", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var versionTimestamp: OffsetDateTime? = null

    fun includeId(skip: Boolean = false): BoardJson {
        if (skip) return this
        id = board?.id
        return this
    }

    fun includeTitle(skip: Boolean = false): BoardJson {
        if (skip) return this
        title = board?.title
        return this
    }

    fun includeDescription(skip: Boolean = false): BoardJson {
        if (skip) return this
        description = board?.description
        return this
    }

    fun includeOwner(skip: Boolean = false, userSupplier: Supplier<UserJson>? = null): BoardJson {
        if (skip) return this
        owner = userSupplier?.get() ?: UserJson().apply {
            id = board?.ownerUUID
        }
        return this
    }

    fun includeSettings(skip: Boolean = false): BoardJson {
        if (skip) return this
        settings = Settings().apply {
            visibility = board?.visibility
            accessMode = board?.accessMode
        }
        return this
    }

    fun includeVersionTimestamp(skip: Boolean = false): BoardJson {
        if (skip) return this
        versionTimestamp = board?.modifiedAt
        return this
    }

}