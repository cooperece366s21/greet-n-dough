package com.company;

public class Main {

    public static void main(String[] args) {

        System.out.println( "Test" );
        User me = new User("Tony");
        System.out.println( "My name is " + me.name );

        me.makePost( "Hi guys, make sure to Belladonate to my channel" );
        me.makePost( "Hi guys, now wouldn't be a bad time to Belladonate to my channel" );
        me.checkFeed();

    }

}
