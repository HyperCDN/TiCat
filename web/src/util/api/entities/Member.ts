import {User} from "./User";
import {Board} from "./Board";

export type Member = {
    user?: User,
    board?: Board,
    status?: BoardMembershipStatus,
    permissions?: {
        canView?: boolean,
        canUse?: boolean,
        canManage?: boolean,
        canAdministrate?: boolean
    }
}

export enum BoardMembershipStatus {
    REQUESTED,
    OFFERED,
    GRANTED,
    BLOCKED
}