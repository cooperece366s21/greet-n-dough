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

    public PostStoreImpl( int start ) {
        super(start);
    }

    @Override
    public Post getPost( int ID ) {
        return super.get(ID);
    }

    @Override
    public boolean hasPost( int ID ) {
        return super.has(ID);
    }

    @Override
    public Post addPost( String contents, int userID ) {
        return this.addPost( contents, userID, null );
    }

    @Override
    public Post addPost( String contents, int userID, Integer imageID ) {

        // Create the post
        int postID = super.getFreeID();
        Post tempPost = new Post( contents, postID, userID, imageID );

        // Add the post
        super.add( tempPost.getID(), tempPost );
        return tempPost;

    }

    @Override
    public void deletePost( int ID ) {
        super.delete(ID);
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
