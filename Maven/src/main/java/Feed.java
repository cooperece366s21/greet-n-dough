import java.util.ArrayList;

public class Feed {

    public ArrayList<Post> posts;

    // Constructor
    Feed() {
        this.posts = new ArrayList<>();
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

    public void view() {

        for ( Post tempPost : this.posts ) {
            System.out.println( tempPost.contents );
        }

    }

}
