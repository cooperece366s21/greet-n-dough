package store.model;

import model.Post;

public class PostStoreImpl extends Store<Post> {

    public PostStoreImpl() {
        super();
    }

    public PostStoreImpl( int start ) {
        super( start );
    }

    public Post getPost( int ID ) {
        return super.get(ID);
    }

    public void addPost( Post newPost ) {
        super.add( newPost.getID(), newPost );
    }

    // Later on, check if user owns the post before deleting
    public boolean deletePost( int ID ) {
        return super.delete(ID);
    }

}
