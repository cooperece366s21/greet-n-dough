package store.model;

import model.Likes;

public interface LikeStore {

    Likes getID( int ID );

    void addLikes( Likes newLikes );

    // like
    void like(int uid, int ID);

    // dislike
    void dislike(int uid, int ID);

    void deleteLikes( Integer ID );

}
