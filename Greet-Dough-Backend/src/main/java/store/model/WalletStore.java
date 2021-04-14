package store.model;

import java.math.BigDecimal;

public interface WalletStore {

    BigDecimal getBalance( int uid );

    // By default, user has a balance of 0
    void addUser( int uid );

    void addUser( int uid, BigDecimal balance );

    void addToBalance( int uid, BigDecimal amount );

    void withdrawFromBalance( int uid, BigDecimal amount );

}
