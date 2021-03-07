package store.model;

import model.Post;

import java.util.List;

public interface PostStore {

    Post getPost( int ID );

    void addPost( Post newPost );

    boolean deletePost( int ID );

    List<Post> makeFeed( int userID );

    int getFreeID();

}
