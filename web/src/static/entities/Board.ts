import {User} from "./User";
import {Member} from "./Member";
import {BoardVisibility} from "./enums/BoardVisibility";
import {BoardAccessMode} from "./enums/BoardAccessMode";

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