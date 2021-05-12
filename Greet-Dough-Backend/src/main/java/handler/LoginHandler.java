package handler;

import store.model.LoginStore;
import store.model.PasswordStore;

import com.google.gson.Gson;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.util.Properties;

public class LoginHandler {

    private final LoginStore loginStore;
    private final PasswordStore passwordStore;
    private final Gson gson = new Gson();

    public LoginHandler( LoginStore loginStore, PasswordStore passwordStore ) {

        this.loginStore = loginStore;
        this.passwordStore = passwordStore;

    }

    /**
     * The method checks if the token is valid.
     * Sets res.status().
     *
     * @return   true if the token is valid; false otherwise
     */
    private boolean isValidToken( String token, Response res ) {

        if ( loginStore.hasSession(token) ) {

            res.status(200);
            return true;

        } else {

            res.status(401);
            return false;

        }

    }

    /**
     * The method checks if the token is valid.
     * Adds a uid attribute to the request for later use.
     *
     * @return   true if the token is valid; false otherwise
     */
    public boolean checkToken( Request req, Response res ) {

        // Check the token
        String token = req.headers("token");
        if ( isValidToken( token, res ) ) {

            // Get the uid and store it in the request
            int uid = loginStore.getUserID(token);
            req.attribute("cuid", String.valueOf(uid) );
            return true;

        } else {
            return false;
        }


    }

    public String tokenToId( Request req, Response res ) {

        // Check the token
        String token = req.headers("token");
        if ( !isValidToken( token, res ) ) {
            return "";
        }
        int uid = loginStore.getUserID(token);

        // Not sure if this body part is required, since we are returning uid?
        JSONObject uidJSON = new JSONObject();
        uidJSON.put("uid", uid);
        res.body( uidJSON.toString() );

        res.status(200);
        return res.body();

    }

    public String login( Request req, Response res ) {

        Properties data = gson.fromJson(req.body(), Properties.class);

        // Parse the request
        String email = data.getProperty("email");
        String password = data.getProperty("password");

        System.out.println("Logging in: " + email + ", " + password);

        // Check if login was successful
        Integer uid = passwordStore.getUserID(email, password);
        if ( uid == null ) {

            res.status(403);
            System.err.println("Unsuccessful login!");
            return "";

        }

        System.out.println(uid + " Logged in!");

        String cookie = loginStore.addSession(uid);
        JSONObject cookieJSON = new JSONObject();
        cookieJSON.put("authToken", cookie);
        res.body( cookieJSON.toString() );

        System.out.println( res.body() );
        res.status(200);
        return res.body();

    }

}
