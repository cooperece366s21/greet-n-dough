package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Post implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int ID;       // ID for this post
    private int userID;
    private Integer imageID;    // Optional field to store an image ID
    private String contents;
    private final LocalDateTime timeCreated;

    // Constructor Chaining
    // Uses below constructor
    public Post( String contents, int postID, int userID ) {
        this( contents, postID, userID, null );
    }

    public Post( String contents, int postID, int userID, Integer imageID ) {

        this.ID = postID;
        this.userID = userID;
        this.contents = contents;
        this.timeCreated = LocalDateTime.now();
        this.imageID = imageID;

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

    public String getContents() {
        return this.contents;
    }

    public LocalDateTime getTime() {
        return this.timeCreated;
    }

}
