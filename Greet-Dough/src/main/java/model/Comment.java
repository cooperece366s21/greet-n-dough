package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Comment implements Serializable {

    private final int ID;
    private int userID;
    private int postID;
    private String comment;
    private final LocalDateTime commentTime;

    public Comment( int ID, int postID, int userID, String comment ) {

        this.ID = ID;
        this.postID = postID;
        this.userID = userID;
        this.comment = comment;
        this.commentTime = LocalDateTime.now();

    }

    public int getCommentID() {
        return this.ID;
    }

    public int getUserID() {
        return this.userID;
    }

    public int getPostID() {
        return this.postID;
    }

    public LocalDateTime getCommentTime() {
        return this.commentTime;
    }

    // Supports multiple comments, no directly reply
    // No comment like


}
