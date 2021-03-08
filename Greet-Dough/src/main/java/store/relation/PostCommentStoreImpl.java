package store.relation;

import store.model.PostCommentStore;

import java.util.ArrayList;

public class PostCommentStoreImpl extends Relation implements PostCommentStore {

    public PostCommentStoreImpl() {
        super();
    }

    @Override
    public ArrayList<Integer> getComments( int ID ) {
        return super.get(ID);
    }

    @Override
    public void addComment( int postID, int commentID ) {
        super.add( postID, commentID );
    }

    @Override
    public void removeComment( int postID, int commentID ) {
        super.remove( postID, commentID );
    }

    // Deletes all comments associated with post
    // Need to access CommentReply <- once implemented
    @Override
    public boolean deletePost( int ID ) {
        return super.delete(ID);
    }

}
