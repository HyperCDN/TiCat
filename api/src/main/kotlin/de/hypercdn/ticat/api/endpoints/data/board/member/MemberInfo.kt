package de.hypercdn.ticat.api.endpoints.data.board.member

import de.hypercdn.ticat.api.entities.helper.findByIdElseThrow404
import de.hypercdn.ticat.api.entities.helper.getLoggedInOrFallbackElse403
import de.hypercdn.ticat.api.entities.helper.hasEffectiveManagementPower
import de.hypercdn.ticat.api.entities.helper.isVisibleTo
import de.hypercdn.ticat.api.entities.json.out.BoardJson
import de.hypercdn.ticat.api.entities.json.out.MemberJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.json.out.helper.PagedData
import de.hypercdn.ticat.api.entities.sql.entities.Member
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.MemberRepository
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
import java.util.*

@RestController
class MemberInfo @Autowired constructor(
    val userRepository: UserRepository,
    val boardRepository: BoardRepository,
    val memberRepository: MemberRepository
) {

    @GetMapping("/members/{boardId}")
    @Validated
    fun getMembers(
        @PathVariable("boardId") boardId: String,
        @RequestParam("page", required = false, defaultValue = "0") @Min(0) page: Int,
        @RequestParam("chunkSize", required = false, defaultValue = "100") @Range(min = 1, max = 100) chunkSize: Int
    ): PagedData<MemberJson> {
        val selfUser = userRepository.getLoggedInOrFallbackElse403(fallbackUUID = null)
        val board = boardRepository.findByIdElseThrow404(boardId)
        val selfMember = memberRepository.findByIdOrNull(Member.Key(selfUser.uuid, board.id))
        if (!board.isVisibleTo(selfUser, selfMember))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val pageRequest = PageRequest.of(page, chunkSize)
        val members = memberRepository.getMembersOf(board, pageRequest, selfMember?.hasEffectiveManagementPower() != true)
        return PagedData<MemberJson>(pageRequest).apply {
            entities = members.stream()
                .map {
                    MemberJson(it)
                        .includeUser {
                            UserJson(it.user)
                                .includeId()
                                .includeName()
                        }
                        .includeBoard {
                            BoardJson(it.board)
                                .includeId()
                        }
                        .includePermissions(skip = selfMember?.userUUID != selfUser.uuid && selfMember?.hasEffectiveManagementPower() != true)
                        .includeStatus(skip = selfMember?.hasEffectiveManagementPower() != true)
                }
                .toList()
        }
    }

    @GetMapping("/member/{boardId}/{userUUID}")
    fun getMemberInfo(
        @PathVariable("boardId") boardId: String,
        @PathVariable("userUUID") userUUID: UUID
    ): MemberJson {
        val selfUser = userRepository.getLoggedInOrFallbackElse403(null)
        val board = boardRepository.findByIdElseThrow404(boardId)
        val selfMember = memberRepository.findByIdOrNull(Member.Key(selfUser.uuid, board.id))
        if (!board.isVisibleTo(selfUser, selfMember))
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val member = memberRepository.findByIdElseThrow404(Member.Key(userUUID, board.id))
        return MemberJson(member)
            .includeUser {
                UserJson(member.user)
                    .includeId()
                    .includeName()
            }
            .includeBoard {
                BoardJson(member.board)
                    .includeId()
            }
            .includePermissions(skip = selfMember?.userUUID != selfUser.uuid && selfMember?.hasEffectiveManagementPower() != true)
            .includeStatus(skip = selfMember?.hasEffectiveManagementPower() != true)
    }

}