package de.hypercdn.ticat.api.entities.sql.joinkeys

import lombok.NoArgsConstructor
import java.io.Serializable
import java.util.*

@NoArgsConstructor
class MemberId(
    var userUUID: UUID,
    var boardId: String
) : Serializable {}