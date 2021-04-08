package store.model;

public interface PasswordStore {

    void addPassword( String email, int uid, String password );

    // Get the uid given an email and a password
    Integer getUserID( String email, String password );

    // Checks if user already has a stored password
    boolean hasPassword( String email );

}
