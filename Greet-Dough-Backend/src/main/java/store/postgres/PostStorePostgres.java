package store.postgres;

import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import store.model.PostStore;

import java.util.List;

public class PostStorePostgres implements PostStore {

    // For testing purposes
    public static void main( String[] args ) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        PostStorePostgres PostStorePostgres = new PostStorePostgres(jdbi);

        // Used to DROP and CREATE the posts table
        PostStorePostgres.reset();
        PostStorePostgres.init();

        User yeet = UserStorePostgres.addUser("yeet");

        // Test adding and retrieving a post
        Post yeetPost = PostStorePostgres.addPost( "first!", yeet.getID() );
        Post postAfterWrite = PostStorePostgres.getPost( yeetPost.getID() );
        System.out.println( postAfterWrite.getID() + " " + postAfterWrite.getUserID() +
                            " " + postAfterWrite.getImageID() + " " + postAfterWrite.getContents() );

        // Test deleting the post
        PostStorePostgres.deletePost( postAfterWrite.getID() );

        // Make some more posts
        PostStorePostgres.addPost( "lol", yeet.getID() );
        PostStorePostgres.addPost( "haha very cool!", yeet.getID() );
        System.out.println( PostStorePostgres.makeFeed( yeet.getID() ) );

        // Test deleting the user
        //      Should delete cascade the posts
        UserStorePostgres.deleteUser(yeet.getID());
        System.out.println( PostStorePostgres.makeFeed( yeet.getID() ) );
        System.out.println( PostStorePostgres.getPost() );

    }

    private final Jdbi jdbi;

    public PostStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void reset() {
        jdbi.useHandle(handle -> handle.attach(PostDao.class).resetTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(PostDao.class).createTable());
    }

    @Override
    public Post getPost( int ID ) {
        return jdbi.withHandle( handle -> handle.attach(PostDao.class).getPost(ID) );
    }

    public List<Post> getPost() {
        return jdbi.withHandle( handle -> handle.attach(PostDao.class).listPosts() );
    }

    @Override
    public List<Post> makeFeed( int userID ) {
        return jdbi.withHandle( handle -> handle.attach(PostDao.class).getFeed(userID) );
    }

    @Override
    public boolean hasPost( int ID ) {
        return jdbi.withHandle( handle -> handle.attach(PostDao.class).containsPost(ID) );
    }

    @Override
    public Post addPost( String contents, int userID ) {
        return addPost( contents, userID, null );
    }

    @Override
    public Post addPost( String contents, int userID, Integer imageID ) {

        int ID = jdbi.withHandle( handle -> handle.attach(PostDao.class).insertPost( contents, userID, null ) );
        return getPost(ID);

    }

    @Override
    public void deletePost( int ID ) {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).deletePost(ID) );
    }

}
