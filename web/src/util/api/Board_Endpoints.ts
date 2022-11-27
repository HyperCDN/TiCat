import reduxStore from "../redux/ReduxStore";
import {Board} from "../../static/entities/Board";
import {updateCachedBoard} from "../redux/slice/BoardCache";

export async function getAvailableBoards(page: number, chunk: number, cache: boolean = true) {

}

export async function getBoardInfo(id: string, cache: boolean = true) {
    console.log("getting data for board with id %s", id.toUpperCase())
    const boardCache = reduxStore.getState().boardCache.boards
    let board = boardCache.get(id.toUpperCase())
    if(board) {
        console.log("found cached entity for board with id %s", id)
        return board
    }
    return fetch(`/api/b/${id.toUpperCase()}`, {
        method: `GET`,
        headers: reduxStore.getState().auth.accessToken ? {
            'Authorization': `Bearer ${reduxStore.getState().auth.accessToken}`
        }: {}
    })
    .then(r => {
        if (!r.ok) {
            throw Error(`fetching board from api failed with status ${r.status}`)
        }
        return r.json() as Board
    })
    .then(board => {
        if (cache) {
            reduxStore.dispatch(updateCachedBoard(board))
        }
        return board
    })
}

export async function createNewBoard() {

}

export async function updateBoard() {

}

export async function deleteBoard() {

}