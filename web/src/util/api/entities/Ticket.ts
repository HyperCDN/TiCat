import {Board} from "./Board";
import {User} from "./User";

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

export enum TicketStatus {
    OPEN,
    CLOSED,
    DELETED
}

export enum TicketPriority {
    CRITICAL,
    HIGH,
    NORMAL,
    LOW,
    NONE
}

export enum TicketCategory {
    EPIC,
    BUG,
    STORY
}