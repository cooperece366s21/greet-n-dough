package Handler;

import model.*;
import store.model.*;
import utility.ImageHandler;
import utility.PathDefs;
import utility.Tiers;

import java.io.IOException;
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
    private final SubscriptionStore subscriptionStore;
    private final CommentStore commentStore;
    private final ProfileStore profileStore;
    private final UtilityHandler utilityHandler;

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
                   ProfileStore profileStore,
                   UtilityHandler utilityHandler) {

        this.userStore = userStore;
        this.postStore = postStore;
        this.imageStore = imageStore;
        this.likeStore = likeStore;
        this.commentStore = commentStore;
        this.subscriptionStore = subscriptionStore;
        this.profileStore = profileStore;
        this.utilityHandler = utilityHandler;

    }

    /////////////// PRIVATE HELPER FUNCTIONS ///////////////
    /**
     * Checks if {@code uidCurrent} can view the Post specified
     * by {@code pid}. Sets res.status().
     *
     * @return  true if the user can view the post;
     *          false otherwise
     */
    private boolean hasUserPostPerms( int cuid, int pid, Response res ) {

        if ( !userStore.hasUser(cuid) ) {

            System.err.println("User does not exist");
            res.status(404);
            return false;

        }

        if ( !postStore.hasPost(pid) ) {

            System.err.println("Post does not exist");
            res.status(404);
            return false;

        }

        int tuid = postStore.getPost(pid).getUserID();

        // Check if uidCurrent is the owner of the post
        if ( cuid == tuid ) {

            res.status(200);
            return true;

        }

        List<UserTier> subscriptions = subscriptionStore.getSubscriptions(cuid);

        // Check if uidTarget is in the list of uidCurrent's subscriptions
        for ( UserTier userTier : subscriptions ) {

            if ( userTier.getUserID() == tuid && userTier.getTier() >= postStore.getPost(pid).getTier() ) {

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

    /////////////// POST ACTIONS ///////////////
    /**
     * The method combines a Post object with its corresponding
     * like count.
     *
     * @return a JSONObject with the Post object and like count
     */
    private JSONObject formatPostJson( Post post, boolean hidden ) {

        int pid = post.getID();

        // Creates a dummy post
        if ( hidden ) {
            post = new Post("This post is hidden :(",
                        "Subscribe at tier " + post.getTier() + " to see!",
                            pid, post.getUserID(), post.getTier() );
        }

        JSONObject json = new JSONObject();

        json.put( "post", post );

        List<Integer> iidList = post.getImageIDList();
        List<String> postUrlList = new ArrayList<>();

        for ( int iid : iidList ) {
            postUrlList.add( imageStore.getImage(iid).getPath() );
        }

        // Shaping the comment field to be much nicer for the frontend
        LinkedList<JSONObject> comments = new LinkedList<>();

        if ( !hidden ) {

            for ( Comment comment : commentStore.getParents(pid) ) {

                JSONObject parentComment = new JSONObject(comment);
                int uid = parentComment.getInt("userID");
                parentComment.put( "username", userStore.getUser(uid).getName() );
                parentComment.put( "avatar", utilityHandler.getUrlToPFP(uid) );

                System.err.println("Grabbing replies");
                List<Comment> children = commentStore.getReplies( comment.getID() );
                System.err.println("Grabbed replies");

                LinkedList<JSONObject> childComments = new LinkedList<>();
                for ( Comment child : children ) {

                    System.err.println("Iterating through children");

                    JSONObject childComment = new JSONObject(child);
                    int childUid = childComment.getInt("userID");
                    childComment.put( "username", userStore.getUser(childUid).getName() );
                    childComment.put( "avatar", utilityHandler.getUrlToPFP(childUid) );
                    childComments.add(childComment);

                }

                parentComment.put("children", childComments.toArray());
                comments.add(parentComment);

            }

        }

        // Get the likes
        String likeCount = hidden ? "?" : String.valueOf( likeStore.getLikes(pid).getLikeCount() );
        json.put( "likeCount", likeCount );

        json.put( "comments", comments.toArray() );
        json.put( "images", postUrlList.toArray() );
        json.put( "hidden", hidden );

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

        int cuid = Integer.parseInt( req.attribute("cuid") );
        int uid = Integer.parseInt( req.params("uid") );

        if ( !userStore.hasUser(uid) ) {

            res.status(404);
            return new LinkedList<>();

        }

        List<Post> feed = postStore.makeFeed(uid);
        List<JSONObject> listJSON = new LinkedList<>();

        // Check if the user is retrieving their own feed
        if ( cuid == uid ) {

            for ( Post post : feed ) {
                listJSON.add( formatPostJson( post, false ) );
            }

            return listJSON;

        }

        // Check if the user has permissions
        Integer temp = subscriptionStore.hasSubscription( cuid, uid );
        int tier = (temp == null) ? -1 : temp;

        // Check permissions of the user and the post
        for ( Post post : feed ) {

            if ( tier < post.getTier() ) {
                listJSON.add( formatPostJson( post, true) );
            } else {
                listJSON.add( formatPostJson( post, false ) );
            }

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

        int uid = Integer.parseInt( req.attribute("cuid") );

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

    public JSONObject getPost( Request req, Response res ) {
        System.err.println("Reached endpoint");
        int cuid =  Integer.parseInt( req.attribute( "cuid" ) );
        int pid = Integer.parseInt( req.params(":pid") );
        Post post = postStore.getPost( pid );

        if ( cuid != post.getUserID() ) {
            res.status(403);
            return new JSONObject();
        }

        return new JSONObject( post );
    }

    public int deletePost( Request req, Response res ) {

        int uid = Integer.parseInt( req.attribute("cuid") );

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
        int uid = Integer.parseInt( req.attribute("cuid") );

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

        int uid = Integer.parseInt( req.attribute("cuid") );

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

        int uid = Integer.parseInt( req.attribute("cuid") );

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

        int uid = Integer.parseInt( req.attribute("cuid") );
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

        int uid = Integer.parseInt( req.attribute("cuid") );
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

        int cuid = Integer.parseInt( req.attribute("cuid") );

        // Parse the request
        Properties data = gson.fromJson(req.body(), Properties.class);
        int pid = Integer.parseInt( req.params(":pid") );
        String contents = data.getProperty("contents");
        String parent = data.getProperty("parentId");

        // Frontend sends a -1 in the JSON if there is no parent id, AKA comment on main post
        Integer parentId = (Integer.parseInt(parent) != -1) ? Integer.parseInt(parent) : null;

        // Check if the user has permissions
        if ( !hasUserPostPerms( cuid, pid, res ) ) {

            System.err.println("User does not have permission to view the post.");
            return res.status();

        }

        if ( parentId == null ) {
            commentStore.addComment( contents, cuid, pid );
        } else {

            // Check if the desired parentId exists
            if ( !commentStore.hasComment(parentId) ) {
                res.status(404);
                return res.status();

            }

            // Checks if the desired parent comment does not also have a parent
            //      to ensure depth 1
            if ( commentStore.isParent(parentId) ) {
                commentStore.addComment( contents, cuid, pid, parentId );
            } else {
                System.err.println("Error code: " + res.status());
            }

        }

        return res.status();

    }

    /**
     * Post owner cannot delete their own comment for now.
     */
    public int deleteComment( Request req, Response res ) {

        Properties data = gson.fromJson(req.body(), Properties.class);
        int uid = Integer.parseInt( req.attribute("cuid") );

        // Parse the request
        int cid = Integer.parseInt( data.getProperty("cid") );

        Comment tempComment = commentStore.getComment(cid);

        // Check if the user owns the comment
        if ( tempComment.getUserID() == uid ) {

            commentStore.deleteComment(cid);
            res.status(200);

        } else {
            res.status(403);
        }

        // Check if the comment was successfully deleted
        if ( commentStore.hasComment(cid) ) {
            res.status(404);
        } else {

            System.out.println( gson.toJson(tempComment) );
            res.status(200);

        }

        return res.status();

    }

}
