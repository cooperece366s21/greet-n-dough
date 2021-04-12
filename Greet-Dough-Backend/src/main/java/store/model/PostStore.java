package store.model;

import model.Post;

import java.util.List;

public interface PostStore {

    Post getPost( int pid );

    boolean hasPost( int pid );

    Post addPost( String contents, int uid );

    Post addPost( String contents, int uid, Integer iid );

    void deletePost( int pid );

    // Returns all of a user's posts
    List<Post> makeFeed( int uid );

}
