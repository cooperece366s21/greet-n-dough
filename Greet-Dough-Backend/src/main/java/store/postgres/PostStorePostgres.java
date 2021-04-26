package store.postgres;

import model.Post;
import store.model.PostStore;

import org.jdbi.v3.core.Jdbi;

import java.util.LinkedList;

public class PostStorePostgres implements PostStore {

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
    protected LinkedList<Post> getPost() {
        return jdbi.withHandle( handle -> handle.attach(PostDao.class).listPosts() );
    }

    @Override
    public LinkedList<Post> makeFeed( int uid ) {
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
