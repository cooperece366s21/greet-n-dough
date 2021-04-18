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
        Post newPost = PostStorePostgres.addPost( "My first post!", "testing123", newUser.getID() );
        Post postAfterWrite = PostStorePostgres.getPost( newPost.getID() );
        System.out.println( postAfterWrite.getID() + " " + postAfterWrite.getUserID() + " " +
                            postAfterWrite.getImageID() + " " + postAfterWrite.getContents() + " " +
                            postAfterWrite.getTime() );

        // Test deleting the post
        PostStorePostgres.deletePost( postAfterWrite.getID() );

        // Make some more posts
        Post poopPost = PostStorePostgres.addPost( "Feeling well today", "cuz it's a WELLness day hahahahhah", newUser.getID() );
        PostStorePostgres.addPost( "You can graduate with a bachelors and masters with 134 credits at other schools",
                                "haha very cool!", newUser.getID() );
        System.out.println( PostStorePostgres.makeFeed( newUser.getID() ) );

        // Test changing a post
        PostStorePostgres.changeTitle( poopPost.getID(), "Not feeling well today" );
        PostStorePostgres.changeContents( poopPost.getID(), "cuz it's still a wellness day" );
        System.out.println( PostStorePostgres.getPost( poopPost.getID() ).getTitle() );
        System.out.println( PostStorePostgres.getPost( poopPost.getID() ).getContents() );

        // Test deleting the user
        //      Should delete cascade the posts
        UserStorePostgres.deleteUser( newUser.getID() );
        System.out.println( PostStorePostgres.makeFeed( newUser.getID() ) );
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

    /**
     * Currently only used for testing.
     *
     * @return all posts in the database
     */
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
    public Post addPost( String title, String contents, int uid ) {
        return addPost( title, contents, uid, null );
    }

    @Override
    public Post addPost( String title, String contents, int uid, Integer iid ) {

        int ID = jdbi.withHandle( handle -> handle.attach(PostDao.class).addPost( title, contents, uid, iid ) );
        return getPost(ID);

    }

    @Override
    public void deletePost( int pid ) {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).deletePost(pid) );
    }

    @Override
    public void changeTitle( int pid, String newTitle ) {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).changeTitle(pid, newTitle) );
    }

    @Override
    public void changeContents( int pid, String newContents ) {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).changeContents(pid, newContents) );
    }

}
