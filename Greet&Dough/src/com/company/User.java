package com.company;

public class User {

    String name;
    protected int id;   // Stores unique id for a given user

    // Constructor
    User( String n ) {

        name = n;
        id = User.hashName(n);

    }

    // NEED TO WRITE THIS
    private static int hashName( String name ) {

        return 1;

    }

    public static void main( String[] args ) {

    }

}
