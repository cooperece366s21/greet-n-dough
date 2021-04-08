package store.model;

public interface PasswordStore {

    void addPassword( String email, int uid, String password );

    // Get the uid given an email and a password
    Integer getUserID( String email, String password );

    // Checks if email already has an associated password
    boolean hasEmail( String email );

}
