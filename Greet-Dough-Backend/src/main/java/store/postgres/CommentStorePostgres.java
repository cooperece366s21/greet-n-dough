package store.postgres;

import model.Comment;
import model.Post;
import model.User;
import store.model.StoreWithID;
import org.jdbi.v3.core.Jdbi;
import store.model.CommentStore;

import java.util.List;

public class CommentStorePostgres implements CommentStore {

    // Connection test function
    public static void main( String[] args){
        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        PostStorePostgres PostStorePostgres = new PostStorePostgres(jdbi);
        CommentStorePostgres CommentStorePostgres = new CommentStorePostgres(jdbi);

        PostStorePostgres.reset();
        CommentStorePostgres.reset();
        CommentStorePostgres.init();
        PostStorePostgres.init();

        User yeet = UserStorePostgres.addUser("yeet");

        // Add a post
        Post yeetPost = PostStorePostgres.addPost( "first!", yeet.getID() );
        Post postAfterWrite = PostStorePostgres.getPost( yeetPost.getID() );
        System.out.println( postAfterWrite.getID() + " " + postAfterWrite.getUserID() +
                " " + postAfterWrite.getImageID() + " " + postAfterWrite.getContents() );

        // Delete post
        PostStorePostgres.deletePost( postAfterWrite.getID() );

        // Create another post
        PostStorePostgres.addPost( "lol", yeet.getID() );

        // Create a comment (can't delete)
        // addComment
        // int ID = jdbi.withHandle()
        // return getComment(ID);


        // Reply to a comment
        // given a comment id, append current userid to list
        // return the new list of id

        // Delete user

    }

    private final Jdbi jdbi;

    public CommentStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void reset() {
        jdbi.useHandle(handle -> handle.attach(CommentDao.class).resetTable());
    };

    public void init() {
        jdbi.useHandle(handle -> handle.attach(CommentDao.class).resetTable());
    };

    @Override
    public Comment getComment( int ID ) {
        return null;
    }

    // return a list

    @Override
    public void addComment( Comment newComment ) {

    }

    @Override
    public Comment addComment( String contents, int uid ) {
        return null;
    }
}
