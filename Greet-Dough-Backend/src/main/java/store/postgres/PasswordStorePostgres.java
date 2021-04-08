package store.postgres;

import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.postgresql.util.PSQLException;
import store.model.PasswordStore;
import model.User;
import org.jdbi.v3.core.Jdbi;

/////////////////////////////////////////////// CHANGE HASH TO SHA256
public class PasswordStorePostgres implements PasswordStore {

    // For testing purposes
    public static void main( String[] args ) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        PasswordStorePostgres PasswordStorePostgres = new PasswordStorePostgres(jdbi);

        // Used to DROP and CREATE the users table
        PasswordStorePostgres.reset();
        PasswordStorePostgres.init();

        // Create a user
        User newUser = UserStorePostgres.addUser("B. Ryan");
        User userAfterWrite = UserStorePostgres.getUser( newUser.getID() );

        // Add the user with an associated email and password
        String email = "SweetNDough@gmail.com";
        String pass = "password123";
        PasswordStorePostgres.addPassword( email, newUser.getID(), pass );

        // Check if the password is correct
        System.out.println( PasswordStorePostgres.hasEmail(email) );
        System.out.println( PasswordStorePostgres.getUserID(email, pass) );

        // Test adding a second password for the same email
        PasswordStorePostgres.addPassword(email, newUser.getID(), "lol");

        // Test deleting the user
        UserStorePostgres.deleteUser( userAfterWrite.getID() );
        System.out.println( PasswordStorePostgres.hasEmail(email) );

    }

    private final Jdbi jdbi;

    public PasswordStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void reset() {
        jdbi.useHandle(handle -> handle.attach(PasswordDao.class).resetTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(PasswordDao.class).createTable());
    }

    @Override
    public void addPassword( String email, int uid, String password ) {

        try {
            jdbi.useHandle( handle -> handle.attach(PasswordDao.class).insertPassword(email, uid, password) );
        } catch( UnableToExecuteStatementException e ) {
            System.err.println("Error: Email already exists.\n" + e);
        }

    }

    // Checks if the email + password match an entry in the DB
    //      If there is a match, returns the associated uid;
    //      Otherwise, null
    @Override
    public Integer getUserID( String email, String password ) {
        return jdbi.withHandle( handle -> handle.attach(PasswordDao.class).isCorrectPassword(email, password) ).orElse(null);
    }

    @Override
    public boolean hasEmail( String email ) {
        return jdbi.withHandle( handle -> handle.attach(PasswordDao.class).hasEmail(email) );
    }

    @Override
    public void updateEmail( String oldEmail, String newEmail ) {

    }

    @Override
    public void updatePassword( String email, String newPassword ) {

    }


}
