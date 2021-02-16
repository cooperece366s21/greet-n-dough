import java.time.LocalDateTime;
import java.util.ArrayList;

public class Post {

    public String contents;
    public LocalDateTime timeCreated;
    public int likes;
    public ArrayList<String> comments;
    private int ID;         // ID for this post
    private int imageID;    // Optional field to store an image ID

    Post( String contents ) {

        this.ID = Server.getUnusedPostID();
        this.contents = contents;
        this.timeCreated = LocalDateTime.now();
        this.likes = 0;
        this.comments = new ArrayList<>();
        this.imageID = -1;

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

}
