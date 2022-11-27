import {createSlice, PayloadAction} from "@reduxjs/toolkit";

interface AuthState {
    resourceServerURL?: string,
    clientId?: string,
    accessToken?: string
    refreshToken?: string
}

export const authSlice = createSlice({
    name: 'auth-slice',
    initialState: {
        resourceServerURL: undefined,
        clientId: undefined,
        accessToken: localStorage.getItem('accessToken'),
        refreshToken: localStorage.getItem('refreshToken')
    } as AuthState,
    reducers: {
        setResourceServerURL(state, action: PayloadAction<{resourceServerURL: string, clientId: string}|undefined>) {
            console.log("setting auth resource server url and client id")
            state.resourceServerURL = action.payload?.resourceServerURL
            state.clientId = action.payload?.clientId
        },
        setAuthDetails(state, action: PayloadAction<{access_token: string, refresh_token: string}|undefined>) {
            if(action.payload){
                console.log("storing auth details")
                state.accessToken = action.payload?.access_token
                state.refreshToken = action.payload?.refresh_token
                localStorage.setItem('accessToken', state.accessToken)
                localStorage.setItem('refreshToken', state.refreshToken)
            } else {
                console.log("removing stores auth details")
                state.accessToken = undefined
                state.refreshToken = undefined
                localStorage.removeItem('accessToken')
                localStorage.removeItem('refreshToken')
            }
        }
    }
})

export const {
    setResourceServerURL, setAuthDetails
} = authSlice.actions

export default authSlice.reducer