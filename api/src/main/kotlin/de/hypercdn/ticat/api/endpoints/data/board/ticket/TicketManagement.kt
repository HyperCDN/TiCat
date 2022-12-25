package de.hypercdn.ticat.api.endpoints.data.board.ticket

import de.hypercdn.ticat.api.entities.helper.*
import de.hypercdn.ticat.api.entities.json.`in`.TicketCreateJson
import de.hypercdn.ticat.api.entities.json.`in`.TicketUpdateJson
import de.hypercdn.ticat.api.entities.json.out.TicketJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.sql.entities.Audit
import de.hypercdn.ticat.api.entities.sql.entities.Member
import de.hypercdn.ticat.api.entities.sql.entities.Ticket
import de.hypercdn.ticat.api.entities.sql.repo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
class TicketManagement @Autowired constructor(
    val userRepository: UserRepository,
    val boardRepository: BoardRepository,
    val memberRepository: MemberRepository,
    val ticketRepository: TicketRepository,
    val auditLogRepository: AuditLogRepository
) {

    @PostMapping("/ticket/{boardId}")
    fun createNewTicket(
        @RequestBody requestBody: TicketCreateJson,
        @PathVariable("boardId") boardId: String
    ): TicketJson {
        val selfUser = userRepository.getLoggedInOrFallbackElse403(fallbackUUID = null)
        val board = boardRepository.findByIdElseThrow404(boardId)
        val selfMember = memberRepository.findByIdOrNull(Member.Key(selfUser.uuid, board.id))
        if (!board.isUsableBy(selfUser, selfMember))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val newTicket = Ticket().apply {
            this.boardId = board.id
            this.creatorUUID = selfUser.uuid
            this.title = requestBody.title!!
            requestBody.content?.let { this.content = it }
            requestBody.assigneeUUID?.let { this.assigneeUUID = it }
            requestBody.properties?.let {
                it.category?.let { v -> this.category = v }
                it.priority?.let { v -> this.priority = v }
                it.status?.let { v -> this.status = v }
            }
        }
        val savedTicket = ticketRepository.save(newTicket)
        auditLogRepository.save(Audit.of(savedTicket, selfUser, Audit.Action.TICKET_CREATE))
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
        val selfUser = userRepository.getLoggedInOrFallbackElse403(fallbackUUID = null)
        val board = boardRepository.findByIdElseThrow404(boardId)
        val selfMember = memberRepository.findByIdOrNull(Member.Key(selfUser.uuid, board.id))
        if (!board.isUsableBy(selfUser, selfMember))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val ticket = ticketRepository.findByIdElseThrow404(Ticket.Key(ticketId, board.id))
        ticket.also {

        }
        requestBody.versionBaseTimestamp?.let {
            if (ticket.modifiedAt.isAfter(it))
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Update based on outdated entity")
        }
        requestBody.title?.let { ticket.title = it }
        requestBody.content?.let { ticket.content = it }
        requestBody.assigneeUUID?.let { ticket.assigneeUUID = it }
        requestBody.properties?.let {
            it.category?.let { v -> ticket.category = v }
            it.priority?.let { v -> ticket.priority = v }
            it.status?.let { v -> ticket.status = v }
        }
        val updatedTicket = ticketRepository.save(ticket)
        auditLogRepository.save(Audit.of(updatedTicket, selfUser, Audit.Action.TICKET_MODIFY))
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
    ): TicketJson? {
        val selfUser = userRepository.getLoggedInOrFallbackElse403(fallbackUUID = null)
        val board = boardRepository.findByIdElseThrow404(boardId)
        val selfMember = memberRepository.findByIdOrNull(Member.Key(selfUser.uuid, board.id))
        if (!board.isUsableBy(selfUser, selfMember))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val ticket = ticketRepository.findByIdElseThrow404(Ticket.Key(ticketId, board.id))
        return if (actualDelete && selfMember?.hasEffectiveManagementPower() == true) {
            ticketRepository.delete(ticket)
            auditLogRepository.save(Audit.of(ticket, selfUser, Audit.Action.TICKET_DELETE))
            null
        } else {
            ticket.status = Ticket.Status.CLOSED
            val savedTicket = ticketRepository.save(ticket)
            auditLogRepository.save(Audit.of(savedTicket, selfUser, Audit.Action.TICKET_MODIFY))
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