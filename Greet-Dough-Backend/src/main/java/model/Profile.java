package model;

import java.io.Serializable;

public class Profile implements Serializable {

    private int userID;
    private String bio;
    private String path;

    public Profile( int uid, String bio ) {
        this( uid, bio, null);
    }

    public Profile( int uid, String bio, String path ) {

        this.userID = uid;
        this.bio = bio;
        this.path = path;
        
    }

    public int getUserID() {
        return this.userID;
    }

    public String getBio() {
        return this.bio;
    }

    public String getPath() {
        return this.path;
    }

    public boolean equals( Profile tempProfile ) {

        return  this.getBio().equals( tempProfile.getBio() ) &&
                this.getPath().equals( tempProfile.getPath() );

    }

}
