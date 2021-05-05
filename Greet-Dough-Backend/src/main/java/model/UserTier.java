package model;

/**
 * Stores a user ID and a corresponding tier for the user.
 * Can be used for both followers and subscriptions.
 */
public class UserTier {

    private int userID;
    private int tier;

    public UserTier( int uid ) {
        this( uid, 0 );
    }

    public UserTier( int uid, int tier ) {

        this.userID = uid;
        this.tier = tier;

    }

    public int getUserID() {
        return this.userID;
    }

    public int getTier() {
        return this.tier;
    }

}
