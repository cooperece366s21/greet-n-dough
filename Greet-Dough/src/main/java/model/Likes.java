package model;

import java.io.Serializable;
import java.util.HashSet;

public class Likes implements Serializable {

    private int userID;
    private int postID;
    private int likeCount;
    private HashSet<Integer> userLikes;

    public Likes( int postID, int userID ) {
        this( postID, userID, new HashSet<>() );
    }

    public Likes( int postID, int userID, HashSet<Integer> userLikes ) {

        this.userID = userID;
        this.postID = postID;
        this.likeCount = userLikes.size();
        this.userLikes = userLikes;

    }

    public int getUserID() {
        return this.userID;
    }

    public int getPostID() {
        return this.postID;
    }

    public HashSet<Integer> getUserLikes() {
        return userLikes;
    }

    public int getLikeCount() {
        return this.likeCount;
    }

    public void incrementLike( int userID ) {

        this.likeCount++;
        this.userLikes.add(userID);

    }

    public void decrementLike( int userID ) {

        this.likeCount--;
        this.userLikes.remove(userID);

    }

    public void setLikeCount( int likeCount ) {
        this.likeCount = likeCount;
    }

    public void setUserLikes( HashSet<Integer> userLikes ) {
        this.userLikes = userLikes;
    }

    // No dislike option
    // Author of post can like their own post, we believe in self-confidence
}
