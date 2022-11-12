package de.hypercdn.ticat.api.entities.json.out

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.hypercdn.ticat.api.entities.sql.BoardMember
import de.hypercdn.ticat.api.entities.sql.enums.BoardMembershipStatus
import java.util.function.Supplier

class BoardMemberJson(
    @JsonIgnore
    var boardMember: BoardMember? = null
) {

    @JsonProperty(value = "user", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var user: UserJson? = null

    @JsonProperty(value = "board", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var board: BoardJson? = null

    @JsonProperty(value = "status", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var status: BoardMembershipStatus? = null

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

    fun includeUser(skip: Boolean = false, userSupplier: Supplier<UserJson>? = null): BoardMemberJson {
        if (skip) return this
        if (userSupplier == null) {
            val tmp = UserJson()
            tmp.id = boardMember?.userUUID
            user = tmp
        } else {
            user = userSupplier.get()
        }
        return this
    }

    fun includeBoard(skip: Boolean = false, boardSupplier: Supplier<BoardJson>? = null): BoardMemberJson {
        if (skip) return this
        if (boardSupplier == null) {
            val tmp = BoardJson()
            tmp.id = boardMember?.boardId
            board = tmp
        } else {
            board = boardSupplier.get()
        }
        return this
    }

    fun includeStatus(skip: Boolean = false): BoardMemberJson {
        if (skip) return this
        status = boardMember?.status
        return this
    }

    fun includePermissions(skip: Boolean = false): BoardMemberJson {
        if (skip) return this
        val tmp = Permissions()
        tmp.canView = boardMember?.canView
        tmp.canUse = boardMember?.canUse
        tmp.canManage = boardMember?.canManage
        tmp.canAdministrate = boardMember?.canAdministrate
        permissions = tmp
        return this
    }

}