package store.model;

import java.io.Serializable;
import java.util.ArrayList;

public interface SubStore extends Serializable {

    ArrayList<Integer> getSubscriptions( int ID );

    void addSubscription( int curUser, int targetUser );

    // Removes targetUser from curUser's subscriptions
    void removeSubscription( int curUser, int targetUser );

    // Deletes user along with all subscriptions
    boolean deleteUser( int ID );
}
