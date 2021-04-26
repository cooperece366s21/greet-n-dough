package store.model;

public interface PasswordStore {

    /**
     * Adds the email and password associated with a uid to the database.
     * The method returns an int representing the number of rows affected.
     * This should always be 0 or 1.
     * E.g. If the email already exists, will return 0.
     *
     * @return the number of rows affected.
      */
    int addPassword( String email, int uid, String password );

    /**
     * Gets the uid given an email and a password.
     * The method returns the associated uid if the email and password match an entry in the database.
     * The method returns null if there is no match.
     *
     * @return the associated uid
      */
    Integer getUserID( String email, String password );

    /**
     * Checks if email already has an associated password.
      */
    boolean hasEmail( String email );

    void changeEmail( String oldEmail, String newEmail );

    /**
     * The method should only be used after checking getUserID() to validate the old password.
      */
    void changePassword( String email, String newPassword );

}
