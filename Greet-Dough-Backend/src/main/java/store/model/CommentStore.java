package store.model;

import model.Comment;

public interface CommentStore {

    Comment getComment( int cid );

    Comment addComment( String contents, int uid );

    boolean canComment( int pid );

    Comment insertComment( String contents, int uid, Integer parent_id );

    Comment insertComment( String contents, int uid );

    boolean canReply( int comment_id );

    Comment getReplies( int parent_id );

}
