import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import store.model.*;
import utility.ImageHandler;
import utility.PathDefs;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import org.json.*;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

public class Handler {

    private final UserStore userStore;
    private final PostStore postStore;
    private final ImageStore imageStore;
    private final LikeStore likeStore;
    private final SubStore subStore;
    private final FollowStore followStore;
    private final CommentStore commentStore;
    private final PasswordStore passwordStore;
    private final LoginStore loginStore;
    private final WalletStore walletStore;
    private final ProfileStore profileStore;

    private final Gson gson = new Gson();
    private final ObjectMapper mapper = new ObjectMapper();


    // Defines the accepted file extensions for images
    private static final HashSet<String> validImageFileExtensions = Stream
                            .of(".png",".jpg",".gif")
                            .collect( Collectors.toCollection(HashSet::new) );

    public Handler(UserStore userStore,
                   PostStore postStore,
                   ImageStore imageStore,
                   LikeStore likeStore,
                   CommentStore commentStore,
                   SubStore subStore,
                   FollowStore followStore,
                   PasswordStore passwordStore,
                   LoginStore loginStore,
                   WalletStore walletStore,
                   ProfileStore profileStore) {

        this.userStore = userStore;
        this.postStore = postStore;
        this.imageStore = imageStore;
        this.likeStore = likeStore;
        this.commentStore = commentStore;
        this.subStore = subStore;
        this.followStore = followStore;
        this.passwordStore = passwordStore;
        this.loginStore = loginStore;
        this.walletStore = walletStore;
        this.profileStore = profileStore;

    }

    /////////////// PRIVATE HELPER FUNCTIONS ///////////////
    private int checkUserPostPerms( int uid, int pid ) {

        if ( !postStore.hasPost(pid) ) {

            System.err.println("Post does not exist");
            return 404;

        }

        if ( !userStore.hasUser(uid) ) {

            System.err.println("User does not exist");
            return 404;

        }

        ArrayList<Integer> subs = subStore.getSubscriptions(uid);
        int tuid = postStore.getPost(pid).getUserID();

        return ( (subs != null) && (subs.contains(tuid)) ? 200 : 403 );

    }

    /**
     * Checks if the user specified by uid owns the post.
     * Sets res.status().
     *
     * @return  true if the user owns the post; false otherwise
     */
    private boolean hasOwnership( int uid, int pid, Response res ) {

        if ( postStore.getPost(pid).getUserID() != uid ) {

            res.status(401);
            return false;

        } else {

            res.status(200);
            return true;

        }

    }

    /**
     * The method checks if the token is valid.
     * Sets res.status().
     *
     * @return   true if the token is valid; false otherwise
     */
    private boolean isValidToken( String token, Response res ) {

        if ( loginStore.hasSession(token) ) {

            res.status(200);
            return true;

        } else {

            res.status(401);
            return false;

        }

    }

    public boolean checkToken( Request req, Response res ) {

        // Check the token
        String token = req.headers("token");
        System.err.println(token);
        if ( isValidToken( token, res ) ) {

            // Get the uid and place it in the request
            int uid = loginStore.getUserID(token);
            req.attribute("uid", String.valueOf(uid) );
            return true;

        } else {
            return false;
        }


    }

    /**
     * The method checks if the given file extension is valid.
     *
     * @return  true if the file extension is valid; false otherwise
     */
    private boolean isValidImageFile( String fileExtension, Response res ) {

        // Check if the extension is valid
        if ( validImageFileExtensions.contains(fileExtension) ) {

            res.status(200);
            return true;

        } else {

            res.status(403);
            return false;

        }

    }

    /**
     * @return  a string representing a url to the profile picture if exists;
     *          an empty string ("") otherwise
     */
    private String getUrlToPFP( int uid ) {

        Integer iid = profileStore.getProfile(uid).getImageID();
        return (iid != null) ? imageStore.getImage(iid).getPath() : "";

    }

    /////////////// USER ACTIONS ///////////////
    public JSONObject getUser( Request req, Response res ) throws JsonProcessingException {

        int uid = Integer.parseInt( req.params(":uid") );

        if ( userStore.hasUser(uid) ) {


            JSONObject userJSON = new JSONObject( userStore.getUser(uid) );

            System.err.println("Adding avatar now...");

            userJSON.put("avatar", getUrlToPFP(uid) );

            System.err.println("No error :)");
            res.status(200);
            return userJSON;

        } else {

            res.status(404);
            return new JSONObject();

        }
    }

