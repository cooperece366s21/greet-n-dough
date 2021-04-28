package store.postgres;

import store.model.LoginStore;

import org.jdbi.v3.core.Jdbi;

public class LoginStorePostgres implements LoginStore {

    private final Jdbi jdbi;

    public LoginStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle( handle -> handle.attach(LoginDao.class).deleteTable() );
    }

    public void init() {

        jdbi.useHandle( handle -> handle.attach(LoginDao.class).createTable() );
        jdbi.useHandle( handle -> handle.attach(LoginDao.class).createTrigger() );
        jdbi.useHandle( handle -> handle.attach(LoginDao.class).setTrigger() );

    }

    @Override
    public String addSession( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(LoginDao.class).insertSession(uid) );
    }

    @Override
    public boolean hasSession( String token ) {
        return getUserID(token) != null;
    }

    @Override
    public void deleteSession( String token ) {
        jdbi.useHandle( handle -> handle.attach(LoginDao.class).deleteSession(token) );
    }

    @Override
    public Integer getUserID( String token ) {
        return jdbi.withHandle( handle -> handle.attach(LoginDao.class).getUserID(token) ).orElse(null);
    }

}