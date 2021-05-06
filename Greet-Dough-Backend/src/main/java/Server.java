import org.jdbi.v3.core.Jdbi;
import spark.Request;
import store.postgres.*;
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
    private static SubscriptionStore subscriptionStore;

    private static Gson gson = new Gson();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Cleaner cleaner = new Cleaner();

    // Used to debug routes
    // From https://stackoverflow.com/a/35170195
    private static String requestInfoToString( Request req ) {

        return req.requestMethod() +
                " " + req.url() +
                " " + req.body();

    }

    public static void main( String[] args ) {

        // root is 'src/main/resources', so put files in 'src/main/resources/public'
        staticFiles.location(PathDefs.PUBLIC_DIR);
//        staticFiles.header("Access-Control-Allow-Origin", "*");

        initExceptionHandler((e) -> {
            System.out.println("Could not start server on port 5432");
            System.exit(100);
        });
        port(5432);
        init();

        // Schedule a task to clean up the database
        // Executes every hour
        scheduler.scheduleAtFixedRate(cleaner, 0, 1, TimeUnit.HOURS);

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
        subscriptionStore = new SubscriptionStorePostgres(jdbi);

        Handler handler = new Handler(

            Server.userStore,
            Server.postStore,
            Server.imageStore,
            Server.likeStore,
            Server.commentStore,
            Server.subscriptionStore,
            Server.passwordStore,
            Server.loginStore,
            Server.walletStore,
            Server.profileStore

        );

        SubscriptionHandler subHandler = new SubscriptionHandler(
                Server.subscriptionStore,
                Server.walletStore);


        // Copy pasted from
        // https://gist.github.com/saeidzebardast/e375b7d17be3e0f4dddf
        // Changes all headers from all endpoints to allow CORS, which is necessary
        // for the front end to be able to receive the responses properly.
        options("/*", (req, res) -> {

            String accessControlRequestHeaders = req
                    .headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers",
                        accessControlRequestHeaders);
            }

            String accessControlRequestMethod = req
                    .headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods",
                        accessControlRequestMethod);
            }

            return "OK";

        });

        before((req, res) -> {

            res.header("Access-Control-Allow-Origin", "*");
            res.type("application/json");

        });

        //////////////////// No Auth ////////////////////
        path("/noauth", () -> {

            post("/login", handler::login, gson::toJson);

            // Checks if currently logged in
            get("/tokenToId", handler::tokenToId, gson::toJson);

            // Register
            // Creates a new user
            // curl -H "Content-Type: application/json" --data "{"name":"Josh"}" -X post localhost:5432/users/
            post("/register", handler::createUser, gson::toJson);

            // Returns user given an id
            // curl localhost:5432/user/1/
            path("/user", () -> {

                path("/:uid", () -> {

                    get("", handler::getUser, gson::toJson);

                    // biography and profile picture
                    get("/profile", handler::getUserProfile, gson::toJson);

                    get("/subscriptions", subHandler::getSubscriptions, gson::toJson);

                });

            });

            get("/search/:name", handler::searchUsers, gson::toJson);

        });


        //////////////////// Auth ////////////////////
        // Ensures all requests that require authentication have a
        //      valid token. Sets the 'uid' attribute of the request
        //      to the uid corresponding to the token.
        path("/auth", () -> {

            // Check the token
            before("/*", (req, res) -> {

                // Only authenticate non-OPTIONS requests
                if ( !req.requestMethod().equals("OPTIONS") ) {

                    boolean authenticated = handler.checkToken( req, res );
                    if ( !authenticated ) {

                        System.err.println("Invalid token.");
                        halt(401);

                    } else {
                        // Set currently logged in user id "cuid"
                        req.attribute( "cuid", loginStore.getUserID( req.headers("token") ) );
                        System.err.println("Valid token.");
                    }

                }

            });

            ////////// Users //////////
            path("/user", () -> {

                before("/*", (req, res) -> {
                    System.err.println("Route: /user/*");
                });

                before("", (req, res) -> {
                    System.err.println("Route: /user");
                });

                put("/edit", handler::editUser, gson::toJson);

                post("/profilepic", handler::uploadProfilePicture, gson::toJson);

                path("/:uid", () -> {

                    // Deletes user given UserID
                    // curl -X delete localhost:5432/user/1/
                    delete("", handler::deleteUser, gson::toJson);      // Does this work?

                    get("/feed", handler::getUserFeed, gson::toJson);

                    get("/images", handler::makeGallery, gson::toJson);

                    path("/subscriptions", () -> {

                        post("", subHandler::addSubscription, gson::toJson);

                        delete("", subHandler::deleteSubscription, gson::toJson);
                    });

                });

            });

            ////////// Posts //////////
            path("/post", () -> {

                before("/*", (req, res) -> {
                    System.err.println("Route: /post/*");
                });

                before("", (req, res) -> {
                    System.err.println("Route: /post");
                });

                // Creates a new post
                post("", handler::createPost, gson::toJson);

                ////////// Reference a Post //////////
                path("/:pid", () -> {

                    // Returns post object
                    // Currently unused
//                    get("", handler::getPost, gson::toJson);

                    // Edit post Object
                    put("", handler::editPost, gson::toJson);

                    // Deletes post from pid
                    delete("", handler::deletePost, gson::toJson);

                    ////////// Likes //////////
                    path("/like", () -> {

                        // Returns Like object
                        get("", handler::getLikes, gson::toJson);

                        // Likes a post
                        post("", handler::likePost, gson::toJson);

                    });

                    ////////// Comments //////////
                    path("/comment", () -> {

                        // Comments onto a post
                        post("", handler::createComment, gson::toJson);

                    });

                });

            });

            ////////// Wallet //////////
            path("/wallet", () -> {

                before("/*", (req, res) -> {
                    System.err.println("Route: /wallet/*");
                });

                before("", (req, res) -> {
                    System.err.println("Route: /wallet");
                });

                get("", handler::getBalance, gson::toJson);

                post("/add", handler::addToBalance, gson::toJson);

                post("/subtract", handler::subtractFromBalance, gson::toJson);

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

    }

}