    public JSONObject getUserProfile( Request req, Response res ) {

        res.type("application/json");
        int uid = Integer.parseInt( req.params(":uid") );

        JSONObject jsonToReturn = new JSONObject();

        jsonToReturn.put( "name", userStore.getUser(uid).getName() );
        jsonToReturn.put( "bio", profileStore.getProfile(uid).getBio() );
        jsonToReturn.put( "profilePicture", getUrlToPFP(uid) );

        return jsonToReturn;

    }

    public String searchUsers( Request req, Response res ) throws JsonProcessingException {

        res.type("application/json");
        String name = req.params(":name");

        System.out.println( "Found user " + name );

        List<User> results = userStore.searchUsers(name);
        res.status(200);
        System.out.println( mapper.writeValueAsString(results) );
        return mapper.writeValueAsString(results);

    }

    public int createUser( Request req, Response res ) {

        res.type("application/json");
        Properties data = gson.fromJson(req.body(), Properties.class);

        // Parse the request
        String email = data.getProperty("email");
        String username = data.getProperty("username");
        String password = data.getProperty("password");
        System.out.println(email + ", " + username + ", " + password);

        // Check if email has been used already
        if ( passwordStore.hasEmail(email) ) {

            res.status(409);
            return res.status();

        }

        // Check that the username and password are at least 1 character long
        if ( username.length() < 1 || password.length() < 1 ) {

            res.status(403);
            return res.status();

        }

        User tempUser = userStore.addUser(username);

        // Attempt to add a password associated with the email
        //      If return value is 0, attempt was unsuccessful
        if ( passwordStore.addPassword( email, tempUser.getID(), password ) == 0 ) {

            System.err.println( "Cannot add password for email: " + email );
            res.status(409);
            return res.status();

        }

        // Creates a balance for the user
        //      Default $0
        walletStore.addUser( tempUser.getID() );

        // Create a profile for the user
        //      Default empty bio and empty profile picture
        profileStore.addProfile( tempUser.getID() );

        System.out.println( "User Created: " + tempUser.getName() + ", " + tempUser.getID() );
        System.out.println( "PASSWORD STORED\n" );

        res.status(200);
        return res.status();

    }

    public int deleteUser( Request req, Response res ) {

        int uid = Integer.parseInt( req.params(":id") );
        User tempUser = userStore.getUser(uid);

        // Should cascade delete the posts, images, comments, wallet, etc.
        userStore.deleteUser(uid);

        // Checks if the user was successfully deleted
        if ( !userStore.hasUser(uid) ) {

            System.out.println( gson.toJson(tempUser) );
            res.status(200);

        } else {
            res.status(404);
        }

        return res.status();

    }

    /**
     * The method changes the name of the specified user.
     *
     * @param   req     contains the uid of the post to be changed;
     *                  also includes the new name of the user
     */
    public int editUser( Request req, Response res ) {

        res.type("application/json");
        Properties data = gson.fromJson(req.body(), Properties.class);

        // Check the token
        String token = req.headers("token");
        if ( !isValidToken( token, res ) ) {
            return res.status();
        }
        int uid = loginStore.getUserID(token);
        System.out.println(token);
        System.out.println(req.headers());
        // Parse the request
        String newName = data.getProperty("name");
        String newBio = data.getProperty("bio");
        System.err.println(newBio);

        // Check if the request was formatted correctly
        if ( newName == null || newName.equals("") ) {

            res.status(400);
            return res.status();

        }

        // Change the desired fields
        userStore.changeName( uid, newName );
        profileStore.changeBio( uid, newBio );

        res.status(200);
        return res.status();

    }

    // Need to properly map profile fields to JSONObject
/*
    public JSONObject getProfile ( Request req, Response res ) throws JsonProcessingException {

        //res.type("application/json");
        //Properties data = gson.fromJson(req.body(), Properties.class);

        //int uid = Integer.parseInt( data.getProperty("uid") );

        int uid = Integer.parseInt( req.params(":uid") );

        Profile tempProfile = profileStore.getProfile(uid);

        JSONObject tempJSON = new JSONObject();
        tempJSON.put("profile",tempProfile);

        res.status(200);
        return tempJSON;

    }
*/

