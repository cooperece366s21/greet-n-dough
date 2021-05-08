package store.postgres;

import model.Image;
import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImageStorePostgresTest extends ImageStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    private static UserStorePostgres userStorePostgres;
    private static ImageStorePostgres imageStorePostgres;
    private static PostStorePostgres postStorePostgres;

    private static User newUser;
    private static String newPath;

    public ImageStorePostgresTest() {
        super(jdbi);
    }

    @BeforeAll
    static void setUpAll() {

        // Delete all the databases (only use the relevant ones)
        ResetDao.deleteAll(jdbi);

        userStorePostgres = new UserStorePostgres(jdbi);
        postStorePostgres = new PostStorePostgres(jdbi);
        imageStorePostgres = new ImageStorePostgres(jdbi);

        // Get local image
        FileSystem fileSys = FileSystems.getDefault();
        Path tempPath = fileSys.getPath( System.getProperty("user.dir") );
        for ( int a=0; a<3; a++ ) {
            tempPath = tempPath.getParent();
        }
        newPath = fileSys.getPath( tempPath.toString() + File.separator + "beardKoolmodo.png" ).toString();

    }

    @AfterAll
    static void tearDownAll() {
        ResetDao.reset(jdbi);
    }

    @BeforeEach
    void setUpEach() {

        // Delete the databases
        imageStorePostgres.delete();
        userStorePostgres.delete();

        // Initialize the databases
        userStorePostgres.init();
        imageStorePostgres.init();

        // Add a kool user
        newUser = userStorePostgres.addUser("Komodo");

        // Test empty returns
        assert ( imageStorePostgres.getAllImages().isEmpty() );
        assertNull( imageStorePostgres.getImage(1) );

    }

    @Test
    void testAddImage() {

        // Test copying and saving an image
        Image selfie = imageStorePostgres.addImage( newUser.getID(), newPath.toString(), false );

        // Create expected output
        List<Integer> iidList = new LinkedList<>();
        iidList.add( selfie.getID() );

        // Test adding a post with an image
        String title = "Feeling cute, might delete later";
        String contents = "first!";
        Post newPost = postStorePostgres.addPost( title, contents, newUser.getID(), iidList );
        assert ( newPost.getUserID() == newUser.getID() );
        assert ( newPost.getImageIDList().equals( iidList ) );
        assert ( newPost.getTitle().equals( title ) );
        assert ( newPost.getContents().equals( contents ) );

    }

    @Test
    void testGetImage() {

        // Copy and Save an image
        Image selfie = imageStorePostgres.addImage( newUser.getID(), newPath.toString(), false );

        // Test retrieving the image
        assert ( imageStorePostgres.getImage( selfie.getID() ).getPath().equals( selfie.getPath() ) );

    }

    @Test
    void testDeleteImage() {

        // Test soft deleting and then clearing an image
        Image tempImage = imageStorePostgres.addImage( newUser.getID(), newPath.toString(), false );
        assert ( imageStorePostgres.getAllImages().size() == 2 );

        // Check that the number of rows is still the same
        imageStorePostgres.deleteImage( tempImage.getID() );
        assert ( imageStorePostgres.getAllImages().size() == 2 );

        // Check that the soft deleted row has been removed
        imageStorePostgres.clearDeleted();
        assert ( imageStorePostgres.getAllImages().size() == 1 );

    }

    @Test
    void testDeleteUser() {

        // Copy and save an image
        Image selfie = imageStorePostgres.addImage( newUser.getID(), newPath.toString(), false );

        // Test deleting the user
        //      Should delete cascade the image
        userStorePostgres.deleteUser( newUser.getID() );
        assertNull ( imageStorePostgres.getImage( selfie.getID() ) );

        // Check that the soft deleted row has been removed
        imageStorePostgres.clearDeleted();
        assert ( imageStorePostgres.getAllImages().size() == 0 );

    }

}