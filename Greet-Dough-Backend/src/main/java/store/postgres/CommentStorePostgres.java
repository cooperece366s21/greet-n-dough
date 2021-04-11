package store.postgres;

import model.Comment;
import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import store.model.CommentStore;
import utility.ResetDao;

public class CommentStorePostgres implements CommentStore {

    // Connection test function
    public static void main( String[] args){
        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        PostStorePostgres PostStorePostgres = new PostStorePostgres(jdbi);
        CommentStorePostgres CommentStorePostgres = new CommentStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

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

        // Reply to a comment

        // Get the list of parent comments under a post

        // Get the list of replies under a parent comment

        // Delete user, delete the table

    }

    // given a postid
    // return with hierarchy
    // single depth replies

    // canComment() check if post_id exists

    // on frontend you choose post and then choose a comment to reply to
    // requires boolean canReply()
    // checks if the given comment_id exists
    // true
    // feed the parentID, the comment i'm trying to reply to
    // insertComment(uid, content, parentID)
    // insert uid, content, parentID  (commentID auto generated key)
    // false
    // return status error, comment doesnt exist

    // regular comment
    // insertComment(uid, content, null)

    // Columns: commentID, uid, content, parentID
    // commentID -> another commentID (parentID)


    private final Jdbi jdbi;

    public CommentStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle(handle -> handle.attach(CommentDao.class).deleteTable());
    };

    public void init() {
        jdbi.useHandle(handle -> handle.attach(CommentDao.class).createTable());
    };

    @Override
    public Comment getComment( int ID ) {
        return null;
    }

    @Override
    public void addComment( Comment newComment ) {
    }

    @Override
    public Comment addComment( String contents, int uid) {

        return null;

    }

    @Override
    public boolean canComment(int post_id) {

        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).canComment(post_id) );

    }

    @Override
    public Comment insertComment(String contents, int uid, Integer parent_id) {

        int ID = jdbi.withHandle( handle -> handle.attach(CommentDao.class).insertComment(uid, contents, parent_id) );
        return getComment(ID);

    }

    @Override
    public Comment insertComment(String contents, int uid) {

        return insertComment( contents, uid, null );

    }

    @Override
    public boolean canReply(int comment_id) {

        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).canReply(comment_id) );

    }

    @Override
    public Comment getReplies(int parent_id) {
        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).getReplies(parent_id));
    }

    /*
    identify a post
        make a list of every parent_id
            // array_agg(parent_id) where post_id = (:post_id)
        in handler loop through each parent_id
            call getReplies

    comments section:
        parent 1
            replies
            replies
            replies
        parent 2
            replies
        parent 3
        parent 4
            replies
    */

}


