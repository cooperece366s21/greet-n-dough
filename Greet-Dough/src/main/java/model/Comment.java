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
    private String commentContent;
    private LocalDateTime commentTime;
    private HashMap<LocalDateTime, List> comment;

    public Comment(int postID, int userID) {

        this.postID = postID;
        this.userID = userID;
        this.commentContent = "";
        this.commentTime = LocalDateTime.now();
        this.comment = new HashMap<LocalDateTime, List> ();

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

    public void addComment(String commentContent, int currentUser) {
        this.commentTime = LocalDateTime.now();
        List<String> value = new ArrayList<String>();
        value.add(Integer.toString(currentUser));
        value.add(commentContent);
        this.comment.put(this.commentTime, value);
    }

    public HashMap<LocalDateTime, List> getComment() {
        return comment;
    }

    // Supports multiple comments, no directly reply
    // No comment likes
    // Author of post can comment on their own post
    // Comment is defined by time created (commentID), user who comments, and post the comment is under

    // Delete comment????????? Anything you post stays there

}
