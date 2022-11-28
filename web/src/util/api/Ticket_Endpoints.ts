import reduxStore from "../redux/ReduxStore";
import {removeTicketFromCache, updateCachedTicket} from "../redux/slice/DataCache";
import {Ticket, TicketCreate, TicketUpdate} from "./entities/Ticket";
import {Paged} from "./entities/Paged";


export async function getAllTicketsFor(boardId: string, chunk: number = 100, cacheResult: boolean = true) {
    console.log(`getting all available tickets in board %s`, boardId.toUpperCase())
    let tickets: Ticket[] = []
    let page = 0
    while (true) {
        const paged = await getTicketsFor(boardId.toUpperCase(), page++, chunk, cacheResult)
        paged.entities.forEach(ticket => tickets.push(ticket))
        if (paged.entities.length < paged.chunkSize) break;
    }
    return tickets
}

export async function getTicketsFor(boardId: string, page: number, chunk: number, cacheResult: boolean = true) {
    console.log(`getting available tickets in board %s in chunks (%s@%s)`, boardId.toUpperCase(), chunk, page)
    return fetch(`/api/t/${boardId.toUpperCase()}`, {
        method: `GET`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`
        } : undefined
    })
    .then(r => {
        if (!r.ok) throw Error(`fetching tickets from api failed with status ${r.status}`)
        return r.json() as unknown as Paged<Ticket>
    })
    .then(paged => {
        if (cacheResult) paged.entities.forEach(ticket => reduxStore.dispatch(updateCachedTicket(ticket)))
        return paged
    })
}

export async function getTicketFor(boardId: string, ticketId: number, useCache: boolean = false, cacheResult: boolean = true) {
    console.log(`getting data for ticket with id %s from board %s`, ticketId, boardId.toUpperCase())
    if (useCache) {
        const ticketCache = reduxStore.getState().data.tickets.get(boardId.toUpperCase())
        let ticket = ticketCache?.get(ticketId)
        if(ticket) {
            console.log(`found cached entity for ticket with id %s from board %s`, ticketId, boardId.toUpperCase())
            return ticket
        }
    }
    return fetch(`/t/${boardId.toUpperCase()}/${ticketId}`, {
        method: `GET`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`
        } : undefined
    })
    .then(r => {
        if (!r.ok) throw Error(`fetching ticket from api failed with status ${r.status}`)
        return r.json() as Ticket
    })
    .then(ticket => {
        if (cacheResult) reduxStore.dispatch(updateCachedTicket(ticket))
        return ticket
    })
}

export async function createNewTicket(boardId: string, template: TicketCreate, cacheResult: boolean = true) {
    console.log(`creating new ticket in board %s`, boardId.toUpperCase())
    return fetch(`/api/t/${boardId.toUpperCase()}`, {
        method: `POST`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`,
            'Content-Type': 'application/json'
        } : undefined,
        body: JSON.stringify(template)
    })
    .then(r => {
        if(!r.ok) throw Error(`creating new ticket failed with status ${r.status}`)
        return r.json() as Ticket
    })
    .then(ticket => {
        if (cacheResult) reduxStore.dispatch(updateCachedTicket(ticket))
        return ticket
    })
}

export async function updateTicket(boardId: string, template: TicketUpdate, cacheResult: boolean = true) {
    console.log("updating ticket with id %s from board %s", template.id, boardId.toUpperCase())
    return fetch(`/api/t/${boardId.toUpperCase()}/${template.id}`, {
        method: `PATCH`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`,
            'Content-Type': 'application/json'
        } : undefined,
        body: JSON.stringify(template)
    })
    .then(r => {
        if(!r.ok) throw Error(`updating ticket failed with status ${r.status}`)
        return r.json() as Ticket
    })
    .then(ticket => {
        if (cacheResult) {
            reduxStore.dispatch(updateCachedTicket(ticket))
        }
        return ticket
    })
}

export async function deleteTicket(boardId: string, ticketId: number) {
    return fetch(`/api/t/${boardId}/${ticketId}`, {
        method: `DELETE`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`
        }: undefined
    })
    .then(r => r.ok)
    .then(ok => {
        reduxStore.dispatch(removeTicketFromCache({boardId: boardId.toUpperCase(), ticketId: ticketId}))
        return ok
    })
}