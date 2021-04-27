package store.postgres;

import model.User;
import store.model.UserStore;

import org.jdbi.v3.core.Jdbi;

import java.util.LinkedList;

public class UserStorePostgres implements UserStore {

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
    protected LinkedList<User> getUser() {
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
    public LinkedList<User> searchUsers( String name ) {
        return jdbi.withHandle( handle -> handle.attach(UserDao.class).searchUsers(name) );
    }

    @Override
    public void changeName( int uid, String newName ) {
        jdbi.useHandle( handle -> handle.attach(UserDao.class).changeName(uid, newName) );
    }

}
