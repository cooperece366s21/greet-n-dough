package store.model;

import java.math.BigDecimal;

public interface WalletStore {

    /**
     * Retrieves a user's current balance.
     * The method returns null if the user is not in the database.
     *
     * @return the user's current balance
      */
    BigDecimal getBalance( int uid );

    /**
     * Adds a user to the database. By default, user has a balance of 0.
      */
    void addUser( int uid );

    void addUser( int uid, BigDecimal balance );

    void addToBalance( int uid, BigDecimal amount );

    void subtractFromBalance( int uid, BigDecimal amount );

    /**
     * Placeholder function b/c it is out of scope for this project.
     * Would be used to verify a user's attempt to add money to the account.
     *
     * @return true, always
     */
    static boolean verifyPurchase() {
        return true;
    }

}