    public String tokenToId( Request req, Response res ) {

        res.type("application/json");

        // Check the token
        String token = req.headers("token");
        if ( !isValidToken( token, res ) ) {
            return "";
        }
        int uid = loginStore.getUserID(token);

        // Not sure if this body part is required, since we are returning uid?
        JSONObject uidJSON = new JSONObject();
        uidJSON.put("uid", uid);
        res.body( uidJSON.toString() );

        res.status(200);
        return res.body();

    }

    public String login( Request req, Response res ) {

        res.type("application/json");
        Properties data = gson.fromJson(req.body(), Properties.class);

        // Parse the request
        String email = data.getProperty("email");
        String password = data.getProperty("password");

        System.out.println("Logging in: " + email + ", " + password);

        // Check if login was successful
        Integer uid = passwordStore.getUserID(email, password);
        if ( uid == null ) {

            res.status(403);
            System.err.println("Unsuccessful login!");
            return "";

        }

        System.out.println(uid + " Logged in!");

        String cookie = loginStore.addSession(uid);
        JSONObject cookieJSON = new JSONObject();
        cookieJSON.put("authToken", cookie);
        res.body( cookieJSON.toString() );

        System.out.println( res.body() );
        res.status(200);
        return res.body();

    }

    /////////////// WALLET ACTIONS ///////////////
    /**
     * The method returns the user's balance if the user is in the database.
     * The method returns an empty string otherwise.
     * The string returned has 2 digits after the decimal.
     *
     *
     * @throws ArithmeticException  if the user's balance has more than 2 digits
     *                              after the decimal (excluding trailing zeros).
     *                              Should never happen.
     * @return                      the user's balance
     */
    public String getBalance( Request req, Response res ) throws ArithmeticException {

        res.type("application/json");

        // Check the token
        String token = req.headers("token");
        if ( !isValidToken( token, res ) ) {
            return "";
        }
        int uid = loginStore.getUserID(token);

        BigDecimal bal = walletStore.getBalance(uid);

        return bal != null ? bal.setScale(2, RoundingMode.UNNECESSARY).toString() : "";

    }

    private int modifyBalance( Request req, Response res, boolean isAdd ) {

        res.type("application/json");
        Properties data = gson.fromJson(req.body(), Properties.class);

        // Check the token
        String token = req.headers("token");
        if ( !isValidToken( token, res ) ) {
            return res.status();
        }
        int uid = loginStore.getUserID(token);

        // Parse the request
        String amountQuery = data.getProperty("amount");
        BigDecimal amount = new BigDecimal(amountQuery).stripTrailingZeros();

        // Check if the amount is not positive or the number of digits after the decimal is greater than 2
        //      E.g. 0 or -1 or 1.005
        if ( amount.compareTo(BigDecimal.ZERO) != 1 || amount.scale() > 2 ) {

            res.status(401);
            return res.status();

        }

        if ( WalletStore.verifyPurchase() ) {

            if ( isAdd ) {
                walletStore.addToBalance( uid, amount );
            } else {
                walletStore.subtractFromBalance( uid, amount );
            }

            res.status(200);

        } else {
            res.status(401);
        }

        return res.status();

    }

    /**
     * Adds the amount specified to the user's balance.
     * Operation can fail if user doesn't have a balance or if verifyPurchase() failed.
     *
     * @param req   contains the amount to be added;
     *              amount must be positive (greater than 0) and have at most 2 digits after the decimal
     * @return      the HTTP status code
     * @see         WalletStore#verifyPurchase()
      */
    public int addToBalance( Request req, Response res ) {
        return modifyBalance( req, res, true );
    }

    /**
     * Subtracts the amount specified from the user's balance.
     * Operation can fail if user doesn't have a balance or if verifyPurchase() failed.
     *
     * @param req   contains the amount to be subtracted;
     *              amount must be positive (greater than 0) and have at most 2 decimal places
     * @return      the HTTP status code
     * @see         WalletStore#verifyPurchase()
     */
    public int subtractFromBalance( Request req, Response res ) {
        return modifyBalance( req, res, false );
    }

