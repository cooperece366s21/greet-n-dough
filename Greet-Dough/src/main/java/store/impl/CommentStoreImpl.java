package store.impl;

import model.Comment;
import model.CommentContent;
import store.model.StoreWithID;
import store.model.CommentStore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentStoreImpl extends StoreWithID<Comment> implements CommentStore {

    public CommentStoreImpl() {
        super();
    }

    public CommentStoreImpl( int start ) {
        super(start);
    }

    @Override
    public Comment getPostID( int ID ) {
        return super.get(ID);
    }

    @Override
    public void addComment( String commentContent, int currentUser, Comment newComment ) {

        LocalDateTime commentTime = LocalDateTime.now();
        HashMap<LocalDateTime, CommentContent> comment = newComment.getComment();
        //get this using hash atomic integer? ask derek when he is free, should be ez?

        int commentID = 0;

        CommentContent tempContent = new CommentContent(currentUser, commentContent, commentID);
        comment.put(commentTime, tempContent);
        newComment.setComment(comment);

    }

}
