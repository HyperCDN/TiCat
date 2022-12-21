package de.hypercdn.ticat.api.entities.helper

import de.hypercdn.ticat.api.entities.sql.entities.Member

fun Member.hasEffectiveViewPower(): Boolean {
    return user.isAdmin
            || board.ownerUUID == userUUID
            || (status == Member.MembershipStatus.GRANTED
            && (canView || canUse || canManage || canAdministrate))
}

fun Member.hasEffectiveUsePower(): Boolean {
    return user.isAdmin
            || board.ownerUUID == userUUID
            || (status == Member.MembershipStatus.GRANTED
            && (canUse || canManage || canAdministrate))
}

fun Member.hasEffectiveManagementPower(): Boolean {
    return user.isAdmin
            || board.ownerUUID == userUUID
            || (status == Member.MembershipStatus.GRANTED
            && (canManage || canAdministrate))
}

fun Member.hasEffectiveAdministrationPower(): Boolean {
    return user.isAdmin
            || board.ownerUUID == userUUID
            || (status == Member.MembershipStatus.GRANTED
            && (canAdministrate))
}

fun Member.hasOwnershipPower(): Boolean {
    return user.isAdmin
            || board.ownerUUID == userUUID
}