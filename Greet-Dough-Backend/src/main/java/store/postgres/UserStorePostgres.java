package store.postgres;

import model.User;
import store.model.UserStore;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class UserStorePostgres implements UserStore {

    // For testing purposes
    public static void main( String[] args ) {

        UserStorePostgres UserStorePostgres =
                new UserStorePostgres(
                        GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough"));

        // Used to DROP and CREATE the users table
//        UserStorePostgres.reset();
//        UserStorePostgres.init();

        User yeet = UserStorePostgres.addUser("yeet");

        User userAfterWrite = UserStorePostgres.getUser( yeet.getID() );
        System.out.println( userAfterWrite.getID() + " " + userAfterWrite.getName() );
        System.out.println( UserStorePostgres.hasUser(yeet.getID()) );

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
        return jdbi.withHandle( handle -> handle.attach(UserDao.class).getUser(ID) );
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
