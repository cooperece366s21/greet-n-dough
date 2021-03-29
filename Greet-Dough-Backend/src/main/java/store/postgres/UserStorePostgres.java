package store.postgres;

import model.User;
import store.model.UserStore;
import org.jdbi.v3.core.Jdbi;

public class UserStorePostgres implements UserStore {

    private final Jdbi jdbi;

    public UserStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    @Override
    public User getUser(int ID) {
        return null;
    }

    @Override
    public boolean hasUser(int ID) {
        return false;
    }

    @Override
    public void addUser(User newUser) {

    }

    @Override
    public User addUser(String name) {
        return null;
    }

    @Override
    public boolean deleteUser(int ID) {
        return false;
    }
}
