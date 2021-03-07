package store.model;

import model.Post;

import java.io.Serializable;
import java.util.List;

public interface PostStore extends Serializable {

    Post getPost( int ID );

    void addPost( Post newPost );

    boolean deletePost( int ID );

    List<Post> makeFeed( int userID );

    int getFreeID();

}
