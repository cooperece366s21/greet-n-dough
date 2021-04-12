package store.impl;

import model.User;
import store.model.StoreWithID;
import store.model.UserStore;

import java.util.List;

public class UserStoreImpl extends StoreWithID<User> implements UserStore {

    public UserStoreImpl() {
        super();
    }

    public UserStoreImpl( int start ) {
        super(start);
    }

    @Override
    public User getUser( int uid ) {
        return super.get(uid);
    }

    @Override
    public boolean hasUser( int uid ){
        return super.has(uid);
    }

    @Override
    public User addUser( String name ) {

        // Create the user
        int uid = super.getFreeID();
        User tempUser = new User( name, uid );

        // Add the user
        super.add( tempUser.getID(), tempUser );
        return tempUser;

    }

    @Override
    public void deleteUser( int uid ) {
        super.delete(uid);
    }

    @Override
    public List<String> searchUsers( String name ) {
        return null;
    }

}
