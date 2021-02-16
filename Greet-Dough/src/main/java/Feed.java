import java.io.Serializable;
import java.util.HashSet;

public class Feed implements Serializable {

    public HashSet<Integer> posts;

    // Constructor
    Feed() {
        this.posts = new HashSet<>();
    }

    public void addPost( String contents ) {

        Post newPost = new Post( contents );

        // Save the post to the feed
        this.posts.add( newPost.getID() );

        // Save the post to the server
        Server.addPost( newPost );

    }

    // Might need to delete references to the post?
    //      E.g. when sharing someone else's post
    // Can alternatively check if post still exists before displaying,
    //      and if deleted, show message that it was deleted
    // Returns true if successful;
    //         false otherwise.
    public boolean deletePost( int ID ) {

        if ( this.posts.contains(ID) ) {

            // Remove post from the feed
            this.posts.remove(ID);

            // Remove post from the server
            // Should always be true at this point
            assert Server.removePost(ID);

            return true;

        } else {
            return false;
        }

    }

    public HashSet<String> retrieveContents() {

        HashSet<String> feedContents = new HashSet<>();

        for ( int ID : this.posts ) {
            feedContents.add( Server.getPost(ID).contents );
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
