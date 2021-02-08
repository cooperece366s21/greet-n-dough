import static spark.Spark.*;

public class Server {

    public static void main(String[] args) {

        initExceptionHandler((e) -> {
            System.out.println("Could not start server on port 9999");
            System.exit(100);
        });
        port(9999);
        init();

        // you can send requests with curls.
        // curl -X POST localhost:9999/users/*id*

        // USER ROUTES
        get("/users/:id", (req, res) -> {
            // Get user info (in form of JSON, probably)
            return "Searching for user id: " + req.params(":id");
        });

        post("/users/:id", (req, res) -> {
            // Creates a new user into database or wherever
            return "Creating new user: " + req.params(":id");
        });

        put("/users/:id", (req,res) -> {
            // Update the user. Needs a lot of options.
            return "Updating a user: " + req.params(":id");
        });

        delete( "/users/:id", (req,res) -> {
            // Deletes user
            return "Deleting a user: " + req.params(":id");
        });

    }

}