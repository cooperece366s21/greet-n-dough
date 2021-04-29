package store.postgres;

import model.Profile;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import utility.ResetDao;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ProfileStorePostgresTest extends ProfileStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    private final UserStorePostgres userStorePostgres;
    private final ProfileStorePostgres profileStorePostgres;

    private User newUser;
    private final String newPath;
    private final String newBio;

    public ProfileStorePostgresTest() {

        super(jdbi);
        userStorePostgres = new UserStorePostgres(jdbi);
        profileStorePostgres = new ProfileStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        // Add a user
        newUser = userStorePostgres.addUser("Vincent Zheng");

        // Add a bio
        newBio = "Bachelors in CompE and Signals, and Masters in EE";

        // Get local image
        FileSystem fileSys = FileSystems.getDefault();
        Path tempPath = fileSys.getPath( System.getProperty("user.dir") );
        for ( int a=0; a<3; a++ ) {
            tempPath = tempPath.getParent();
        }
        newPath = fileSys.getPath( tempPath.toString() + File.separator + "beardKoolmodo.png" ).toString();

    }

    @Test
    void testDeleteProfilePicture() {

        // Add a profile
        profileStorePostgres.addProfile( newUser.getID(), newBio, newPath );
        assert ( profileStorePostgres.getProfile( newUser.getID() ).getPath() != null );

        // Test deleting the profile picture
        profileStorePostgres.deleteProfilePicture( newUser.getID() );

        // Test the retrieved profile
        assert ( profileStorePostgres.getProfile( newUser.getID() ).getPath() == null );
        assert ( profileStorePostgres.getAllProfiles().size() == 1 );

        // Reset the store for other tests
        profileStorePostgres.delete();

    }

    @Test
    void testDeleteBio() {

        // Add a profile
        profileStorePostgres.addProfile( newUser.getID(), newBio );
        assert ( profileStorePostgres.getProfile( newUser.getID() ).getBio() != null );

        // Test deleting the bio
        profileStorePostgres.deleteBio( newUser.getID() );

        // Test the retrieved profile
        assert ( profileStorePostgres.getProfile( newUser.getID() ).getPath() == null );
        assert ( profileStorePostgres.getAllProfiles().size() == 1 );

        // Reset the store for other tests
        profileStorePostgres.delete();

    }

    @Test
    void testClearDeleted() {

        // Add a profile
        profileStorePostgres.addProfile( newUser.getID(), newBio, newPath );
        assert ( profileStorePostgres.getAllProfiles().size() == 1 );

        // Test deleting the user
        userStorePostgres.deleteUser( newUser.getID() );
        Profile tempProfile = profileStorePostgres.getProfile( newUser.getID() );
        assert ( profileStorePostgres.getAllProfiles().size() == 1 );

        // Test the retrieved profile
        assertNull( tempProfile.getBio() );
        assertNull( tempProfile.getPath() );

        profileStorePostgres.clearDeleted();

        // Test clearing the soft deleted profiles
        assert ( profileStorePostgres.getAllProfiles().isEmpty() );

        // Add the user back
        newUser = userStorePostgres.addUser( newUser.getName() );

        // Reset the store for other tests
        profileStorePostgres.delete();

    }

    @Test
    void testAddProfile() {

        // Test adding a profile
        String newBio = "Bachelors in CompE and Signals, and Masters in EE";
        Profile newProfile = profileStorePostgres.addProfile( newUser.getID(), newBio );

        // Test the profile contents
        assert ( newProfile.getUserID() == newUser.getID() );
        assert ( newProfile.getBio().equals(newBio) );
        assert ( newProfile.getPath() == null );

        // Reset the store for other tests
        profileStorePostgres.delete();

}

    @Test
    void testGetProfile() {

        // Add a profile
        Profile newProfile = profileStorePostgres.addProfile( newUser.getID(), newBio, newPath );

        // Test retrieving the profile
        assert ( profileStorePostgres.getProfile( newUser.getID() ).equals(newProfile) );

        // Delete the profile picture
        profileStorePostgres.deleteProfilePicture( newUser.getID() );

        // Reset the store for other tests
        profileStorePostgres.delete();

    }

    @Test
    void testChangeBio() {

        // Add a profile
        Profile newProfile = profileStorePostgres.addProfile( newUser.getID(), newBio );

        // Test changing the bio
        String newBio = "github.com/Sectoooooor";
        profileStorePostgres.changeBio( newUser.getID(), newBio );

        // Test the bio contents
        assert ( profileStorePostgres.getProfile( newUser.getID() ).getBio().equals(newBio) );

        // Reset the store for other tests
        profileStorePostgres.delete();

    }

//    @Test
//    void testChangeProfilePicture() {
//
//        // Reset the store for other tests
//        profileStorePostgres.delete();
//
//    }

}