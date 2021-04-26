package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Post implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int ID;       // ID for this post
    private int userID;
    private Integer imageID;    // Optional field to store an image ID
    private String title;
    private String contents;
    private final LocalDateTime timeCreated;

    public Post( String title, String contents, int pid, int uid ) {
        this( title, contents, pid, uid, null );
    }

    public Post( String title, String contents, int pid, int uid, Integer iid ) {
        this( title, contents, pid, uid, iid, LocalDateTime.now() );
    }

    public Post( String title, String contents, int pid, int uid, Integer iid, LocalDateTime timeCreated ) {

        this.ID = pid;
        this.userID = uid;
        this.imageID = iid;
        this.title = title;
        this.contents = contents;
        this.timeCreated = timeCreated;

    }

    public int getID() {
        return this.ID;
    }

    public int getUserID() {
        return this.userID;
    }

    public Integer getImageID() {
        return this.imageID;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContents() {
        return this.contents;
    }

    public LocalDateTime getTime() {
        return this.timeCreated;
    }

    public void setContents( String contents ) {
        this.contents = contents;
    }

    public boolean equals( Post tempPost ) {

        if (    this.getID() == tempPost.getID() &&
                this.getUserID() == tempPost.getUserID() &&
                this.getTitle().equals( tempPost.getTitle() ) &&
                this.getContents().equals( tempPost.getContents() ) &&
                this.getTime().equals( tempPost.getTime() ) ) {

            // Check the parent IDs (needed b/c of potential null pointer exception)
            if ( this.getImageID() != null && tempPost.getImageID() != null &&
                    this.getImageID().equals( tempPost.getImageID() ) ) {
                return true;
            } else return this.getImageID() == null && tempPost.getImageID() == null;

        }

        return false;

    }

}
