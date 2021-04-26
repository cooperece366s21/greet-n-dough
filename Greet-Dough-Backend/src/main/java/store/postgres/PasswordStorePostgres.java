package store.postgres;

import store.model.PasswordStore;
import org.jdbi.v3.core.Jdbi;

public class PasswordStorePostgres implements PasswordStore {

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
        return jdbi.withHandle( handle -> handle.attach(PasswordDao.class).addPassword(email, uid, password) );
    }

    /**
     * Checks if the email + password match an entry in the DB.
      */
    @Override
    public Integer getUserID( String email, String password ) {
        return jdbi.withHandle( handle -> handle.attach(PasswordDao.class).isCorrectPassword(email, password) ).orElse(null);
    }

    @Override
    public boolean hasEmail( String email ) {
        return jdbi.withHandle( handle -> handle.attach(PasswordDao.class).hasEmail(email) );
    }

    @Override
    public void changeEmail( String oldEmail, String newEmail ) {
        jdbi.useHandle( handle -> handle.attach(PasswordDao.class).changeEmail( oldEmail, newEmail ) );
    }

    @Override
    public void changePassword( String email, String newPassword ) {
        jdbi.useHandle( handle -> handle.attach(PasswordDao.class).changePassword( email, newPassword ) );
    }


}
