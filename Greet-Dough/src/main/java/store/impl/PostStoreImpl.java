package store.impl;

import model.Post;
import store.model.StoreWithID;
import store.model.PostStore;

import java.util.ArrayList;
import java.util.List;

public class PostStoreImpl extends StoreWithID<Post> implements PostStore {

    public PostStoreImpl() {
        super();
    }

    public PostStoreImpl(int start ) {
        super(start);
    }

    @Override
    public Post getPost( int ID ) {
        return super.get(ID);
    }

    @Override
    public boolean hasPost( int ID ) {
        return this.has(ID);
    }

    @Override
    public void addPost( Post newPost ) {
        super.add( newPost.getID(), newPost );
    }

    @Override
    public Post addPost( String contents, int userID ) {
        return this.addPost( contents, userID, -1 );
    }

    @Override
    public Post addPost( String contents, int userID, int imageID ) {

        // Create the post
        int postID = super.getFreeID();
        Post tempPost = new Post( contents, postID, userID, imageID );

        // Add the post
        this.addPost( tempPost );
        return tempPost;

    }

    // Later on, check if user owns the post before deleting
    @Override
    public boolean deletePost( int ID ) {
        return super.delete(ID);
    }

    // Functions unique to PostStore
    // I'm not sure how to grab the list of items, since the implementation doesnt have access
    // I guess when we use database we wont have this issue of permissions.
    @Override
    public List<Post> makeFeed( int userID ) {

        List<Post> usersPosts = new ArrayList<Post>();

        for( Post post : this.getItems().values() ) {
            if ( post.getUserID() == userID ) {
                usersPosts.add(post);
            }
        }

        return usersPosts;

    }

}
