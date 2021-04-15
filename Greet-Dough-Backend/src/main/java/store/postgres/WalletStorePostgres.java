package store.postgres;

import model.User;
import store.model.WalletStore;
import utility.ResetDao;

import org.jdbi.v3.core.Jdbi;
import java.math.BigDecimal;

// Method to add directly to DB?
// Need to have has()?
public class WalletStorePostgres implements WalletStore {

    public static void main( String[] args ) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        WalletStorePostgres WalletStorePostgres = new WalletStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        User steve = UserStorePostgres.addUser("Steve Ree");
        User juan = UserStorePostgres.addUser("Juan Lam");

        // Test adding a user
        WalletStorePostgres.addUser( steve.getID() );
        WalletStorePostgres.addUser( juan.getID(), new BigDecimal("10.50") );

        // Check balances
        System.out.println( WalletStorePostgres.getBalance( steve.getID() ) );
        System.out.println( WalletStorePostgres.getBalance( juan.getID() ) );

        // Test changing balances
        WalletStorePostgres.addToBalance( steve.getID(), new BigDecimal("1.005") ); // Rounds to 1.01
        WalletStorePostgres.withdrawFromBalance( juan.getID(), new BigDecimal("0.50") );

        // Check balances
        System.out.println( WalletStorePostgres.getBalance( steve.getID() ) );
        System.out.println( WalletStorePostgres.getBalance( juan.getID() ) );

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
    public BigDecimal getBalance( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(WalletDao.class).getBalance( uid ) );
    }

    @Override
    public void addUser( int uid ) {
    addUser(uid,BigDecimal.ZERO);
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
    public void withdrawFromBalance( int uid, BigDecimal amount ) {
        addToBalance(uid, amount.negate());
    }

}
