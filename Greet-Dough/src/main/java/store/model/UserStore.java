package store.model;

import model.User;

public interface UserStore {

    User getUser( int ID );

    boolean hasUser( int ID );

    void addUser( User newUser );

    boolean deleteUser( int ID );

    int getFreeID();

}
