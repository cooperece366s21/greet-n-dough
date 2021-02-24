package utility;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class UtilityID implements Serializable {

    ////////////////// Members //////////////////
    // Uses a stack to keep track of deleted accounts
    //      to reuse the IDs
    // Bottom of stack will always contain the largest free ID
    ///////// NEED TO STORE IDS IN SORTED ORDER???
    private static AtomicInteger freeUserIDs = new AtomicInteger(0);
    private static AtomicInteger freePostIDs = new AtomicInteger(0);
    private static AtomicInteger freeImageIDs = new AtomicInteger(0);

    ////////////////// Functions //////////////////
    public UtilityID() {

    }

//    // Handles retrieving the next free ID
//    private static int getID( Stack<Integer> IDs ) {
//
//        // Checking for safety
//        assert IDs.size() > 0;
//
//        int newID = IDs.pop();
//
//        // If the stack is empty, then newID is the largest free ID;
//        // Push the new largest (newID+1) to the stack
//        if ( IDs.size() == 0 ) {
//            IDs.push(newID+1);
//        }
//
//        return newID;
//
//    }

//    public Stack<Integer> getUserStack() {
//        return this.freeUserIDs;
//    }
//
//    public Stack<Integer> getPostStack() {
//        return this.freePostIDs;
//    }
//
//    public Stack<Integer> getImageStack() {
//        return this.freeImageIDs;
//    }

    // Used to load the saved stacks
//    public void loadIDs( Stack<Integer> userIDs, Stack<Integer> postIDs ) {
//
//        this.freeUserIDs = userIDs;
//        this.freePostIDs = postIDs;
//
//    }

    public int getUnusedUserID() {
        return UtilityID.freeUserIDs.getAndIncrement();
    }

    public int getUnusedPostID() {
        return UtilityID.freePostIDs.getAndIncrement();
    }

    public int getUnusedImageID() { return UtilityID.freeImageIDs.getAndIncrement(); }

}
