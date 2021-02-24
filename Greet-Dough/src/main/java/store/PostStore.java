package store;

import model.Post;
import utility.TrackerID;

public interface PostStore extends TrackerID {

    Post getPost( int postID );

    void addPost( Post newPost );

    boolean deletePost( int postID );

}
