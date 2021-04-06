package model;

import java.io.Serializable;
import java.util.HashSet;

public class Likes implements Serializable {

    private int userID;
    private int postID;
    private HashSet<Integer> userLikes;

    public Likes( int pid, int uid ) {
        this( pid, uid, new HashSet<>() );
    }

    public Likes( int pid, int uid, HashSet<Integer> userLikes ) {

        this.userID = uid;
        this.postID = pid;
        this.userLikes = userLikes;

    }

    public int getUserID() {
        return this.userID;
    }

    public int getPostID() {
        return this.postID;
    }

    public HashSet<Integer> getUserLikes() {
        return this.userLikes;
    }

    public int getLikeCount() {
        return this.userLikes.size();
    }

    public void incrementLike( int userID ) {
        this.userLikes.add(userID);
    }

    public void decrementLike( int userID ) {
        this.userLikes.remove(userID);
    }

    // No dislike option
    // Author of post can like their own post, we believe in self-confidence
}
