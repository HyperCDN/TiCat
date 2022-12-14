export type Paged<T> = {
    chunkSize: number,
    page: number,
    entities: T[]
}