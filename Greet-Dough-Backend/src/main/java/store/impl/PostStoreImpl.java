package store.impl;

import model.Post;
import store.model.StoreWithID;
import store.model.PostStore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PostStoreImpl extends StoreWithID<Post> implements PostStore {

    public PostStoreImpl() {
        super();
    }

    public PostStoreImpl( int start ) {
        super(start);
    }

    @Override
    public Post getPost( int pid ) {
        return super.get(pid);
    }

    // Functions unique to PostStore
    // I'm not sure how to grab the list of items, since the implementation doesnt have access
    // I guess when we use database we wont have this issue of permissions.
    @Override
    public LinkedList<Post> makeFeed( int uid ) {

        LinkedList<Post> usersPosts = new LinkedList<>();

        for( Post post : this.getItems().values() ) {
            if ( post.getUserID() == uid ) {
                usersPosts.add(post);
            }
        }

        return usersPosts;

    }

    @Override
    public boolean hasPost( int pid ) {
        return super.has(pid);
    }

    @Override
    public Post addPost( String title, String contents, int uid ) {
        return addPost( title, contents, uid, null );
    }

    @Override
    public Post addPost( String title, String contents, int uid, Integer iid ) {

        // Create the post
        int pid = super.getFreeID();
        Post tempPost = new Post( title, contents, pid, uid, iid );

        // Add the post
        super.add( tempPost.getID(), tempPost );
        return tempPost;

    }

    @Override
    public void deletePost( int pid ) {
        super.delete(pid);
    }

    @Override
    public void changeTitle( int pid, String newTitle ) {

    }

    @Override
    public void changeContents( int pid, String newContents ) {

    }

}
