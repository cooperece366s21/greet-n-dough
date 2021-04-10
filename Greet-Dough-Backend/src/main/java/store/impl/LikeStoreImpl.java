package store.impl;

import model.Likes;
import store.StorageRetrieval;
import store.model.LikeStore;

public class LikeStoreImpl extends StorageRetrieval<Likes> implements LikeStore {

    public LikeStoreImpl() {
        super();
    }

    @Override
    public Likes getID( int ID ) {
        return super.get(ID);
    }

    @Override
    public void addLikes( Likes newLikes ) {
        super.add( newLikes.getPostID(), newLikes );
    }

    @Override
    public void deleteLikes( Integer ID ) {
        super.delete( ID );
    }

    @Override
    public Likes addLikes(int pid, int uid ) {

        // Create the like
        Likes tempLike = new Likes( pid, uid );

        // Add the like
        this.addLikes( tempLike );
        return tempLike;

    }

    @Override
    public void insertLikes(int postID, int uid) {

    }

    @Override
    public boolean containsLike(int postID, int uid) {
        return false;
    }
}
