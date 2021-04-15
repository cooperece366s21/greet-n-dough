package model;
import java.io.Serializable;

public class Comment implements Serializable {

    private final int ID;
    private final int userID;
    private int postID;
    private Integer parentID;
    private String content;

    public Comment( String content, int cid, int uid, int pid ) {
        this( content, cid, uid, pid, null );
    }

    public Comment( String content, int cid, int uid, int pid, Integer parentID ) {

        this.ID = cid;
        this.userID = uid;
        this.postID = pid;
        this.content = content;
        this.parentID = parentID;

    }

    public Comment( String content, int cid, int uid ) {

        this.ID = cid;
        this.userID = uid;
        this.content = content;

    }

    public int getID() {
        return this.ID;
    }

    public int getUserID() {
        return this.userID;
    }

    public int getPostID() {
        return this.postID;
    }

    public Integer getParentID() {
        return this.parentID;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent( String content ) {
        this.content = content;
    }

}
