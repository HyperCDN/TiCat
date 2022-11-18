package de.hypercdn.ticat.api.endpoints.data.board.ticket

import de.hypercdn.ticat.api.entities.json.`in`.TicketCreateJson
import de.hypercdn.ticat.api.entities.json.`in`.TicketUpdateJson
import de.hypercdn.ticat.api.entities.json.out.TicketJson
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.MemberRepository
import de.hypercdn.ticat.api.entities.sql.repo.TicketRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class TicketManagement @Autowired constructor(
    val userRepository: UserRepository,
    val boardRepository: BoardRepository,
    val memberRepository: MemberRepository,
    val ticketRepository: TicketRepository
) {

    @PostMapping("/t/{boardId}")
    fun createNewTicket(
        @RequestBody requestBody: TicketCreateJson,
        @PathVariable("boardId") boardId: String
    ): TicketJson {
        throw NotImplementedError()
    }

    @PatchMapping("/t/[")
    fun updateTicket(
        @RequestBody requestBody: TicketUpdateJson,
        @PathVariable("boardId") boardId: String,
        @PathVariable("ticketId") ticketId: Int
    ): TicketJson {
        throw NotImplementedError()
    }

    @DeleteMapping("/t/{boardId}/{ticketId}")
    fun deleteTicket(
        @PathVariable("boardId") boardId: String,
        @PathVariable("ticketId") ticketId: Int
    ) {
        throw NotImplementedError()
    }

}