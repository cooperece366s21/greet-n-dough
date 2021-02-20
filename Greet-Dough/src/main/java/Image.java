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


        // Get image ID
            // Post class
            // Rename each image to it's ID so each function call creates a new arbitrary image.png file
        // Get User ID
            // have to be logged in
            // return from the get user id function
            // associate the image id with the user id
        // Get Post ID
            //once post is created, exist a post ID
            //associate the image id with the post ID

        // 8 place number, 24 digits (hex on id)
        // user id how many places,
        // img id is how many places
        // post id is how many places

        Image() {
            
        }
            public String getImage() {
                Scanner myObj = new Scanner(System.in);  // Create a Scanner object
                System.out.println("Enter filepath");

                String path = myObj.nextLine();  // Read user input
                String extension = "";
                int i = path.lastIndexOf('.');
                if (i >= 0) { extension = path.substring(i+1); }

                String validTypes = "jpg png jpeg";
                if (!(validTypes.contains(extension))){
                    System.out.println("Invalid Input, Please Input a Valid Image!");
                    path = getImage();
                }
                return path;
            }

            public void moveImage() {
                FileSystem fileSys = FileSystems.getDefault();
                Path srcPath = fileSys.getPath(getImage());
                //change this abomination if you are testing it (for now)
                //NEED TO EDIT LATER
                Path destPath = fileSys.getPath("c:\\Users\\brian\\OneDrive\\Documents\\Github\\Lee-Ko\\Greet-Dough\\data\\images.png");
                try {
                    //COPY image from source to destination folder
                    Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);


                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
}
