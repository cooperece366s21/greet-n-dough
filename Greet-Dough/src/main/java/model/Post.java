package model;

import java.time.LocalDateTime;
import database.Server;

public class Post {

    private final int ID;   // ID for this post
    private int userID;
    private int imageID;    // Optional field to store an image ID
    private String contents;
    private final LocalDateTime timeCreated;

    public Post( String contents, int userID ) {

        this.ID = Server.getUnusedPostID();
        this.userID = userID;
        this.imageID = -1;
        this.contents = contents;
        this.timeCreated = LocalDateTime.now();

    }

    public Post( String contents, int userID, int imageID ) {

        // Constructor Chaining
        // Uses above constructor
        this(contents, userID);
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
