package de.hypercdn.ticat.api.endpoints.data.board.ticket

import de.hypercdn.ticat.api.entities.helper.getBoardIfExists
import de.hypercdn.ticat.api.entities.helper.getLoggedInOrFallbackWhenAllowed
import de.hypercdn.ticat.api.entities.helper.hasEffectiveManagementPower
import de.hypercdn.ticat.api.entities.helper.hasEffectiveUsePower
import de.hypercdn.ticat.api.entities.json.`in`.TicketCreateJson
import de.hypercdn.ticat.api.entities.json.`in`.TicketUpdateJson
import de.hypercdn.ticat.api.entities.json.out.TicketJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.sql.Ticket
import de.hypercdn.ticat.api.entities.sql.joinkeys.MemberId
import de.hypercdn.ticat.api.entities.sql.joinkeys.TicketId
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.MemberRepository
import de.hypercdn.ticat.api.entities.sql.repo.TicketRepository
import de.hypercdn.ticat.api.entities.sql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
class TicketManagement @Autowired constructor(
    val userRepository: UserRepository,
    val boardRepository: BoardRepository,
    val memberRepository: MemberRepository,
    val ticketRepository: TicketRepository
) {

    @PostMapping("/ticket/{boardId}")
    fun createNewTicket(
        @RequestBody requestBody: TicketCreateJson,
        @PathVariable("boardId") boardId: String
    ): TicketJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID = null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(MemberId(selfUser.uuid, board.id)).orElse(null)
        if (!selfUser.isAdmin && selfMember?.hasEffectiveUsePower() != true)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)

        val newTicket = Ticket()
        newTicket.boardId = board.id
        newTicket.creatorUUID = selfUser.uuid
        newTicket.title = requestBody.title!!
        requestBody.content?.let { newTicket.content = it }
        requestBody.assigneeUUID?.let { newTicket.assigneeUUID = it }
        requestBody.properties?.let {
            it.category?.let { v -> newTicket.category = v }
            it.priority?.let { v -> newTicket.priority = v }
            it.status?.let { v -> newTicket.status = v }
        }

        val savedTicket = ticketRepository.save(newTicket)
        return TicketJson(savedTicket)
            .includeId()
            .includeTitle()
            .includeContent()
            .includeCreator {
                UserJson(savedTicket.creator)
                    .includeId()
            }
            .includeAssignee(skip = savedTicket.assignee == null) {
                UserJson(savedTicket.assignee)
                    .includeId()
                    .includeName()
            }
            .includeProperties()
    }

    @PatchMapping("/ticket/{boardId}/{ticketId}")
    fun updateTicket(
        @RequestBody requestBody: TicketUpdateJson,
        @PathVariable("boardId") boardId: String,
        @PathVariable("ticketId") ticketId: Int
    ): TicketJson {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID = null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(MemberId(selfUser.uuid, board.id)).orElse(null)
        if (!selfUser.isAdmin && selfMember?.hasEffectiveUsePower() != true)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val ticket = ticketRepository.findById(TicketId(ticketId, board.id)).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        requestBody.versionBaseTimestamp?.let { if (ticket.modifiedAt.isAfter(it)) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Update based on outdated entity") }

        requestBody.title?.let { ticket.title = it }
        requestBody.content?.let { ticket.content = it }
        requestBody.assigneeUUID?.let { ticket.assigneeUUID = it }
        requestBody.properties?.let {
            it.category?.let { v -> ticket.category = v }
            it.priority?.let { v -> ticket.priority = v }
            it.status?.let { v -> ticket.status = v }
        }

        val updatedTicket = ticketRepository.save(ticket)
        return TicketJson(updatedTicket)
            .includeId()
            .includeTitle()
            .includeContent()
            .includeCreator {
                UserJson(updatedTicket.creator)
                    .includeId()
                    .includeName()
            }
            .includeAssignee(skip = updatedTicket.assignee == null) {
                UserJson(updatedTicket.assignee)
                    .includeId()
                    .includeName()
            }
            .includeProperties()
    }

    @DeleteMapping("/ticket/{boardId}/{ticketId}")
    fun deleteTicket(
        @PathVariable("boardId") boardId: String,
        @PathVariable("ticketId") ticketId: Int,
        @RequestParam("actualDelete", required = false, defaultValue = "false") actualDelete: Boolean
    ):TicketJson? {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(fallbackUUID = null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(MemberId(selfUser.uuid, board.id)).orElse(null)
        if (!selfUser.isAdmin && selfMember?.hasEffectiveUsePower() != true)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val ticket = ticketRepository.findById(TicketId(ticketId, board.id)).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        return if(actualDelete && selfMember.hasEffectiveManagementPower()) {
            ticketRepository.delete(ticket)
            null
        } else {
            ticket.status = Ticket.Status.CLOSED
            val savedTicket = ticketRepository.save(ticket)
            TicketJson(savedTicket)
                .includeId()
                .includeTitle()
                .includeContent()
                .includeCreator {
                    UserJson(savedTicket.creator)
                        .includeId()
                        .includeName()
                }
                .includeAssignee(skip = savedTicket.assignee == null) {
                    UserJson(savedTicket.assignee)
                        .includeId()
                        .includeName()
                }
                .includeProperties()
        }
    }
}