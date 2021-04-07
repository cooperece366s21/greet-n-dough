package store.postgres;

import model.User;
import store.model.UserStore;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Optional;

// IMPLEMENT A PREFIX TRIE TO ALLOW SEARCHING FOR USERS?
public class UserStorePostgres implements UserStore {

    // For testing purposes
    public static void main( String[] args ) {

        UserStorePostgres UserStorePostgres =
                new UserStorePostgres(
                        GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough"));

        // Used to DROP and CREATE the users table

        UserStorePostgres.reset();
        UserStorePostgres.init();

        // Test adding and retrieving a user
        User newUser = UserStorePostgres.addUser("Josh");
        User userAfterWrite = UserStorePostgres.getUser( newUser.getID() );
        System.out.println( userAfterWrite.getID() + " " + userAfterWrite.getName() );
        System.out.println( UserStorePostgres.hasUser(newUser.getID()) );

        // Test deleting the user
        UserStorePostgres.deleteUser( userAfterWrite.getID() );
        System.out.println( UserStorePostgres.getUser() );

    }

    private final Jdbi jdbi;

    public UserStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void reset() {
        jdbi.useHandle(handle -> handle.attach(UserDao.class).resetTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(UserDao.class).createTable());
    }

    @Override
    public User getUser( int ID ) {
        return jdbi.withHandle( handle -> handle.attach(UserDao.class).getUser(ID) ).orElse(null);
    }

    public List<User> getUser() {
        return jdbi.withHandle( handle -> handle.attach(UserDao.class).listUsers() );
    }

    @Override
    public boolean hasUser( int ID ) {
        return jdbi.withHandle( handle -> handle.attach(UserDao.class).containsUser(ID) );
    }

    @Override
    public User addUser( String name ) {

        int ID = jdbi.withHandle( handle -> handle.attach(UserDao.class).insertUser(name) );
        return getUser(ID);

    }

    @Override
    public void deleteUser( int ID ) {
        jdbi.useHandle( handle -> handle.attach(UserDao.class).deleteUser(ID) );
    }

}
