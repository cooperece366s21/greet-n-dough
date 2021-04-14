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
    public Comment addComment( String contents, int uid ) {

        // Create the comment
        int commentID = super.getFreeID();
        Comment tempComment = new Comment( contents, commentID, uid );

        // Add the comment
        super.add( tempComment.getID(), tempComment );
        return tempComment;

    }

    @Override
    public boolean canComment( int pid ) {
        return false;
    }

    @Override
    public Comment insertComment( String contents, int uid, int pid, Integer parent_id ) {
        return null;
    }

    @Override
    public Comment insertComment( String contents, int uid, int pid ) {
        return null;
    }

    @Override
    public boolean canReply( int cid ) {
        return false;
    }

    @Override
    public Comment getReplies( int parent_id ) {
        return null;
    }

    @Override
    public Comment getParents(int post_id) {
        return null;
    }

}
