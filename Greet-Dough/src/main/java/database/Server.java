package database;

import model.Post;
import model.User;
import store.PostStoreImpl;
import store.UserStoreImpl;
import utility.IOservice;

import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;

// IMPLEMENT A PREFIX TRIE TO ALLOW SEARCHING FOR USERS
public class Server {

    ////////////////// File Paths //////////////////
    private static final String PATH_TO_USER = "/users/";
    private static final String PATH_TO_USER_ID = PATH_TO_USER + ":id";
    private static final String PATH_TO_POST = "/posts/";
    private static final String PATH_TO_POST_ID = PATH_TO_POST + ":id";

    ////////////////// Members //////////////////
    private static ObjectMapper mapper = new ObjectMapper();
    private static UserStoreImpl userStore = new UserStoreImpl();
    private static PostStoreImpl postStore = new PostStoreImpl();


    ////////////////// Functions //////////////////
    /*
    public static User getUser(int ID) {
        return userStore.getUser(ID);
    }

    public static Post getPost(int ID) {
        return postHash.get(ID);
    }

    public static void addUser(User newUser) {
        userHash.put(newUser.getID(), newUser);
    }

    public static void addPost(Post newPost) {
        postHash.put(newPost.getID(), newPost);
    }

    // Returns true if successful;
    //         false otherwise.
    public static boolean removeUser(int ID) {

        return (Server.userHash.remove(ID) != null);
//        if ( Server.removeFromMap( Server.userHash, ID ) ) {
//
//            Server.addUnusedUserID(ID); // This user's ID is now unused, so add to stack
//            return true;
//
//        } else {
//            return false;
//        }

    }

    // Returns true if successful;
    //         false otherwise.
    public static boolean removePost(int postID) {

        Post tempPost = Server.postHash.get(postID);
        User tempUser = Server.userHash.get(tempPost.getUserID());

//            if ( tempUser.getFeed().deletePost(postID) ){
//                Server.addUnusedPostID(postID);
//                return true;
//            }
//            else {
//                return false;
//            }

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

//    public static void addUnusedUserID( int ID ) {
//        recordID.addUnusedUserID(ID);
//    }
//
//    public static void addUnusedPostID( int ID ) {
//        recordID.addUnusedPostID(ID);
//    }
//
//    public static void addUnusedImageID( int ID ) {
//        recordID.addUnusedImageID(ID);
//    }

     */

    public static void main(String[] args) {

        initExceptionHandler((e) -> {
            System.out.println("Could not start server on port 9999");
            System.exit(100);
        });
        port(9999);
        init();

        try {
            Server.userStore = (UserStoreImpl)
                    IOservice.loadObject("data/users.txt");
        } catch (ClassCastException e) {
            System.out.println("(User load) Empty file or wrong class cast");
        }

        try {
            Server.postStore = (PostStoreImpl)
                    IOservice.loadObject("data/posts.txt");
        } catch (ClassCastException e) {
            System.out.println("(Post load) Empty file or wrong class cast");
        }

        // USER ROUTES
        /////////////////

        // Returns user given an id
        get(Server.PATH_TO_USER_ID, (req, res) -> {

            int id = Integer.parseInt( req.params(":id") );
            return Server.userStore.getUser(id);

        });

        // Prints all users, for debugging only!
//        get(Server.PATH_TO_USER, (req, res) -> mapper.writeValueAsString(userStore));

        // Creates a new user
        post(Server.PATH_TO_USER, (req, res) -> {

            // curl -d "name=Tony Belladonna" -X post localhost:9999/users/

            User tempUser = new User( req.queryParams("name"), userStore.getFreeID() );
            Server.userStore.addUser( tempUser );

            IOservice.saveObject(Server.userStore, "data/users.txt");
            res.body( "User Created: " + tempUser.getName() + ", " + tempUser.getID() );

            return res.body();

        });

        // Update the user. Needs a lot of options.
        put(Server.PATH_TO_USER_ID, (req, res) -> {

            int id = Integer.parseInt(req.params(":id"));
//            User tempUser = Server.getUser(id);
//            System.out.println(tempUser.getFollowers().size());
//            tempUser.subscribe(0);
//            System.out.println(tempUser.getFollowers().size());

            return "Updating a user: " + id;

        });

        // Deletes user given UserID
        delete(Server.PATH_TO_USER_ID, (req, res) -> {

            int ID = Integer.parseInt(req.params(":id"));

            if ( Server.userStore.deleteUser(ID) ){
                res.body( "User Successfully deleted" );
                IOservice.saveObject(Server.userStore, "data/users.txt");
            }
            else{
                res.body( "Error deleting User" );
            }

            return res.body();

        });

        // POST ROUTES
        ////////////////////

        //  Returns post object
        get(Server.PATH_TO_POST_ID, (req, res) -> {

            int ID = Integer.parseInt(req.params(":id"));
            System.out.println(
                    Server.mapper.writeValueAsString(Server.postStore.getPost(ID))
            );
            return Server.postStore.getPost(ID);

        });

        //  Creates a new post
        post(Server.PATH_TO_POST, (req, res) -> {

            // curl -d "userID=0&contents=hello world" -X post localhost:9999/posts/
            int userID = Integer.parseInt( req.queryParams("userID") );
            String imageQuery = req.queryParams("imageID");
            String contentQuery = req.queryParams("contents");
            int imageID = imageQuery != null ? Integer.parseInt(imageQuery) : -1;

            Post tempPost = new Post( contentQuery, postStore.getFreeID(), userID, imageID );

            Server.postStore.addPost( tempPost );

            IOservice.saveObject( Server.postStore, "data/posts.txt" );
            res.body( "Added post: " + mapper.writeValueAsString( tempPost ) );

            return res.body();

        });

        //  Deletes post given postID
        delete(Server.PATH_TO_POST_ID, (req, res) -> {
            // curl -X delete localhost:9999/posts/0

            int postID = Integer.parseInt( req.params(":id") );

            if ( Server.postStore.deletePost( postID ) ) {
                IOservice.saveObject (Server.postStore, "data/posts.txt" );
                res.body( "Post successfully deleted" );
            } else {
                res.body( "Error deleting post" );
            }

            return res.body();

        });

    }
}