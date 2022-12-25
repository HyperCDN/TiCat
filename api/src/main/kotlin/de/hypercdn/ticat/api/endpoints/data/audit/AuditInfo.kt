package de.hypercdn.ticat.api.endpoints.data.audit

import de.hypercdn.ticat.api.entities.helper.findByIdElseThrow404
import de.hypercdn.ticat.api.entities.helper.getLoggedInOrFallbackElse403
import de.hypercdn.ticat.api.entities.helper.isManageableBy
import de.hypercdn.ticat.api.entities.json.out.AuditJson
import de.hypercdn.ticat.api.entities.json.out.helper.PagedData
import de.hypercdn.ticat.api.entities.sql.entities.Member
import de.hypercdn.ticat.api.entities.sql.repo.*
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
class AuditInfo @Autowired constructor(
    val userRepository: UserRepository,
    val boardRepository: BoardRepository,
    val memberRepository: MemberRepository,
    val ticketRepository: TicketRepository,
    val auditLogRepository: AuditLogRepository
) {

    @GetMapping("/audit")
    @Validated
    fun getGlobalScopedAuditLog(
        @RequestParam("page", required = false, defaultValue = "0") @Min(0) page: Int,
        @RequestParam("chunkSize", required = false, defaultValue = "100") @Range(min = 1, max = 100) chunkSize: Int
    ): PagedData<AuditJson> {
        val selfUser = userRepository.getLoggedInOrFallbackElse403(fallbackUUID = null)
        if (!selfUser.isAdmin)
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val pageRequest = PageRequest.of(page, chunkSize)
        val auditLogs = auditLogRepository.getAllPaged(pageRequest)
        return PagedData<AuditJson>(pageRequest).apply {
            entities = auditLogs.stream()
                .map {
                    AuditJson(it)
                        .includeBasic()
                        .includeReferenceEntities()
                        .includeEntityHints()
                }
                .toList()
        }
    }

    @GetMapping("/audit/{boardId}")
    @Validated
    fun getBoardScopedAuditLog(
        @PathVariable("boardId") boardId: String,
        @RequestParam("page", required = false, defaultValue = "0") @Min(0) page: Int,
        @RequestParam("chunkSize", required = false, defaultValue = "100") @Range(min = 1, max = 100) chunkSize: Int,
    ): PagedData<AuditJson> {
        val selfUser = userRepository.getLoggedInOrFallbackElse403(fallbackUUID = null)
        val board = boardRepository.findByIdElseThrow404(boardId)
        val selfMember = memberRepository.findByIdOrNull(Member.Key(selfUser.uuid, board.id))
        if (!board.isManageableBy(selfUser, selfMember))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val pageRequest = PageRequest.of(page, chunkSize)
        val auditLogs = auditLogRepository.getAllFor(board, pageRequest)
        return PagedData<AuditJson>(pageRequest).apply {
            entities = auditLogs.stream()
                .map {
                    AuditJson(it)
                        .includeBasic()
                        .includeReferenceEntities()
                        .includeEntityHints()
                }
                .toList()
        }
    }

}