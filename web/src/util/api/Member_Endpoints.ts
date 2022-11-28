import {Board} from "./entities/Board";
import {Member} from "./entities/Member";
import reduxStore from "../redux/ReduxStore";
import {Paged} from "./entities/Paged";
import {Ticket} from "./entities/Ticket";
import {updateCachedMember, updateCachedTicket} from "../redux/slice/DataCache";


export async function getAllMembersOf(boardId: string, chunk: number = 100, cacheResult: boolean = true) {
    console.log(`getting all members in board %s`, boardId.toUpperCase())
    let members: Member[] = []
    let page = 0
    while (true) {
        const paged = await getMembersFor(boardId, page++, chunk, cacheResult)
        paged.entities.forEach(member => members.push(member))
        if (paged.entities.length < paged.chunkSize) break;
    }
    return members
}

export async function getMembersFor(boardId: string, page: number, chunk: number, cacheResult: boolean = true) {
    console.log(`getting available members in board %s in chunks (%s@%s)`, boardId.toUpperCase(), chunk, page)
    return fetch(`/api/m/${boardId.toUpperCase()}`, {
        method: `GET`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`
        } : undefined
    })
    .then(r => {
        if (!r.ok) throw Error(`fetching members from api failed with status ${r.status}`)
        return r.json() as unknown as Paged<Member>
    })
    .then(paged => {
        if (cacheResult) paged.entities.forEach(member => reduxStore.dispatch(updateCachedMember(member)))
        return paged
    })
}

export async function getMemberInfo() {

}

export async function requestAccessOrAcceptInvite() {

}

export async function inviteMemberOrAcceptRequest() {

}

export async function updateMembership() {

}

export async function deleteOrDenyMembership() {

}