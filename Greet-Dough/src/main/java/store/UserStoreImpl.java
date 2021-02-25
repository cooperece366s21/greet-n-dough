package store;

import model.User;
import utility.Store;

public class UserStoreImpl extends Store<User> {

    public UserStoreImpl() {
        super();
    }

    public UserStoreImpl( int start ) {
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
