package store.model;

import model.Comment;

import java.util.List;

public interface CommentStore {

    Comment getComment( int cid );

    List<Comment> getReplies( int parent_id );

    List<Comment> getParents( int pid );

    Comment addComment( String contents, int uid, int pid, Integer parent_id );

    Comment addComment( String contents, int uid, int pid );

    //boolean canComment( int pid );

    boolean hasComment( int cid );

    boolean isParent(int cid);
}
