package store.model;

import model.User;

import java.util.List;

public interface UserStore {

    User getUser( int uid );

    boolean hasUser( int uid );

    User addUser( String name );

    void deleteUser( int uid );

    // Returns a list of users given the first portion of their name
    List<User> searchUsers( String name );

    void changeName( int uid, String name );

}
