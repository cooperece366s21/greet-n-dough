package store.model;

public interface LoginStore {

    /**
     * @return a token for the specified uid
      */
    String addSession( int uid );

    /**
     * Checks if the specified token is valid.
     */
    boolean hasSession( String token );

    /**
     * Invalidates the specified token.
      */
    void deleteSession( String token );

    /**
     * The method returns the associated uid if the token is valid.
     * The method returns null if the token is invalid.
     *
     * @return the uid associated with a token
      */
    Integer getUserID( String token );

    /**
     * Deletes all soft deleted user tokens.
     */
    void clearDeleted();

}
