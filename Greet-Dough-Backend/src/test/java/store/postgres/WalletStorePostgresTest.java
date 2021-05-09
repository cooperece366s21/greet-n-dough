package store.postgres;

import model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WalletStorePostgresTest extends WalletStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    private static UserStorePostgres userStorePostgres;
    private static WalletStorePostgres walletStorePostgres;

    private static User steve;
    private static User juan;

    public WalletStorePostgresTest() {
        super(jdbi);
    }

    @BeforeAll
    static void setUpAll() {

        // Delete all the databases (only use the relevant ones)
        ResetDao.deleteAll(jdbi);

        userStorePostgres = new UserStorePostgres(jdbi);
        walletStorePostgres = new WalletStorePostgres(jdbi);

    }

    @AfterAll
    static void tearDownAll() {
        ResetDao.reset(jdbi);
    }

    @BeforeEach
    void setUpEach() {

        // Delete the databases
        walletStorePostgres.delete();
        userStorePostgres.delete();

        // Initialize the databases
        userStorePostgres.init();
        walletStorePostgres.init();

        // Add users
        steve = userStorePostgres.addUser("Steve Ree");
        juan = userStorePostgres.addUser("Juan Lam");

    }

    @Test
    void testAddUser() {

        // Test retrieving an invalid user
        assertNull( walletStorePostgres.getBalance( steve.getID() ) );

        // Test adding a user
        walletStorePostgres.addUser( steve.getID() );
        walletStorePostgres.addUser( juan.getID(), new BigDecimal("10.50") );

        // Check balances
        assert ( walletStorePostgres.getBalance( steve.getID() ).compareTo( BigDecimal.ZERO ) == 0 );   // Should be 0 by default
        assert ( walletStorePostgres.getBalance( juan.getID() ).compareTo( new BigDecimal("10.50") ) == 0 );
        assertNull( walletStorePostgres.getBalance(-1) );                                      // Should be null b/c user doesn't exist

    }

    @Test
    void testStripTrailingZeros() {

        // Test .stripTrailingZeros()
        BigDecimal amount = new BigDecimal("150000");
        amount = amount.stripTrailingZeros();
        assertFalse( amount.compareTo(BigDecimal.ZERO) != 1 || amount.scale() > 2 );

    }

    @Test
    void testChangeBalance() {

        // Add users
        walletStorePostgres.addUser( steve.getID() );
        walletStorePostgres.addUser( juan.getID(), new BigDecimal("10.50") );

        // Test changing balances
        walletStorePostgres.addToBalance( steve.getID(), new BigDecimal("1.005") );
        walletStorePostgres.subtractFromBalance( juan.getID(), new BigDecimal("0.50") );
        walletStorePostgres.addToBalance( -1, new BigDecimal("15") );

        // Check balances
        assert ( walletStorePostgres.getBalance( steve.getID() ).compareTo( new BigDecimal("1.005") ) == 0 );
        assert ( walletStorePostgres.getBalance( juan.getID() ).compareTo( BigDecimal.TEN ) == 0 );
        assertNull( walletStorePostgres.getBalance(-1) );

    }

}