import reduxStore from "../redux/ReduxStore";
import {setAuthDetails} from "../redux/slice/AuthSlice";

export const ROOT_URI = window.location.protocol + '//' + window.location.hostname + (window.location.port ? ':' + window.location.port : '')

export async function getOIDCConfig() {
    return fetch(reduxStore.getState().auth.resourceServerURL + '/.well-known/openid-configuration')
        .then(r => {
            if(!r.ok){
                throw new Error('Failed to get configuration from api')
            }
            return r.json()
        })
}

export async function initializeLogin() {
    console.log("Initializing login...")
    await getOIDCConfig()
        .then(oidcConfig => {
            const requestParams =
                '?response_type=code' +
                '&client_id=' + reduxStore.getState().auth.clientId +
                '&login=true' +
                '&scope=openid profile email' +
                '&redirect_uri=' + encodeURI(ROOT_URI + '/auth_response')
            window.location.href = oidcConfig['authorization_endpoint'] + requestParams
        })
        .catch(e => {
            console.error(e)
        })
}

export async function login(code: string){
    console.log("Verifying auth code...")
    await getOIDCConfig()
        .then(oidcConfig => {
            return fetch(oidcConfig['token_endpoint'], {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    'grant_type': 'authorization_code',
                    'client_id': reduxStore.getState().auth.clientId!,
                    'code': code,
                    'redirect_uri': encodeURI(ROOT_URI + '/auth_response')
                })
            })
            .then(r => {
                if(!r.ok){
                    throw new Error()
                }
                return r.json()
            })
        })
        .then(oidcToken => {
            reduxStore.dispatch(setAuthDetails(oidcToken))
            updateUserInfo()
        })
        .catch(e => {
            console.error(e)
        })
}

export async function refresh(){
    console.log("Updating jwt...")
    await getOIDCConfig()
        .then(oidcConfig => {
            return fetch(oidcConfig['token_endpoint'], {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    'grant_type': 'refresh_token',
                    'client_id': reduxStore.getState().auth.clientId!,
                    'refresh_token': reduxStore.getState().auth.refreshToken!
                })
            })
            .then(r => {
                if(!r.ok){
                    throw new Error("Failed to refresh jwt")
                }
                return r.json()
            })
        })
        .then(oidcToken => {
            reduxStore.dispatch(setAuthDetails(oidcToken))
            return updateUserInfo()
        })
        .catch(e => {
            reduxStore.dispatch(setAuthDetails(null))
            console.error(e)
        })
}

export async function logout(){
    console.log("Logging out...")
    await getOIDCConfig()
        .then(oidcConfig => {
            return fetch(oidcConfig['end_session_endpoint'], {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    'client_id': reduxStore.getState().auth.clientId!,
                    'refresh_token': reduxStore.getState().auth.refreshToken!,
                    'redirect_uri': encodeURI(ROOT_URI + '/')
                })
            })
            .then(() => {
                reduxStore.dispatch(setAuthDetails(null))
            })
        }).catch(()=>{})
}

export async function testAuth(){
    if(reduxStore.getState().auth.accessToken){
        console.log("Testing auth...")
        await fetch('/api/user/test', {
            headers: {
                "Authorization": "Bearer " + reduxStore.getState().auth.accessToken!
            }
        })
        .then(r => {
            if(!r.ok){
                refresh()
            }
        })
        .catch(() => logout())
    }

}

export async function updateUserInfo(){
    console.log("Updating user info...")
    await fetch('/api/user/sync', {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + reduxStore.getState().auth.accessToken!
        }
    })
    .then(r => {
        if(r.ok) {
            throw new Error()
        }
        return r.json()
    })
    .catch(() => {
        console.error("Failed to update user info")
    })
}