import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;
import java.io.*;
import java.util.HashMap;

// IMPLEMENT A PREFIX TRIE TO ALLOW SEARCHING FOR USERS
public class Server {

    ////////////////// File Paths //////////////////
    private static final String PATH_TO_USER = "/users/";
    private static final String PATH_TO_USER_ID = PATH_TO_USER + ":id";
    private static final String PATH_TO_POST = "/posts/";
    private static final String PATH_TO_POST_ID = PATH_TO_POST + ":id";

    ////////////////// Members //////////////////
    private static ObjectMapper mapper = new ObjectMapper();
    private static UtilityID recordID = new UtilityID();
    private static HashMap<Integer, User> userHash = new HashMap<>();
    private static HashMap<Integer, Post> postHash = new HashMap<>();

    ////////////////// Functions //////////////////
    public static User getUser( int ID ) {
        return userHash.get(ID);
    }

    public static Post getPost( int ID ) {
        return postHash.get(ID);
    }

    public static void addUser( User newUser ) {
        userHash.put( newUser.getID(), newUser );
    }

    public static void addPost( Post newPost ) {
        postHash.put( newPost.getID(), newPost );
    }

    // Removes a given ID from a given map
    public static <T> boolean removeFromMap( HashMap<Integer, T> map, int ID ) {

        if ( map.containsKey(ID) ) {

            map.remove(ID);
            return true;

        } else {
            return false;
        }

    }

    // Returns true if successful;
    //         false otherwise.
    public static boolean removeUser( int ID ) {

        if ( Server.removeFromMap( Server.userHash, ID ) ) {

            Server.addUnusedUserID(ID); // This user's ID is now unused, so add to stack
            return true;

        } else {
            return false;
        }

    }

    // Returns true if successful;
    //         false otherwise.
    public static boolean removePost( int postID ) {

            Post tempPost = Server.postHash.get( postID );
            User tempUser = Server.userHash.get( tempPost.getUserID() );

            if ( tempUser.getFeed().deletePost(postID) ){
                Server.addUnusedPostID(postID);
                return true;
            }
            else {
                return false;
            }

//        if ( Server.removeFromMap( Server.postHash, ID ) ) {
//
//            Server.addUnusedPostID(ID); // This post's ID is now unused, so add to stack
//            return true;
//
//        } else {
//            return false;
//        }

    }

    public static int getUnusedUserID() {
        return recordID.getUnusedUserID();
    }

    public static int getUnusedPostID() {
        return recordID.getUnusedPostID();
    }

    public static int getUnusedImageID() {
        return recordID.getUnusedImageID();
    }

    public static void addUnusedUserID( int ID ) {
        recordID.addUnusedUserID(ID);
    }

    public static void addUnusedPostID( int ID ) {
        recordID.addUnusedPostID(ID);
    }

    public static void addUnusedImageID( int ID ) {
        recordID.addUnusedImageID(ID);
    }

