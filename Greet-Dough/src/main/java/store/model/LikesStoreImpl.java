package store.model;

import model.Likes;

import java.util.ArrayList;

public class LikesStoreImpl extends Store<Likes> {

    public LikesStoreImpl() {
        super();
    }

    public LikesStoreImpl( int start ) {
        super(start);
    }

    public Likes getID( int ID ) {
        return super.get(ID);
    }

    public void attemptLike(Likes currentPost, int currentUser) {
        int count = currentPost.getLikeCount();
        ArrayList<Integer> likes = currentPost.getUserLikes();

        // Check list of users, if user already liked
        // If user did not like, add 1 to the like count
            // From checkID() if false append userID to list
            // From checkID() if true
                // removes like by decrementing likeCount
                // deletes userID from userLikes list
        if (likes.contains(currentUser)){
            likes.remove(likes.indexOf(currentUser));
            count--;
        }
        else{
            likes.add(count - 1 ,currentUser);
            count++;
        }
        currentPost.setLikeCount(count);
        currentPost.setUserLikes(likes);
    }





}
