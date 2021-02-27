package store.relation;

import store.StorageRetrieval;
import java.util.ArrayList;

public abstract class Relation extends StorageRetrieval< ArrayList<Integer> > {

    public Relation() {
        super();
    }

    // Essentially an implementation of ArrayList.getOrDefault
    //      Default is an empty ArrayList
    private ArrayList<Integer> getAttempt( int ID ) {

        ArrayList<Integer> tempList = super.get(ID);
        return ( tempList != null ? tempList : new ArrayList<>() );

    }

    /////////////////////////////////////////////// Check if targetUser is already related??
    // Relates curUser to targetUser
    //      curUser : [ User_1, User_2, ..., targetUser ]
    // Returns:
    //      0   if successful
    //      -1  if curUser == targetUser
    protected int add( int curUser, int targetUser ) {

        // Cannot subscribe to self
        if ( curUser == targetUser ) {
            return -1;
        }

        // Get the current list and add the new user
        ArrayList<Integer> curList = this.getAttempt( curUser );
        curList.add( targetUser );

        // Update the database
        super.add( curUser, curList );
        return 0;

    }

    // Returns:
    //      0   if successful
    //      -1  if targetUser is not in the list
    protected int remove( int curUser, int targetUser ) {

        // Get the current list
        ArrayList<Integer> curList = this.getAttempt( curUser );

        // Removes targetUser if possible
        //      Uses a wrapper Integer object b/c ArrayList.remove() requires an object
        boolean success = curList.remove( Integer.valueOf(targetUser) );

        // Update the database
        super.add( curUser, curList );
        return ( success ? 0 : -1 );

    }

}