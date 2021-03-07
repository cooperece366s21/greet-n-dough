package store.model;

import model.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentStoreImpl extends StoreWithID<Comment> {

    public CommentStoreImpl() {
        super();
    }

    public CommentStoreImpl(int start ) {
        super(start);
    }

    public Comment getPostID( int ID ) {
        return super.get(ID);
    }

    public void addComment(String commentContent, int currentUser, Comment newComment) {
        LocalDateTime commentTime = LocalDateTime.now();
        HashMap<LocalDateTime, List> comment = newComment.getComment();
        List<String> value = new ArrayList<String>();
        value.add(Integer.toString(currentUser));
        value.add(commentContent);
        comment.put(commentTime, value);
        newComment.setComment(comment);
    }

}
