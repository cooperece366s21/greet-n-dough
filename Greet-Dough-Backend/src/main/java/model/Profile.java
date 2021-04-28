package model;

import java.io.Serializable;

public class Profile implements Serializable {

    private int userID;
    private String bio;
    private Integer profileImageID;

    public Profile( int userID, String bio ) {
        this( userID, bio, null);
    }

    public Profile( int userID, String bio, Integer profileImageID ) {

        this.userID = userID;
        this.bio = bio;
        this.profileImageID = profileImageID;
    }

    public int getUserID() {
        return this.userID;
    }

    public String getBio() {
        return this.bio;
    }

    public Integer getProfileImageID() {
        return this.profileImageID;
    }

}
