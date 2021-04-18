package store.postgres;

import model.User;
import store.model.UserStore;
import utility.ResetDao;

import org.jdbi.v3.core.Jdbi;
import java.util.List;
import java.util.stream.Collectors;

// ADD PROFILE PICTURES
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

        // Test searching for a user given the first portion of their name
        User jon = UserStorePostgres.addUser("Jon");

        // Prints the names of the users given a list of users
        //      Playing around with mapping a list
        System.out.println( UserStorePostgres.searchUsers("Jo").stream().map(User::getName).collect(Collectors.toList()) );
        System.out.println( UserStorePostgres.searchUsers("jo").stream().map(User::getName).collect(Collectors.toList()) );

        // Test changing the user's name
        UserStorePostgres.changeName( jon.getID(), "John" );
        System.out.println( UserStorePostgres.getUser( jon.getID() ).getName() );

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

    /**
     * Currently only used for testing.
     *
     * @return all users in the database
     */
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

    @Override
    public List<User> searchUsers( String name ) {
        return jdbi.withHandle( handle -> handle.attach(UserDao.class).searchUsers(name) );
    }

    @Override
    public void changeName( int uid, String name ) {
        jdbi.useHandle( handle -> handle.attach(UserDao.class).changeName(uid, name) );
    }

}
