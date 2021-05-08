package store.postgres;

import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import static org.junit.jupiter.api.Assertions.*;

class LoginStorePostgresTest extends LoginStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    private static UserStorePostgres userStorePostgres;
    private static ImageStorePostgres imageStorePostgres;
    private static LoginStorePostgres loginStorePostgres;

    private static User newUser;

    public LoginStorePostgresTest() {
        super(jdbi);
    }

    @BeforeAll
    static void setUpAll() {

        // Delete all the databases (only use the relevant ones)
        ResetDao.deleteAll(jdbi);

        userStorePostgres = new UserStorePostgres(jdbi);
        imageStorePostgres = new ImageStorePostgres(jdbi);
        loginStorePostgres = new LoginStorePostgres(jdbi);

    }

    @AfterAll
    static void tearDownAll() {
        ResetDao.reset(jdbi);
    }

    @BeforeEach
    void setUpEach() {

        // Delete the databases
        loginStorePostgres.delete();
        imageStorePostgres.delete();
        userStorePostgres.delete();

        // Initialize the databases
        userStorePostgres.init();
        imageStorePostgres.init();
        loginStorePostgres.init();

        // Create a user
        newUser = userStorePostgres.addUser("Dan");

    }

    @Test
    void testAddSession() {

        // Get a token for the user
        String token = loginStorePostgres.addSession( newUser.getID() );

        // Get the user ID using the token
        Integer uid = loginStorePostgres.getUserID(token);

        assert ( loginStorePostgres.hasSession(token) );
        assertNull( loginStorePostgres.getUserID("abc") );
        assert ( uid == newUser.getID() );
        assertNotNull(token);

    }

    @Test
    void testDeleteSession() {

        // Get a token for the user
        String token = loginStorePostgres.addSession( newUser.getID() );

        // Get the user ID using the token
        Integer uid = loginStorePostgres.getUserID(token);

        // Test deleting a session
        loginStorePostgres.deleteSession(token);
        assertFalse( loginStorePostgres.hasSession(token) );

    }

    @Test
    void testDeleteUser() {

        // Get a token for the user
        String token = loginStorePostgres.addSession( newUser.getID() );

        // Get the user ID using the token
        Integer uid = loginStorePostgres.getUserID(token);

        // Test deleting a session
        loginStorePostgres.deleteSession(token);
        assertFalse( loginStorePostgres.hasSession(token) );

        // Test deleting a user
        //      Should delete cascade the token
        String newToken = loginStorePostgres.addSession(uid);
        userStorePostgres.deleteUser(uid);
        assertFalse( loginStorePostgres.hasSession(newToken) );

    }

}