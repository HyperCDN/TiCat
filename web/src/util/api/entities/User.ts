export type User = {
    id?: string,
    name?: {
        firstName?: string,
        lastName?: string,
        displayName?: string
    }
    email?: string,
    permissions?: {
        isSystem?: boolean,
        isAdmin?: boolean,
        canLogin?: boolean,
        canBoardCreate?: boolean,
        canBoardJoin?: boolean
    }
}