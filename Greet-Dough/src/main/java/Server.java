import com.google.gson.Gson;
import store.impl.*;
import store.relation.*;
import store.model.*;
import utility.IOservice;
import database.Handler;

import static spark.Spark.*;

public class Server {

    ////////////////// Members //////////////////
    private static UserStore userStore = new UserStoreImpl();
    private static PostStore postStore = new PostStoreImpl();
    private static ImageStore imageStore = new ImageStoreImpl();
    private static LikeStore likeStore = new LikeStoreImpl();
    private static CommentStore commentStore = new CommentStoreImpl();
    private static SubStore subStore = new SubStoreImpl();
    private static FollowStore followStore = new FollowStoreImpl();
    private static PostCommentStore postCommentStore = new PostCommentStoreImpl();

    private static Gson gson = new Gson();

    public static void main(String[] args) {

        initExceptionHandler((e) -> {
            System.out.println("Could not start server on port 4321");
            System.exit(100);
        });
        port(4321);
        init();

        /*
        try {
            Server.userStore = (UserStore) IOservice.loadObject("data/users.txt");
        } catch (ClassCastException e) {
            System.out.println("(User load) Empty file or wrong class cast");
        }

        try {
            Server.postStore = (PostStore) IOservice.loadObject("data/posts.txt");
        } catch (ClassCastException e) {
            System.out.println("(Post load) Empty file or wrong class cast");
        }

        try {
            Server.subStore = (SubStore) IOservice.loadObject("data/subs.txt");
        } catch (ClassCastException e) {
            System.out.println("(Sub load) Empty file or wrong class cast");
        }

        try {
            Server.followStore = (FollowStore) IOservice.loadObject("data/follows.txt");
        } catch (ClassCastException e) {
            System.out.println("(Follow load) Empty file or wrong class cast");
        }

        try {
            Server.likeStore = (LikeStore) IOservice.loadObject("data/likes.txt");
        } catch (ClassCastException e) {
            System.out.println("(Like load) Empty file or wrong class cast");
        }

        try {
            Server.commentStore = (CommentStore) IOservice.loadObject("data/comments.txt");
        } catch (ClassCastException e) {
            System.out.println("(Comment load) Empty file or wrong class cast");
        }

        try {
            Server.postCommentStore = (PostCommentStore) IOservice.loadObject("data/postsComments.txt");
        } catch (ClassCastException e) {
            System.out.println("(PostComment load) Empty file or wrong class cast");
        }
        */

        Handler handler = new Handler(
                Server.userStore,
                Server.postStore,
                Server.imageStore,
                Server.likeStore,
                Server.commentStore,
                Server.subStore,
                Server.followStore,
                Server.postCommentStore );

        // USER ROUTES
        /////////////////

        // Returns user given an id
        get("/users/:id/", handler::getUser, gson::toJson);

        // Creates a new user
        // curl -d "name=Tony" -X post localhost:4321/users/
        post("/users/", handler::createUser, gson::toJson );

        // Deletes user given UserID
        delete("/users/:id/", handler::deleteUser, gson::toJson);
        
        // USER RELATION ROUTES
        ///////////////////////

        // curl -d "uid=2" -X post localhost:4321/users/0/subscribe/
        post( "/users/:id/subscribe/", handler::subscribe, gson::toJson );

        // curl -d "uid=2" -X post localhost:4321/users/0/unsubscribe/
        post( "/users/:id/unsubscribe/", handler::unsubscribe, gson::toJson );

//        // curl -d "uid=2" -X post localhost:4321/users/0/follow/
//        post( "/users/:id/follow/", (req,res) -> handler.follow(req), gson::toJson );
//
//        // curl -d "uid=2" -X post localhost:4321/users/0/unfollow/
//        post( "/users/:id/unfollow/", (req,res) -> handler.unfollow(req), gson::toJson );

        // POST ROUTES
        ////////////////////

        //  Returns post object
        // curl localhost:4321/posts/0/
        get("/posts/:id/", handler::getPost, gson::toJson);

        //  Creates a new post
        // curl -d "uid=0&contents=hello world" -X post localhost:4321/posts/
        post("/posts/", handler::createPost, gson::toJson);

        //  Deletes post given postID
        // curl -X delete localhost:4321/posts/0
        delete("/posts/:id/", handler::deletePost, gson::toJson);

        // returns feed if user is subscribed.
        // curl -d "uid=2" -X post localhost:4321/users/0/feed/
        post( "/users/:id/feed/", handler::getFeed, gson::toJson );

        // LIKES ROUTES
        ////////////////////

        // curl localhost:4321/posts/0/likes/
        post("/posts/:postID/likes/", handler::getLikes, gson::toJson);

        // Like, put request
        // curl -d "uid=0" -X post localhost:4321/posts/0/addLike/
        post("/posts/:postID/addLike/", handler::likePost, gson::toJson);

        // COMMENTS ROUTES
        /////////////////////

        get("/posts/:postID/comments/", handler::getComments, gson::toJson);

        // Comment, post for now, put request since we are updating something about the post??
        // curl -d "uid=1&contents=ok post!" -X post localhost:4321/posts/0/comments/
        post("/posts/:postID/comments/", handler::createComment, gson::toJson);


        // Upload Image, which is createPost but imageID exists
        // curl -d "userID=0&contents=hello world&imageID=0" -X post localhost:4321/posts/
        // uploadImage() will prompt user for a path

    }
}