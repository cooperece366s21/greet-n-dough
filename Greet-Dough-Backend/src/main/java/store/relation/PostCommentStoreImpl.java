package store.relation;

import store.model.PostCommentStore;

import java.util.ArrayList;

public class PostCommentStoreImpl extends Relation implements PostCommentStore {

    public PostCommentStoreImpl() {
        super();
    }

    @Override
    public ArrayList<Integer> getComments( int pid ) {
        return super.get(pid);
    }

    @Override
    public void addComment( int pid, int cid ) {
        super.add( pid, cid );
    }

    @Override
    public void removeComment( int pid, int cid ) {
        super.remove( pid, cid );
    }

    // Deletes all comments associated with post
    // Need to access CommentReply <- once implemented
    @Override
    public boolean deletePost( int pid ) {
        return super.delete(pid);
    }

}