    /////////////// USER RELATION ACTIONS ///////////////
//    public int subscribe( Request req, Response res ) {
//
//        Pair userPair = this.grabUserPair(req);
//
//        if ( userPair == null ) {
//
//            res.status(404);
//            return res.status();
//
//        }
//
//        List<Integer> curSubs = subStore.getSubscriptions(userPair.getLeft());
//
//        if ( curSubs != null && curSubs.contains( userPair.getRight() ) ) {
//
//            System.err.println("Current user already is subscribed");
//            res.status(404);
//            return res.status();
//
//        }
//
//        subStore.addSubscription( userPair.getLeft(), userPair.getRight() );
//        System.out.println( "current subs: " + subStore.getSubscriptions(userPair.getLeft()) );
//
//        return 0;
//
//    }

//    public int unsubscribe( Request req, Response res ) {
//
//        Pair userPair = this.grabUserPair(req);
//        if ( userPair == null ) {
//
//            res.status(404);
//            return res.status();
//
//        }
//
//        List<Integer> curSubs = subStore.getSubscriptions( userPair.getLeft() );
//
//        if ( curSubs == null ) {
//
//            System.err.println("User not subscribed to anyone");
//            res.status(404);
//            return res.status();
//
//        }
//
//        if ( !curSubs.contains(userPair.getRight()) ) {
//            System.err.println("Current user not subscribed to target user");
//        }
//
//        subStore.removeSubscription( userPair.getLeft(), userPair.getRight() );
//        System.out.println( subStore.getSubscriptions(userPair.getRight()) );
//        res.status(200);
//        return 0;
//
//    }

/*
    public Integer follow( Request req ) {

        Pair userPair = this.grabUserPair(req);
        if ( userPair == null ) {
            return null;
        }
        List<Integer> curFollows = followStore.getFollowers( userPair.getLeft() );

        if ( curFollows!=null && curFollows.contains( userPair.getRight() ) ) {
            System.out.println("Current user is already following the target user");
            return null;
        }

        followStore.addFollower( userPair.getLeft(), userPair.getRight() );
        IOservice.saveObject(followStore, "data/follows.txt");
        System.out.println("Currently following: " + followStore.getFollowers( userPair.getLeft() ) );

        return 0;

    }

    public Integer unfollow( Request req ) {

        Pair userPair = this.grabUserPair(req);
        if ( userPair == null ) {
            return null;
        }
        List<Integer> curFollows = followStore.getFollowers( userPair.getLeft() );

        if ( curFollows == null ) {
            System.out.println("Current user not following anyone");
            return null;
        }

        if ( !curFollows.contains( userPair.getRight()) ) {
            System.out.println("Current user not following target user");
            return null;
        }

        followStore.removeFollower( userPair.getLeft(), userPair.getRight() );
        IOservice.saveObject(followStore, "data/follows.txt");
        System.out.println("Currently following: " + followStore.getFollowers( userPair.getLeft() ) );

        return 0;

    }

 */

    /////////////// POST ACTIONS ///////////////
    /**
     * The method combines a Post object with its corresponding
     * like count.
     *
     * @return a JSONObject with the Post object and like count
     */
    private JSONObject makePostJson( int pid ) {

        JSONObject json = new JSONObject();

        // Get the post
        Post tempPost = postStore.getPost(pid);

        json.put( "post", tempPost );

        // Get the likes
        Likes tempLikes = likeStore.getLikes(pid);
        json.put( "likeCount", tempLikes.getLikeCount() );

        List<Integer> iidList = tempPost.getImageIDList();
        List<String> postUrlList = new ArrayList<>();

        for ( int iid : iidList ) {
            postUrlList.add( imageStore.getImage(iid).getPath() );
        }

        // Shaping the comment field to be much nicer for the frontend
        LinkedList<JSONObject> comments = new LinkedList<>();
        for ( Comment comment: commentStore.getParents(pid) ) {

            JSONObject tempCommentJson = new JSONObject(comment);
            int uid =  tempCommentJson.getInt("userID");
            tempCommentJson.put("username", userStore.getUser(uid).getName() );
            tempCommentJson.put("avatar", getUrlToPFP(uid) );
            comments.add( tempCommentJson );

        }

        json.put( "comments", comments.toArray() );
        json.put( "images", postUrlList.toArray() );

        return json;

    }

