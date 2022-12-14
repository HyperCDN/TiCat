import {getDataForUserWithId} from "./User_Endpoints";
import jwtDecode, {JwtPayload} from "jwt-decode";
import reduxStore from "../redux/ReduxStore";

export function getJwTContent() {
    if (reduxStore.getState().auth.accessToken) return jwtDecode<JwtPayload>(reduxStore.getState().auth.accessToken!)
    return undefined
}

export function getSelfUserUUID() {
    return getJwTContent()?.["sub"];
}

export async function getSelfUser() {
    return getDataForUserWithId(getSelfUserUUID()!, true, true)
}