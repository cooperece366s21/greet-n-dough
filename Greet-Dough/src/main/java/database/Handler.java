package database;

import model.*;
import utility.*;
import store.model.*;

import java.util.HashSet;
import java.util.List;
import com.google.gson.Gson;
import spark.Request;

public class Handler {

    private final UserStore userStore;
    private final PostStore postStore;
    private final ImageStore imageStore;
    private final LikeStore likeStore;
    private final SubStore subStore;
    private final FollowStore followStore;
    private final Gson gson = new Gson();

    public Handler( UserStore userStore,
                    PostStore postStore,
                    ImageStore imageStore,
                    LikeStore likeStore,
                    SubStore subStore,
                    FollowStore followStore ) {
                       
        this.userStore = userStore;
        this.postStore = postStore;
        this.imageStore = imageStore;
        this.likeStore = likeStore;
        this.subStore = subStore;
        this.followStore = followStore;

    }

    // USER ACTIONS
    public User getUser( Request req ) {

        int id = Integer.parseInt( req.params(":id") );
        return this.userStore.getUser(id);

    }

    public Integer createUser( Request req ) {

        User tempUser = new User( req.queryParams("name"), userStore.getFreeID() );
        this.userStore.addUser( tempUser );
        IOservice.saveObject(this.userStore, "data/users.txt");
        System.out.println( "User Created: " + tempUser.getName() + ", " + tempUser.getID() );
        return 0;

    }

    public Integer deleteUser( Request req ) {

        int ID = Integer.parseInt( req.params(":id") );
        User tempUser = this.userStore.getUser(ID);

        if ( this.userStore.deleteUser(ID) ){
            System.out.println( gson.toJson(tempUser) );
            IOservice.saveObject(this.userStore, "data/users.txt");
        }
        else{
            return -1;
        }

        return 0;

    }

    // USER RELATION ACTIONS
    private Pair grabUserPair(Request req ) {

        int curUser = Integer.parseInt( req.queryParams("curUser") );
        int targetUser = Integer.parseInt( req.params(":id") );

        if ( userStore.getUser(curUser) == null ){
            System.out.println("Current user "+curUser+" does not exist");
            return  null;
        }
        if ( userStore.getUser(targetUser) == null ){
            System.out.println("Target user "+targetUser+" does not exist");
            return  null;
        }
        return new Pair(curUser, targetUser);

    }

    public Integer subscribe( Request req ) {

        Pair userPair = this.grabUserPair(req);
        if ( userPair == null ) {
            return null;
        }
        List<Integer> curSubs = subStore.getSubscriptions(userPair.getLeft());

        if ( curSubs != null && curSubs.contains( userPair.getRight() ) ) {
            System.out.println("Current User already is subscribed");
            return null;
        }

        subStore.addSubscription( userPair.getLeft(), userPair.getRight() );
        System.out.println( "current subs: " + subStore.getSubscriptions(userPair.getLeft()) );
        IOservice.saveObject(subStore, "data/subs.txt");

        return 0;

    }

    public Integer unsubscribe( Request req ) {

        Pair userPair = this.grabUserPair(req);
        if ( userPair == null ) {
            return null;
        }
        List<Integer> curSubs = subStore.getSubscriptions( userPair.getLeft() );

        if ( curSubs == null ) {
            System.out.println("User not subscribed to anyone");
            return null;
        }

        if ( !curSubs.contains(userPair.getRight()) ) {
            System.out.println("Current user not subscribed to target user");
        }

        subStore.removeSubscription( userPair.getLeft(), userPair.getRight() );
        IOservice.saveObject(subStore, "data/subs.txt");
        System.out.println( subStore.getSubscriptions(userPair.getRight()) );

        return 0;
    }

    public Integer follow( Request req ) {

        Pair userPair = this.grabUserPair(req);
        if ( userPair == null ) {
            return null;
        }
        List<Integer> curFollows = followStore.getFollowers(userPair.getLeft());

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

    // POST ACTIONS
    public Post getPost( Request req ) {

        int ID = Integer.parseInt( req.params(":id") );
        return this.postStore.getPost(ID);

    }

    public Integer createPost( Request req ) {

        int userID = Integer.parseInt( req.queryParams("userID") );
        String imageQuery = req.queryParams("imageID");
        String contentQuery = req.queryParams("contents");
        int imageID = (imageQuery != null) ? Integer.parseInt(imageQuery) : -1;

        Post tempPost = new Post( contentQuery, postStore.getFreeID(), userID, imageID );
        this.postStore.addPost( tempPost );
        IOservice.saveObject( this.postStore, "data/posts.txt" );
        System.out.println( gson.toJson(tempPost) );

        return 0;

    }

    public Integer deletePost( Request req ) {

        int postID = Integer.parseInt( req.params(":id") );
        Post tempPost = this.postStore.getPost(postID);

        if ( this.postStore.deletePost( postID ) ) {
            IOservice.saveObject (this.postStore, "data/posts.txt" );
            System.out.println( gson.toJson(tempPost) );
        } else {
            return -1;
        }

        return 0;
    }

    public List<Post> getFeed( Request req ) {
        int curUser = Integer.parseInt( req.queryParams("curUser") );
        int targetUser = Integer.parseInt( req.params(":id") );
        List<Integer> curSubs = subStore.getSubscriptions(curUser);

        if ( userStore.getUser(targetUser) == null ){
            System.out.println("Target user does not exist");
            return null;
        }

        if ( (curSubs == null) || (!curSubs.contains(targetUser)) ){
            System.out.println("Current user does not have permission to this feed");
            return null;
        }

        return postStore.makeFeed(targetUser);
    }


    public void likePost( Post curPost, int curUser ) {

        Likes postLikes = this.likeStore.getID( curPost.getID() );
        HashSet<Integer> userLikes = postLikes.getUserLikes();

        // Check list of users, if user already liked
        // If user did not like, add 1 to the like count
        // From checkID() if false append userID to list
        // From checkID() if true
        // removes like by decrementing likeCount
        // deletes userID from userLikes list
        if ( userLikes.contains(curUser) ) {
            postLikes.decrementLike(curUser);
        } else {
            postLikes.incrementLike(curUser);
        }

    }




}



