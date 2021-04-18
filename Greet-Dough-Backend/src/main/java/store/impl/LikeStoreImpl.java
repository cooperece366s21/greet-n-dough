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
    public void addUserLike( int pid, int uid ) {

        // Attempts to get the Likes object
        //      If doesn't exist, creates a new Likes object
        Likes tempLike = super.items.getOrDefault(pid, new Likes(pid));
        tempLike.incrementLike(uid);

        super.add( tempLike.getPostID(), tempLike );

    }

    @Override
    public void deleteUserLike( int pid, int uid ) {

        Likes tempLike = super.get(pid);
        tempLike.decrementLike(uid);

        super.add( tempLike.getPostID(), tempLike );

    }

    @Override
    public boolean hasUserLike( int pid, int uid ) {
        return false;
    }

}
