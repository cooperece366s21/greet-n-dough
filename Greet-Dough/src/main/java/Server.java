import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;
import java.io.*;
import java.util.HashMap;
import java.util.Stack;

public class Server {

    ////////////////// File Paths //////////////////
    private static final String PATH_TO_USER = "/users/";
    private static final String PATH_TO_USER_ID = PATH_TO_USER + ":id";

    ////////////////// Members //////////////////
    // Uses a stack to keep track of deleted accounts
    //      to reuse the IDs
    // Bottom of stack will always contain the largest free ID
    ///////// NEED TO STORE IDS IN SORTED ORDER???
    private static Stack<Integer> freeUserIDs = new Stack<>();
    private static Stack<Integer> freePostIDs = new Stack<>();
    private static ObjectMapper mapper = new ObjectMapper();

    ////////////////// Functions //////////////////
    Server() {

        // Initialize the stacks
        freeUserIDs.push(0);
        freePostIDs.push(0);

    }

    public static int getID( Stack<Integer> IDs ) {

        // Checking for safety
        assert IDs.size() > 0;

        int newID = IDs.pop();

        // If newID is the largest free ID, push the next largest
        if ( IDs.size() == 0 ) {
            IDs.push(newID+1);
        }

        return newID;

    }

    public static int getUnusedUserID() {
        return Server.getID( Server.freeUserIDs );
    }

    public static int getUnusedPostID() {
        return Server.getID( Server.freePostIDs );
    }

    public static void addUnusedUserID( int ID ) {
        Server.freeUserIDs.push(ID);
    }

    public static void addUnusedPostID( int ID ) {
        Server.freePostIDs.push(ID);
    }



    /////////// NEED TO SAVE STACKS BEFORE SERVER SHUTDOWN
    public static void main(String[] args) {

        new Server();

        initExceptionHandler((e) -> {
            System.out.println("Could not start server on port 9999");
            System.exit(100);
        });
        port(9999);
        init();

        // you can send requests with curls.
        // curl -X POST localhost:9999/users/*id*

        // USER ROUTES
        // Returns the user id
        get( Server.PATH_TO_USER_ID, (req, res) -> {

            HashMap<Integer,User> userHashMap = loadUsers();
            int id = Integer.parseInt( req.params(":id") );

            return userHashMap.get(id);

        });

        post( Server.PATH_TO_USER, (req, res) -> {

            // Creates a new user into database or wherever
            // curl -d “name=Tony Belladonna” -X post localhost:9999/users/

            String name = req.queryParams("name");
            User tempUser = new User(name);
            System.out.println( "Creating a user: " + tempUser.getName() + ", " + tempUser.getID() );
            HashMap<Integer,User> userHashMap = loadUsers();

//            // Check uniqueness
//            for ( User user : userArrayList ) {
//                if (user.getName().equals(tempUser.getName())) {  // Java doesn't like ==?
//                    System.out.println("Username is already taken.");
//                    return -1;
//                }
//            }

            userHashMap.put(tempUser.getID(),tempUser);

            // Save to file
            saveUsers(userHashMap);

            return Server.mapper.writeValueAsString(userHashMap);

        });

        // Update the user. Needs a lot of options.
        put( Server.PATH_TO_USER_ID, (req,res) -> {
            return "Updating a user: " + req.params(":id");
        });

        // Deletes user
        delete( Server.PATH_TO_USER_ID, (req,res) -> {

            HashMap<Integer,User> userHashMap = loadUsers();
            int id = Integer.parseInt( req.params(":id") );

            // Check if the ID is valid
            assert id >= 0: "Invalid ID.";
            assert userHashMap.containsKey(id): "User does not exist.";

            // Delete target user dependencies
            User targetUser = userHashMap.get(id);
            targetUser.deleteUser();

            // Remove target user from server
            userHashMap.remove(id);
            saveUsers(userHashMap);

            System.out.println( "Deleted a user: " + targetUser.getName() + ", " + targetUser.getID() );

            return Server.mapper.writeValueAsString(userHashMap);

        });

    }

    // Helper Functions
    private static HashMap<Integer,User> loadUsers() {

        HashMap<Integer,User> userHashMap = new HashMap<>();

        try {

            FileInputStream fi = new FileInputStream( new File("data/users.txt") );
            ObjectInputStream oi = new ObjectInputStream(fi);

            if( fi.available() != 0 ) {
                userHashMap = (HashMap<Integer,User>) oi.readObject();
                oi.close();
                fi.close();
            }

        } catch( Exception ex ) {
            ex.printStackTrace();
        }

        return userHashMap;
    }

    private static Integer saveUsers( HashMap<Integer,User> mapToSave ) {

        try {

            FileOutputStream fo = new FileOutputStream(new File("data/users.txt"), false);
            ObjectOutputStream oo = new ObjectOutputStream(fo);

            oo.writeObject(mapToSave);
            oo.flush();
            oo.close();
            fo.close();

        } catch ( Exception ex ) {
            ex.printStackTrace();
            return -1;
        }

        return 0;
    }


}