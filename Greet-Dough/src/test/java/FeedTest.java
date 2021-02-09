import org.junit.jupiter.api.Test;

import java.util.HashSet;

class FeedTest extends Feed {

    @Test
    void test() {

        // New feed should have no posts
        Feed myFeed = new Feed();
        assert myFeed.retrieveContents().size() == 0;

        // Add a single post
        String myContents = "Testing 1,2,3";
        myFeed.addPost( myContents );
        HashSet<String> myPosts = myFeed.retrieveContents();

        // Ensure number of posts is 1 and the post contains the correct information
        assert myPosts.size() == 1;
        assert myPosts.contains( myContents );

        // REMOVE A POST

    }

}