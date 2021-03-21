package store.impl;

import model.Comment;
import model.Likes;
import store.StorageRetrieval;
import store.model.LikeStore;

public class LikeStoreImpl extends StorageRetrieval<Likes> implements LikeStore {

    public LikeStoreImpl() {
        super();
    }

    @Override
    public Likes getID(int ID) {
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
    public Likes addLikes( int postID, int uid ) {

        // Create the like
        Likes tempLike = new Likes( postID, uid );

        // Add the like
        this.addLikes( tempLike );
        return tempLike;

    }
}
