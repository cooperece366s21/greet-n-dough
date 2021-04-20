package store.postgres;

import model.User;
import model.Post;
import model.Comment;
import store.model.CommentStore;
import utility.ResetDao;

import org.jdbi.v3.core.Jdbi;

import java.util.LinkedList;

public class CommentStorePostgres implements CommentStore {

    // For testing purposes
    public static void main( String[] args ) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        PostStorePostgres PostStorePostgres = new PostStorePostgres(jdbi);
        CommentStorePostgres CommentStorePostgres = new CommentStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        User dan = UserStorePostgres.addUser("Dan Bim");

        // Add a post
        Post post1 = PostStorePostgres.addPost( "Testing", "first!", dan.getID() );

        // Create another post
        Post post2 = PostStorePostgres.addPost( "I'm new here", "lol", dan.getID() );

        // Create a comment (can't delete individually)
        System.out.println(PostStorePostgres.hasPost(post1.getID()));
        Comment comment1 = CommentStorePostgres.addComment("haha croissant", dan.getID(), post1.getID() );
        Comment comment2 = CommentStorePostgres.addComment("nawrrr", dan.getID(), post1.getID());

        // Reply to a comment
        int cid = comment2.getID();
        System.out.println(CommentStorePostgres.hasComment(cid));
        Comment parentTemp = CommentStorePostgres.addComment("i love jlab", dan.getID(), post1.getID(), comment2.getID());

        // Reply to a reply, we want to force hasComment to be false
        int cidTemp = parentTemp.getID();
        System.out.println(CommentStorePostgres.hasComment(cidTemp));

        // Get the list of parent comments under a post
        LinkedList<Comment> yeetPostParents = CommentStorePostgres.getParents(post1.getID());
        yeetPostParents.forEach( x -> System.out.println( x.getContents() ) );

        // Get the list of replies under a parent comment
        LinkedList<Comment> yeetPostReplies = CommentStorePostgres.getReplies( comment2.getID() );
        yeetPostReplies.forEach( x -> System.out.println( x.getContents() ) );

        // Test deleting the post
        //      Should delete cascade the comments
        PostStorePostgres.deletePost( post1.getID() );
        System.out.println( CommentStorePostgres.getParents( post1.getID() ) );
        System.out.println( CommentStorePostgres.getComment( comment1.getID() ) );

    }

    private final Jdbi jdbi;

    public CommentStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle(handle -> handle.attach(CommentDao.class).deleteTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(CommentDao.class).createTable());
    }

    @Override
    public Comment getComment( int cid ) {
        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).getComment(cid) );
    }

    @Override
    public LinkedList<Comment> getReplies( int parentID ) {
        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).getReplies(parentID) );
    }

    @Override
    public LinkedList<Comment> getParents( int pid ) {
        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).getParents(pid) );
    }

    @Override
    public Comment addComment( String contents, int uid, int pid, Integer parent_id ) {

        int ID = jdbi.withHandle( handle -> handle.attach(CommentDao.class).addComment( contents, uid, pid, parent_id ) );
        return getComment(ID);

    }

    @Override
    public Comment addComment( String contents, int uid, int pid ) {
        return addComment( contents, uid, pid, null );
    }

    @Override
    public boolean hasComment( int cid ) {
        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).hasComment(cid) );
    }

    @Override
    public boolean isParent( int cid ) {
        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).isParent(cid) );
    }

}


