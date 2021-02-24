package store;

import model.Post;

public interface PostStore {

    Post getPost( int postID );

    void addPost( Post newPost );

    boolean deletePost( int postID );

}
