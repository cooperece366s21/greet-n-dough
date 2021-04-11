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
    public Comment getComment( int cid ) {
        return super.get(cid);
    }

    @Override
    public void addComment( Comment newComment ) {
        super.add( newComment.getID(), newComment );
    }

    @Override
    public Comment addComment( String contents, int uid ) {

        // Create the comment
        int commentID = super.getFreeID();
        Comment tempComment = new Comment( contents, commentID, uid );

        // Add the comment
        this.addComment( tempComment );
        return tempComment;

    }

}
