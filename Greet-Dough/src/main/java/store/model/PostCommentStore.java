package store.model;

import java.util.ArrayList;

public interface PostCommentStore {

    ArrayList<Integer> getComments( int ID );

    void addComment( int postID, int commentID );

    void removeComment( int postID, int commentID );

    // Deletes all comments associated with post
    // Need to access CommentReply <- once implemented
    boolean deletePost( int ID );

}
