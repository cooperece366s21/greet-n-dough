package store.model;

import model.User;

import java.io.Serializable;

public interface UserStore extends Serializable {

    User getUser( int ID );

    void addUser( User newUser );

    boolean deleteUser( int ID );

    int getFreeID();

}
