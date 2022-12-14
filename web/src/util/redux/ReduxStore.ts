import {configureStore} from "@reduxjs/toolkit"
import {enableMapSet} from "immer"
import AuthSlice from "./slice/AuthSlice";
import UISlice from "./slice/UISlice";
import DataCache from "./slice/DataCache";
enableMapSet()

const reduxStore = configureStore({
    reducer: {
        auth: AuthSlice,
        ui: UISlice,
        data: DataCache
    },
    middleware: getDefaultMiddleware => getDefaultMiddleware({
        serializableCheck: false
    })
})

export default reduxStore
export type RootState = ReturnType<typeof reduxStore.getState>
export type AppDispatch = typeof reduxStore.dispatch