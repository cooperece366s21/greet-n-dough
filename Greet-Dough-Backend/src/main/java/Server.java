import org.jdbi.v3.core.Jdbi;
import store.postgres.*;
import store.relation.*;
import store.model.*;

import com.google.gson.Gson;
import utility.Cleaner;
import utility.GreetDoughJdbi;
import utility.PathDefs;
import utility.ResetDao;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

public class Server {

    private static UserStore userStore;
    private static PostStore postStore;
    private static ImageStore imageStore;
    private static PasswordStore passwordStore;
    private static LikeStore likeStore;
    private static CommentStore commentStore;
    private static LoginStore loginStore;
    private static WalletStore walletStore;
    private static ProfileStore profileStore;
    private static SubStore subStore = new SubStoreImpl();
    private static FollowStore followStore = new FollowStoreImpl();

    private static Gson gson = new Gson();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Cleaner cleaner = new Cleaner();

    public static void main( String[] args ) {

        // root is 'src/main/resources', so put files in 'src/main/resources/public'
        staticFiles.location(PathDefs.PUBLIC_DIR);

        initExceptionHandler((e) -> {
            System.out.println("Could not start server on port 5432");
            System.exit(100);
        });
        port(5432);
        init();

        // Schedule a task to clean up the database
        // Executes every hour
        scheduler.scheduleAtFixedRate(cleaner, 0, 1, TimeUnit.HOURS);

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
        profileStore = new ProfileStorePostgres(jdbi);

        Handler handler = new Handler(

                Server.userStore,
                Server.postStore,
                Server.imageStore,
                Server.likeStore,
                Server.commentStore,
                Server.subStore,
                Server.followStore,
                Server.passwordStore,
                Server.loginStore,
                Server.walletStore,
                Server.profileStore

            );

        // Path Groups from https://sparkjava.com/documentation#path-groups
//        path("/api", () -> {

//            path("/login/")
            // curl -H "Content-Type: application/json" --data "{"email":"a@gmail.com", "password":"123"}" localhost:5432/login






        // No auth needed
        path("/noauth", () -> {

            post("/login", handler::login, gson::toJson);

            // Checks if currently logged in
            post("/auth", handler::tokenToID, gson::toJson);

            // Register
            // Creates a new user
            // curl -H "Content-Type: application/json" --data "{"name":"Josh"}" -X post localhost:5432/users/
            post("/register", handler::createUser, gson::toJson);

            // Returns user given an id
            // curl localhost:5432/users/1/
            path("/user", () -> {
                get("/:uid", handler::getUser, gson::toJson);
            });

            get("/search/:name", handler::searchUsers, gson::toJson);

        });

        // Auth
        path("/auth", () -> {

            // Check the token
            before("/*", (req,res) -> {

                boolean authenticated = handler.checkToken( req, res );
                System.out.println("Checked the token");
                System.out.println( (String) req.attribute("uid") );
                if ( !authenticated ) {

                    System.err.println("Invalid token.");
                    halt(401);

                }

            });

            // Users
            path("/user", () -> {

                path("/:uid", () -> {

                    // biography and profile picture
                    get("/profile", handler::getUserProfile, gson::toJson);

                    // Deletes user given UserID
                    // curl -X delete localhost:5432/users/1/
                    delete("", handler::deleteUser, gson::toJson);      // Does this work?

                    get("/feed", handler::getUserFeed, gson::toJson);

                });

                put("/edit", handler::editUser, gson::toJson);

                post("/profilepic", handler::uploadProfilePicture, gson::toJson);

            });

        });


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
        get("/posts/:id", handler::getPost, gson::toJson);

        // Creates a new post
        // curl -d "uid=0&contents=hello world" -X post localhost:5432/posts/
        post("/posts", handler::createPost, gson::toJson);

        put("/posts", handler::editPost, gson::toJson);

        // Deletes post given postID
        // curl -X delete localhost:5432/posts/0
        delete("/posts/:id", handler::deletePost, gson::toJson);

        // Returns feed if user is subscribed.
        // curl -G -d "uid=1" -X post localhost:5432/users/0/feed/
        //        get("/users/:id/feed/", handler::getFeed, gson::toJson );

        // LIKES ROUTES
        ////////////////////

        // curl -G -d "uid=1" localhost:5432/posts/0/likes/
        get("/posts/:pid/likes", handler::getLikes, gson::toJson);

        // Like, put request
        // curl -d "uid=0" -X post localhost:5432/posts/0/addLike/
        post("/posts/:pid/likes", handler::likePost, gson::toJson);

        // COMMENTS ROUTES
        /////////////////////

        // curl -G -d "uid=1" localhost:5432/posts/0/comments/
        //        get("/posts/:postID/comments/", handler::getComments, gson::toJson);

        // Comment, post for now, put request since we are updating something about the post??
        // curl -d "uid=1&contents=ok post!" -X post localhost:5432/posts/0/comments/
        post("/posts/comments", handler::createComment, gson::toJson);

        get("/wallet/", handler::getBalance, gson::toJson);
        // Upload Image, which is createPost but imageID exists
        // curl -d "userID=0&contents=hello world&imageID=0" -X post localhost:5432/posts/
        // uploadImage() will prompt user for a path

        post("/wallet/add", handler::addToBalance, gson::toJson);

        post("/wallet/subtract", handler::subtractFromBalance, gson::toJson);

        // IMAGE ROUTES

        //        post( "/images/", handler::createImage, gson::toJson );


        get("/images/:uid", handler::makeGallery, gson::toJson);


    }

}