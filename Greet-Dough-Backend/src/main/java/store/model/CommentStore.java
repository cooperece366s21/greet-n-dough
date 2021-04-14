package store.model;

import model.Comment;

public interface CommentStore {

    Comment getComment( int cid );

    Comment addComment( String contents, int uid );

    boolean canComment( int pid );

    Comment insertComment( String contents, int uid, int pid, Integer parent_id );

    Comment insertComment( String contents, int uid, int pid );

    boolean canReply( int comment_id );

    Comment getReplies( int parent_id );

    Comment getParents(int post_id);
}
