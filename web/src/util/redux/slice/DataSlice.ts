import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {User} from "../../../static/entities/User";
import {Board} from "../../../static/entities/Board";

interface DataSlice {
    selfUser?: User,
    userCache: Map<string, User>,
    boardCache: Map<string, Board>
}

export const dataSlice = createSlice({
    name: 'data-slice',
    initialState: {
        selfUser: undefined,
        userCache: new Map<string, User>(),
        boardCache: new Map<string, Board>()
    } as DataSlice,
    reducers: {
        setSelfUserData(state, action: PayloadAction<User | undefined>) {
            state.selfUser = action.payload
        },
        updateUserCache(state, action: PayloadAction<{user: User | undefined, id: string}>) {
            if (action.payload.user === undefined) {
                state.userCache.delete(action.payload.id)
            } else {
                state.userCache.set(action.payload.id, action.payload.user)
            }
        },
        updateBoardCache(state, action: PayloadAction<{board: Board | undefined, id: string}>) {
            if (action.payload.board === undefined) {
                state.boardCache.delete(action.payload.id)
            } else {
                state.boardCache.set(action.payload.id, action.payload.board)
            }
        }
    }
})

export const {
    setSelfUserData,
    updateUserCache,
    updateBoardCache
} = dataSlice.actions

export default dataSlice.reducer