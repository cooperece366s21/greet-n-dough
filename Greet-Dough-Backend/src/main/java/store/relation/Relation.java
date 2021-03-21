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

    // Relates curUser to targetUser
    //      curUser : [ User_1, User_2, ..., targetUser ]
    protected void add( int curUser, int targetUser ) {

        ///////////////////// REMOVE THIS LATER
        ///////////////////// CHECK FOR THIS AT A HIGHER LEVEL
        ///////////////////// Check if targetUser is already related??
        // Cannot subscribe to self
        if ( curUser == targetUser ) {
            return;
        }

        // Get the current list and add the new user
        ArrayList<Integer> curList = this.getAttempt( curUser );
        curList.add( targetUser );

        // Update the database
        super.add( curUser, curList );

    }

    ///////////////////// CHECK FOR THIS AT A HIGHER LEVEL
    ///////////////////// if targetUser is not in the list
    protected void remove( int curUser, int targetUser ) {

        // Get the current list
        ArrayList<Integer> curList = this.getAttempt( curUser );

        // Removes targetUser if possible
        //      Uses a wrapper Integer object b/c ArrayList.remove() requires an object
        curList.remove( Integer.valueOf(targetUser) );

        // Update the database
        super.add( curUser, curList );

    }

}