package store.model;

import model.Comment;

public interface CommentStore {

    Comment getComment( int ID );

    void addComment( Comment newComment );

    Comment addComment( int uid, String content );

    int getFreeID();

}
