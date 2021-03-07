package store.impl;

import model.Comment;
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
        HashMap<LocalDateTime, List> comment = newComment.getComment();
        List<String> value = new ArrayList<String>();
        value.add(Integer.toString(currentUser));
        value.add(commentContent);
        comment.put(commentTime, value);
        newComment.setComment(comment);

    }

}
