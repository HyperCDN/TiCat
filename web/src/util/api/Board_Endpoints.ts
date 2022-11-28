import reduxStore from "../redux/ReduxStore";
import {removeBoardFromCache, updateCachedBoard} from "../redux/slice/DataCache";
import {Board, BoardCreate, BoardUpdate} from "./entities/Board";
import {Paged} from "./entities/Paged";

export async function getAllAvailableBoards(chunk: number = 50, cacheResult: boolean = true) {
    console.log(`getting all available boards in chunks`)
    let boards: Board[] = []
    let page = 0
    while (true) {
        const paged = await getAvailableBoards(page++, chunk, cacheResult)
        paged.entities.forEach(board => boards.push(board))
        if (paged.entities.length < paged.chunkSize) break;
    }
    return boards
}

export async function getAvailableBoards(page: number, chunk: number, cacheResult: boolean = true) {
    console.log(`getting available boards in chunks (%s@%s)`, chunk, page)
    return fetch(`/api/b`, {
        method: `GET`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`
        } : undefined
    })
    .then(r => {
        if (!r.ok) throw Error(`fetching boards from api failed with status ${r.status}`)
        return r.json() as unknown as Paged<Board>
    })
    .then(paged => {
        if (cacheResult) paged.entities.forEach(board => reduxStore.dispatch(updateCachedBoard(board)))
        return paged
    })
}

export async function getBoardInfo(id: string, useCache: boolean = false, cacheResult: boolean = true) {
    console.log(`getting data for board with id %s`, id.toUpperCase())
    if (useCache) {
        const boardCache = reduxStore.getState().data.boards
        let board = boardCache.get(id.toUpperCase())
        if(board) {
            console.log(`found cached entity for board with id %s`, id)
            return board
        }
    }
    return fetch(`/api/b/${id.toUpperCase()}`, {
        method: `GET`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`
        } : undefined
    })
    .then(r => {
        if (!r.ok) throw Error(`fetching board from api failed with status ${r.status}`)
        return r.json() as Board
    })
    .then(board => {
        if (cacheResult) reduxStore.dispatch(updateCachedBoard(board))
        return board
    })
}

export async function createNewBoard(template: BoardCreate, cacheResult: boolean = true) {
    console.log(`creating board with id %s`, template.id)
    return fetch(`/api/b`, {
        method: `POST`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`,
            'Content-Type': 'application/json'
        } : undefined,
        body: JSON.stringify(template)
    })
    .then(r => {
        if(!r.ok) throw Error(`creating board failed with status ${r.status}`)
        return r.json() as Board
    })
    .then(board => {
        if (cacheResult) reduxStore.dispatch(updateCachedBoard(board))
        return board
    })
}

export async function updateBoard(id: string, template: BoardUpdate, cacheResult: boolean = true) {
    console.log("updating board with id %s", id.toUpperCase())
    return fetch(`/api/b/${id.toUpperCase()}`, {
        method: `PATCH`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`,
            'Content-Type': 'application/json'
        } : undefined,
        body: JSON.stringify(template)
    })
    .then(r => {
        if(!r.ok) throw Error(`updating board failed with status ${r.status}`)
        return r.json() as Board
    })
    .then(board => {
        if (cacheResult) reduxStore.dispatch(updateCachedBoard(board))
        return board
    })
}

export async function deleteBoard(id: string) {
    return fetch(`/api/b/${id.toUpperCase()}`, {
        method: `DELETE`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`
        }: undefined
    })
    .then(r => r.ok)
    .then(ok => {
        reduxStore.dispatch(removeBoardFromCache(id.toUpperCase()))
        return ok
    })
}