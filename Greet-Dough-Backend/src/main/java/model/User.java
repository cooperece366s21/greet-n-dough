package model;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private final int ID;                   // Stores unique id for a given user

    public User( String name, int uid ) {

        this.name = name;
        this.ID = uid;

    }

    public String getName() {
        return this.name;
    }

    public int getID() {
        return this.ID;
    }

    public void setName( String name ) {
        this.name = name;
    }

}
