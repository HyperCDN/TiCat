import {User} from "./User";
import {Member} from "./Member";

export type Board = {
    id?: string,
    title?: string,
    description?: string,
    owner?: User,
    settings?: {
        visibility?: BoardVisibility,
        accessMode?: BoardAccessMode,
    },
    members?: Member[]
}

export type BoardCreate = {
    id: string,
    title: string,
    description?: string,
    settings?: {
        visibility?: BoardVisibility,
        accessMode?: BoardAccessMode,
    },
}

export type BoardUpdate = {
    title: string,
    description?: string,
    settings?: {
        visibility?: BoardVisibility,
        accessMode?: BoardAccessMode,
    },
}

export enum BoardVisibility {
    ANYONE,
    LOGGED_IN_USER,
    MEMBERS_ONLY
}

export enum BoardAccessMode {
    PUBLIC_JOIN,
    MANUAL_VERIFY,
    MANUAL_ADD
}