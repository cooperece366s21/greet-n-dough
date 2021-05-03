package store.postgres;

import model.Comment;
import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import utility.ResetDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentStorePostgresTest extends CommentStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    public CommentStorePostgresTest() {
        super(jdbi);
    }

    @Test
    void test() {

        UserStorePostgres userStorePostgres = new UserStorePostgres(jdbi);
        PostStorePostgres postStorePostgres = new PostStorePostgres(jdbi);
        CommentStorePostgres commentStorePostgres = new CommentStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        // Add a user and a post
        User newUser = userStorePostgres.addUser("Dan Bim");
        Post newPost = postStorePostgres.addPost( "Testing", "first!", newUser.getID() );

        // Test adding some comments
        String contents1 = "haha croissant";
        String contents2 = "nawrrr";
        Comment parent1 = commentStorePostgres.addComment( contents1, newUser.getID(), newPost.getID() );
        Comment parent2 = commentStorePostgres.addComment( contents2, newUser.getID(), newPost.getID() );

        // Test retrieving a parent comment
        Comment parentCommentAfterWrite = commentStorePostgres.getComment( parent2.getID() );
        assert ( parentCommentAfterWrite.equals(parent2) );

        // Reply to a comment
        String contents3 = "i love jlab";
        Comment reply = commentStorePostgres.addComment( contents3, newUser.getID(), newPost.getID(), parent2.getID() );

        // Test retrieving a reply comment
        Comment replyCommentAfterWrite = commentStorePostgres.getComment( reply.getID() );
        assert ( replyCommentAfterWrite.equals(reply) );

        // Test retrieving the list of parent comments under a post
        List<Comment> postParents = commentStorePostgres.getParents( newPost.getID() );
        assert ( postParents.size() == 2 );
        assert ( postParents.get(0).getContents().equals(contents1) );
        assert ( postParents.get(1).getContents().equals(contents2) );

        // Test retrieving the list of replies under a parent comment
        List<Comment> postReplies = commentStorePostgres.getReplies( parent2.getID() );
        assert ( postReplies.size() == 1 );
        assert ( postReplies.get(0).getContents().equals(contents3) );

        // Test deleting a comment
        commentStorePostgres.deleteComment( parent1.getID() );
        assertNull( commentStorePostgres.getComment( parent1.getID() ) );

        // Test deleting the post
        //      Should delete cascade the comments
        postStorePostgres.deletePost( newPost.getID() );
        assert ( commentStorePostgres.getParents( newPost.getID() ).isEmpty() );
        assertNull( commentStorePostgres.getComment( parent2.getID() ) );
        assertNull( commentStorePostgres.getComment( reply.getID() ) );

    }

}