    /////////// NEED TO SAVE STACKS BEFORE SERVER SHUTDOWN
    public static void main(String[] args) {

        initExceptionHandler((e) -> {
            System.out.println("Could not start server on port 9999");
            System.exit(100);
        });
        port(9999);
        init();

        // Load the saved stack and users
        try {
            Server.recordID = (UtilityID) Server.loadObject("data/stack.txt");
        } catch( ClassCastException e) {
            System.out.println("(Stack load) Empty file or wrong class cast");
        }

        try {
            Server.userHash = (HashMap<Integer, User>) Server.loadObject("data/users.txt");
        } catch ( ClassCastException e ) {
            System.out.println("(User load) Empty file or wrong class cast");
        }

        try {
            Server.postHash = (HashMap<Integer, Post>) Server.loadObject("data/posts.txt");
        } catch ( ClassCastException e ) {
            System.out.println("(Post load) Empty file or wrong class cast");
        }


        // you can send requests with curls.
        // curl -X POST localhost:9999/users/*id*

        // USER ROUTES
        /////////////////

        // Returns user given an id
        get( Server.PATH_TO_USER_ID, (req, res) -> {

            int id = Integer.parseInt( req.params(":id") );
            return Server.userHash.get(id);

        });

        // Prints all users, for debugging only!
        get( Server.PATH_TO_USER, (req,res) -> mapper.writeValueAsString( userHash ));

        // Creates a new user into database or wherever
        post( Server.PATH_TO_USER, (req, res) -> {

            // curl -d "name=Tony Belladonna" -X post localhost:9999/users/

            String name = req.queryParams("name");
            User tempUser = new User(name);
            System.out.println( "Creating a user: " + tempUser.getName() + ", " + tempUser.getID() );

            // Save target user to server
            Server.addUser( tempUser );
            saveObject(Server.userHash, "data/users.txt");
            saveObject(Server.recordID, "data/stack.txt");

            return Server.mapper.writeValueAsString(Server.userHash);

        });

        // Update the user. Needs a lot of options.
        put( Server.PATH_TO_USER_ID, (req,res) -> {

            int id = Integer.parseInt( req.params(":id") );
            User tempUser = Server.getUser(id);
            System.out.println( tempUser.getFollowers().size() );
            tempUser.subscribe(0);
            System.out.println( tempUser.getFollowers().size() );

            return "Updating a user: " + id;

        });

        // Deletes user
        delete( Server.PATH_TO_USER_ID, (req,res) -> {

            int ID = Integer.parseInt( req.params(":id") );

            // Check if the ID is valid
            assert ID >= 0: "Invalid ID.";
            assert Server.userHash.containsKey(ID): "User does not exist.";

            // Delete target user dependencies
            User targetUser = Server.userHash.get(ID);
            targetUser.deleteUser();

            // Remove target user from server
            Server.removeUser(ID);
            saveObject(Server.userHash, "data/users.txt");
            saveObject(Server.recordID, "data/stack.txt");

            System.out.println( "Deleted a user: " + targetUser.getName() + ", " + targetUser.getID() );

            return Server.mapper.writeValueAsString(Server.userHash);

        });

        // POST ROUTES
        ////////////////////

        // Returns post object
        get(Server.PATH_TO_POST_ID, (req,res) -> {

            int ID = Integer.parseInt( req.params( ":id" ) );
            System.out.println(
                    Server.mapper.writeValueAsString( Server.getPost( ID ) )
            );
            return Server.getPost( ID );

        });

        //  Creates a new post. Data query must include the ID of the user who is posting.
        //  Updates the user's feed to include the postID.
        post(Server.PATH_TO_POST, (req, res) -> {

            // curl -d "userID=0&contents=hello world" -X post localhost:9999/posts/
            int userID = Integer.parseInt( req.queryParams( "userID" ) );
            String imageQuery = req.queryParams( "imageID" );
            String contentQuery = req.queryParams( "contents" );
            int imageID = imageQuery!=null ? Integer.parseInt( imageQuery ) : -1;

            User tempUser = Server.userHash.get( userID );

            tempUser.getFeed().addPost( contentQuery, userID, imageID );
            System.out.println( "Added posts" );

            saveObject( Server.userHash, "data/users.txt" );
            saveObject( Server.recordID, "data/stack.txt" );
            saveObject( Server.postHash, "data/posts.txt" );
            System.out.println( "Saving all files" );

            return Server.mapper.writeValueAsString( Server.postHash );

        });

        delete(Server.PATH_TO_POST_ID, (req,res) -> {
            // curl -X delete localhost:9999/posts/0

            int postID = Integer.parseInt( req.params( ":id" ) );

            if ( Server.removePost( postID ) ) {

                saveObject(Server.userHash, "data/users.txt");
                saveObject(Server.recordID, "data/stack.txt");
                saveObject(Server.postHash, "data/posts.txt");

            }
            else {
                System.out.println( "Error deleting a post");
            }

            return Server.mapper.writeValueAsString( Server.postHash );

        });

    }

    // IO Helper Functions
    private static Integer saveObject( Object objToSave, String fileName ){

        try {

            FileOutputStream fo = new FileOutputStream(new File(fileName), false);
            ObjectOutputStream oo = new ObjectOutputStream(fo);

            oo.writeObject( objToSave );
            oo.flush();
            oo.close();
            fo.close();

        } catch ( Exception ex ) {
            ex.printStackTrace();
            return -1;
        }

        return 0;

    }

    // Generalized Load Function. Requires casting on call.
    private static Object loadObject( String fileName ){

        Object objToLoad = new Object();

        try {

            FileInputStream fi = new FileInputStream( new File( fileName ) );
            ObjectInputStream oi = new ObjectInputStream(fi);

            if ( fi.available() != 0 ) {
                objToLoad = oi.readObject();
                oi.close();
                fi.close();
            }
            else {
                System.out.println("Empty file " + fileName);
            }

        }
        catch ( EOFException e ){
            System.out.println( fileName + " Is empty");
        }
        catch ( Exception ex ) {
            ex.printStackTrace();
        }

        return objToLoad;
    }

}