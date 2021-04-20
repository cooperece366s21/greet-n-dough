package store.postgres;

import model.User;
import store.model.WalletStore;
import utility.ResetDao;

import org.jdbi.v3.core.Jdbi;
import java.math.BigDecimal;

public class WalletStorePostgres implements WalletStore {

    private final Jdbi jdbi;

    public WalletStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle(handle -> handle.attach(WalletDao.class).deleteTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(WalletDao.class).createTable());
    }

    @Override
    public BigDecimal getBalance( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(WalletDao.class).getBalance(uid) );
    }

    @Override
    public void addUser( int uid ) {
        addUser(uid, BigDecimal.ZERO);
    }

    @Override
    public void addUser( int uid, BigDecimal balance ) {
        jdbi.useHandle( handle -> handle.attach(WalletDao.class).insertUser( uid, balance ) );
    }

    @Override
    public void addToBalance( int uid, BigDecimal amount ) {
        jdbi.useHandle( handle -> handle.attach(WalletDao.class).addToBalance( uid, amount ) );
    }

    @Override
    public void subtractFromBalance( int uid, BigDecimal amount ) {
        addToBalance(uid, amount.negate());
    }

}
