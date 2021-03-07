package store.model;

import model.Comment;

import java.io.Serializable;

public interface CommentStore extends Serializable {

    Comment getPostID( int ID );

    void addComment( String commentContent, int currentUser, Comment newComment );

}
