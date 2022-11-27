import reduxStore from '../redux/ReduxStore';
import {setAuthDetails} from '../redux/slice/AuthSlice';
import {User} from '../../static/entities/User';

export const ROOT_URI = window.location.protocol + `//` + window.location.hostname + (window.location.port ? `:` + window.location.port : ``)

export async function getOIDCConfig() {
    return fetch(reduxStore.getState().auth.resourceServerURL + `/.well-known/openid-configuration`)
        .then(r => {
            if(!r.ok){
                throw new Error(`failed to get configuration from api`)
            }
            return r.json()
        })
}

export async function initializeLogin() {
    console.log(`initializing login`)
    return getOIDCConfig()
        .then(oidcConfig => {
            const requestParams =
                `?response_type=code` +
                `&client_id=${reduxStore.getState().auth.clientId}` +
                `&login=true` +
                `&scope=openid profile email` +
                `&redirect_uri=${encodeURI( `${ROOT_URI}/auth_response`)}`
            window.location.href = oidcConfig[`authorization_endpoint`] + requestParams
        })
        .catch(e => {
            console.error(e)
        })
}

export async function login(code: string){
    console.log(`verifying auth code`)
    return getOIDCConfig()
        .then(oidcConfig => {
            return fetch(oidcConfig[`token_endpoint`], {
                method: `POST`,
                headers: {
                    'Content-Type': `application/x-www-form-urlencoded`
                },
                body: new URLSearchParams({
                    'grant_type': `authorization_code`,
                    'client_id': `${reduxStore.getState().auth.clientId!}`,
                    'code': code,
                    'redirect_uri': encodeURI(`${ROOT_URI}/auth_response`)
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
        })
        .then(() => updateUserInfo())
        .catch(e => {
            console.error(e)
        })
}

export async function refresh(){
    console.log(`updating jwt`)
    return getOIDCConfig()
        .then(oidcConfig => {
            return fetch(oidcConfig[`token_endpoint`], {
                method: `POST`,
                headers: {
                    'Content-Type': `application/x-www-form-urlencoded`
                },
                body: new URLSearchParams({
                    'grant_type': `refresh_token`,
                    'client_id': reduxStore.getState().auth.clientId!,
                    'refresh_token': reduxStore.getState().auth.refreshToken!
                })
            })
            .then(r => {
                if(!r.ok){
                    throw new Error(`failed to refresh jwt`)
                }
                return r.json()
            })
        })
        .then(oidcToken => {
            reduxStore.dispatch(setAuthDetails(oidcToken))
        })
        .then(() => updateUserInfo())
        .catch(e => {
            reduxStore.dispatch(setAuthDetails(undefined))
            console.error(e)
        })
}

export async function logout(){
    console.log(`logging out`)
    return getOIDCConfig()
        .then(oidcConfig => {
            return fetch(oidcConfig[`end_session_endpoint`], {
                method: `POST`,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    'client_id': reduxStore.getState().auth.clientId!,
                    'refresh_token': reduxStore.getState().auth.refreshToken!,
                    'redirect_uri': encodeURI(`${ROOT_URI}/`)
                })
            })
            .then(() => {
                reduxStore.dispatch(setAuthDetails(undefined))
            })
        }).catch(e => console.error(e))
}

export async function testAuth(){
    if(reduxStore.getState().auth.accessToken){
        console.log(`testing auth`)
        return fetch(`/api/jwt`, {
            headers: reduxStore.getState().auth.accessToken ? {
                'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`
            }:{}
        })
        .then(r => {
            if(!r.ok){
                return refresh()
            }
        })
        .catch(() => logout())
    }

}

export async function updateUserInfo(){
    console.log(`syncing user info with api`)
    return fetch('/api/jwt', {
        method: `POST`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`
        }:{}
    })
    .then(r => {
        if(!r.ok) {
            throw new Error(`failed to sync user info with api`)
        }
        return r.json() as User
    })
    .catch(e => console.error(e))
}