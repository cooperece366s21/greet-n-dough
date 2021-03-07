package store.model;

import model.Likes;
import store.StorageRetrieval;

import java.util.HashSet;

public class LikeStoreImpl extends StorageRetrieval<Likes> {

    public LikeStoreImpl() {
        super();
    }

    public Likes getID( int ID ) {
        return super.get(ID);
    }

}
