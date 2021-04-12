package store.postgres;

import model.User;
import model.Post;
import model.Image;
import store.model.ImageStore;
import utility.ResetDao;

import org.jdbi.v3.core.Jdbi;
import java.util.List;

public class ImageStorePostgres implements ImageStore {

    // For testing purposes
    public static void main( String[] args ) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        PostStorePostgres PostStorePostgres = new PostStorePostgres(jdbi);
        ImageStorePostgres ImageStorePostgres = new ImageStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        User newUser = UserStorePostgres.addUser("Tony");

        // Test empty returns
        System.out.println( ImageStorePostgres.getImage() );
        System.out.println( ImageStorePostgres.getImage(1) );

        // Test adding and retrieving a post
        Post newPost = PostStorePostgres.addPost( "first!", newUser.getID() );
        Post postAfterWrite = PostStorePostgres.getPost( newPost.getID() );
        System.out.println( postAfterWrite.getID() + " " + postAfterWrite.getUserID() +
                " " + postAfterWrite.getImageID() + " " + postAfterWrite.getContents() );

        // Test deleting the post
        PostStorePostgres.deletePost( postAfterWrite.getID() );

        // Make some more posts
        PostStorePostgres.addPost( "lol", newUser.getID() );
        PostStorePostgres.addPost( "haha very cool!", newUser.getID() );
        System.out.println( PostStorePostgres.makeFeed( newUser.getID() ) );

        // Test deleting the user
        //      Should delete cascade the posts
        UserStorePostgres.deleteUser( newUser.getID() );
        System.out.println( PostStorePostgres.makeFeed( newUser.getID() ));
        System.out.println( PostStorePostgres.getPost() );

    }

    private final Jdbi jdbi;

    public ImageStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle(handle -> handle.attach(ImageDao.class).deleteTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(ImageDao.class).createTable());
    }

    @Override
    public Image getImage( int iid ) {
        return jdbi.withHandle( handle -> handle.attach(ImageDao.class).getImage(iid) ).orElse(null);
    }

    // Returns all users in the database
    //      Currently only used for testing
    public List<Image> getImage() {
        return jdbi.withHandle( handle -> handle.attach(ImageDao.class).listImages() );
    }

    @Override
    public List<Image> makeGallery( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(ImageDao.class).getGallery(uid) );
    }

    @Override
    public boolean hasImage( int iid ) {
        return getImage(iid) != null;
    }

    @Override
    public Image addImage( String path, int uid ) {

        int ID = jdbi.withHandle( handle -> handle.attach(ImageDao.class).insertImage( path, uid ) );
        return getImage(ID);

    }

    @Override
    public void deleteImage( int iid ) {
        jdbi.useHandle( handle -> handle.attach(ImageDao.class).deleteImage(iid) );
    }

}
