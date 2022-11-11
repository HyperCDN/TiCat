package de.hypercdn.ticat.api.endpoints.board

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class BoardMembershipHandler {

    // auth required
    @PostMapping("/board/:boardId/invite")
    fun inviteMemberToBoard(@PathVariable("boardId") boardId: String): Any {
        return Any()
    }

    // auth required
    @GetMapping("/board/:boardId/join")
    fun requestOrAcceptBoardMembership(@PathVariable("boardId") boardId: String): Any {
        return Any()
    }

    // auth required
    @GetMapping("/board/:boardId/membership")
    fun getCurrentMembershipStatus(@PathVariable("boardId") boardId: String): Any {
        return Any()
    }

    // auth required
    @GetMapping("/board/:boardId/members")
    fun getListOfAllMembers(@PathVariable("boardId") boardId: String): List<Any> {
        return emptyList()
    }

    // auth required
    @PostMapping("/board/:boardId/member/:userId")
    fun updateMembershipDetails(@PathVariable("boardId") boardId: String, @PathVariable("userId") userUUID: UUID): Any {
        return Any()
    }



}