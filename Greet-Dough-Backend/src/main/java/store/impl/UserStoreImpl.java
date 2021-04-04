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
    public User addUser( String name ) {

        // Create the user
        int userID = super.getFreeID();
        User tempUser = new User( name, userID );

        // Add the user
        super.add( tempUser.getID(), tempUser );
        return tempUser;

    }

    @Override
    public void deleteUser( int ID ) {
        super.delete(ID);
    }

}
