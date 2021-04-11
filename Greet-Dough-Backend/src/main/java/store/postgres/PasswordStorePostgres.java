package store.postgres;

import store.model.PasswordStore;
import model.User;
import org.jdbi.v3.core.Jdbi;
import utility.ResetDao;

/////////////////////////////////////////////// CHANGE HASH TO SHA256
public class PasswordStorePostgres implements PasswordStore {

    // For testing purposes
    public static void main( String[] args ) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        PasswordStorePostgres PasswordStorePostgres = new PasswordStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        // Create a user
        User newUser = UserStorePostgres.addUser("B. Ryan");
        User userAfterWrite = UserStorePostgres.getUser( newUser.getID() );

        // Add the user with an associated email and password
        String email = "SweetNDough@gmail.com";
        String pass = "password123";
        if ( PasswordStorePostgres.addPassword( email, newUser.getID(), pass ) == 0 ) {
            System.err.println("Duplicate Email");
        } else {
            System.out.println("Password Insert Successful");
        }

        // Check if the password is correct
        System.out.println( PasswordStorePostgres.hasEmail(email) );
        System.out.println( PasswordStorePostgres.getUserID(email, pass) );

        // Test adding a second password for the same email
        if ( PasswordStorePostgres.addPassword( email, newUser.getID(), "lol" ) == 0 ) {
            System.out.println("Duplicate Email");
        } else {
            System.err.println("SHOULD NOT HAPPEN!!!");
        }

        // Test changing the email/password
        String newEmail = "MeatNGrow@aol.com";
        String newPass = "password1234";
        PasswordStorePostgres.updateEmail( email, newEmail );
        PasswordStorePostgres.updatePassword( newEmail, newPass );

        // Check if update was successful
        System.out.println( PasswordStorePostgres.hasEmail(newEmail) );
        System.out.println( PasswordStorePostgres.getUserID(newEmail, newPass) );

        // Test deleting the user
        UserStorePostgres.deleteUser( userAfterWrite.getID() );
        System.out.println( PasswordStorePostgres.hasEmail(email) );

    }

    private final Jdbi jdbi;

    public PasswordStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle(handle -> handle.attach(PasswordDao.class).deleteTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(PasswordDao.class).createTable());
    }

    @Override
    public int addPassword( String email, int uid, String password ) {
        return jdbi.withHandle( handle -> handle.attach(PasswordDao.class).insertPassword(email, uid, password) );
    }

    // Checks if the email + password match an entry in the DB
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
        jdbi.useHandle( handle -> handle.attach(PasswordDao.class).updateEmail( oldEmail, newEmail ) );
    }

    @Override
    public void updatePassword( String email, String newPassword ) {
        jdbi.useHandle( handle -> handle.attach(PasswordDao.class).updatePassword( email, newPassword ) );
    }


}
