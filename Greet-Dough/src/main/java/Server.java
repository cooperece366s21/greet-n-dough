import com.google.gson.Gson;
import store.model.PostStoreImpl;
import store.model.UserStoreImpl;
import store.model.ImageStoreImpl;
import utility.IOservice;
import database.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;

public class Server {

    ////////////////// Members //////////////////
    private static UserStoreImpl userStore = new UserStoreImpl();
    private static PostStoreImpl postStore = new PostStoreImpl();
    private static ImageStoreImpl imageStore = new ImageStoreImpl();
    private static Gson gson = new Gson();

    public static void main(String[] args) {

        initExceptionHandler((e) -> {
            System.out.println("Could not start server on port 9999");
            System.exit(100);
        });
        port(9999);
        init();

        try {
            Server.userStore = (UserStoreImpl)
                    IOservice.loadObject("data/users.txt");
        } catch (ClassCastException e) {
            System.out.println("(User load) Empty file or wrong class cast");
        }

        try {
            Server.postStore = (PostStoreImpl)
                    IOservice.loadObject("data/posts.txt");
        } catch (ClassCastException e) {
            System.out.println("(Post load) Empty file or wrong class cast");
        }

        Handler handler = new Handler(Server.userStore, Server.postStore, Server.imageStore);


        // USER ROUTES
        /////////////////

        // Returns user given an id
        get("/users/:id/", (req, res) -> handler.getUser(req), gson::toJson);

        // Creates a new user
        // curl -d "name=Tony" -X post localhost:9999/users/
        post("/users/", (req, res) -> handler.createUser(req) );

        // Update the user. Needs a lot of options.
        put("/users/:id/", (req, res) -> {

            int id = Integer.parseInt(req.params(":id"));
            return "Updating a user: " + id;

        });

        // Deletes user given UserID
        delete("/users/:id/", (req, res) -> handler.deleteUser(req));

        // POST ROUTES
        ////////////////////

        //  Returns post object
        // curl localhost:9999/posts/0/
        get("/posts/:id/", (req, res) -> handler.getPost(req), gson::toJson);

        //  Creates a new post
        // curl -d "userID=0&contents=hello world" -X post localhost:9999/posts/
        post("/posts/", (req, res) -> handler.createPost(req));

        //  Deletes post given postID
        // curl -X delete localhost:9999/posts/0
        delete("/posts/:id/", (req, res) -> handler.deletePost(req));

        // FEED ROUTES
        ////////////////

        // curl localhost:9999/users/0/feed/
        get( "/users/:id/feed/", (req,res) -> handler.getFeed(req), gson::toJson );

    }
}