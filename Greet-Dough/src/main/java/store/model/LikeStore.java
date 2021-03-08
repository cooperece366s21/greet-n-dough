package store.model;

import model.Likes;

public interface LikeStore {

    Likes getID( int ID );

    void addLikes( Likes newLikes );

    void deleteLikes( Integer ID );

}
