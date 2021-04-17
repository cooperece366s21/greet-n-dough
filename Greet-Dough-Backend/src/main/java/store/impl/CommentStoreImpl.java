package store.impl;

import model.Comment;
import store.model.StoreWithID;
import store.model.CommentStore;

import java.util.List;

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
    public boolean canComment( int pid ) {
        return false;
    }

    @Override
    public Comment addComment( String contents, int uid, int pid, Integer parentID ) {

        // Create the comment
        int commentID = super.getFreeID();
        Comment tempComment = new Comment( contents, commentID, uid, pid, parentID );

        // Add the comment
        super.add( tempComment.getID(), tempComment );
        return tempComment;

    }

    @Override
    public Comment addComment( String contents, int uid, int pid ) {
        return addComment( contents, uid, pid, null );
    }

    @Override
    public boolean hasParent( int cid ) {
        return false;
    }

    @Override
    public boolean isParent(int cid) {
        return false;
    }

    @Override
    public List<Comment> getReplies( int parent_id ) {
        return null;
    }

    @Override
    public List<Comment> getParents( int pid ) {
        return null;
    }

}
