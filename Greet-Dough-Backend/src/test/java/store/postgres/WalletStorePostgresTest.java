package store.postgres;

import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import utility.ResetDao;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WalletStorePostgresTest extends WalletStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    public WalletStorePostgresTest() {
        super(jdbi);
    }

    @Test
    void test() {

        UserStorePostgres userStorePostgres = new UserStorePostgres(jdbi);
        WalletStorePostgres walletStorePostgres = new WalletStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        User steve = userStorePostgres.addUser("Steve Ree");
        User juan = userStorePostgres.addUser("Juan Lam");

        // Test retrieving an invalid user
        assert ( walletStorePostgres.getBalance( steve.getID() ) == null );

        // Test adding a user
        walletStorePostgres.addUser( steve.getID() );
        walletStorePostgres.addUser( juan.getID(), new BigDecimal("10.50") );

        // Check balances
        assert ( walletStorePostgres.getBalance( steve.getID() ).compareTo( BigDecimal.ZERO ) == 0 );   // Should be 0 by default
        assert ( walletStorePostgres.getBalance( juan.getID() ).compareTo( new BigDecimal("10.50") ) == 0 );
        assert ( walletStorePostgres.getBalance(-1) == null );                              // Should be null b/c user doesn't exist

        // Test .stripTrailingZeros()
        BigDecimal amount = new BigDecimal("150000");
        amount = amount.stripTrailingZeros();
        assert ( !(amount.compareTo(BigDecimal.ZERO) != 1 || amount.scale() > 2) );

        // Test changing balances
        walletStorePostgres.addToBalance( steve.getID(), new BigDecimal("1.005") );
        walletStorePostgres.subtractFromBalance( juan.getID(), new BigDecimal("0.50") );
        walletStorePostgres.addToBalance( -1, new BigDecimal("15") );

        // Check balances
        assert ( walletStorePostgres.getBalance( steve.getID() ).compareTo( new BigDecimal("1.005") ) == 0 );
        assert ( walletStorePostgres.getBalance( juan.getID() ).compareTo( BigDecimal.TEN ) == 0 );
        assert ( walletStorePostgres.getBalance(-1) == null );

    }

}