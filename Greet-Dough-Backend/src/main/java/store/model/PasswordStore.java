package store.model;

public interface PasswordStore {

    // Returns the number of rows affected
    //      E.g. If the email already exists, will return 0
    int addPassword( String email, int uid, String password );

    // Get the uid given an email and a password
    Integer getUserID( String email, String password );

    // Checks if email already has an associated password
    boolean hasEmail( String email );

    void updateEmail( String oldEmail, String newEmail );

    // Should only be used after checking getUserID() to validate the old password
    void updatePassword( String email, String newPassword );

}
