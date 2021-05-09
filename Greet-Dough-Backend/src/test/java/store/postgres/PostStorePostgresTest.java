package store.postgres;

import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostStorePostgresTest extends PostStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    private static UserStorePostgres userStorePostgres;
    private static ImageStorePostgres imageStorePostgres;
    private static PostStorePostgres postStorePostgres;

    private static User newUser;

    public PostStorePostgresTest() {
        super(jdbi);
    }

    @BeforeAll
    static void setUpAll() {

        // Delete all the databases (only use the relevant ones)
        ResetDao.deleteAll(jdbi);

        userStorePostgres = new UserStorePostgres(jdbi);
        imageStorePostgres = new ImageStorePostgres(jdbi);
        postStorePostgres = new PostStorePostgres(jdbi);

    }

    @AfterAll
    static void tearDownAll() {
        ResetDao.reset(jdbi);
    }

    @BeforeEach
    void setUpEach() {

        // Delete the databases
        postStorePostgres.delete();
        imageStorePostgres.delete();
        userStorePostgres.delete();

        // Initialize the databases
        userStorePostgres.init();
        imageStorePostgres.init();
        postStorePostgres.init();

        // Add a user
        newUser = userStorePostgres.addUser("Bill");

    }

    @Test
    void testAddPost() {

        // Test adding and retrieving a post
        Post newPost = postStorePostgres.addPost( "My first post!", "testing123", newUser.getID() );

        Post postAfterWrite = postStorePostgres.getPost( newPost.getID() );
        assert ( postAfterWrite.equals(newPost) );

    }

    @Test
    void testDeletePost() {

        // Add and retrieve a post
        Post newPost = postStorePostgres.addPost( "My first post!", "testing123", newUser.getID() );
        Post postAfterWrite = postStorePostgres.getPost( newPost.getID() );

        // Test deleting the post
        postStorePostgres.deletePost( postAfterWrite.getID() );
        assertNull( postStorePostgres.getPost( newPost.getID() ) );

    }

    @Test
    void testGetFeed() {

        // Add and retrieve a post
        Post newPost = postStorePostgres.addPost( "My first post!", "testing123", newUser.getID() );
        Post postAfterWrite = postStorePostgres.getPost( newPost.getID() );
        assert ( postAfterWrite.equals(newPost) );

        // Delete the post
        postStorePostgres.deletePost( postAfterWrite.getID() );

        // Make some more posts
        Post poopPost1 = postStorePostgres.addPost( "Feeling well today", "cuz it's a WELLness day hahahahhah", newUser.getID() );
        Post poopPost2 = postStorePostgres.addPost( "You can graduate with a bachelors and masters with 134 credits at other schools",
                "haha very cool!", newUser.getID() );
        List<Post> feed = postStorePostgres.makeFeed( newUser.getID() );
        assert ( feed.size() == 2 );
        assert ( ( feed.get(0).equals(poopPost1) && feed.get(1).equals(poopPost2) ) ||
                ( feed.get(0).equals(poopPost2) && feed.get(1).equals(poopPost1) ) );

    }

    @Test
    void testEditPost() {

        // Make a post
        Post poopPost1 = postStorePostgres.addPost( "Feeling well today", "cuz it's a WELLness day hahahahhah", newUser.getID() );

        // Test changing a post
        String newTitle = "Not feeling well today";
        String newContents = "cuz it's still a wellness day";
        postStorePostgres.changeTitle( poopPost1.getID(), newTitle );
        postStorePostgres.changeContents( poopPost1.getID(), newContents );
        assert ( postStorePostgres.getPost( poopPost1.getID() ).getTitle().equals( newTitle ) );
        assert ( postStorePostgres.getPost( poopPost1.getID() ).getContents().equals( newContents ) );

    }

    @Test
    void testDeleteUser() {

        // Test adding and retrieving a post
        Post newPost = postStorePostgres.addPost( "My first post!", "testing123", newUser.getID() );

        // Test deleting the user
        //      Should delete cascade the posts
        userStorePostgres.deleteUser( newUser.getID() );
        assert ( postStorePostgres.makeFeed( newUser.getID() ).isEmpty() );
        assert ( postStorePostgres.getAllPosts().isEmpty() );

    }
}
