package store.impl;

import model.Likes;
import store.StorageRetrieval;
import store.model.LikeStore;

public class LikeStoreImpl extends StorageRetrieval<Likes> implements LikeStore {

    public LikeStoreImpl() {
        super();
    }

    @Override
    public Likes getLikes( int pid ) {
        return super.get(pid);
    }

    @Override
    public void deleteLikes( int pid ) {
        super.delete(pid);
    }

    @Override
    public Likes addLikes( int pid, int uid ) {

        // Create the like
        Likes tempLike = new Likes( pid, uid );

        // Add the like
        super.add( tempLike.getPostID(), tempLike );
        return tempLike;

    }

    @Override
    public void insertLikes( int pid, int uid ) {

    }

    @Override
    public boolean hasUserLike( int pid, int uid ) {
        return false;
    }

}
