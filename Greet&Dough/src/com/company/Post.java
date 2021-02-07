package com.company;

import java.time.LocalDateTime;

public class Post {

    String contents;
    LocalDateTime timeCreated;

    Post( String contents ) {

        this.contents = contents;
        this.timeCreated = LocalDateTime.now();

    }

}
