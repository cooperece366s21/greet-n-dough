import com.fasterxml.jackson.core.JsonProcessingException;
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
import utility.Tiers;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

public class Handler {

    private final UserStore userStore;
    private final PostStore postStore;
    private final ImageStore imageStore;
    private final LikeStore likeStore;
    private final SubscriptionStore subscriptionStore;
    private final CommentStore commentStore;
    private final PasswordStore passwordStore;
    private final LoginStore loginStore;
    private final WalletStore walletStore;
    private final ProfileStore profileStore;

    private final Gson gson = new Gson();

    // Defines the accepted file extensions for images
    private static final HashSet<String> validImageFileExtensions = Stream
                            .of(".png",".jpg",".gif")
                            .collect( Collectors.toCollection(HashSet::new) );

    public Handler(UserStore userStore,
                   PostStore postStore,
                   ImageStore imageStore,
                   LikeStore likeStore,
                   CommentStore commentStore,
                   SubscriptionStore subscriptionStore,
                   PasswordStore passwordStore,
                   LoginStore loginStore,
                   WalletStore walletStore,
                   ProfileStore profileStore) {

        this.userStore = userStore;
        this.postStore = postStore;
        this.imageStore = imageStore;
        this.likeStore = likeStore;
        this.commentStore = commentStore;
        this.subscriptionStore = subscriptionStore;
        this.passwordStore = passwordStore;
        this.loginStore = loginStore;
        this.walletStore = walletStore;
        this.profileStore = profileStore;

    }

