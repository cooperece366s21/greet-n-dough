import Handler.*;
import org.jdbi.v3.core.Jdbi;
import spark.Request;
import store.postgres.*;
import store.model.*;
import utility.Cleaner;
import utility.GreetDoughJdbi;
import utility.PathDefs;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
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
//        ResetDao.reset(jdbi);

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

        UtilityHandler utilityHandler = new UtilityHandler(
            Server.profileStore,
            Server.imageStore
        );

        LoginHandler loginHandler = new LoginHandler(
            Server.loginStore,
            Server.passwordStore
        );

        Handler handler = new Handler(
            Server.userStore,
            Server.postStore,
            Server.imageStore,
            Server.likeStore,
            Server.commentStore,
            Server.subscriptionStore,
            Server.profileStore,
            utilityHandler
        );

        UserHandler userHandler = new UserHandler(
            Server.userStore,
            Server.subscriptionStore,
            Server.walletStore,
            Server.profileStore,
            Server.passwordStore,
            utilityHandler
        );

        SubscriptionHandler subHandler = new SubscriptionHandler(
            Server.subscriptionStore,
            Server.walletStore
        );

        WalletHandler walletHandler = new WalletHandler(
            Server.walletStore
        );

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

            post("/login", loginHandler::login, gson::toJson);

            // Checks if currently logged in
            get("/tokenToId", loginHandler::tokenToId, gson::toJson);

            // Register
            // Creates a new user
            // curl -H "Content-Type: application/json" --data "{"name":"Josh"}" -X post localhost:5432/users/
            post("/register", userHandler::createUser, gson::toJson);

            // Returns user given an id
            // curl localhost:5432/user/1/
            path("/user", () -> {

                path("/:uid", () -> {

                    get("", userHandler::getUser, gson::toJson);

                    // biography and profile picture
                    get("/profile", userHandler::getUserProfile, gson::toJson);

                    get("/subscriptions", subHandler::getSubscriptions, gson::toJson);

                });

            });

            get("/search/:name", userHandler::searchUsers, gson::toJson);

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

                    boolean authenticated = loginHandler.checkToken( req, res );
                    if ( !authenticated ) {

                        System.err.println("Invalid token.");
                        halt(401);

                    } else {
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

                put("/edit", userHandler::editUser, gson::toJson);

                post("/profilepic", handler::uploadProfilePicture, gson::toJson);

                path("/:uid", () -> {

                    // Deletes user given UserID
                    // curl -X delete localhost:5432/user/1/
                    delete("", userHandler::deleteUser, gson::toJson);

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

                    get("", handler::getPost, gson::toJson);

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

                get("", walletHandler::getBalance, gson::toJson);

                post("/add", walletHandler::addToBalance, gson::toJson);

                post("/subtract", walletHandler::subtractFromBalance, gson::toJson);

            });

        });

        // Add a newline
        after((req,res) -> System.out.println());

    }

}