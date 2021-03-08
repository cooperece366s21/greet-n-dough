package store.impl;

import model.NestedComment;
import model.Comment;
import store.model.StoreWithID;
import store.model.CommentStore;

import java.time.LocalDateTime;
import java.util.HashMap;

public class CommentStoreImpl extends StoreWithID<Comment> implements CommentStore {

    public CommentStoreImpl() {
        super();
    }

    public CommentStoreImpl( int start ) {
        super(start);
    }

    @Override
    public Comment getComment( int ID ) {
        return super.get(ID);
    }

    @Override
    public void addComment( Comment newComment ) {

//        LocalDateTime commentTime = LocalDateTime.now();
//        HashMap<LocalDateTime, Comment> comment = newComment.getComment();
        //get this using hash atomic integer? ask derek when he is free, should be ez?
//
//        int commentID = 0;

//        Comment tempContent = new Comment(currentUser, commentContent, commentID);
//        comment.put(commentTime, tempContent);
//        newComment.setComment(comment);
        super.add( newComment.getID(), newComment );

    }

}
