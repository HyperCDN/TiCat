package de.hypercdn.ticat.api.entities.sql.joinkeys

import lombok.NoArgsConstructor
import java.io.Serializable

@NoArgsConstructor
class TicketId(
    var id: Int,
    var boardId: String
) : Serializable {}