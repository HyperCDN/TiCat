import {createSlice, PayloadAction} from "@reduxjs/toolkit";

interface AuthState {
    resourceServerURL: string | null,
    clientId: string | null,
    accessToken: string | null
    refreshToken: string | null
}

export const authSlice = createSlice({
    name: 'auth-slice',
    initialState: {
        resourceServerURL: null,
        clientId: null,
        accessToken: localStorage.getItem('accessToken'),
        refreshToken: localStorage.getItem('refreshToken')
    } as AuthState,
    reducers: {
        setResourceServerURL(state, action: PayloadAction<{resourceServerURL: string, clientId: string}|null>) {
            state.resourceServerURL = action.payload?.resourceServerURL!
            state.clientId = action.payload?.clientId!
        },
        setAuthDetails(state, action: PayloadAction<{access_token: string, refresh_token: string}|null>) {
            if(action.payload == null || !action.payload?.access_token || !action.payload?.refresh_token){
                state.accessToken = null
                state.refreshToken = null
                localStorage.removeItem('accessToken')
                localStorage.removeItem('refreshToken')
            } else {
                state.accessToken = action.payload?.access_token
                state.refreshToken = action.payload?.refresh_token
                localStorage.setItem('accessToken', state.accessToken)
                localStorage.setItem('refreshToken', state.refreshToken)
            }
        }
    }
})

export const {
    setResourceServerURL, setAuthDetails
} = authSlice.actions

export default authSlice.reducer