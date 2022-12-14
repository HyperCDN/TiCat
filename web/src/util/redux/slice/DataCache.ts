import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {User} from "../../api/entities/User";
import {Board} from "../../api/entities/Board";
import {Ticket} from "../../api/entities/Ticket";
import {Member} from "../../api/entities/Member";

interface DataCache {
    users: Map<string, User>
    boards: Map<string, Board>
    tickets: Map<string, Map<number, Ticket>>
    members: Map<string, Map<string, Member>>
}

export const dataCache = createSlice({
    name: 'data-cache',
    initialState: {
        users: new Map<string, User>(),
        boards: new Map<string, Board>(),
        tickets: new Map<string, Map<number, Ticket>>(),
        members: new Map<string, Map<string, Member>>(),
    } as DataCache,
    reducers: {
        updateCachedUser(state, action: PayloadAction<User>) {
            console.log("updating cached user %s", action.payload)
            state.users.set(action.payload.id!, action.payload)
        },
        removeUserFromCache(state, action: PayloadAction<string>) {
            console.log("removing user %s from cache", action.payload)
            state.users.delete(action.payload)
        },
        clearUserCache(state) {
            console.log("clearing users from cache")
            state.users.clear()
        },

        updateCachedBoard(state, action: PayloadAction<Board>) {
            console.log("updating cached board %s", action.payload)
            state.boards.set(action.payload.id!.toUpperCase(), action.payload)
        },
        removeBoardFromCache(state, action: PayloadAction<string>) {
            const boardId = action.payload.toUpperCase()
            console.log("removing board %s from cache. this will also remove cached tickets and members", boardId)
            state.boards.delete(boardId)
            state.tickets.delete(boardId)
            state.members.delete(boardId)
        },
        clearBoardCache(state) {
            console.log("clearing boards from cache. this will also remove cached tickets and members")
            state.boards.clear()
            state.tickets.clear()
            state.members.clear()
        },

        updateCachedTicket(state, action: PayloadAction<Ticket>) {
            const boardId = action.payload.board?.id!
            const ticketId = action.payload.id!
            console.log("updating cached ticket %s for board %s",ticketId, boardId)
            if (!state.tickets.has(boardId)) state.tickets.set(boardId, new Map<number, Ticket>())
            state.tickets.get(boardId)!.set(ticketId, action.payload)
        },
        removeTicketFromCache(state, action: PayloadAction<{boardId: string, ticketId: number}>) {
            const boardId = action.payload.boardId.toUpperCase()
            const ticketId = action.payload.ticketId
            console.log("removing ticket %s for board %s from cache", boardId, ticketId)
            state.tickets.get(boardId)?.delete(ticketId)
        },
        clearTicketCache(state, action: PayloadAction<string>) {
            const boardId = action.payload.toUpperCase()
            console.log("clearing tickets for board %s from cache", boardId)
            state.tickets.delete(boardId)
        },

        updateCachedMember(state, action: PayloadAction<Member>) {
            const boardId = action.payload.board?.id!
            const userUUID = action.payload.user?.id!
            console.log("updating cached member %s for board %s", userUUID, boardId)
            if (!state.members.has(boardId)) state.members.set(boardId, new Map<string, Member>())
            state.members.get(boardId)!.set(userUUID, action.payload)
        },
        removeMemberFromCache(state, action: PayloadAction<{boardId: string, userUUID: string}>) {
            const boardId = action.payload.boardId.toUpperCase()
            const userUUID = action.payload.userUUID
            console.log("removing member %s for board %s from cache", boardId, userUUID)
            state.members.get(boardId)?.delete(userUUID)
        },
        clearMemberCache(state, action: PayloadAction<string>) {
            const boardId = action.payload.toUpperCase()
            console.log("clearing members for board %s from cache", boardId)
            state.members.delete(boardId)
        }
    }
})

export const {
    updateCachedUser, removeUserFromCache, clearUserCache,
    updateCachedBoard, removeBoardFromCache, clearBoardCache,
    updateCachedTicket, removeTicketFromCache, clearTicketCache,
    updateCachedMember, removeMemberFromCache, clearMemberCache
} = dataCache.actions

export default dataCache.reducer
