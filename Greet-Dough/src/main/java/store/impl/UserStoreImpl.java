package store.impl;

import model.User;
import store.model.StoreWithID;
import store.model.UserStore;

// IMPLEMENT A PREFIX TRIE TO ALLOW SEARCHING FOR USERS
public class UserStoreImpl extends StoreWithID<User> implements UserStore {

    public UserStoreImpl() {
        super();
    }

    public UserStoreImpl( int start ) {
        super(start);
    }

    @Override
    public User getUser( int ID ) {
        return super.get(ID);
    }

    @Override
    public boolean hasUser( int ID ){
        return super.has(ID);
    }

    @Override
    public void addUser( User newUser ) {
        super.add( newUser.getID(), newUser );
    }

    @Override
    public User addUser( String name ) {

        // Create the user
        int userID = super.getFreeID();
        User tempUser = new User( name, userID );

        // Add the user
        this.addUser( tempUser );
        return tempUser;

    }

    @Override
    public boolean deleteUser( int ID ) {
        return super.delete(ID);
    }

}
