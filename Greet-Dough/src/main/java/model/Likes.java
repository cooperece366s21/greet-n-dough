package model;

import java.io.Serializable;
import java.util.*;

public class Likes implements Serializable {
    private int likeCount;
    private int userID;
    private int postID;
    private List<Integer> userLikes;

    public Likes(int postID, int userID){
        this.postID = postID;
        this.userID = userID;
    }

    // Check list of users, if user already liked
    public boolean checkID(){ return this.userLikes.contains(this.userID); }

    // If user did not like, add 1 to the like count
    public int addLike(){ return this.likeCount++;}

    // From checkID() if false append userID to list

    // From checkID() if true
        // removes like by decrementing likeCount
        // deletes userID from userLikes list


    // No dislike option

    // If user exists
}
