type FeedState = {
    cuid: number,
    uid: number,
    feed: PostJson[] | null,
    hasOwnership: boolean,
    deleteAlert: boolean,
}

type PostJson = {
    map: PostObject,
}

type PostObject = {
    post: Post,
    likeCount : number,
    images: string[],
    comments: CommentJson[]
    hidden: boolean,
}

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
    reloadComments: boolean,
    children: CommentJson[],
}

type UserJson = {
    map: UserObject
}

type UserObject = {
    name: string,
    ID: number,
    avatar: string,
}

type UploadedImage = {
    url: string,
    id: number,
}

export type { Post, PostObject, Time, Date, TimeCreated,
    FeedState, PostJson, CommentJson, CommentObject,
    UserObject, UserJson, UploadedImage }