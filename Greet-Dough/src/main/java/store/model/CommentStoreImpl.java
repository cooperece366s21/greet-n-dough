package store.model;

import model.Comment;

public class CommentStoreImpl extends Store<Comment> {

    public CommentStoreImpl() {
        super();
    }

    public CommentStoreImpl( int start ) {
        super(start);
    }

    public void attemptComment(Comment newComment, String commentContent, int currentUser ) {
        newComment.addComment(commentContent, currentUser);
    }

}
