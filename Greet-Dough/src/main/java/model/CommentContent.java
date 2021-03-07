package model;
import java.io.Serializable;

public class CommentContent implements Serializable{

    private int userID;
    private String content;
    private int commentID;

    public CommentContent (int userID, String content, int commentID){
        this.userID = userID;
        this.content = content;
        this.commentID = commentID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    //Edited out due to not being used, can be used if we are going to implement edit comment option

    /*
    public void setContent(String content) {
        this.content = content;
    }

    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }
    */
}
