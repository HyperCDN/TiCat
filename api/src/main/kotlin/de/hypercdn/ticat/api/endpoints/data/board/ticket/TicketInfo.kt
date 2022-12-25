package de.hypercdn.ticat.api.endpoints.data.board.ticket

import de.hypercdn.ticat.api.entities.helper.getBoardIfExistsElse404
import de.hypercdn.ticat.api.entities.helper.getLoggedInOrFallbackElse403
import de.hypercdn.ticat.api.entities.helper.getTicketIfExistsElse404
import de.hypercdn.ticat.api.entities.helper.isVisibleTo
import de.hypercdn.ticat.api.entities.json.out.BoardJson
import de.hypercdn.ticat.api.entities.json.out.TicketJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.json.out.helper.PagedData
import de.hypercdn.ticat.api.entities.sql.entities.Member
import de.hypercdn.ticat.api.entities.sql.entities.Ticket
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.MemberRepository
import de.hypercdn.ticat.api.entities.sql.repo.TicketRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import jakarta.validation.constraints.Min
import org.hibernate.validator.constraints.Range
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
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
        val selfUser = userRepository.getLoggedInOrFallbackElse403()
        val board = boardRepository.getBoardIfExistsElse404(boardId)
        val selfMember = memberRepository.findByIdOrNull(Member.Key(selfUser.uuid, board.id))
        if (!board.isVisibleTo(selfUser, selfMember))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val pageRequest = PageRequest.of(page, chunkSize)
        val tickets = ticketRepository.getTicketsOf(board, pageRequest)
        return PagedData<TicketJson>(pageRequest).apply {
            entities = tickets.stream()
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
        }
    }

    @GetMapping("/ticket/{boardId}/{ticketId}")
    fun getTicketInfo(
        @PathVariable("boardId") boardId: String,
        @PathVariable("ticketId") ticketId: Int
    ): TicketJson {
        val selfUser = userRepository.getLoggedInOrFallbackElse403()
        val board = boardRepository.getBoardIfExistsElse404(boardId)
        val selfMember = memberRepository.findByIdOrNull(Member.Key(selfUser.uuid, board.id))
        if (!board.isVisibleTo(selfUser, selfMember))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val ticket = ticketRepository.getTicketIfExistsElse404(Ticket.Key(ticketId, board.id))
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