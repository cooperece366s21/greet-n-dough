package model;
import java.io.Serializable;

public class Comment implements Serializable {

    private final int ID;
    private final int userID;
    private int postID;
    private Integer parentID;
    private String contents;

    public Comment( String contents, int cid, int uid, int pid ) {
        this( contents, cid, uid, pid, null );
    }

    public Comment( String contents, int cid, int uid, int pid, Integer parentID ) {

        this.ID = cid;
        this.userID = uid;
        this.postID = pid;
        this.contents = contents;
        this.parentID = parentID;

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

    public String getContents() {
        return this.contents;
    }

    public void setContent( String contents ) {
        this.contents = contents;
    }

    public boolean hasParent() {
        return getParentID() != null;
    }

    public boolean equals( Comment tempComment ) {

        if (    this.getID() == tempComment.getID() &&
                this.getUserID() == tempComment.getUserID() &&
                this.getPostID() == tempComment.getPostID() &&
                this.getContents().equals( tempComment.getContents() ) ) {

            // Check the parent IDs (needed b/c of potential null pointer exception)
            if ( this.getParentID() != null && tempComment.getParentID() != null &&
                    this.getParentID().equals( tempComment.getParentID() ) ) {
                return true;
            } else return this.getParentID() == null && tempComment.getParentID() == null;

        }

        return false;

    }

}
