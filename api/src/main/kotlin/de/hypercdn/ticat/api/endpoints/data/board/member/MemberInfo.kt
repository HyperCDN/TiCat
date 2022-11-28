package de.hypercdn.ticat.api.endpoints.data.board.member

import de.hypercdn.ticat.api.entities.helper.getBoardIfExists
import de.hypercdn.ticat.api.entities.helper.getLoggedInOrFallbackWhenAllowed
import de.hypercdn.ticat.api.entities.helper.hasEffectiveManagementPower
import de.hypercdn.ticat.api.entities.helper.isVisibleTo
import de.hypercdn.ticat.api.entities.json.out.BoardJson
import de.hypercdn.ticat.api.entities.json.out.MemberJson
import de.hypercdn.ticat.api.entities.json.out.UserJson
import de.hypercdn.ticat.api.entities.json.out.helper.PagedData
import de.hypercdn.ticat.api.entities.sql.joinkeys.MemberId
import de.hypercdn.ticat.api.entities.sql.repo.BoardRepository
import de.hypercdn.ticat.api.entities.sql.repo.MemberRepository
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
import java.util.*

@RestController
class MemberInfo @Autowired constructor(
    val userRepository: UserRepository,
    val boardRepository: BoardRepository,
    val memberRepository: MemberRepository
) {

    @GetMapping("/m/{boardId}")
    @Validated
    fun getMembers(
        @PathVariable("boardId") boardId: String,
        @RequestParam("page", required = false, defaultValue = "0") @Min(0) page: Int,
        @RequestParam("chunkSize", required = false, defaultValue = "100") @Range(min = 1, max = 100) chunkSize: Int
    ): PagedData<MemberJson> {
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(MemberId(selfUser.uuid, board.id)).orElse(null)
        if(!board.isVisibleTo(selfUser, selfMember)){
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        val pageRequest = PageRequest.of(page, chunkSize)
        val members = memberRepository.getMembersOf(board, pageRequest, !selfMember.hasEffectiveManagementPower())
        val pagedData = PagedData<MemberJson>(pageRequest)
        pagedData.entities = members.stream()
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
                    .includePermissions(skip = selfMember.userUUID != selfUser.uuid && !selfMember.hasEffectiveManagementPower())
                    .includeStatus(skip = !selfMember.hasEffectiveManagementPower())
            }
            .toList()
        return pagedData
    }

    @GetMapping("/m/{boardId}/{userUUID}")
    fun getMemberInfo(
        @PathVariable("boardId") boardId: String,
        @PathVariable("userUUID") userUUID: UUID
    ): MemberJson{
        val selfUser = userRepository.getLoggedInOrFallbackWhenAllowed(null)
        val board = boardRepository.getBoardIfExists(boardId)
        val selfMember = memberRepository.findById(MemberId(selfUser.uuid, board.id)).orElse(null)
        if(!board.isVisibleTo(selfUser, selfMember)){
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        val member = memberRepository.findById(MemberId(userUUID, board.id)).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
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
            .includePermissions(skip = selfMember.userUUID != selfUser.uuid && !selfMember.hasEffectiveManagementPower())
            .includeStatus(skip = !selfMember.hasEffectiveManagementPower())
    }

}