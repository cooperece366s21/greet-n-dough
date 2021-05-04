package store.postgres;

import model.Profile;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

class ProfileStorePostgresTest extends ProfileStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    private static UserStorePostgres userStorePostgres;
    private static ImageStorePostgres imageStorePostgres;
    private static ProfileStorePostgres profileStorePostgres;

    private static User newUser;
    private static String newPath;
    private static final String newBio = "Bachelors in CompE and Signals, and Masters in EE";

    public ProfileStorePostgresTest() {
        super(jdbi);
    }

    @BeforeAll
    static void setupAll() {

        // Delete all the databases (only use the relevant ones)
        ResetDao.deleteAll(jdbi);

        userStorePostgres = new UserStorePostgres(jdbi);
        imageStorePostgres = new ImageStorePostgres(jdbi);
        profileStorePostgres = new ProfileStorePostgres(jdbi);

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
    void setupEach() {

        // Delete the databases
        profileStorePostgres.delete();
        imageStorePostgres.delete();
        userStorePostgres.delete();

        // Initialize the databases
        userStorePostgres.init();
        imageStorePostgres.init();
        profileStorePostgres.init();

        // Add a user
        newUser = userStorePostgres.addUser("Vincent Zheng");

    }

    @Test
    void testDeleteProfilePicture() {

        // Add a profile
        profileStorePostgres.addProfile( newUser.getID(), newBio, newPath, false );
        assert ( profileStorePostgres.getProfile( newUser.getID() ).getImageID() != null );

        // Test deleting the profile picture
        profileStorePostgres.deleteProfilePicture( newUser.getID() );

        // Test the retrieved profile
        assert ( profileStorePostgres.getProfile( newUser.getID() ).getImageID() == null );
        assert ( profileStorePostgres.getAllProfiles().size() == 1 );

    }

    @Test
    void testDeleteBio() {

        // Add a profile
        profileStorePostgres.addProfile( newUser.getID(), newBio );
        assert ( profileStorePostgres.getProfile( newUser.getID() ).getBio() != null );

        // Test deleting the bio
        profileStorePostgres.deleteBio( newUser.getID() );

        // Test the retrieved profile
        assert ( profileStorePostgres.getProfile( newUser.getID() ).getImageID() == null );
        assert ( profileStorePostgres.getAllProfiles().size() == 1 );

    }

    @Test
    void testAddProfile() {

        // Test adding a profile
        Profile newProfile = profileStorePostgres.addProfile( newUser.getID(), newBio );

        // Test the profile contents
        assert ( newProfile.getUserID() == newUser.getID() );
        assert ( newProfile.getBio().equals(newBio) );
        assert ( newProfile.getImageID() == null );

}

    @Test
    void testGetProfile() {

        // Add a profile
        Profile newProfile = profileStorePostgres.addProfile( newUser.getID(), newBio, newPath, false );

        // Test retrieving the profile
        assert ( profileStorePostgres.getProfile( newUser.getID() ).equals(newProfile) );

        // Delete the profile picture
        profileStorePostgres.deleteProfilePicture( newUser.getID() );

    }

    @Test
    void testChangeBio() {

        // Add a profile
        Profile newProfile = profileStorePostgres.addProfile( newUser.getID(), newBio );

        // Test changing the bio
        String tempBio = "github.com/Sectoooooor";
        profileStorePostgres.changeBio( newUser.getID(), tempBio );

        // Test the bio contents
        assert ( profileStorePostgres.getProfile( newUser.getID() ).getBio().equals(tempBio) );

    }

//    @Test
//    void testChangeProfilePicture() {
//
//        // Reset the store for other tests
//        profileStorePostgres.delete();
//
//    }

}