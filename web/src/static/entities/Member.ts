import {User} from "./User";
import {Board} from "./Board";
import {BoardMembershipStatus} from "./enums/BoardMembershipStatus";

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