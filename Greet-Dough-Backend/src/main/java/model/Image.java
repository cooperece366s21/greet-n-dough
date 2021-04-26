package model;

import java.io.Serializable;

public class Image implements Serializable {

    private final int ID;
    private String path;
    private int userID;

    public Image( String path, int iid, int uid ) {

        this.ID = iid;
        this.userID = uid;
        this.path = path;

    }

    public String getPath() {
        return path;
    }

    public int getID() {
        return ID;
    }

    public int getUserID() {
        return userID;
    }

}
