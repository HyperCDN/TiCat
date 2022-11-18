package de.hypercdn.ticat.api.entities.helper

import de.hypercdn.ticat.api.entities.sql.Member
import de.hypercdn.ticat.api.entities.sql.enums.BoardMembershipStatus

fun Member.hasEffectiveViewPower(): Boolean {
    return user.isAdmin
            || board.ownerUUID == userUUID
            || (status == BoardMembershipStatus.GRANTED
            && (canView || canUse || canManage || canAdministrate))
}

fun Member.hasEffectiveUsePower(): Boolean {
    return user.isAdmin
            || board.ownerUUID == userUUID
            || (status == BoardMembershipStatus.GRANTED
            && (canUse || canManage || canAdministrate))
}

fun Member.hasEffectiveManagementPower(): Boolean {
    return user.isAdmin
            || board.ownerUUID == userUUID
            || (status == BoardMembershipStatus.GRANTED
            && (canManage || canAdministrate))
}

fun Member.hasEffectiveAdministrationPower(): Boolean {
    return user.isAdmin
            || board.ownerUUID == userUUID
            || (status == BoardMembershipStatus.GRANTED
            && (canAdministrate))
}

fun Member.hasOwnershipPower(): Boolean {
    return user.isAdmin
            || board.ownerUUID == userUUID
}