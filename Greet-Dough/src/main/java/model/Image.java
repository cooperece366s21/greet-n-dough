package model;

// Import the Scanner class
import java.util.Scanner;

// Move file libraries
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.io.Serializable;

public class Image implements Serializable {
        // Prompt user to input an image path
            //input path for terminal (first phase)
        // Copy the image from the path to the data directory
            // os.system("mv 'picture.png' "")

        private int imageID;
        private String path;
        private int userID;
        private int postID;

        // Get image ID
            // base.Post class
            // Rename each image to it's ID so each function call creates a new arbitrary image.png file
        // Get model.User ID
            // have to be logged in
            // return from the get user id function
            // associate the image id with the user id
        // Get base.Post ID
            //once post is created, exist a post ID
            //associate the image id with the post ID

        // 8 place number, 24 digits (hex on id)
        // user id how many places,
        // img id is how many places
        // post id is how many places

        public void Image(String path) {
            this.path = path;
        }

        public int getID() {
            return this.imageID;
        }

        public String getPath() {
            return this.path;
        }



}
