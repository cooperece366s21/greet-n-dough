package store.model;

import model.Likes;

public interface LikeStore {

    Likes getID( int ID );

    Likes addLikes( int pid, int uid );

    void deleteLikes( int lid );

    void insertLikes( int pid, int uid );

    boolean containsLike( int pid, int uid );
}
