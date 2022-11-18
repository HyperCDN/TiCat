package de.hypercdn.ticat.api.entities.json.out

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.hypercdn.ticat.api.entities.sql.Board
import de.hypercdn.ticat.api.entities.sql.enums.BoardAccessMode
import de.hypercdn.ticat.api.entities.sql.enums.BoardVisibility
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
        var visibility: BoardVisibility? = null

        @JsonProperty(value = "accessMode", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var accessMode: BoardAccessMode? = null

    }

    @JsonProperty(value = "members", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var members: List<MemberJson>? = null

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
        if (userSupplier == null){
            val tmp = UserJson()
            tmp.id = board?.ownerUUID
            owner = tmp
        } else {
            owner = userSupplier.get()
        }
        return this
    }

    fun includeSettings(skip: Boolean = false): BoardJson {
        if (skip) return this
        val tmp = Settings()
        tmp.visibility = board?.visibility
        tmp.accessMode = board?.accessMode
        return this
    }

    fun includeMembers(skip: Boolean = false, memberSupplier: Supplier<List<MemberJson>>? = null): BoardJson {
        if (skip) return this
        members = memberSupplier?.get()
        return this
    }

}