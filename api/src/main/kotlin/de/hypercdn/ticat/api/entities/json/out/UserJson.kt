package de.hypercdn.ticat.api.entities.json.out

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.hypercdn.ticat.api.entities.sql.User
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

class UserJson(
    @JsonIgnore
    val user: User? = null
) {

    @JsonProperty(value = "id", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var id: UUID? = null

    @JsonProperty(value = "name", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var name: Name? = null

    class Name {

        @JsonProperty(value = "firstName", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var firstName: String? = null

        @JsonProperty(value = "lastName", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var lastName: String? = null

        @JsonProperty(value = "displayName", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var displayName: String? = null

    }

    @JsonProperty(value = "email", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var email: String? = null

    @JsonProperty(value = "permissions", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var permissions: Permissions? = null

    class Permissions {

        @JsonProperty(value = "isSystem", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var isSystem: Boolean? = null

        @JsonProperty(value = "isAdmin", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var isAdmin: Boolean? = null

        @JsonProperty(value = "canLogin", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var canLogin: Boolean? = null

        @JsonProperty(value = "canBoardCreate", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var canBoardCreate: Boolean? = null

        @JsonProperty(value = "canBoardJoin", required = false)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var canBoardJoin: Boolean? = null

    }

    @JsonProperty(value = "versionTimestamp", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var versionTimestamp: OffsetDateTime? = null

    fun includeId(skip: Boolean = false): UserJson {
        if (skip) return this
        id = user?.uuid
        return this
    }

    fun includeName(skip: Boolean = false): UserJson {
        if (skip) return this
        val tmp = Name()
        tmp.displayName = user?.displayName
        name = tmp
        return this
    }

    fun includeFullName(skip: Boolean = false): UserJson {
        if (skip) return this
        val tmp = Name()
        tmp.firstName = user?.firstName
        tmp.lastName = user?.lastName
        tmp.displayName = user?.displayName
        name = tmp
        return this
    }

    fun includeEmail(skip: Boolean = false): UserJson {
        if (skip) return this
        email = user?.email
        return this
    }

    fun includePermissions(skip: Boolean = false): UserJson {
        if (skip) return this
        val tmp = Permissions()
        tmp.isSystem = user?.isSystem
        tmp.isAdmin = user?.isAdmin
        tmp.canLogin = user?.canLogin
        tmp.canBoardCreate = user?.canBoardCreate
        tmp.canBoardJoin = user?.canBoardJoin
        permissions = tmp
        return this
    }

    fun includeVersionTimestamp(skip: Boolean = false): UserJson {
        if (skip) return this
        versionTimestamp = user?.updatedAt
        return this
    }

    fun includeAll(): UserJson {
        return includeId()
            .includeFullName()
            .includeEmail()
            .includePermissions()
            .includeVersionTimestamp()
    }

}