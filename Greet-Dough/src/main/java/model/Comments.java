package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Comments implements Serializable{

    private final LocalDateTime commentTime;
    private int userID;
    private int postID;
    private int commentID;
    private String comment;

    public Comments(int postID, int userID, String comment){
        this.postID = postID;
        this.userID = userID;
        this.comment = comment;
        this.commentTime = LocalDateTime.now();
    }

    public int getCommentID(){ return this.commentID;}

    public int getUserID(){ return this.userID; }

    public int getPostID(){ return this.postID; }

    public LocalDateTime getCommentTime() {
        return this.commentTime;
    }

    // Supports multiple comments, no directly reply
    // No comment like


}
