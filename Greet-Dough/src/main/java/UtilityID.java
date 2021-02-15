import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Stack;

public class UtilityID implements Serializable {

    ////////////////// Members //////////////////
    // Uses a stack to keep track of deleted accounts
    //      to reuse the IDs
    // Bottom of stack will always contain the largest free ID
    ///////// NEED TO STORE IDS IN SORTED ORDER???
    private Stack<Integer> freeUserIDs = new Stack<>();
    private Stack<Integer> freePostIDs = new Stack<>();

    ////////////////// Functions //////////////////
    UtilityID() {

        // Initialize the stacks
        freeUserIDs.push(0);
        freePostIDs.push(0);

    }

    // Handles retrieving the next free ID
    private static int getID( Stack<Integer> IDs ) {

        // Checking for safety
        assert IDs.size() > 0;

        int newID = IDs.pop();

        // If the stack is empty, then newID is the largest free ID;
        // Push the new largest (newID+1) to the stack
        if ( IDs.size() == 0 ) {
            IDs.push(newID+1);
        }

        return newID;

    }

    public Stack<Integer> getUserStack() {
        return this.freeUserIDs;
    }

    public Stack<Integer> getPostStack() {
        return this.freePostIDs;
    }

    // Used to load the saved stacks
    public void loadIDs( Stack<Integer> userIDs, Stack<Integer> postIDs ) {

        this.freeUserIDs = userIDs;
        this.freePostIDs = postIDs;

    }

    public int getUnusedUserID() {
        return UtilityID.getID(this.freeUserIDs);
    }

    public int getUnusedPostID() {
        return UtilityID.getID(this.freePostIDs);
    }

    // Maybe need to ensure ID is non-negative?
    public void addUnusedUserID( int ID ) {
        this.freeUserIDs.push(ID);
    }

    public void addUnusedPostID( int ID ) {
        this.freePostIDs.push(ID);
    }

}
