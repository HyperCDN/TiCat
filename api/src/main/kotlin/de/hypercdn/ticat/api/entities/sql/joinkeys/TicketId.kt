package de.hypercdn.ticat.api.entities.sql.joinkeys

import java.io.Serializable
import kotlin.properties.Delegates

class TicketId : Serializable {
    var ticketId by Delegates.notNull<Int>()
    lateinit var boardId: String
}