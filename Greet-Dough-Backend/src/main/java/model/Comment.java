package model;
import java.io.Serializable;

public class Comment implements Serializable {

    private final int ID;
    private final int userID;
    private String content;

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

    public String getContent() {
        return this.content;
    }

    public void setContent( String content ) {
        this.content = content;
    }

}
