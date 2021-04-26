package store.postgres;

import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import utility.ResetDao;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class PostStorePostgresTest extends PostStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    public PostStorePostgresTest() {
        super(jdbi);
    }

    @Test
    void test() {

        UserStorePostgres userStorePostgres = new UserStorePostgres(jdbi);
        PostStorePostgres postStorePostgres = new PostStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        User newUser = userStorePostgres.addUser("Bill");

        // Test adding and retrieving a post
        Post newPost = postStorePostgres.addPost( "My first post!", "testing123", newUser.getID() );
        Post postAfterWrite = postStorePostgres.getPost( newPost.getID() );
        assert ( postAfterWrite.equals(newPost) );

        // Test deleting the post
        postStorePostgres.deletePost( postAfterWrite.getID() );
        assertNull ( postStorePostgres.getPost( newPost.getID() ) );

        // Make some more posts
        Post poopPost1 = postStorePostgres.addPost( "Feeling well today", "cuz it's a WELLness day hahahahhah", newUser.getID() );
        Post poopPost2 = postStorePostgres.addPost( "You can graduate with a bachelors and masters with 134 credits at other schools",
                "haha very cool!", newUser.getID() );
        LinkedList<Post> feed = postStorePostgres.makeFeed( newUser.getID() );
        assert ( feed.size() == 2 );
        assert ( feed.get(0).equals( poopPost1 ) );
        assert ( feed.get(1).equals( poopPost2 ) );

        // Test changing a post
        String newTitle = "Not feeling well today";
        String newContents = "cuz it's still a wellness day";
        postStorePostgres.changeTitle( poopPost1.getID(), newTitle );
        postStorePostgres.changeContents( poopPost1.getID(), newContents );
        assert ( postStorePostgres.getPost( poopPost1.getID() ).getTitle().equals( newTitle ) );
        assert ( postStorePostgres.getPost( poopPost1.getID() ).getContents().equals( newContents ) );

        // Test deleting the user
        //      Should delete cascade the posts
        userStorePostgres.deleteUser( newUser.getID() );
        assert ( postStorePostgres.makeFeed( newUser.getID() ).isEmpty() );
        assert ( postStorePostgres.getPost().isEmpty() );

    }

}