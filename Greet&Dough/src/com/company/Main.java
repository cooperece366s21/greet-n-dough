package com.company;

public class Main {

    public static void main(String[] args) {

        User me = new User("Tony");
        System.out.println( "My name is " + me.getName() );

        me.makePost( "Hi guys, make sure to Belladonate to my channel" );
        me.makePost( "Hi guys, now wouldn't be a bad time to Belladonate to my channel" );
        me.checkFeed();

    }

}
