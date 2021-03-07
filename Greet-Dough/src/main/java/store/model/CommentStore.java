package store.model;

import model.Comment;

public interface CommentStore {

    Comment getPostID( int ID );

    void addComment( String commentContent, int currentUser, Comment newComment );

}
