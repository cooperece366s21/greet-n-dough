package com.company;

import java.util.ArrayList;

public class Feed {

    ArrayList<Post> posts;

    // Constructor
    Feed() {
        this.posts = new ArrayList<>();
    }


    // NEED TO TEST THIS
    // Should set number of references to 0
    // Only works if no other variables reference the post
    // Might be ??
    public void deletePost( Post target ) {

        target = null;

    }

}
