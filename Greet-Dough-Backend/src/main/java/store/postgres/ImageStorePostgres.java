package store.postgres;

import model.Image;
import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import store.model.ImageStore;

import java.util.List;

public class ImageStorePostgres implements ImageStore {

    // For testing purposes
    public static void main( String[] args ) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        PostStorePostgres PostStorePostgres = new PostStorePostgres(jdbi);
        ImageStorePostgres ImageStorePostgres = new ImageStorePostgres(jdbi);

        // Used to DROP and CREATE the posts and images table
        PostStorePostgres.reset();
        ImageStorePostgres.reset();
        ImageStorePostgres.init();
        PostStorePostgres.init();

        User yeet = UserStorePostgres.addUser("yeet");

        // Test adding and retrieving a post
        Post yeetPost = PostStorePostgres.addPost( "first!", yeet.getID() );
        Post postAfterWrite = PostStorePostgres.getPost( yeetPost.getID() );
        System.out.println( postAfterWrite.getID() + " " + postAfterWrite.getUserID() +
                " " + postAfterWrite.getImageID() + " " + postAfterWrite.getContents() );

        // Test deleting the post
        PostStorePostgres.deletePost( postAfterWrite.getID() );

        // Make some more posts
        PostStorePostgres.addPost( "lol", yeet.getID() );
        PostStorePostgres.addPost( "haha very cool!", yeet.getID() );
        System.out.println( PostStorePostgres.makeFeed( yeet.getID() ) );

        // Test deleting the user
        //      Should delete cascade the posts
        UserStorePostgres.deleteUser( yeet.getID() );
        System.out.println( PostStorePostgres.makeFeed( yeet.getID() ));
        System.out.println( PostStorePostgres.getPost() );

    }

    private final Jdbi jdbi;

    public ImageStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void reset() {
        jdbi.useHandle(handle -> handle.attach(ImageDao.class).resetTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(ImageDao.class).createTable());
    }

    @Override
    public Image getImage( int ID ) {
        return jdbi.withHandle( handle -> handle.attach(ImageDao.class).getImage(ID) );
    }

    public List<Image> getImage() {
        return jdbi.withHandle( handle -> handle.attach(ImageDao.class).listImages() );
    }

    public List<Image> makeGallery( int userID ) {
        return jdbi.withHandle( handle -> handle.attach(ImageDao.class).getGallery(userID) );
    }

    @Override
    public boolean hasImage( int ID ) {
        return jdbi.withHandle( handle -> handle.attach(ImageDao.class).containsImage(ID) );
    }

    @Override
    public Image addImage( String path, int userID ) {

        int ID = jdbi.withHandle( handle -> handle.attach(ImageDao.class).insertImage( path, userID ) );
        return getImage(ID);

    }

    @Override
    public void deleteImage( int ID ) {
        jdbi.useHandle( handle -> handle.attach(ImageDao.class).deleteImage(ID) );
    }

}
