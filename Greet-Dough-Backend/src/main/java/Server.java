import org.checkerframework.checker.units.qual.C;
import org.jdbi.v3.core.Jdbi;
import store.impl.*;
import store.postgres.*;
import store.relation.*;
import store.model.*;

import com.google.gson.Gson;
import utility.ResetDao;

import static spark.Spark.*;

public class Server {

    ////////////////// Members //////////////////
    private static UserStore userStore;
    private static PostStore postStore;
    private static ImageStore imageStore;
    private static PasswordStore passwordStore;
    private static LikeStore likeStore;
    private static CommentStore commentStore;
    private static LoginStore loginStore;
    private static WalletStore walletStore;
    private static SubStore subStore = new SubStoreImpl();
    private static FollowStore followStore = new FollowStoreImpl();
    private static PostCommentStore postCommentStore = new PostCommentStoreImpl();

    private static Gson gson = new Gson();

    public static void main( String[] args ) {

        // root is 'src/main/resources', so put files in 'src/main/resources/public'
        staticFiles.location("/public");

        initExceptionHandler((e) -> {
            System.out.println("Could not start server on port 5432");
            System.exit(100);
        });
        port(5432);
        init();

        // Copy pasted from
        // https://gist.github.com/saeidzebardast/e375b7d17be3e0f4dddf
        // Changes all headers from all endpoints to allow CORS, which is necessary
        // for the front end to be able to receive the responses properly.
        options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "OK";
                });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));


        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        ResetDao.reset(jdbi);

        userStore = new UserStorePostgres(jdbi);
        postStore = new PostStorePostgres(jdbi);
        imageStore = new ImageStorePostgres(jdbi);
        likeStore = new LikeStorePostgres(jdbi);
        commentStore = new CommentStorePostgres(jdbi);
        passwordStore = new PasswordStorePostgres(jdbi);
        loginStore = new LoginStorePostgres(jdbi);
        walletStore = new WalletStorePostgres(jdbi);

        Handler handler = new Handler(
                Server.userStore,
                Server.postStore,
                Server.imageStore,
                Server.likeStore,
                Server.commentStore,
                Server.subStore,
                Server.followStore,
                Server.postCommentStore,
                Server.passwordStore,
                Server.loginStore,
                Server.walletStore
                );

        // USER ROUTES
        /////////////////

        // Returns user given an id
        // curl localhost:5432/users/1/
        get("/users/:uid/", handler::getUser, gson::toJson);

        get("/users/search/:name/", handler::searchUsers, gson::toJson);

        get("/users/:uid/feed/", handler::getUserFeed, gson::toJson);

        // curl -H "Content-Type: application/json" --data "{"email":"a@gmail.com", "password":"123"}" localhost:5432/login
        post("/login/", handler::login, gson::toJson);

        // Creates a new user
        // curl -H "Content-Type: application/json" --data "{"name":"Josh"}" -X post localhost:5432/users/
        post("/users/", handler::createUser, gson::toJson );

        // Deletes user given UserID
        // curl -X delete localhost:5432/users/1/
        delete("/users/:id/", handler::deleteUser, gson::toJson);

        post("/auth/", handler::tokenToID, gson::toJson);
        
        // USER RELATION ROUTES
        ///////////////////////

        // curl -d "uid=2" -X post localhost:5432/users/0/subscribe/
//        post( "/users/:id/subscribe/", handler::subscribe, gson::toJson );

        // curl -d "uid=2" -X post localhost:5432/users/0/unsubscribe/
//        post( "/users/:id/unsubscribe/", handler::unsubscribe, gson::toJson );

//        // curl -d "uid=2" -X post localhost:5432/users/0/follow/
//        post( "/users/:id/follow/", (req,res) -> handler.follow(req), gson::toJson );
//
//        // curl -d "uid=2" -X post localhost:5432/users/0/unfollow/
//        post( "/users/:id/unfollow/", (req,res) -> handler.unfollow(req), gson::toJson );

        // POST ROUTES
        ////////////////////

        // Returns post object
        // curl localhost:5432/posts/0/
        get("/posts/:id/", handler::getPost, gson::toJson);

        // Creates a new post
        // curl -d "uid=0&contents=hello world" -X post localhost:5432/posts/
        post("/posts/:token/", handler::createPost, gson::toJson);

        // Deletes post given postID
        // curl -X delete localhost:5432/posts/0
        delete("/posts/:id/:token", handler::deletePost, gson::toJson);

        // Returns feed if user is subscribed.
        // curl -G -d "uid=1" -X post localhost:5432/users/0/feed/
//        get("/users/:id/feed/", handler::getFeed, gson::toJson );

        // LIKES ROUTES
        ////////////////////

        // curl -G -d "uid=1" localhost:5432/posts/0/likes/
        get("/posts/:postID/likes/", handler::getLikes, gson::toJson);

        // Like, put request
        // curl -d "uid=0" -X post localhost:5432/posts/0/addLike/
        post("/posts/:postID/addLike/", handler::likePost, gson::toJson);

        // COMMENTS ROUTES
        /////////////////////

        // curl -G -d "uid=1" localhost:5432/posts/0/comments/
//        get("/posts/:postID/comments/", handler::getComments, gson::toJson);

        // Comment, post for now, put request since we are updating something about the post??
        // curl -d "uid=1&contents=ok post!" -X post localhost:5432/posts/0/comments/
        post("/posts/:postID/comments/", handler::createComment, gson::toJson);

        get("/wallet/get/:token/", handler::getBalance, gson::toJson);
        // Upload Image, which is createPost but imageID exists
        // curl -d "userID=0&contents=hello world&imageID=0" -X post localhost:5432/posts/
        // uploadImage() will prompt user for a path

        post( "/wallet/add/:token/", handler::addToBalance, gson::toJson);
        post( "/wallet/subtract/:token/", handler::subtractFromBalance, gson::toJson);

    }
}