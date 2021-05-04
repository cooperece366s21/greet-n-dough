package store.postgres;

import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import static org.junit.jupiter.api.Assertions.*;

class LoginStorePostgresTest extends LoginStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    public LoginStorePostgresTest() {
        super(jdbi);
    }

    @Test
    void test() {

        UserStorePostgres userStorePostgres = new UserStorePostgres(jdbi);
        LoginStorePostgres loginStorePostgres = new LoginStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        // Create a user and get a token for that user
        User newUser = userStorePostgres.addUser("Dan");
        String token = loginStorePostgres.addSession( newUser.getID() );

        // Get the user ID using the token
        Integer uid = loginStorePostgres.getUserID(token);

        assert ( loginStorePostgres.hasSession(token) );
        assertNull( loginStorePostgres.getUserID("abc") );
        assert ( uid == newUser.getID() );
        assertNotNull(token);

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