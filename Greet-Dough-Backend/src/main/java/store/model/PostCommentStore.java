package store.model;

import java.util.ArrayList;

public interface PostCommentStore {

    ArrayList<Integer> getComments( int pid );

    void addComment( int pid, int cid );

    void removeComment( int pid, int cid );

    // Deletes all comments associated with post
    // Need to access CommentReply <- once implemented
    boolean deletePost( int pid );

}
