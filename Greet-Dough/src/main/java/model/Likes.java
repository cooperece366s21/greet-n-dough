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

    public int getLikeCount() {return this.likeCount; }

    // Check list of users, if user already liked
    public boolean checkID() {
        return this.userLikes.contains(this.userID);
    }

    // If user did not like, add 1 to the like count
    public int addLike() {
        return this.likeCount++;
    }

    // From checkID() if false append userID to list
    public void userLiked(int userID, int likeCount) {
        this.userLikes.add(likeCount - 1, userID);
    }

    // From checkID() if true
        // removes like by decrementing likeCount
        // deletes userID from userLikes list


    // No dislike option

    // If user exists
}
