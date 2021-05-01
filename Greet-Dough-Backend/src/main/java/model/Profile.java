package model;

import java.io.Serializable;

public class Profile implements Serializable {

    private final int userID;
    private String bio;
    private Integer imageID;

    public Profile( int uid ) {
        this( uid, null, null );
    }

    public Profile( int uid, String bio ) {
        this( uid, bio, null);
    }

    public Profile( int uid, String bio, Integer iid ) {

        this.userID = uid;
        this.bio = bio;
        this.imageID = iid;
        
    }

    public int getUserID() {
        return this.userID;
    }

    public String getBio() {
        return this.bio;
    }

    public Integer getImageID() {
        return this.imageID;
    }

    public boolean equals( Profile tempProfile ) {

        if (    this.getUserID() == tempProfile.getUserID() &&
                this.getBio().equals( tempProfile.getBio() ) ) {

            if ( this.getImageID() != null && tempProfile.getImageID() != null &&
                    this.getImageID().equals( tempProfile.getImageID() ) ) {
                return true;
            } else return this.getImageID() == null && tempProfile.getImageID() == null;

        }

        return false;

    }

}
