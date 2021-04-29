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

    public ProfileStorePostgresTest() {
        super(jdbi);
    }

    @Test
    void test() {

        UserStorePostgres userStorePostgres = new UserStorePostgres(jdbi);
        ProfileStorePostgres profileStorePostgres = new ProfileStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        // Add a user and a post
        User newUser = userStorePostgres.addUser("Vincent Zheng");

        // Get local image
        FileSystem fileSys = FileSystems.getDefault();
        Path tempPath = fileSys.getPath( System.getProperty("user.dir") );
        for ( int a=0; a<3; a++ ) {
            tempPath = tempPath.getParent();
        }
        Path newPath = fileSys.getPath( tempPath.toString() + File.separator + "beardKoolmodo.png" );

        // Test adding a profile
        String newBio = "Bachelors in CompE and Signals, and Masters in EE";
        Profile newProfile = profileStorePostgres.addProfile( newUser.getID(), newBio, newPath.toString() );

        // Test the profile contents
        assert ( newProfile.getUserID() == newUser.getID() );
        assert ( newProfile.getBio().equals(newBio) );

        // Test retrieving the profile
        assert ( profileStorePostgres.getProfile( newUser.getID() ).equals(newProfile) );

    }

}