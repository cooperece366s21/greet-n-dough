package store.model;

import model.Likes;

public class LikesStoreImpl extends Store<Likes> {

    public LikesStoreImpl() {
        super();
    }

    public LikesStoreImpl( int start ) {
        super(start);
    }

    //public Likes getID(Likes NewLikes ) {
    //    return ;
    //}

    public void attemptLike(Likes currentPost, int currentUser) {
        if (currentPost.checkID(currentUser)){
            currentPost.removeLike(currentUser);
        }
        else{
            currentPost.addLike((currentUser));
        }
    }

}
