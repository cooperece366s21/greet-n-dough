package model;
import java.io.Serializable;

public class Comment implements Serializable {

    private final int ID;
    private final int userID;
    private int pid;
    private Integer parentID;
    private String content;

    public Comment( String contents, int cid, int uid, int pid ) {
        this( contents, cid, uid, pid, 0 );
    }

    public Comment( String content, int cid, int uid, int pid, Integer parentID ) {

        this.ID = cid;
        this.userID = uid;
        this.pid = pid;
        this.content = content;
        this.parentID = parentID;

    }

    public Comment(String contents, int commentID, int uid) {
        this.ID = commentID;
        this.userID = uid;
        this.content = contents;
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
