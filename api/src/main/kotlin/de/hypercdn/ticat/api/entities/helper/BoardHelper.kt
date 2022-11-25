package de.hypercdn.ticat.api.entities.helper

import de.hypercdn.ticat.api.entities.sql.Board
import de.hypercdn.ticat.api.entities.sql.Member
import de.hypercdn.ticat.api.entities.sql.User

fun Board.isVisibleTo(user: User, member: Member? = null): Boolean {
    if (user.isAdmin) return true
    member?.let { return it.hasEffectiveViewPower() }
    return when (visibility) {
        Board.Visibility.ANYONE -> true
        Board.Visibility.LOGGED_IN_USER -> User.isAuthenticated()
        else -> false
    }
}