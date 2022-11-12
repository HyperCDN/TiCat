package de.hypercdn.ticat.api.endpoints.board

import de.hypercdn.ticat.api.entities.json.out.BoardMemberJson
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class BoardMembershipHandler {

    // auth required
    @PostMapping("/board/:boardId/invite")
    fun inviteMemberToBoard(@PathVariable("boardId") boardId: String): BoardMemberJson {
        throw NotImplementedError()
    }

    // auth required
    @GetMapping("/board/:boardId/join")
    fun requestOrAcceptBoardMembership(@PathVariable("boardId") boardId: String): BoardMemberJson {
        throw NotImplementedError()
    }

    // auth required
    @GetMapping("/board/:boardId/membership")
    fun getCurrentMembershipStatus(@PathVariable("boardId") boardId: String): BoardMemberJson {
        throw NotImplementedError()
    }

    // auth required
    @GetMapping("/board/:boardId/members")
    fun getListOfAllMembers(@PathVariable("boardId") boardId: String): List<BoardMemberJson> {
        throw NotImplementedError()
    }

    // auth required
    @PostMapping("/board/:boardId/member/:userId")
    fun updateMembershipDetails(@PathVariable("boardId") boardId: String, @PathVariable("userId") userUUID: UUID): BoardMemberJson {
        throw NotImplementedError()
    }



}