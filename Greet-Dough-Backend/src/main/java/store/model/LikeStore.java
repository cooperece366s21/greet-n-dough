package store.model;

import model.Likes;

public interface LikeStore {

    /**
     * The method returns a Likes object associated with the post.
     * If there are no users that have liked the post,
     * the userLikes member is created as an empty HashSet.
     *
     * @return a Likes object
     */
    Likes getLikes( int pid );

    /**
     * Adds a user to the set of users that liked the post.
     */
    void addUserLike( int pid, int uid );

    /**
     * Deletes a user from the set of users that liked the post.
      */
    void deleteUserLike( int pid, int uid );

    /**
     * @returns true if the user has liked the post; false otherwise
      */
    boolean hasUserLike( int pid, int uid );

}
