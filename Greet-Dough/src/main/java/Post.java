import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Post implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int ID;   // ID for this post
    private int imageID;    // Optional field to store an image ID
    private String contents;
    private final LocalDateTime timeCreated;
    private int likes;
    private ArrayList<String> comments;

    Post( String contents ) {

        this.ID = Server.getUnusedPostID();
        this.imageID = -1;
        this.contents = contents;
        this.timeCreated = LocalDateTime.now();
        this.likes = 0;
        this.comments = new ArrayList<>();

    }

    Post( String contents, int imageID ) {

        // Constructor Chaining
        // Uses above constructor
        this(contents);
        this.imageID = imageID;

    }

    public int getID() {
        return this.ID;
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

    public int getLikes() {
        return this.likes;
    }

    public ArrayList<String> getComments() {
        return this.comments;
    }

}
