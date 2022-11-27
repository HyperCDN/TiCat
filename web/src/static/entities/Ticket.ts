import {Board} from "./Board";
import {User} from "./User";
import {TicketCategory} from "./enums/TicketCategory";
import {TicketStatus} from "./enums/TicketStatus";
import {TicketPriority} from "./enums/TicketPriority";

export type Ticket = {
    id?: number,
    board?: Board,
    creator?: User,
    title?: string,
    content?: string,
    assignee?: User,
    properties?: {
        category?: TicketCategory,
        priority?: TicketPriority,
        status?: TicketStatus
    }
}

export type TicketCreate = {
    title: string,
    content?: string,
    assignee?: User,
    properties?: {
        category?: TicketCategory,
        priority?: TicketPriority,
        status?: TicketStatus
    }
}


export type TicketUpdate = {
    id: number,
    title?: string,
    content?: string,
    assignee?: User,
    properties?: {
        category?: TicketCategory,
        priority?: TicketPriority,
        status?: TicketStatus
    }
}