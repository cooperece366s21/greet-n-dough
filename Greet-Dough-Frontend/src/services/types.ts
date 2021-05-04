type Post = {
    ID: number,
    userID: number,
    title: string,
    contents: string,
    timeCreated: TimeCreated,
    time: Time,
}

type TimeCreated = {
    date: Date,
}

type Date = {
    year: number,
    month: number,
    day: number,
}

type Time = {
    hour: number,
    minute: number,
    second: number,
    nano: number,
}

type FeedState = {
    cuid: number,
    uid: number,
    feed: Map[] | null,
    hasOwnership: boolean,
    deleteAlert: boolean,
}

type Map = {
    map: PostObject,
}

type PostJson = {
    map: PostObject,
}

type PostObject = {
    post: Post,
    likeCount : number,
    images: string[],
    comments: CommentJson[]
}

type CommentJson = {
    map: CommentObject,
}

type CommentObject = {
    contents: string,
    ID: number,
    postID: number,
    avatar: string,
    userID: number,
    username: string,
}

export type { Post, PostObject, Time, Date, TimeCreated, Map, FeedState, PostJson, CommentJson }