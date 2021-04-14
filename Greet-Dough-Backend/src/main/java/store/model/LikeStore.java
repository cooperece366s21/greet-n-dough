package store.model;

import model.Likes;

public interface LikeStore {

    Likes getLikes( int pid );

    // Adds a user to the set of users that liked the post
    void addUserLike( int pid, int uid );

    // Removes a user from the set of users that liked the post
    void removeUserLike( int pid, int uid );

    void deleteLikes( int pid );

    // Returns whether the user has liked the post
    boolean hasUserLike( int pid, int uid );

}
