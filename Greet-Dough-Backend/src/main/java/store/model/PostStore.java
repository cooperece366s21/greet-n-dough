package store.model;

import model.Post;

import java.util.List;

public interface PostStore {

    Post getPost( int ID );

    boolean hasPost( int ID );

    Post addPost( String contents, int userID );

    Post addPost( String contents, int userID, Integer imageID );

    void deletePost( int ID );

    List<Post> makeFeed( int userID );

}
