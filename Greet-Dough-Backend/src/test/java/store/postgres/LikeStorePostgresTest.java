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

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class LikeStorePostgresTest extends LikeStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    private static UserStorePostgres userStorePostgres;
    private static ImageStorePostgres imageStorePostgres;
    private static PostStorePostgres postStorePostgres;
    private static LikeStorePostgres likeStorePostgres;

    private static User newUser;
    private static User tempUser;
    private static Post newPost;
    private static Post secondPost;

    public LikeStorePostgresTest() {
        super(jdbi);
    }

    @BeforeAll
    static void setUpAll() {

        // Delete all the databases (only use the relevant ones)
        ResetDao.deleteAll(jdbi);

        userStorePostgres = new UserStorePostgres(jdbi);
        imageStorePostgres = new ImageStorePostgres(jdbi);
        postStorePostgres = new PostStorePostgres(jdbi);
        likeStorePostgres = new LikeStorePostgres(jdbi);

    }

    @AfterAll
    static void tearDownAll() {
        ResetDao.reset(jdbi);
    }

    @BeforeEach
    void setUpEach() {

        // Delete the databases
        likeStorePostgres.delete();
        postStorePostgres.delete();
        imageStorePostgres.delete();
        userStorePostgres.delete();

        // Initialize the databases
        userStorePostgres.init();
        imageStorePostgres.init();
        postStorePostgres.init();
        likeStorePostgres.init();

        // Add users
        newUser = userStorePostgres.addUser("Felipe");
        tempUser = userStorePostgres.addUser("Jill");

        // Make two posts
        newPost = postStorePostgres.addPost( "My first post", "first!", newUser.getID() );
        secondPost = postStorePostgres.addPost( "My second post", "haha very cool!", newUser.getID() );

        // Check empty likes
        assert ( likeStorePostgres.getLikes( newPost.getID() ).getLikeCount() == 0 );

    }

    @Test
    void testAddUserLike() {

        // Like one post
        likeStorePostgres.addUserLike( newPost.getID(), newUser.getID() );
        likeStorePostgres.addUserLike( newPost.getID(), tempUser.getID() );

        // Count how many people liked a post
        assert ( likeStorePostgres.getLikes( newPost.getID() ).getLikeCount() == 2 );

    }

    @Test
    void testGetUserLikes() {

        // Like one post
        likeStorePostgres.addUserLike( newPost.getID(), newUser.getID() );
        likeStorePostgres.addUserLike( newPost.getID(), tempUser.getID() );

        // Create expected output
        HashSet<Integer> correctLikes = new HashSet<>();
        correctLikes.add( newUser.getID() );
        correctLikes.add( tempUser.getID() );

        // Check who liked the post
        assert ( likeStorePostgres.getLikes( newPost.getID() ).getUserLikes().equals(correctLikes) );
        assert ( likeStorePostgres.getLikes( secondPost.getID() ).getUserLikes().isEmpty() );

    }

    @Test
    void testDeleteUserLike() {

        // Like one post
        likeStorePostgres.addUserLike( newPost.getID(), newUser.getID() );
        likeStorePostgres.addUserLike( newPost.getID(), tempUser.getID() );

        // Create expected output
        HashSet<Integer> correctLikes = new HashSet<>();
        correctLikes.add( newUser.getID() );
        correctLikes.add( tempUser.getID() );

        // A single user unlikes a post
        likeStorePostgres.deleteUserLike( newPost.getID(), newUser.getID() );
        correctLikes.remove( newUser.getID() );
        assert ( likeStorePostgres.getLikes( newPost.getID() ).getUserLikes().equals(correctLikes) );

        // Check if the user liked a specific post
        assertFalse ( likeStorePostgres.hasUserLike( newPost.getID(), newUser.getID() ) );

    }

    @Test
    void testDeletePost() {

        // Like one post
        likeStorePostgres.addUserLike( newPost.getID(), newUser.getID() );
        likeStorePostgres.addUserLike( newPost.getID(), tempUser.getID() );

        // Delete post
        //      Should delete cascade all user likes
        postStorePostgres.deletePost( newPost.getID() );
        assert ( likeStorePostgres.getLikes( newPost.getID() ).getUserLikes().isEmpty() );

    }

}