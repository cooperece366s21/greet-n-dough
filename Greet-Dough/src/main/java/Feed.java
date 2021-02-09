import java.util.HashSet;

public class Feed {

    public HashSet<Post> posts;

    // Constructor
    Feed() {
        this.posts = new HashSet<>();
    }

    public void addPost( String contents ) {
        this.posts.add( new Post( contents ) );
    }

    // NEED TO TEST THIS
    // Should set number of references to 0
    // Only works if no other variables reference the post
    // Might be ??
    // Also think about using hashmap to identify posts
    public void deletePost( Post target ) {
        target = null;
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
