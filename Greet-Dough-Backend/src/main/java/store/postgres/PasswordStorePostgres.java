package store.postgres;

import model.User;
import org.jdbi.v3.core.Jdbi;

public class PasswordStorePostgres {

    // For testing purposes
    public static void main( String[] args ) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        PasswordStorePostgres PasswordStorePostgres = new PasswordStorePostgres(jdbi);

        // Used to DROP and CREATE the users table
        PasswordStorePostgres.reset();
        PasswordStorePostgres.init();

        // Create a user and add an associated password
        User newUser = UserStorePostgres.addUser("B. Ryan");
        User userAfterWrite = UserStorePostgres.getUser( newUser.getID() );
        String pass = "password123";
        PasswordStorePostgres.addPassword( newUser.getID(), pass );

        // Check if the password is correct
        System.out.println( PasswordStorePostgres.hasPassword( newUser.getID() ) );
        System.out.println( PasswordStorePostgres.isCorrectPassword(newUser.getID(), pass) );

        // Test deleting the user
        UserStorePostgres.deleteUser( userAfterWrite.getID() );
        System.out.println( PasswordStorePostgres.hasPassword( newUser.getID() ) );

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

    public void addPassword( int uid, String password ) {
        jdbi.useHandle( handle -> handle.attach(PasswordDao.class).insertPassword(uid, password) );
    }

    public boolean isCorrectPassword( int uid, String password ) {
        return jdbi.withHandle( handle -> handle.attach(PasswordDao.class).isCorrectPassword(uid, password) );
    }

    // Checks if user already has a stored password
    public boolean hasPassword( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(PasswordDao.class).hasPassword(uid) );
    }

}
