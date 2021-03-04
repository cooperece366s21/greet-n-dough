package database;

import com.google.gson.Gson;
import spark.Request;
import model.*;
import spark.Response;
import store.model.ImageStoreImpl;
import store.model.PostStoreImpl;
import store.model.UserStoreImpl;
import utility.IOservice;

import java.util.List;

public class Handler {

    private final UserStoreImpl userStore;
    private final PostStoreImpl postStore;
    private final ImageStoreImpl imageStore;
    private final Gson gson = new Gson();

    public Handler( UserStoreImpl userStore, PostStoreImpl postStore, ImageStoreImpl imageStore  ) {
        this.userStore = userStore;
        this.postStore = postStore;
        this.imageStore = imageStore;
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

    public List<Post> getFeed(Request req ){
        // code here to authenticate who is requesting
        int id = Integer.parseInt( req.params(":id") );

        if ( userStore.getUser(id) == null ){
            System.out.println("User does not exist");
            return null;
        }

        return postStore.makeFeed(id);
    }

}



