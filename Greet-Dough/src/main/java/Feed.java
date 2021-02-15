import java.io.Serializable;
import java.util.HashSet;

public class Feed implements Serializable {

    public HashSet<Post> posts;

    // Constructor
    Feed() {
        this.posts = new HashSet<>();
    }

    public void addPost( String contents ) {
        this.posts.add( new Post( contents ) );
    }

    // Might need to delete references to the post?
    //      E.g. when sharing someone else's post
    // Can alternatively check if post still exists before displaying,
    //      and if deleted, show message that it was deleted
    public void deletePost( int ID ) {
        posts.remove(ID);
    }

    public HashSet<String> retrieveContents() {

        HashSet<String> feedContents = new HashSet<>();

        for ( Post tempPost : this.posts ) {
            feedContents.add( tempPost.contents );
        }

        return feedContents;

    }

    public void display() {

        HashSet<String> feedContents = this.retrieveContents();

        for ( String postContents : feedContents ) {
            System.out.println( postContents );
        }

    }

}
