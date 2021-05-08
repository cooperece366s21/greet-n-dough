package store.postgres;

import model.Comment;
import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentStorePostgresTest extends CommentStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    private static UserStorePostgres userStorePostgres;
    private static ImageStorePostgres imageStorePostgres;
    private static PostStorePostgres postStorePostgres;
    private static CommentStorePostgres commentStorePostgres;

    private static User newUser;
    private static Post newPost;

    public CommentStorePostgresTest() {
        super(jdbi);
    }

    @BeforeAll
    static void setUpAll() {

        // Delete all the databases (only use the relevant ones)
        ResetDao.deleteAll(jdbi);

        userStorePostgres = new UserStorePostgres(jdbi);
        imageStorePostgres = new ImageStorePostgres(jdbi);
        postStorePostgres = new PostStorePostgres(jdbi);
        commentStorePostgres = new CommentStorePostgres(jdbi);

    }

    @AfterAll
    static void tearDownAll() {
        ResetDao.reset(jdbi);
    }

    @BeforeEach
    void setUpEach() {

        // Delete the databases
        commentStorePostgres.delete();
        postStorePostgres.delete();
        imageStorePostgres.delete();
        userStorePostgres.delete();

        // Initialize the databases
        userStorePostgres.init();
        imageStorePostgres.init();
        postStorePostgres.init();
        commentStorePostgres.init();

        // Add a user and a post
        newUser = userStorePostgres.addUser("Dan Bim");
        newPost = postStorePostgres.addPost( "Testing", "first!", newUser.getID() );

    }

    @Test
    void testAddComment() {

        // Test adding some comments
        String contents1 = "haha croissant";
        String contents2 = "nawrrr";
        Comment parent1 = commentStorePostgres.addComment( contents1, newUser.getID(), newPost.getID() );
        Comment parent2 = commentStorePostgres.addComment( contents2, newUser.getID(), newPost.getID() );

        // Test the content of the comment
        assert ( parent1.getUserID() == newUser.getID() );
        assert ( parent2.getUserID() == newUser.getID() );
        assert ( parent1.getContents().equals(contents1) );
        assert ( parent2.getContents().equals(contents2) );
        assert ( commentStorePostgres.hasComment( parent1.getID() ) );
        assert ( commentStorePostgres.hasComment( parent2.getID() ) );

    }

    @Test
    void testGetComment() {

        // Test adding some comments
        String contents1 = "haha croissant";
        String contents2 = "nawrrr";
        Comment parent1 = commentStorePostgres.addComment( contents1, newUser.getID(), newPost.getID() );
        Comment parent2 = commentStorePostgres.addComment( contents2, newUser.getID(), newPost.getID() );

        // Test retrieving a parent comment
        Comment parentCommentAfterWrite_2 = commentStorePostgres.getComment( parent2.getID() );
        assert ( parentCommentAfterWrite_2.equals(parent2) );

        // Test retrieving the other parent comment
        Comment parentCommentAfterWrite_1 = commentStorePostgres.getComment( parent1.getID() );
        assert ( parentCommentAfterWrite_1.equals(parent1) );

    }

    @Test
    void testGetParents() {

        // Add some comments
        String contents1 = "haha croissant";
        String contents2 = "nawrrr";
        Comment parent1 = commentStorePostgres.addComment( contents1, newUser.getID(), newPost.getID() );
        Comment parent2 = commentStorePostgres.addComment( contents2, newUser.getID(), newPost.getID() );

        // Test retrieving a parent comment
        Comment parentCommentAfterWrite_2 = commentStorePostgres.getComment( parent2.getID() );
        assert ( parentCommentAfterWrite_2.equals(parent2) );

        // Test retrieving the other parent comment
        Comment parentCommentAfterWrite_1 = commentStorePostgres.getComment( parent1.getID() );
        assert ( parentCommentAfterWrite_1.equals(parent1) );

        // Test retrieving the list of parent comments under a post
        List<Comment> postParents = commentStorePostgres.getParents( newPost.getID() );
        assert ( postParents.size() == 2 );
        assert ( postParents.get(0).getContents().equals(contents1) );
        assert ( postParents.get(1).getContents().equals(contents2) );

    }

    @Test
    void testReplyComment() {

        // Add some comments
        String contents1 = "haha croissant";
        String contents2 = "nawrrr";
        Comment parent1 = commentStorePostgres.addComment( contents1, newUser.getID(), newPost.getID() );
        Comment parent2 = commentStorePostgres.addComment( contents2, newUser.getID(), newPost.getID() );

        // Reply to a comment
        String contents3 = "i love jlab";
        Comment reply = commentStorePostgres.addComment( contents3, newUser.getID(), newPost.getID(), parent2.getID() );

        // Test retrieving a reply comment
        Comment replyCommentAfterWrite = commentStorePostgres.getComment( reply.getID() );
        assert ( replyCommentAfterWrite.equals(reply) );

        // Test the content of the replies
        assert ( reply.getUserID() == newUser.getID() );
        assert ( reply.getContents().equals(contents3) );
        assert ( commentStorePostgres.hasComment( reply.getID() ) );

    }

    @Test
    void testGetReplies() {

        // Add some comments
        String contents1 = "haha croissant";
        String contents2 = "nawrrr";
        Comment parent1 = commentStorePostgres.addComment( contents1, newUser.getID(), newPost.getID() );
        Comment parent2 = commentStorePostgres.addComment( contents2, newUser.getID(), newPost.getID() );

        // Reply to a comment
        String contents3 = "i love jlab";
        Comment reply = commentStorePostgres.addComment( contents3, newUser.getID(), newPost.getID(), parent2.getID() );

        // Test retrieving a reply comment
        Comment replyCommentAfterWrite = commentStorePostgres.getComment( reply.getID() );
        assert ( replyCommentAfterWrite.equals(reply) );

        // Test retrieving the list of replies under a parent comment
        List<Comment> postReplies = commentStorePostgres.getReplies( parent2.getID() );
        assert ( postReplies.size() == 1 );
        assert ( postReplies.get(0).getContents().equals(contents3) );

    }

    @Test
    void testDeleteComment() {

        // Add some comments
        String contents1 = "haha croissant";
        String contents2 = "nawrrr";
        Comment parent1 = commentStorePostgres.addComment( contents1, newUser.getID(), newPost.getID() );
        Comment parent2 = commentStorePostgres.addComment( contents2, newUser.getID(), newPost.getID() );

        // Reply to a comment
        String contents3 = "i love jlab";
        Comment reply = commentStorePostgres.addComment( contents3, newUser.getID(), newPost.getID(), parent2.getID() );

        // Test deleting a comment
        commentStorePostgres.deleteComment( parent1.getID() );
        assertNull( commentStorePostgres.getComment( parent1.getID() ) );

    }

    @Test
    void testDeletePost() {

        // Add some comments
        String contents1 = "haha croissant";
        String contents2 = "nawrrr";
        Comment parent1 = commentStorePostgres.addComment( contents1, newUser.getID(), newPost.getID() );
        Comment parent2 = commentStorePostgres.addComment( contents2, newUser.getID(), newPost.getID() );

        // Reply to a comment
        String contents3 = "i love jlab";
        Comment reply = commentStorePostgres.addComment( contents3, newUser.getID(), newPost.getID(), parent2.getID() );

        // Test deleting the post
        //      Should delete cascade the comments
        postStorePostgres.deletePost( newPost.getID() );
        assert ( commentStorePostgres.getParents( newPost.getID() ).isEmpty() );
        assertNull( commentStorePostgres.getComment( parent2.getID() ) );
        assertNull( commentStorePostgres.getComment( reply.getID() ) );

    }

}