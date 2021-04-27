package store.postgres;

import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import utility.ResetDao;

import static org.junit.jupiter.api.Assertions.*;

class PasswordStorePostgresTest extends PasswordStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    public PasswordStorePostgresTest() {
        super(jdbi);
    }

    @Test
    void test() {

        UserStorePostgres userStorePostgres = new UserStorePostgres(jdbi);
        PasswordStorePostgres passwordStorePostgres = new PasswordStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        // Create a user
        User newUser = userStorePostgres.addUser("B. Ryan");

        // Add the user with an associated email and password
        // Returns 0 if there is a duplicate email
        String email = "SweetNDough@gmail.com";
        String pass = "password123";
        assert ( passwordStorePostgres.addPassword( email, newUser.getID(), pass ) != 0 );

        // Check if the password is correct
        assert ( passwordStorePostgres.hasEmail(email) );
        assert ( passwordStorePostgres.getUserID(email, pass) == newUser.getID() );

        // Test adding a second password for the same email but lowercase
        assert ( passwordStorePostgres.addPassword( email.toLowerCase(), newUser.getID(), "lol" ) == 0 );

        // Test changing the email/password
        String newEmail = "WheatNGrow@aol.com";
        String newPass = "password1234";
        passwordStorePostgres.changeEmail( email, newEmail );
        passwordStorePostgres.changePassword( newEmail, newPass );

        // Check if update was successful
        assert ( passwordStorePostgres.hasEmail(newEmail) );
        assert ( passwordStorePostgres.getUserID( newEmail, newPass ) == newUser.getID() );

        // Test deleting the user
        //      Should delete cascade the email and password
        userStorePostgres.deleteUser( newUser.getID() );
        assertFalse( passwordStorePostgres.hasEmail(email) );

    }

}