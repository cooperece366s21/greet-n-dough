package store;

import utility.Relation;
import java.util.ArrayList;

// Store subscriptions as ArrayList or HashMap?
// A single user's subscriptions probably won't be so large that a linear search will take very long
public class SubStore extends Relation {

    public SubStore() {
        super();
    }

    public ArrayList<Integer> getSubscriptions( int ID ) {
        return super.get(ID);
    }

    public int addSubscription( int curUser, int targetUser ) {
        return super.add( curUser, targetUser );
    }

    // Removes targetUser from curUser's subscriptions
    public int removeSubscription( int curUser, int targetUser ) {
        return super.remove( curUser, targetUser );
    }

    // Deletes user along with all subscriptions
    public boolean deleteUser( int ID ) {
        return super.delete(ID);
    }

}
