package store.model;

import model.Post;

import java.util.List;

public interface PostStore {

    Post getPost( int pid );

    /**
     * @return a list composed of every post made by a user
     */
    List<Post> makeFeed( int uid );

    boolean hasPost( int pid );

    /**
     * Adds a tier 0 post with no images.
     */
    Post addPost( String title, String contents, int uid );

    /**
     * Adds a tier 0 post with images.
     */
    Post addPost( String title, String contents, int uid, List<Integer> iidList );

    /**
     * Adds a post with no images with the specified tier.
     */
    Post addPost( String title, String contents, int uid, int tier );

    /**
     * Adds a post with images with the specified tier.
     */
    Post addPost( String title, String contents, int uid, int tier, List<Integer> iidList );

    void addPostImage( int pid, int iid );

    void deletePostImage( int pid, int iid );

    void deletePost( int pid );

    void changeTitle( int pid, String newTitle );

    void changeContents( int pid, String newContents );

    void changeTier( int pid, int newTier );

}
