import {configureStore} from "@reduxjs/toolkit"
import {enableMapSet} from "immer"
import AuthSlice from "./slice/AuthSlice";
import UISlice from "./slice/UISlice";
import UserCache from "./slice/UserCache";
import BoardCache from "./slice/BoardCache";
enableMapSet()

const reduxStore = configureStore({
    reducer: {
        auth: AuthSlice,
        ui: UISlice,
        userCache: UserCache,
        boardCache: BoardCache
    },
    middleware: getDefaultMiddleware => getDefaultMiddleware({
        serializableCheck: false
    })
})

export default reduxStore
export type RootState = ReturnType<typeof reduxStore.getState>
export type AppDispatch = typeof reduxStore.dispatch