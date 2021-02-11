import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;
import java.io.*;
import java.util.ArrayList;

public class Server {

    private static final String PATH_TO_USER = "/users";
    private static final String PATH_TO_USER_ID = PATH_TO_USER + "/:id";

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
        // Returns the user id
        get(Server.PATH_TO_USER_ID, (req, res) -> {

            ArrayList<User> userArrayList = loadUsers();
            String id = req.params(":id");
            User userToReturn = new User("");

            for ( User user : userArrayList) {
                if (user.getName().equals(id))  userToReturn = user;
            }

            return userToReturn;

        });

        post(Server.PATH_TO_USER, (req, res) -> {

            // Creates a new user into database or wherever
            // curl -d “name=Tony Belladonna” -X post localhost:9999/users/

            String name = req.queryParams("name");
            User tempUser = new User(name);
            ArrayList<User> userArrayList = loadUsers();

            // Check uniqueness
            for (User user : userArrayList) {
                if (user.getName().equals(tempUser.getName())) {  // Java doesn't like ==?
                    System.out.println("Username is already taken.");
                    return -1;
                }
            }

            userArrayList.add(tempUser);

            // Save to file
            saveUsers(userArrayList);

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(userArrayList);

        });

        // Update the user. Needs a lot of options.
        put(Server.PATH_TO_USER_ID, (req,res) -> {
            return "Updating a user: " + req.params(":id");
        });

        // Deletes user
        delete( Server.PATH_TO_USER_ID, (req,res) -> {
            ArrayList<User> userArrayList = loadUsers();
            String id = req.params(":id");

            for ( int i=0; i < userArrayList.size(); i++) {
                System.out.println(i);
                if (userArrayList.get(i).getName().equals(id)) {
                    userArrayList.remove(i);
                    System.out.println("User was sucessfully removed");
                }
            }
            saveUsers(userArrayList);
            return "Deleting a user: " + id;
        });

    }

    // Helper Functions
    private static ArrayList<User> loadUsers() {

        ArrayList<User> userArrayList = new ArrayList<User>();

        try {
            FileInputStream fi = new FileInputStream( new File("data/users.txt") );
            ObjectInputStream oi = new ObjectInputStream(fi);

            if( fi.available() != 0 ) {
                userArrayList = (ArrayList<User>) oi.readObject();
                oi.close();
                fi.close();
            }
        } catch( Exception ex ) {
            ex.printStackTrace();
        }

        return userArrayList;
    }

    private static Integer saveUsers( ArrayList<User> listToSave) {

        try {
            FileOutputStream fo = new FileOutputStream(new File("data/users.txt"), false);
            ObjectOutputStream oo = new ObjectOutputStream(fo);

            oo.writeObject(listToSave);
            oo.flush();
            oo.close();
            fo.close();

        } catch ( Exception ex ) {
            ex.printStackTrace();
            return -1;
        }

        return 0;
    }


}