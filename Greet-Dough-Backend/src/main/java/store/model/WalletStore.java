package store.model;

public interface WalletStore {

    float getBalance( int uid );

    // By default, user has a balance of 0
    void addUser( int uid );

    void addUser( int uid, float balance );

    void addToBalance( int uid, float amount );

    void withdrawFromBalance( int uid, float amount );

}
