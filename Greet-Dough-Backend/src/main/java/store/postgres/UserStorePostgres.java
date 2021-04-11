package store.postgres;

import model.User;
import store.model.UserStore;
import org.jdbi.v3.core.Jdbi;
import utility.ResetDao;

import java.util.List;

// IMPLEMENT A PREFIX TRIE TO ALLOW SEARCHING FOR USERS?
public class UserStorePostgres implements UserStore {

    // For testing purposes
    public static void main( String[] args ) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

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

    public void delete() {
        jdbi.useHandle(handle -> handle.attach(UserDao.class).deleteTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(UserDao.class).createTable());
    }

    @Override
    public User getUser( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(UserDao.class).getUser(uid) ).orElse(null);
    }

    public List<User> getUser() {
        return jdbi.withHandle( handle -> handle.attach(UserDao.class).listUsers() );
    }

    @Override
    public boolean hasUser( int uid ) {
        return getUser(uid) != null;
    }

    @Override
    public User addUser( String name ) {

        int uid = jdbi.withHandle( handle -> handle.attach(UserDao.class).insertUser(name) );
        return getUser(uid);

    }

    @Override
    public void deleteUser( int uid ) {
        jdbi.useHandle( handle -> handle.attach(UserDao.class).deleteUser(uid) );
    }

}
