package store.model;

import model.User;

// IMPLEMENT A PREFIX TRIE TO ALLOW SEARCHING FOR USERS
public class UserStoreImpl extends StoreWithID<User> {

    public UserStoreImpl() {
        super();
    }

    public UserStoreImpl(int start ) {
        super(start);
    }

    public User getUser( int ID ) {
        return super.get(ID);
    }

    public void addUser( User newUser ) {
        super.add( newUser.getID(), newUser );
    }

    public boolean deleteUser( int ID ) {
        return super.delete(ID);
    }

}
