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
            get("/tokenToId", handler::tokenToId, gson::toJson);

            // Register
            // Creates a new user
            // curl -H "Content-Type: application/json" --data "{"name":"Josh"}" -X post localhost:5432/users/
            post("/register", handler::createUser, gson::toJson);

            // Returns user given an id
            // curl localhost:5432/user/1/
            path("/user", () -> {
                get("/:uid", handler::getUser, gson::toJson);

                // biography and profile picture
                get("/:uid/profile", handler::getUserProfile, gson::toJson);
            });

            get("/search/:name", handler::searchUsers, gson::toJson);

        });

        // Auth
        path("/auth", () -> {

            // Check the token
            before((req,res) -> {

                System.err.println( req.headers() );
                boolean authenticated = handler.checkToken( req, res );
                System.out.println("Checked the token");
                System.out.println( (String) req.attribute("uid") );
                if ( !authenticated ) {

                    System.err.println("Invalid token.");
                    halt(401);

                }

            });

            ////////// Users //////////
            path("/user", () -> {

                path("/:uid", () -> {


                    // Deletes user given UserID
                    // curl -X delete localhost:5432/user/1/
                    delete("", handler::deleteUser, gson::toJson);      // Does this work?

                    get("/feed", handler::getUserFeed, gson::toJson);

                    get("/images", handler::makeGallery, gson::toJson);

                });

                put("/edit", handler::editUser, gson::toJson);

                post("/profilepic", handler::uploadProfilePicture, gson::toJson);

            });

            ////////// Posts //////////
            path("/post", () -> {

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