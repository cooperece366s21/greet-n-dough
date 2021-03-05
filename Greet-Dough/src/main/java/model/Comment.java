package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Comment implements Serializable {

    // private final int ID;
    private int userID;
    private int postID;

    private HashMap<LocalDateTime, List> comment;

    public Comment(int postID, int userID) {

        this.postID = postID;
        this.userID = userID;
        this.comment = new HashMap<LocalDateTime, List> ();

    }

    public int getUserID() {
        return this.userID;
    }

    public int getPostID() {
        return this.postID;
    }

    public HashMap<LocalDateTime, List> getComment() {
        return comment;
    }

    public void setComment(HashMap<LocalDateTime, List> comment) {
        this.comment = comment;
    }

    // Supports multiple comments, no directly reply
    // No comment likes
    // Author of post can comment on their own post
    // Comment is defined by time created (commentID), user who comments, and post the comment is under

    // Delete comment????????? Anything you post stays there

}
