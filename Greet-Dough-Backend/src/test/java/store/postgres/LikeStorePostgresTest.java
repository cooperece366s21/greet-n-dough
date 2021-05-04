package store.postgres;

import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class LikeStorePostgresTest extends LikeStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    public LikeStorePostgresTest() {
        super(jdbi);
    }

    @Test
    void test() {

        UserStorePostgres userStorePostgres = new UserStorePostgres(jdbi);
        PostStorePostgres postStorePostgres = new PostStorePostgres(jdbi);
        LikeStorePostgres likeStorePostgres = new LikeStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        User newUser = userStorePostgres.addUser("Felipe");
        User tempUser = userStorePostgres.addUser("Jill");

        // Make two posts
        Post newPost = postStorePostgres.addPost( "My first post", "first!", newUser.getID() );
        Post secondPost = postStorePostgres.addPost( "My second post", "haha very cool!", newUser.getID() );

        // Check empty likes
        assert ( likeStorePostgres.getLikes( newPost.getID() ).getLikeCount() == 0 );

        // Like one post
        likeStorePostgres.addUserLike( newPost.getID(), newUser.getID() );
        likeStorePostgres.addUserLike( newPost.getID(), tempUser.getID() );
        HashSet<Integer> correctLikes = new HashSet<>();
        correctLikes.add( newUser.getID() );
        correctLikes.add( tempUser.getID() );

        // Check who liked the post
        assert ( likeStorePostgres.getLikes( newPost.getID() ).getUserLikes().equals(correctLikes) );
        assert ( likeStorePostgres.getLikes( secondPost.getID() ).getUserLikes().isEmpty() );

        // Count how many people liked a post
        assert ( likeStorePostgres.getLikes( newPost.getID() ).getLikeCount() == 2 );

        // A single user unlikes a post
        likeStorePostgres.deleteUserLike( newPost.getID(), newUser.getID() );
        correctLikes.remove( newUser.getID() );
        assert ( likeStorePostgres.getLikes( newPost.getID() ).getUserLikes().equals(correctLikes) );

        // Check if the user liked a specific post
        assertFalse ( likeStorePostgres.hasUserLike( newPost.getID(), newUser.getID() ) );

        // Delete post
        //      Should delete cascade all user likes
        postStorePostgres.deletePost( newPost.getID() );
        assert ( likeStorePostgres.getLikes( newPost.getID() ).getUserLikes().isEmpty() );

    }

}