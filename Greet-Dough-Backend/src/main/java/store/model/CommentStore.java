package store.model;

import model.Comment;

import java.util.LinkedList;

public interface CommentStore {

    Comment getComment( int cid );

    LinkedList<Comment> getReplies( int parent_id );

    LinkedList<Comment> getParents( int pid );

    Comment addComment( String contents, int uid, int pid, Integer parent_id );

    Comment addComment( String contents, int uid, int pid );

    boolean hasComment( int cid );

    boolean isParent( int cid );

    void deleteComment( int cid );

}
