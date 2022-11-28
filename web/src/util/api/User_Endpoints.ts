import reduxStore from "../redux/ReduxStore";
import {updateCachedUser} from "../redux/slice/DataCache";
import {User} from "./entities/User";

export async function getDataForUsers(uuids: string[], useCache: boolean = false, cacheResult: boolean = true) {

}

export async function getDataForUserWithId(uuid: string, useCache: boolean = false, cacheResult: boolean = true) {
    console.log("getting data for user with id %s", uuid)
    if (useCache) {
        const userCache = reduxStore.getState().data.users
        let user = userCache.get(uuid)
        if(user) {
            console.log("found cached entity for user with id %s", uuid)
            return user
        }
    }
    return fetch(`/api/u/${uuid}`, {
        method: `GET`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`
        } : undefined
    })
    .then(r => {
        if (!r.ok) throw Error(`fetching user from api failed with status ${r.status}`)
        return r.json() as User
    })
    .then(user => {
        if (cacheResult) reduxStore.dispatch(updateCachedUser(user))
        return user
    })
}