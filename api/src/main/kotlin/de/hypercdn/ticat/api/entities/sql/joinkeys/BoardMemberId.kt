package de.hypercdn.ticat.api.entities.sql.joinkeys

import java.io.Serializable
import java.util.*

class BoardMemberId() : Serializable {
    lateinit var userUUID: UUID
    lateinit var boardId: String
}