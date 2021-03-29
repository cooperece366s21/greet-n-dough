package model;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private final int ID;                   // Stores unique id for a given user

    public User( String name, Integer ID ) {

        this.name = name;
        this.ID = ID;

    }

    public String getName() {
        return this.name;
    }

    public int getID() {
        return this.ID;
    }

}
