package de.hypercdn.ticat.api.entities.json.out

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.hypercdn.ticat.api.entities.sql.entities.Member
import java.time.OffsetDateTime
import java.util.function.Supplier

class MemberJson(
    @JsonIgnore
    var member: Member? = null
) {

    @JsonProperty(value = "user", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var user: UserJson? = null

    @JsonProperty(value = "board", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var board: BoardJson? = null

    @JsonProperty(value = "status", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var status: Member.MembershipStatus? = null

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

        companion object {

            val ALL = Permissions().apply {
                canView = true
                canUse = true
                canManage = true
                canAdministrate = true
            }

            val MIN = Permissions().apply {
                canView = true
                canUse = false
                canManage = false
                canAdministrate = false
            }

        }

    }

    @JsonProperty(value = "versionTimestamp", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var versionTimestamp: OffsetDateTime? = null

    fun includeUser(skip: Boolean = false, userSupplier: Supplier<UserJson>? = null): MemberJson {
        if (skip) return this
        user = userSupplier?.get() ?: UserJson().apply {
            id = member?.userUUID
        }
        return this
    }

    fun includeBoard(skip: Boolean = false, boardSupplier: Supplier<BoardJson>? = null): MemberJson {
        if (skip) return this
        board = boardSupplier?.get() ?: BoardJson().apply {
            id = member?.boardId
        }
        return this
    }

    fun includeStatus(skip: Boolean = false): MemberJson {
        if (skip) return this
        status = member?.status
        return this
    }

    fun includePermissions(skip: Boolean = false): MemberJson {
        if (skip) return this
        permissions = Permissions().apply {
            canView = member?.canView
            canUse = member?.canUse
            canManage = member?.canManage
            canAdministrate = member?.canAdministrate
        }
        return this
    }

    fun includeVersionTimestamp(skip: Boolean = false): MemberJson {
        if (skip) return this
        versionTimestamp = member?.modifiedAt
        return this
    }

}