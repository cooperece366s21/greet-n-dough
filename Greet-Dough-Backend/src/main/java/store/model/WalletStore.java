package store.model;

import java.math.BigDecimal;

public interface WalletStore {

    // Returns null if user does not have a balance
    //      E.g. User has not been added to table
    BigDecimal getBalance( int uid );

    // By default, user has a balance of 0
    void addUser( int uid );

    void addUser( int uid, BigDecimal balance );

    void addToBalance( int uid, BigDecimal amount );

    void subtractFromBalance( int uid, BigDecimal amount );

    // Placeholder function b/c out of scope for this project
    // Used to verify a user's attempt to add money to the account
    static boolean verifyPurchase() {
        return true;
    }

}
