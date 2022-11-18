package de.hypercdn.ticat.api.endpoints.data.board.ticket

import de.hypercdn.ticat.api.entities.helper.getBoardIfExists
import de.hypercdn.ticat.api.entities.helper.getLoggedInOrFallbackWhenAllowed
import de.hypercdn.ticat.api.entities.helper.getTicketIfExists
import de.hypercdn.ticat.api.entities.helper.isVisibleTo
import de.hypercdn.ticat.api.entities.json.out.BoardJson
import de.hypercdn.ticat.api.entities.json.out.TicketJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.json.out.helper.PagedData
import de.hypercdn.ticat.api.entities.sql.joinkeys.MemberId
import de.hypercdn.ticat.api.entities.sql.joinkeys.TicketId
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.MemberRepository
import de.hypercdn.ticat.api.entities.sql.repo.TicketRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
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

    @GetMapping("/t/{boardId}")
    fun getTickets(
        @PathVariable("boardId") boardId: String,
        @RequestParam("page", required = false, defaultValue = "0") page: Int,
        @RequestParam("chunkSize", required = false, defaultValue = "50") chunkSize: Int
    ): PagedData<TicketJson> {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(MemberId(selfUser.uuid, board.id)).orElse(null)
        if (!board.isVisibleTo(selfUser, selfMember)){
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        val pageRequest = PageRequest.of(page.coerceAtLeast(0), chunkSize.coerceIn(1, 50))
        val tickets = ticketRepository.getTicketsOf(board, pageRequest)
        val pagedData = PagedData<TicketJson>(pageRequest)
        pagedData.entities = tickets.stream()
            .map {
                TicketJson(it)
                    .includeId()
                    .includeTitle()
                    .includeCreator() {
                        UserJson(it.creator)
                            .includeId()
                            .includeName()
                    }
                    .includeProperties()
            }
            .toList()
        return pagedData
    }

    @GetMapping("/t/{boardId}/{ticketId}")
    fun getTicketInfo(
        @PathVariable("boardId") boardId: String,
        @PathVariable("ticketId") ticketId: Int
    ): TicketJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed()
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(MemberId(selfUser.uuid, board.id)).orElse(null)
        if (!board.isVisibleTo(selfUser, selfMember)){
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        val ticket = ticketRepository.getTicketIfExists(TicketId(ticketId, board.id))
        return TicketJson(ticket)
            .includeId()
            .includeTitle()
            .includeBoard() {
                BoardJson(ticket.board)
                    .includeId()
            }
            .includeCreator() {
                UserJson(ticket.creator)
                    .includeId()
            }
            .includeAssignee(skip = ticket.assigneeUUID == null) {
                UserJson(ticket.assignee)
                    .includeId()
            }
            .includeContent()
            .includeProperties()
    }

}