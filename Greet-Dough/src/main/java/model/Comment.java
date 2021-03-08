package model;
import java.io.Serializable;

public class Comment implements Serializable {

    private final int ID;
    private final int userID;
    private String content;

    public Comment( int userID, String content, int commentID ) {

        this.ID = commentID;
        this.userID = userID;
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

    //Edited out due to not being used, can be used if we are going to implement edit comment option

    /*
    public void setContent(String content) {
        this.content = content;
    }
    */
}
