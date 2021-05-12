package handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import store.model.*;

import java.util.List;
import java.util.Properties;

public class UserHandler {

    private final UserStore userStore;
    private final SubscriptionStore subscriptionStore;
    private final WalletStore walletStore;
    private final ProfileStore profileStore;
    private final PasswordStore passwordStore;
    private final UtilityHandler utilityHandler;
    private final Gson gson = new Gson();

    public UserHandler( UserStore userStore,
                        SubscriptionStore subscriptionStore,
                        WalletStore walletStore,
                        ProfileStore profileStore,
                        PasswordStore passwordStore,
                        UtilityHandler utilityHandler ) {

        this.userStore = userStore;
        this.subscriptionStore = subscriptionStore;
        this.walletStore = walletStore;
        this.profileStore = profileStore;
        this.passwordStore = passwordStore;
        this.utilityHandler = utilityHandler;

    }

    public JSONObject getUser(Request req, Response res ) {

        int uid = Integer.parseInt( req.params(":uid") );

        if ( userStore.hasUser(uid) ) {

            JSONObject userJSON = new JSONObject( userStore.getUser(uid) );
            userJSON.put( "avatar", utilityHandler.getUrlToPFP(uid) );

            res.status(200);
            return userJSON;

        } else {

            res.status(404);
            return new JSONObject();

        }
    }

    public JSONObject getUserProfile( Request req, Response res ) {

        int uid = Integer.parseInt( req.params(":uid") );

        JSONObject jsonToReturn = new JSONObject();

        jsonToReturn.put( "name", userStore.getUser(uid).getName() );
        jsonToReturn.put( "bio", profileStore.getProfile(uid).getBio() );
        jsonToReturn.put( "profilePicture", utilityHandler.getUrlToPFP(uid) );
        jsonToReturn.put( "subscribers", subscriptionStore.getFollowers(uid).size() );

        return jsonToReturn;

    }

    public JSONArray searchUsers(Request req, Response res ) throws JsonProcessingException {

        String name = req.params(":name");
        System.out.println( "Found user " + name );

        List<User> userList = userStore.searchUsers(name);
        JSONArray userJSONarray = new JSONArray();

        for ( User user : userList ) {

            JSONObject userJSON = new JSONObject(user);
            userJSON.put( "avatar", utilityHandler.getUrlToPFP(user.getID()) );
            userJSONarray.put( userJSON.toMap() );

        }

        res.status(200);
        return userJSONarray;

    }

    public int createUser( Request req, Response res ) {

        Properties data = gson.fromJson(req.body(), Properties.class);

        // Parse the request
        String email = data.getProperty("email");
        String username = data.getProperty("username");
        String password = data.getProperty("password");
        System.out.println(email + ", " + username + ", " + password);

        // Check if email has been used already
        if ( passwordStore.hasEmail(email) ) {

            res.status(409);
            return res.status();

        }

        // Check that the username and password are at least 1 character long
        if ( username.length() < 1 || password.length() < 1 ) {

            res.status(403);
            return res.status();

        }

        User tempUser = userStore.addUser(username);

        // Attempt to add a password associated with the email
        //      If return value is 0, attempt was unsuccessful
        if ( passwordStore.addPassword( email, tempUser.getID(), password ) == 0 ) {

            System.err.println( "Cannot add password for email: " + email );
            res.status(409);
            return res.status();

        }

        // Creates a balance for the user
        //      Default $0
        walletStore.addUser( tempUser.getID() );

        // Create a profile for the user
        //      Default empty bio and empty profile picture
        profileStore.addProfile( tempUser.getID() );

        System.out.println( "User Created: " + tempUser.getName() + ", " + tempUser.getID() );
        System.out.println( "PASSWORD STORED\n" );

        res.status(200);
        return res.status();

    }

    public int deleteUser( Request req, Response res ) {

        int uid = Integer.parseInt( req.attribute("cuid") );
        User tempUser = userStore.getUser(uid);

        // Should cascade delete the posts, images, comments, wallet, etc.
        userStore.deleteUser(uid);

        // Checks if the user was successfully deleted
        if ( !userStore.hasUser(uid) ) {

            System.out.println( gson.toJson(tempUser) );
            res.status(200);

        } else {
            res.status(404);
        }

        return res.status();

    }

    /**
     * The method changes the name of the specified user.
     *
     * @param   req     contains the uid of the post to be changed;
     *                  also includes the new name of the user
     */
    public int editUser( Request req, Response res ) {

        Properties data = gson.fromJson(req.body(), Properties.class);
        int uid = Integer.parseInt( req.attribute("cuid") );

        // Parse the request
        String newName = data.getProperty("name");
        String newBio = data.getProperty("bio");
        System.err.println(newBio);

        // Check if the request was formatted correctly
        if ( newName == null || newName.equals("") ) {

            res.status(400);
            return res.status();

        }

        // Change the desired fields
        userStore.changeName( uid, newName );
        profileStore.changeBio( uid, newBio );

        res.status(200);
        return res.status();

    }

}
