package store.model;

import model.Likes;

public interface LikeStore {

    Likes getLikes( int pid );

    Likes addLikes( int pid, int uid );

    void deleteLikes( int pid );

    void insertLikes( int pid, int uid );

    // Returns whether the user has liked the post
    boolean hasUserLike( int pid, int uid );

}
