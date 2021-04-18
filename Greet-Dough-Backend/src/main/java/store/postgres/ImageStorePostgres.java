package store.postgres;

import model.User;
import model.Post;
import model.Image;
import store.model.ImageStore;
import utility.ImageHandler;
import utility.ResetDao;

import org.jdbi.v3.core.Jdbi;

import java.io.File;
import java.nio.file.*;
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

        User komodo = UserStorePostgres.addUser("Komodo");

        // Test empty returns
        System.out.println( ImageStorePostgres.getImage() );
        System.out.println( ImageStorePostgres.getImage(1) );

        // Get local image
        FileSystem fileSys = FileSystems.getDefault();
        Path tempPath = fileSys.getPath( System.getProperty("user.dir") );
        for ( int a=0; a<3; a++ ) {
            tempPath = tempPath.getParent();
        }
        Path newPath = fileSys.getPath( tempPath.toString() + File.separator + "beardKoolmodo.png" );
        System.out.println(newPath.toString());

        // Test copying and saving an image
        Image selfie = ImageStorePostgres.addImage( newPath.toString(), komodo.getID() );

        // Test adding a post with an image
        Post newPost = PostStorePostgres.addPost( "Feeling cute, might delete later", "first!", komodo.getID(), selfie.getID() );
        System.out.println( newPost.getID() + " " + newPost.getUserID() +
                " " + newPost.getImageID() + " " + newPost.getContents() );

        // Test deleting the post
        //      Should delete cascade the image
        PostStorePostgres.deletePost( newPost.getID() );
        System.out.println( ImageStorePostgres.getImage( selfie.getID() ).getPath() );

    }

    private final Jdbi jdbi;
    private final ImageHandler ImageHandler;

    public ImageStorePostgres( final Jdbi jdbi ) {

        this.jdbi = jdbi;
        this.ImageHandler = new ImageHandler();

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

    /**
     * Currently only used for testing.
     *
     * @return all images in the database
     */
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

        String newPath = ImageHandler.copyImage(path);
        int ID = jdbi.withHandle( handle -> handle.attach(ImageDao.class).addImage( newPath, uid ) );
        return getImage(ID);

    }

    @Override
    public void deleteImage( int iid ) {
        jdbi.useHandle( handle -> handle.attach(ImageDao.class).deleteImage(iid) );
    }

}
