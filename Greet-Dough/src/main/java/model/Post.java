package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Post implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int ID;   // ID for this post
    private int userID;
    private int imageID;    // Optional field to store an image ID
    private String contents;
    private final LocalDateTime timeCreated;

    public Post( String contents, int postID, int userID ) {

        this.ID = postID;
        this.userID = userID;
        this.imageID = -1;
        this.contents = contents;
        this.timeCreated = LocalDateTime.now();

    }

    public Post( String contents, int postID, int userID, int imageID ) {

        // Constructor Chaining
        // Uses above constructor
        this(contents, postID, userID );
        this.imageID = imageID;

    }

    public int getID() {
        return this.ID;
    }

    public int getUserID() {
        return this.userID;
    }

    public int getImageID() {
        return this.imageID;
    }

    public String getContents() {
        return this.contents;
    }

    public LocalDateTime getTime() {
        return this.timeCreated;
    }

}
