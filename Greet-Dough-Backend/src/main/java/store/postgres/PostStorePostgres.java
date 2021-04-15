package store.postgres;

import model.User;
import model.Post;
import store.model.PostStore;
import utility.ResetDao;

import org.jdbi.v3.core.Jdbi;
import java.util.List;

public class PostStorePostgres implements PostStore {

    // For testing purposes
    public static void main( String[] args ) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        PostStorePostgres PostStorePostgres = new PostStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        User newUser = UserStorePostgres.addUser("Bill");

        // Test adding and retrieving a post
        Post newPost = PostStorePostgres.addPost( "first!", newUser.getID() );
        Post postAfterWrite = PostStorePostgres.getPost( newPost.getID() );
        System.out.println( postAfterWrite.getID() + " " + postAfterWrite.getUserID() + " " +
                            postAfterWrite.getImageID() + " " + postAfterWrite.getContents() + " " +
                            postAfterWrite.getTime() );

        // Test deleting the post
        PostStorePostgres.deletePost( postAfterWrite.getID() );

        // Make some more posts
        PostStorePostgres.addPost( "lol", newUser.getID() );
        PostStorePostgres.addPost( "haha very cool!", newUser.getID() );
        System.out.println( PostStorePostgres.makeFeed( newUser.getID() ) );

        // Test deleting the user
        //      Should delete cascade the posts
        UserStorePostgres.deleteUser( newUser.getID() );
        System.out.println( PostStorePostgres.makeFeed( newUser.getID() ));
        System.out.println( PostStorePostgres.getPost() );

    }

    private final Jdbi jdbi;

    public PostStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle(handle -> handle.attach(PostDao.class).deleteTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(PostDao.class).createTable());
    }

    @Override
    public Post getPost( int pid ) {
        return jdbi.withHandle( handle -> handle.attach(PostDao.class).getPost(pid) ).orElse(null);
    }

    // Returns all posts in the database
    //      Currently only used for testing
    public List<Post> getPost() {
        return jdbi.withHandle( handle -> handle.attach(PostDao.class).listPosts() );
    }

    @Override
    public List<Post> makeFeed( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(PostDao.class).getFeed(uid) );
    }

    @Override
    public boolean hasPost( int pid ) {
        return getPost(pid) != null;
    }

    @Override
    public Post addPost( String contents, int uid ) {
        return addPost( contents, uid, null );
    }

    @Override
    public Post addPost( String contents, int uid, Integer iid ) {

        int ID = jdbi.withHandle( handle -> handle.attach(PostDao.class).addPost( contents, uid, iid ) );
        return getPost(ID);

    }

    @Override
    public void deletePost( int ID ) {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).deletePost(ID) );
    }

}
