package store;

import model.Post;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PostStoreImpl implements PostStore {

    private AtomicInteger freeIDs;
    private final HashMap<Integer, Post> posts;

    public PostStoreImpl() {
        this(0);
    }

    public PostStoreImpl( int start ) {

        this.freeIDs = new AtomicInteger(start);
        this.posts = new HashMap<>();

    }

    @Override
    public int getFreeID() {
        return freeIDs.getAndIncrement();
    }

    @Override
    public Post getPost( int postID ) {
        return this.posts.get( postID );
    }

    @Override
    public void addPost( Post newPost ) {
        this.posts.put( newPost.getID(), newPost );
    }

    // Later on, check if user owns the post before deleting
    @Override
    public boolean deletePost( int postID ) {
        return ( this.posts.remove( postID ) != null );
    }

}
