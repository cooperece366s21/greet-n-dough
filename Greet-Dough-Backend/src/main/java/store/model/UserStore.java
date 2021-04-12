package store.model;

import model.User;

import java.util.List;

public interface UserStore {

    User getUser( int uid );

    boolean hasUser( int uid );

    User addUser( String name );

    void deleteUser( int uid );

    List<User> searchUsers( String name );

}
