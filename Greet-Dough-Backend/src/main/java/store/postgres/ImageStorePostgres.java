package store.postgres;

import model.User;
import model.Post;
import model.Image;
import store.model.ImageStore;
import utility.ResetDao;

import org.jdbi.v3.core.Jdbi;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Random;

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
        Path tempPath = fileSys.getPath( System.getProperty("user.dir") );
        for ( int a=0; a<3; a++ ) {
            tempPath = tempPath.getParent();
        }
        Path newPath = fileSys.getPath( tempPath.toString() + File.separator + "beardKoolmodo.png" );
        System.out.println(newPath.toString());

        // Test copying and saving an image
        Image selfie = ImageStorePostgres.addImage( newPath.toString(), komodo.getID() );

        // Test adding a post with an image
        Post newPost = PostStorePostgres.addPost( "first!", komodo.getID(), selfie.getID() );
        System.out.println( newPost.getID() + " " + newPost.getUserID() +
                " " + newPost.getImageID() + " " + newPost.getContents() );

        // Test deleting the post
        //      Should delete cascade the image
        PostStorePostgres.deletePost( newPost.getID() );
        System.out.println( ImageStorePostgres.getImage( selfie.getID() ) );

    }

    private final Jdbi jdbi;
    private final Path imageDir;
    private final Random filenameGen;
    private static final int MAX_FILENAME_SIZE = 10;
    private static final FileSystem fileSys = FileSystems.getDefault();

    public ImageStorePostgres( final Jdbi jdbi ) {

        this.jdbi = jdbi;
        this.imageDir = setImageDir();
        this.filenameGen = new Random();

    }

    private Path setImageDir() {

        Path tempPath = fileSys.getPath( System.getProperty("user.dir") );

        // Stores in Greet-Dough-Backend/data/images
        Path newPath = fileSys.getPath( tempPath.toString() + File.separator + "data" + File.separator + "images" );

        return newPath;

    }

    // From https://stackoverflow.com/a/21974043
    private String getFileExtension( String filename ) {

        String extension = "";

        int i = filename.lastIndexOf('.');
        int p = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));

        if (i > p) {
            extension = filename.substring(i);
        }

        return extension;

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

        String newPath = copyImage(path);
        int ID = jdbi.withHandle( handle -> handle.attach(ImageDao.class).insertImage( newPath, uid ) );
        return getImage(ID);

    }

    @Override
    public void deleteImage( int iid ) {
        jdbi.useHandle( handle -> handle.attach(ImageDao.class).deleteImage(iid) );
    }

    private String copyImage( String path ) {

        Path srcPath = fileSys.getPath(path);
        String extension = getFileExtension(path);

        // Generate a random alphanumeric filename
        //      Characters from '0' to 'z'
        // From https://www.baeldung.com/java-random-string
        String filename = filenameGen.ints(48,122+1)
                            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                            .limit( MAX_FILENAME_SIZE )
                            .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                            .toString();

        // Writes to imageDir/RANDOM_NAME
        Path destPath = fileSys.getPath( imageDir.toString() + File.separator + filename + extension );

        // Attempt to save the image
        try {

            // Creates the file to write to
            new File( destPath.toString() ).createNewFile();

            // Copies the file
            Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
            return destPath.toString();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return destPath.toString();

    }

}
