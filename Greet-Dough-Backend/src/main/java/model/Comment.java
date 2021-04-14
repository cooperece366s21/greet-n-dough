package model;
import java.io.Serializable;

public class Comment implements Serializable {

    private final int ID;
    private final int userID;
    private Integer parentID;
    private String content;

    public Comment( String content, int cid, int uid ) {
        this( content, cid, uid, null );
    }

    public Comment( String content, int cid, int uid, Integer parentID ) {

        this.ID = cid;
        this.userID = uid;
        this.content = content;
        this.parentID = parentID;

    }

    public int getID() {
        return this.ID;
    }

    public int getUserID() {
        return this.userID;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent( String content ) {
        this.content = content;
    }

}
