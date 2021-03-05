package model;

import java.io.Serializable;
import java.util.*;

public class Likes implements Serializable {

    private int userID;
    private int postID;
    private int likeCount;
    private ArrayList<Integer> userLikes;

    public Likes( int postID, int userID , ArrayList<Integer> userLikes) {

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

    public ArrayList<Integer> getUserLikes() {
        return userLikes;
    }

    public int getLikeCount() {
        return this.likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setUserLikes(ArrayList<Integer> userLikes) {
        this.userLikes = userLikes;
    }

    // No dislike option
    // Author of post can like their own post, we believe in self-confidence
}
