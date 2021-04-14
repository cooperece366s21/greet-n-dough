package store.postgres;

import store.model.WalletStore;
import utility.ResetDao;

import org.jdbi.v3.core.Jdbi;

// Method to add directly to DB?
// Need to have has()?
public class WalletStorePostgres implements WalletStore {

    public static void main( String[] args ) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        WalletStorePostgres WalletStorePostgres = new WalletStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

    }

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
    public float getBalance( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(WalletDao.class).getBalance( uid ) );
    }

    @Override
    public void addUser( int uid ) {
        addUser(uid,0);
    }

    @Override
    public void addUser( int uid, float balance ) {
        jdbi.useHandle( handle -> handle.attach(WalletDao.class).insertUser( uid, balance ) );
    }

    @Override
    public void addToBalance( int uid, float amount ) {

        float curBalance = getBalance(uid);
        float newBalance = curBalance + amount;

        jdbi.useHandle( handle -> handle.attach(WalletDao.class).updateBalance( uid, newBalance ) );

    }

    @Override
    public void withdrawFromBalance( int uid, float amount ) {
        addToBalance(uid, -1*amount);
    }

}
