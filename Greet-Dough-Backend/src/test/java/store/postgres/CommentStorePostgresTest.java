package store.postgres;

import model.Comment;
import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import utility.ResetDao;

import java.util.LinkedList;

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
        User dan = userStorePostgres.addUser("Dan Bim");
        Post post1 = postStorePostgres.addPost( "Testing", "first!", dan.getID() );

        // Test adding some comments
        String contents1 = "haha croissant";
        String contents2 = "nawrrr";
        Comment parent1 = commentStorePostgres.addComment( contents1, dan.getID(), post1.getID() );
        Comment parent2 = commentStorePostgres.addComment( contents2, dan.getID(), post1.getID() );

        // Test retrieving a parent comment
        Comment parentCommentAfterWrite = commentStorePostgres.getComment( parent2.getID() );
        assert ( parentCommentAfterWrite.equals(parent2) );

        // Reply to a comment
        String contents3 = "i love jlab";
        Comment reply = commentStorePostgres.addComment( contents3, dan.getID(), post1.getID(), parent2.getID() );

        // Test retrieving a reply comment
        Comment replyCommentAfterWrite = commentStorePostgres.getComment( reply.getID() );
        assert ( replyCommentAfterWrite.equals( reply ) );

        // Test retrieving the list of parent comments under a post
        LinkedList<Comment> postParents = commentStorePostgres.getParents( post1.getID() );
        assert ( postParents.size() == 2 );
        assert ( postParents.get(0).getContents().equals(contents1) );
        assert ( postParents.get(1).getContents().equals(contents2) );

        // Test retrieving the list of replies under a parent comment
        LinkedList<Comment> postReplies = commentStorePostgres.getReplies( parent2.getID() );
        assert ( postReplies.size() == 1 );
        assert ( postReplies.getFirst().getContents().equals(contents3) );

        // Test deleting a comment
        commentStorePostgres.deleteComment( parent1.getID() );
        assertNull ( commentStorePostgres.getComment( parent1.getID() ) );

        // Test deleting the post
        //      Should delete cascade the comments
        postStorePostgres.deletePost( post1.getID() );
        assert ( commentStorePostgres.getParents( post1.getID() ).isEmpty() );
        assertNull ( commentStorePostgres.getComment( parent2.getID() ) );
        assertNull ( commentStorePostgres.getComment( reply.getID() ) );

    }

}