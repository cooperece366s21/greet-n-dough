package store.model;

public interface LoginStore {

    String addSession( int uid );

    boolean hasSession( String token );

    void deleteSession( String token );

    Integer getUserID( String token );

}
