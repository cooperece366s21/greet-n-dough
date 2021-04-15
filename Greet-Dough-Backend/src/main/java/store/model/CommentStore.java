package store.model;

import model.Comment;

public interface CommentStore {

    Comment getComment( int cid );

    Comment getReplies( int parent_id );

    Comment getParents( int pid );

    Comment addComment( String contents, int uid, int pid, Integer parent_id );

    Comment addComment( String contents, int uid, int pid );

    boolean canComment( int pid );

    boolean canReply( int cid );

}
