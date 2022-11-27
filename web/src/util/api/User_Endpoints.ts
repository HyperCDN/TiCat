import {User} from "../../static/entities/User";
import reduxStore from "../redux/ReduxStore";
import {updateCachedUser} from "../redux/slice/UserCache";

export async function getDataForUserWithId(uuid: string, cache: boolean = true) {
    console.log("getting data for user with id %s", uuid)
    const userCache = reduxStore.getState().userCache.users
    let user = userCache.get(uuid)
    if(user) {
        console.log("found cached entity for user with id %s", uuid)
        return user
    }
    return fetch(`/api/u/${uuid}`, {
        method: `GET`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`
        }: {}
    })
    .then(r => {
        if (!r.ok) {
            throw Error(`fetching user from api failed with status ${r.status}`)
        }
        return r.json() as User
    })
    .then(user => {
        if (cache) {
            reduxStore.dispatch(updateCachedUser(user))
        }
        return user
    })
}