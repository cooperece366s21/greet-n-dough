package com.company;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Post {

    public String contents;
    public LocalDateTime timeCreated;
    public int likes;
    public ArrayList<String> comments;

    Post( String contents ) {

        this.contents = contents;
        this.timeCreated = LocalDateTime.now();
        this.likes = 0;
        this.comments = new ArrayList<>();

    }

}
