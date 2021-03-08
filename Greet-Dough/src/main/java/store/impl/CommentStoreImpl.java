package store.impl;

import model.Comment;
import store.model.StoreWithID;
import store.model.CommentStore;

public class CommentStoreImpl extends StoreWithID<Comment> implements CommentStore {

    public CommentStoreImpl() {
        super();
    }

    public CommentStoreImpl( int start ) {
        super(start);
    }

    @Override
    public Comment getComment( int ID ) {
        return super.get(ID);
    }

    @Override
    public void addComment( Comment newComment ) {
        super.add( newComment.getID(), newComment );
    }

}
