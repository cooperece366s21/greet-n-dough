package model;

import database.Server;
import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private final int ID;                   // Stores unique id for a given user
    private int wallet;

    public User( String name ) {

        this.name = name;
        this.ID = Server.getUnusedUserID();
        this.wallet = 0;

    }

    public String getName() {
        return this.name;
    }

    public int getID() {
        return this.ID;
    }

}
