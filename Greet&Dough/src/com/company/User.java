package com.company;

import java.util.ArrayList;
import java.util.HashSet;

public class User {

    ////////////////// Members //////////////////
    String name;
    protected int id;                   // Stores unique id for a given user

    // Stores a list of a user's subscriptions
    // < Content_Creator_ID, Subscription_Tier >
    protected ArrayList<Pair> subscribedTo;

    // Stores a list of a user's followers
    // Used to send notifications, emails, etc.
    protected HashSet<Integer> subscribers;

    ////////////////// Constructor //////////////////
    User( String name ) {

        this.name = name;
        this.id = User.hashName(name);
        this.subscribedTo = new ArrayList<>();
        this.subscribers = new HashSet<>();

    }

    ////////////////// Functions //////////////////
    // NEED TO WRITE THIS
    private static int hashName( String name ) {

        return 1;

    }

    public static void main( String[] args ) {

    }

}