    /////////////// PRIVATE HELPER FUNCTIONS ///////////////
    /**
     * Checks if {@code uidCurrent} can view the Post specified
     * by {@code pid}. Sets res.status().
     *
     * @return  true if the user can view the post;
     *          false otherwise
     */
    private boolean hasUserPostPerms( int uidCurrent, int pid, Response res ) {

        if ( !userStore.hasUser(uidCurrent) ) {

            System.err.println("User does not exist");
            res.status(404);
            return false;

        }

        if ( !postStore.hasPost(pid) ) {

            System.err.println("Post does not exist");
            res.status(404);
            return false;

        }

        int uidTarget = postStore.getPost(pid).getUserID();

        // Check if uidCurrent is the owner of the post
        if ( uidCurrent == uidTarget ) {

            res.status(200);
            return true;

        }

        List<UserTier> subscriptions = subscriptionStore.getSubscriptions(uidCurrent);

        // Check if uidTarget is in the list of uidCurrent's subscriptions
        for ( UserTier userTier : subscriptions ) {

            if ( userTier.getUserID() == uidTarget ) {

                res.status(200);
                return true;

            }

        }

        res.status(403);
        return false;

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

    /**
     * The method checks if the token is valid.
     * Adds a uid attribute to the request for later use.
     *
     * @return   true if the token is valid; false otherwise
     */
    public boolean checkToken( Request req, Response res ) {

        // Check the token
        String token = req.headers("token");
        if ( isValidToken( token, res ) ) {

            // Get the uid and store it in the request
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
    public JSONObject getUser( Request req, Response res ) {

        int uid = Integer.parseInt( req.params(":uid") );

        if ( userStore.hasUser(uid) ) {

            JSONObject userJSON = new JSONObject( userStore.getUser(uid) );
            userJSON.put( "avatar", getUrlToPFP(uid) );

            res.status(200);
            return userJSON;

        } else {

            res.status(404);
            return new JSONObject();

        }
    }

    public JSONObject getUserProfile( Request req, Response res ) {

        int uid = Integer.parseInt( req.params(":uid") );

        JSONObject jsonToReturn = new JSONObject();

        jsonToReturn.put( "name", userStore.getUser(uid).getName() );
        jsonToReturn.put( "bio", profileStore.getProfile(uid).getBio() );
        jsonToReturn.put( "profilePicture", getUrlToPFP(uid) );
        jsonToReturn.put( "subscribers", subscriptionStore.getSubscriptions(uid).size() );

        return jsonToReturn;

    }

    public JSONArray searchUsers( Request req, Response res ) throws JsonProcessingException {

        String name = req.params(":name");
        System.out.println( "Found user " + name );

        List<User> userList = userStore.searchUsers(name);
        JSONArray userJSONarray = new JSONArray();

        for ( User user : userList ) {

            JSONObject userJSON = new JSONObject(user);
            userJSON.put( "avatar", getUrlToPFP(user.getID()) );
            userJSONarray.put( userJSON.toMap() );

        }

        res.status(200);
        return userJSONarray;

    }

    public int createUser( Request req, Response res ) {

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

        int uid = Integer.parseInt( req.attribute("uid") );
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

        Properties data = gson.fromJson(req.body(), Properties.class);
        int uid = Integer.parseInt( req.attribute("uid") );

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

        int uid = Integer.parseInt( req.attribute("uid") );

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

    // Not currently used
    // Will need to check permissions (post tier)
//    /**
//     * @return a JSONObject containing the Post object and like count
//     */
//    public JSONObject getPost( Request req, Response res ) {
//
//        int pid = Integer.parseInt( req.params(":pid") );
//
//        if ( !postStore.hasPost(pid) ) {
//
//            System.err.println("Post does not exist");
//            res.status(404);
//            return new JSONObject();
//
//        }
//
//        // Get the post and like count
//        JSONObject json = makePostJson(pid);
//
//        res.status(200);
//        return json;
//
//    }

    /**
     * The method returns a JSONObject containing a list of
     * JSONObjects that contain the Post object and like count.
     * Each JSONObject in the list represents an individual post.
     *
     * @return a list of JSONObjects
     */
    public List<JSONObject> getUserFeed( Request req, Response res ) {

        int uid = Integer.parseInt( req.attribute("uid") );

        if ( !userStore.hasUser(uid) ) {

            res.status(404);
            return new LinkedList<>();

        }

        // ToDo: Check permissions for each post
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

        int uid = Integer.parseInt( req.attribute("uid") );

        // Parse the request
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        String title =  readFormField(req, "title");
        String contents = readFormField(req, "contents");
        int numberOfImages = Integer.parseInt( readFormField(req, "numberOfImages") );
        int tier = Integer.parseInt( readFormField(req, "tier") );
        System.out.println( "Grabbed number of images" );

        // Check the parsed items
        if ( title.equals("") && contents.equals("") && numberOfImages == 0 ) {

            System.err.println("Error: Post is empty.");
            res.status(403);
            return res.status();

        }

        // Check the tier
        if ( !Tiers.isValidTier(tier) ) {
            System.err.println("Error: Invalid tier");
            res.status(403);
            return res.status();

        }

        // Get the images if they exist
        List<Integer> iidList = parsePostImages( req, res, uid, numberOfImages );
        if ( res.status() != 200 ) {
            return res.status();
        }

        Post tempPost = postStore.addPost( title, contents, uid, tier, iidList );
        System.out.println( gson.toJson(tempPost) );

        res.status(200);
        return res.status();

    }

    public int deletePost( Request req, Response res ) {

        int uid = Integer.parseInt( req.attribute("uid") );

        // Parse the request
        int pid = Integer.parseInt( req.params(":pid") );
        Post tempPost = postStore.getPost(pid);
        System.out.println("uid: " + uid);
        System.out.println("pid: " + pid);

        // Check if user has permissions
        if ( uid == tempPost.getUserID() ) {
            postStore.deletePost(pid);
        } else {

            res.status(403);
            return res.status();

        }

        // Check if the post was successfully deleted
        if ( postStore.hasPost(pid) ) {
            res.status(404);
        } else {

            System.out.println( gson.toJson(tempPost) );
            res.status(200);

        }

        return res.status();

    }

    /**
     * Used in {@link #deletePost(Request,Response)}.
     * Checks if {@code uid} is the owner of the image specified
     * by {@code iid}. If so, deletes the image.
     * Sets res.status().
     *
     * @return  true if successful;
     *          false otherwise
     */
    private boolean deleteImage( int uid, int iid, Response res ) {

        // Check if user has permissions
        if ( uid == imageStore.getImage(iid).getUserID() ) {
            imageStore.deleteImage(iid);
        } else {

            res.status(403);
            return false;

        }

        // Check if the image was successfully deleted
        if ( imageStore.hasImage(iid) ) {

            res.status(404);
            return false;

        }

        res.status(200);
        return true;

    }

    // ToDo: Implement addImage() and deleteImage() here
    /**
     * The method changes the title and/or contents of the
     * specified post.
     *
     * @param   req     contains the pid of the post to be changed;
     *                  optionally includes the new title or new contents
     *                  of the post
     */
    public int editPost( Request req, Response res ) {

        Properties data = gson.fromJson(req.body(), Properties.class);
        int uid = Integer.parseInt( req.attribute("uid") );

        // Check if the user owns the post
        int pid = Integer.parseInt( req.params(":pid") );
        if ( !hasOwnership( uid, pid, res ) ) {
            return res.status();
        }

        // Parse the request
        String newTitle = data.getProperty("title");
        String newContents = data.getProperty("contents");
        String tempTier = data.getProperty("tier");
        Integer newTier = tempTier != null ? Integer.parseInt(tempTier) : null;

        // Check if the request was formatted properly
        if ( newTitle == null && newContents == null && newTier == null ) {

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
        if ( newTier != null ) {
            postStore.changeTier( pid, newTier );
        }

        // Add the new images
//        for ( int iid : imagesToAdd ) {
//            postStore.addPostImage( pid, iid );
//        }

        // Delete the specified images
//        for ( int iid : imagesToDelete ) {
//
//            if ( !deleteImage( uid, iid, res ) ) {
//
//                System.err.println("Failed to delete an image");
//                return res.status();
//
//            }
//
//        }

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

        int uid = Integer.parseInt( req.attribute("uid") );

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

        int uid = Integer.parseInt( req.attribute("uid") );

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

    public HashSet<Integer> getLikes( Request req, Response res ) {

        int uid = Integer.parseInt( req.attribute("uid") );
        int pid = Integer.parseInt( req.params(":pid") );
        System.out.println(uid);
        System.out.println(pid);

        // Check if the user has permissions
        if ( !hasUserPostPerms( uid, pid, res ) ) {

            System.err.println("User does not have permission to view the post.");
            return new HashSet<>();

        }

        return likeStore.getLikes(pid).getUserLikes();

    }

    public int likePost( Request req, Response res ) {

        int uid = Integer.parseInt( req.attribute("uid") );
        int pid = Integer.parseInt( req.params(":pid") );

        // Check if the user has permissions
        if ( !hasUserPostPerms( uid, pid, res ) ) {

            System.err.println("User does not have permission to view the post.");
            return res.status();

        }

        if ( !likeStore.hasUserLike( pid, uid ) ) {
            likeStore.addUserLike( pid, uid );
        } else {
            likeStore.deleteUserLike( pid, uid );
        }

        return res.status();

    }

    public int createComment( Request req, Response res ) {

        int uid = Integer.parseInt( req.attribute("uid") );

        // Parse the request
        Properties data = gson.fromJson(req.body(), Properties.class);
        int pid = Integer.parseInt( req.params(":pid") );
        String contents = data.getProperty("contents");
        String parent = data.getProperty("parentId");

        // Frontend sends a -1 in the JSON if there is no parent id, AKA comment on main post
        Integer parentId = (Integer.parseInt(parent) != -1) ? Integer.parseInt(parent) : null;

        // Check if the user has permissions
        if ( !hasUserPostPerms( uid, pid, res ) ) {

            System.err.println("User does not have permission to view the post.");
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

    public List<Comment> getParentComments( Request req, Response res ) {

        Properties data = gson.fromJson(req.body(), Properties.class);
        int uid = Integer.parseInt( req.attribute("uid") );

        // Parse the request
        int pid = Integer.parseInt( data.getProperty("pid") );

        // Check if the user has permissions
        if ( !hasUserPostPerms( uid, pid, res ) ) {

            System.err.println("User does not have permission to view the post.");
            return new LinkedList<>();

        }

        if ( res.status() != 200 ) {

            System.err.println("Error code: " + res.status());
            return new LinkedList<>();

        }

        return commentStore.getParents(pid);


    }

    public List<Comment> getRepliesComments( Request req, Response res ) {

        Properties data = gson.fromJson(req.body(), Properties.class);
        int uid = Integer.parseInt( req.attribute("uid") );

        // Parse the request
        int pid = Integer.parseInt( data.getProperty("pid") );
        int cid = Integer.parseInt( data.getProperty("cid") );

        // Check if the user has permissions
        if ( !hasUserPostPerms( uid, pid, res ) ) {

            System.err.println("User does not have permission to view the post.");
            return new LinkedList<>();

        }

        return commentStore.getReplies(cid);

    }

    // Currently not implemented
    public int deleteComment( Request req, Response res ) {

        return res.status();

    }

}