    /**
     * @return a JSONObject containing the Post object and like count
     */
    public JSONObject getPost( Request req, Response res ) {

        int pid = Integer.parseInt( req.params(":pid") );

        if ( !postStore.hasPost(pid) ) {

            System.err.println("Post does not exist");
            res.status(404);
            return new JSONObject();

        }

        // Get the post and like count
        JSONObject json = makePostJson(pid);

        res.status(200);
        return json;

    }

    /**
     * The method returns a JSONObject containing a list of
     * JSONObjects that contain the Post object and like count.
     * Each JSONObject in the list represents an individual post.
     *
     * @return a list of JSONObjects
     */
    public List<JSONObject> getUserFeed( Request req, Response res ) {

        res.type("application/json");
        int uid = Integer.parseInt( req.params(":uid") );

        if ( !userStore.hasUser(uid) ) {

            res.status(404);
            return new LinkedList<>();

        }

        // Get the feed and convert each post into a JSONObject
        List<Post> feed = postStore.makeFeed(uid);
        List<JSONObject> listJSON = new LinkedList<>();
        for ( Post tempPost : feed ) {
            listJSON.add( makePostJson( tempPost.getID() ) );
        }

        res.status(200);
        return listJSON;

    }

    private String readFormField( Request req, String partName ) throws IOException, ServletException {

        return new String(
                req.raw().getPart(partName).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

    }

    /**
     * Iterates through all images in res and adds them to ImageStore.
     *
     * @return  a list of image IDs representing the added images
     */
    private List<Integer> parsePostImages( Request req, Response res, int uid, int numberOfImages ) throws ServletException, IOException {

        List<Integer> iidList = new LinkedList<>();

        for ( int i=0; i<numberOfImages; i++ ) {

            Image createdImage = createImage( req, res, uid,"image"+i, "imageType"+i );
            if ( res.status() != 200 ) {

                System.err.println("Failed to add one of the images.");
                return iidList;

            }

            iidList.add( createdImage.getID() );
            System.out.println( "Created post: " + createdImage.getPath() );

        }

        return iidList;

    }

    public int createPost( Request req, Response res ) throws IOException, ServletException {
        System.out.println("Creating post");
        // Check the token
        String token = req.headers("token");
        if ( !isValidToken( token, res ) ) {
            return res.status();
        }
        int uid = loginStore.getUserID(token);

        // Parse the request
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        String title =  readFormField(req, "title");
        String contents = readFormField(req, "contents");
        int numberOfImages = Integer.parseInt( readFormField(req, "numberOfImages") );
        System.out.println( "Grabbed number of images" );

        // Check the parsed items
        if ( title.equals("") && contents.equals("") && numberOfImages == 0 ) {

            System.err.println("Error: Post is empty.");
            res.status(403);
            return res.status();

        }

        // Get the images if they exist
        List<Integer> iidList = parsePostImages( req, res, uid, numberOfImages );
        if ( res.status() != 200 ) {
            return res.status();
        }

        Post tempPost = postStore.addPost( title, contents, uid, iidList );
        System.out.println( gson.toJson(tempPost) );

        res.status(200);
        return res.status();

    }

    // ToDo: deletePostImage() or deleteImage()
    // Both would be implemented by ImageStore.deleteImage()
    // Check if user is the owner

    public int deletePost( Request req, Response res ) {

        res.type("application/json");
        System.out.println("Reached endpoint");

        // Check the token
        String token = req.headers("token");
        if ( !isValidToken( token, res ) ) {
            return res.status();
        }
        int uid = loginStore.getUserID(token);

        // Parse the request
        int pid = Integer.parseInt( req.params(":pid") );
        Post tempPost = postStore.getPost(pid);
        System.out.println("uid: " + uid);
        System.out.println("pid: " + pid);

        // Should cascade delete the image, comments, likes, etc.
        if ( uid == tempPost.getUserID() ) {
            postStore.deletePost(pid);
        } else {

            res.status(403);
            return res.status(); // because the code below will trigger

        }

        // Checks if the post was successfully deleted
        if ( postStore.hasPost(pid) ) {
            res.status(404);
        } else {

            System.out.println( gson.toJson(tempPost) );
            res.status(200);

        }

        return res.status();

    }

    /**
     * The method changes the title and/or contents of the
     * specified post.
     *
     * @param   req     contains the pid of the post to be changed;
     *                  optionally includes the new title or new contents
     *                  of the post
     */
    public int editPost( Request req, Response res ) {

        res.type("application/json");
        Properties data = gson.fromJson(req.body(), Properties.class);

        // Check the token
        String token = req.headers("token");
        if ( !isValidToken( token, res ) ) {
            return res.status();
        }
        int uid = loginStore.getUserID(token);

        // Check if the user owns the post
        int pid = Integer.parseInt( req.params(":pid") );
        if ( !hasOwnership( uid, pid, res ) ) {
            return res.status();
        }

        // Parse the request
        String newTitle = data.getProperty("title");
        String newContents = data.getProperty("contents");

        // Check if the request was formatted properly
        if ( newTitle == null && newContents == null ) {

            res.status(400);
            return res.status();

        }

        // Change the desired fields
        if ( newTitle != null ) {
            postStore.changeTitle( pid, newTitle );
        }
        if ( newContents != null ) {
            postStore.changeContents( pid, newContents );
        }

        res.status(200);
        return res.status();

    }

    /**
     * Saves an image from bytes.
     *
     * @return  a string representing the path to the saved image
     */
    private String saveImage( Request req, Response res, String partName, String fileExtension ) {

        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

        String pathToTempFile = ImageHandler.copyFromBytes( PathDefs.TEMP_DIR, req, res, partName, fileExtension );
        if ( res.status() != 200 ) {

            System.err.println("Failed to copy the file from bytes.");
            return null;

        }

        return pathToTempFile;

    }

    /**
     * Saves and stores the image in res under partName.
     *
     * @param   uid   Assumes the token has been checked already.
     */
    private Image createImage( Request req, Response res, int uid, String partName, String partTypeName ) throws ServletException, IOException {

        // Check the file extension
        String fileExtension = readFormField( req, partTypeName );
        if ( !isValidImageFile( fileExtension, res ) ) {
            return new Image("",-1,-1);
        }

        // Save the bytes from the request
        String pathToTempFile = saveImage( req, res, partName, fileExtension );
        if ( res.status() != 200 ) {

            System.err.println("Something went wrong inside saveImage()");
            return new Image("", -1, -1);

        }

        // Copy the image and delete after copying
        Image createdImage = imageStore.addImage( uid, pathToTempFile, true );
        System.out.println("Created file: " + pathToTempFile );

        res.status(200);
        return createdImage;

    }

    public int uploadProfilePicture( Request req, Response res ) {

        // Check the token
        String token = req.headers("token");
        if ( !isValidToken( token, res ) ) {
            return res.status();
        }
        int uid = loginStore.getUserID(token);

        // Save the bytes from the request
        String pathToTempFile = saveImage( req, res, "file", "fileType" );
        if ( res.status() != 200 ) {
            return res.status();
        }

        // Copy the image and delete after copying
        profileStore.changeProfilePicture( uid, pathToTempFile, true );

        res.status(200);
        return res.status();

    }

    public List<JSONObject> makeGallery( Request req, Response res ) {

        res.type("application/json");
        int uid = Integer.parseInt( req.params(":uid") );

        if ( !userStore.hasUser(uid) ) {

            res.status(404);
            return new LinkedList<>();

        }

        List<Image> images = imageStore.makeGallery(uid);
        List<JSONObject> jsonList = new LinkedList<>();

        for( Image img : images ) {
            jsonList.add( new JSONObject(img) );
        }

        return jsonList;

    }

//    public List<Post> getFeed( Request req, Response res ) {
//
//        int uid = Integer.parseInt( req.queryParams("uid") );
//        int tuid = Integer.parseInt( req.params(":id") );
//        List<Integer> curSubs = subStore.getSubscriptions(uid);
//
//        if ( !userStore.hasUser(uid) || !userStore.hasUser(tuid) ) {
//
//            res.status(404);
//            return null;
//
//        }
//
//        if ( (curSubs == null) || (!curSubs.contains(tuid)) ) {
//
//            System.out.println("Current user does not have permission to this feed");
//            res.status(403);
//            return null;
//
//        }
//
//        return postStore.makeFeed(tuid);
//
//    }

    public HashSet<Integer> getLikes( Request req, Response res ) {

        res.type("application/json");

        // Parse the request
        int pid = Integer.parseInt( req.params(":pid") );
        System.out.println(pid);

        // Check the token
        String token = req.headers("token");
        if ( !isValidToken( token, res ) ) {
            return new HashSet<>();
        }
        int uid = loginStore.getUserID(token);
        System.out.println(uid);

//        int status = checkUserPostPerms(uid, pid);
//        res.status(status);
        // Assume it works cause we dont have subs yet
        res.status(200);

        if ( res.status() != 200 ) {

            System.err.println("Error code: " + res.status() );
            return new HashSet<>();

        }

        return likeStore.getLikes(pid).getUserLikes();

    }

    public int likePost( Request req, Response res ) {

        System.out.println("Reached endpoint");

        res.type("application/json");

        // Check the token
        String token = req.headers("token");
        if ( !isValidToken( token, res ) ) {
            return res.status();
        }
        int uid = loginStore.getUserID(token);

        int pid = Integer.parseInt( req.params(":pid") );

//        int status = checkUserPostPerms(uid, pid);
//        res.status(status);
        // assume match for now
        res.status(200);

        if ( res.status() == 200 ) {

            if ( !likeStore.hasUserLike(pid, uid) ) {
                likeStore.addUserLike(pid, uid);
            } else {
                likeStore.deleteUserLike(pid, uid);
            }

        } else {
            System.err.println("Error code: " + res.status());
        }

        return res.status();

    }

    public int createComment( Request req, Response res ) {

        res.type("application/json");

        String token = req.headers("token");
        if ( !isValidToken( token, res ) ) {
            return res.status();
        }
        int uid = loginStore.getUserID(token);

        // Parse the request
        Properties data = gson.fromJson(req.body(), Properties.class);
        int pid = Integer.parseInt( req.params(":pid") );
        String contents = data.getProperty("contents");
        String parent = data.getProperty("parentId");

        // Frontend sends a -1 in the JSON if there is no parent id, AKA comment on main post
        Integer parentId = (Integer.parseInt(parent) != -1) ? Integer.parseInt(parent) : null;

//        int status = checkUserPostPerms(uid, pid);
//        res.status(status);

        // Check if the status is not OK
        if ( res.status() != 200 ) {

            System.err.println("Error code: " + res.status());
            return res.status();

        }

        if ( parentId == null ) {
            commentStore.addComment( contents, uid, pid );
        } else {

            // Check if the desired parentId exists
            if ( !commentStore.hasComment(parentId) ) {

                res.status(404);
                return res.status();

            }

            // Checks if the desired parent comment does not also have a parent
            //      to ensure depth 1
            if ( commentStore.isParent(parentId) ) {
                commentStore.addComment( contents, uid, pid, parentId );
            } else {
                System.err.println("Error code: " + res.status());
            }

        }

        return res.status();

    }

    // havent checked permissions for this yet
    public List<Comment> getParentComments( Request req, Response res ) {

        res.type("application/json");
        Properties data = gson.fromJson(req.body(), Properties.class);

        // Parse the request
        int pid = Integer.parseInt( data.getProperty("pid") );
        int uid = Integer.parseInt( data.getProperty("uid") );
        int status = checkUserPostPerms(uid, pid);
        res.status(status);

        if ( res.status() != 200 ) {

            System.err.println("Error code: " + res.status());
            return new LinkedList<>();

        }

        return commentStore.getParents(pid);


    }

    public List<Comment> getRepliesComments( Request req, Response res ) {

        res.type("application/json");
        Properties data = gson.fromJson(req.body(), Properties.class);

        // Parse the request
        int pid = Integer.parseInt( data.getProperty("pid") );
        int uid = Integer.parseInt( data.getProperty("uid") );
        int cid = Integer.parseInt( data.getProperty("cid") );
        int status = checkUserPostPerms(uid, pid);
        res.status(status);

        if ( res.status() != 200 ) {

            System.err.println("Error code: " + res.status());
            return new LinkedList<>();

        }

        return commentStore.getReplies(cid);

    }

    // Currently not implemented
    public int deleteComment( Request req, Response res ) {

        return res.status();

    }

}
