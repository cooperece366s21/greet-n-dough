package database;

import com.google.gson.Gson;
import spark.Request;
import model.*;
import spark.Response;
import store.model.ImageStoreImpl;
import store.model.PostStoreImpl;
import store.model.UserStoreImpl;
import store.relation.SubStoreImpl;
import utility.IOservice;

import java.util.List;

public class Handler {

    private final UserStoreImpl userStore;
    private final PostStoreImpl postStore;
    private final SubStoreImpl subStore;
    private final ImageStoreImpl imageStore;
    private final Gson gson = new Gson();

    public Handler( UserStoreImpl userStore,
                    PostStoreImpl postStore,
                    ImageStoreImpl imageStore,
                    SubStoreImpl subStore ) {

        this.userStore = userStore;
        this.postStore = postStore;
        this.imageStore = imageStore;
        this.subStore = subStore;

    }

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

        if ( (curSubs==null) || (!curSubs.contains(targetUser)) ){
            System.out.println("Current user does not have permission to this feed");
            return null;
        }

        return postStore.makeFeed(targetUser);
    }

    public Integer subscribe( Request req ) {
        int curUser = Integer.parseInt( req.queryParams("curUser") );
        int targetUser = Integer.parseInt( req.params(":id") );

        if( userStore.getUser(curUser) == null ){
            System.out.println("Current user "+curUser+" does not exist");
            return  null;
        }
        if( userStore.getUser(targetUser) == null ){
            System.out.println("Target user "+targetUser+" does not exist");
            return  null;
        }

        if( (subStore.getSubscriptions(curUser) !=null)
                && (subStore.getSubscriptions(curUser).contains(targetUser)) ){
            System.out.println("Current User already is subscribed");
            return null;
        }

        subStore.addSubscription( curUser, targetUser );
        System.out.println( "current subs: " + subStore.getSubscriptions(curUser) );
        IOservice.saveObject(subStore, "data/subs.txt");

        return 0;
    }

    public Integer unsubscribe( Request req ) {
        int curUser = Integer.parseInt( req.queryParams("curUser") );
        int targetUser = Integer.parseInt( req.params(":id") );
        List<Integer> curSubs = subStore.getSubscriptions(curUser);

        if( userStore.getUser(curUser) == null ){
            System.out.println("Current user "+curUser+" does not exist");
            return  null;
        }

        if( userStore.getUser(targetUser) == null ){
            System.out.println("Target user "+targetUser+" does not exist");
            return  null;
        }

        if( curSubs==null ){
            System.out.println("User not subscribed to anyone");
            return null;
        }

        if( !curSubs.contains(targetUser) ){
            System.out.println("Current user not subscribed to target user");
        }

        subStore.removeSubscription( curUser, targetUser );
        IOservice.saveObject(subStore, "data/subs.txt");
        System.out.println( subStore.getSubscriptions(curUser) );

        return 0;
    }

}



