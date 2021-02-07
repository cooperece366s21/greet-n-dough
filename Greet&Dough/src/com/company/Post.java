package com.company;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Post {

    public String contents;
    public LocalDateTime timeCreated;
    public int likes;
    public ArrayList<String> comments;
    private int id;

    Post( String contents ) {

        this.contents = contents;
        this.timeCreated = LocalDateTime.now();
        this.likes = 0;
        this.comments = new ArrayList<>();
        this.id = this.genID();

    }

    private static int genID() {
        return 0;
    }

    public int getID() {
        return this.id;
    }


}
