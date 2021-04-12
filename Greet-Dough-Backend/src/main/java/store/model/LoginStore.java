package store.model;

public interface LoginStore {

    // Returns a token for the user
    String addSession( int uid );

    // Checks if a token is valid
    boolean hasSession( String token );

    // Invalidates a token
    void deleteSession( String token );

    // Returns the uid associated with a token
    // If the token is invalid, returns null
    Integer getUserID( String token );

}
