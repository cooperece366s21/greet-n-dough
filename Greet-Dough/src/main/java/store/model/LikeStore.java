package store.model;

import model.Likes;

import java.io.Serializable;

public interface LikeStore extends Serializable {

    Likes getID( int ID );

}
