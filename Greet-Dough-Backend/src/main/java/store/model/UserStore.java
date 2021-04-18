package store.model;

import model.User;

import java.util.List;

public interface UserStore {

    User getUser( int uid );

    boolean hasUser( int uid );

    User addUser( String name );

    void deleteUser( int uid );

    /**
     * Matches user's names based on the regex expression |name*|, where name is the provided argument.
     *
     * @param   name    a string representing the first portion of a user's name
     * @return          a list of users, where each user's name matches the regex expression
     */
    List<User> searchUsers( String name );

    void changeName( int uid, String name );

}
