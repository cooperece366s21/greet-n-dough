package store.postgres;

import model.Post;
import store.model.PostStore;

import org.jdbi.v3.core.Jdbi;

import java.util.LinkedList;
import java.util.List;

public class PostStorePostgres implements PostStore {

    private final Jdbi jdbi;

    public PostStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).deleteTable() );
    }

    public void init() {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).createTable() );
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
    protected List<Post> getAllPosts() {
        return jdbi.withHandle( handle -> handle.attach(PostDao.class).getAllPosts() );
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
        return addPost( title, contents, uid, 0 );
    }

    @Override
    public Post addPost( String title, String contents, int uid, List<Integer> iidList ) {
        return addPost( title, contents, uid, 0, iidList );
    }

    @Override
    public Post addPost( String title, String contents, int uid, int tier ) {
        return addPost( title, contents, uid, tier, new LinkedList<>() );
    }

    @Override
    public Post addPost( String title, String contents, int uid, int tier, List<Integer> iidList ) {

        // Add the post
        int pid = jdbi.withHandle( handle -> handle.attach(PostDao.class).addPost( title, contents, uid, tier ) );

        // Add the images
        for ( int iid : iidList ) {
            addPostImage( pid, iid );
        }

        return getPost(pid);

    }

    @Override
    public void addPostImage( int pid, int iid ) {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).addPostImage( pid, iid ) );
    }

    @Override
    public void deletePostImage( int pid, int iid ) {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).deletePostImage( pid, iid ) );
    }

    @Override
    public void deletePost( int pid ) {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).deletePost(pid) );
    }

    @Override
    public void changeTitle( int pid, String newTitle ) {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).changeTitle( pid, newTitle ) );
    }

    @Override
    public void changeContents( int pid, String newContents ) {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).changeContents( pid, newContents ) );
    }

    @Override
    public void changeTier( int pid, int newTier ) {
        jdbi.useHandle( handle -> handle.attach(PostDao.class).changeTier( pid, newTier ) );
    }

}
