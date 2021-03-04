package store.relation;

import java.util.ArrayList;

// Store subscriptions as ArrayList or HashMap?
// A single user's subscriptions probably won't be so large that a linear search will take very long
public class SubStoreImpl extends Relation {

    public SubStoreImpl() {
        super();
    }

    public ArrayList<Integer> getSubscriptions( int ID ) {
        return super.get(ID);
    }

    public void addSubscription( int curUser, int targetUser ) {
        super.add( curUser, targetUser );
    }

    // Removes targetUser from curUser's subscriptions
    public void removeSubscription( int curUser, int targetUser ) {
        super.remove( curUser, targetUser );
    }

    // Deletes user along with all subscriptions
    public boolean deleteUser( int ID ) {
        return super.delete(ID);
    }

}
