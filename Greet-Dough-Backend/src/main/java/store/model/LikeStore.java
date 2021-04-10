package store.model;

import model.Likes;

public interface LikeStore {

    Likes getID( int ID );

    void addLikes( Likes newLikes );

    void deleteLikes( Integer ID );

    Likes addLikes(int postID, int uid);

    void insertLikes(int postID, int uid);

    boolean containsLike(int postID, int uid);
}
