package de.hypercdn.ticat.api.endpoints.data.board.ticket

import de.hypercdn.ticat.api.entities.helper.getBoardIfExists
import de.hypercdn.ticat.api.entities.helper.getLoggedInOrFallbackWhenAllowed
import de.hypercdn.ticat.api.entities.helper.getTicketIfExists
import de.hypercdn.ticat.api.entities.helper.isVisibleTo
import de.hypercdn.ticat.api.entities.json.out.BoardJson
import de.hypercdn.ticat.api.entities.json.out.TicketJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.json.out.helper.PagedData
import de.hypercdn.ticat.api.entities.sql.Member
import de.hypercdn.ticat.api.entities.sql.Ticket
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.MemberRepository
import de.hypercdn.ticat.api.entities.sql.repo.TicketRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import jakarta.validation.constraints.Min
import org.hibernate.validator.constraints.Range
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class TicketInfo @Autowired constructor(
    val userRepository: UserRepository,
    val boardRepository: BoardRepository,
    val memberRepository: MemberRepository,
    val ticketRepository: TicketRepository
) {

    @GetMapping("/tickets/{boardId}")
    @Validated
    fun getTickets(
        @PathVariable("boardId") boardId: String,
        @RequestParam("page", required = false, defaultValue = "0") @Min(0) page: Int,
        @RequestParam("chunkSize", required = false, defaultValue = "100") @Range(min = 1, max = 100) chunkSize: Int
    ): PagedData<TicketJson> {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(Member.Key(selfUser.uuid, board.id)).orElse(null)
        if (!board.isVisibleTo(selfUser, selfMember))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)

        val pageRequest = PageRequest.of(page, chunkSize)
        val tickets = ticketRepository.getTicketsOf(board, pageRequest)
        val pagedData = PagedData<TicketJson>(pageRequest)
        pagedData.entities = tickets.stream()
            .map {
                TicketJson(it)
                    .includeId()
                    .includeTitle()
                    .includeBoard {
                        BoardJson(it.board)
                            .includeId()
                    }
                    .includeCreator {
                        UserJson(it.creator)
                            .includeId()
                            .includeName()
                    }
                    .includeProperties()
            }
            .toList()
        return pagedData
    }

    @GetMapping("/ticket/{boardId}/{ticketId}")
    fun getTicketInfo(
        @PathVariable("boardId") boardId: String,
        @PathVariable("ticketId") ticketId: Int
    ): TicketJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(Member.Key(selfUser.uuid, board.id)).orElse(null)
        if (!board.isVisibleTo(selfUser, selfMember))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)

        val ticket = ticketRepository.getTicketIfExists(Ticket.Key(ticketId, board.id))
        return TicketJson(ticket)
            .includeId()
            .includeTitle()
            .includeBoard {
                BoardJson(ticket.board)
                    .includeId()
            }
            .includeCreator {
                UserJson(ticket.creator)
                    .includeId()
                    .includeName()
            }
            .includeAssignee(skip = ticket.assigneeUUID == null) {
                UserJson(ticket.assignee)
                    .includeId()
                    .includeName()
            }
            .includeContent()
            .includeProperties()
    }

}