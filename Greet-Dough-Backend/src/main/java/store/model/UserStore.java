package store.model;

import model.User;

public interface UserStore {

    User getUser( int ID );

    boolean hasUser( int ID );

    User addUser( String name );

    void deleteUser( int ID );

}
