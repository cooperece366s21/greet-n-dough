package store.impl;

import model.Likes;
import store.StorageRetrieval;
import store.model.LikeStore;

public class LikeStoreImpl extends StorageRetrieval<Likes> implements LikeStore {

    public LikeStoreImpl() {
        super();
    }

    @Override
    public Likes getID(int ID) {return super.get(ID); }

    @Override
    public void addLikes( Likes newLikes ) { super.add( newLikes.getPostID(), newLikes ); }

    @Override
    public void deleteLikes( Integer ID ) { super.delete( ID ); }

}
