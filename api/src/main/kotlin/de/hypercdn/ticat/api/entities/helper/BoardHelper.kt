package de.hypercdn.ticat.api.entities.helper

import de.hypercdn.ticat.api.entities.sql.Board
import de.hypercdn.ticat.api.entities.sql.Member
import de.hypercdn.ticat.api.entities.sql.User
import de.hypercdn.ticat.api.entities.sql.enums.BoardVisibility

fun Board.isVisibleTo(user: User, member: Member? = null): Boolean {
    if (user.isAdmin) return true
    member?.let { return it.hasEffectiveViewPower() }
    return when (visibility) {
        BoardVisibility.ANYONE -> true
        BoardVisibility.LOGGED_IN_USER -> User.isAuthenticated()
        else -> false
    }
}