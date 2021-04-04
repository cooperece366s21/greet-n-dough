package store.impl;

import model.Image;
import store.model.StoreWithID;
import store.model.ImageStore;

import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;

public class ImageStoreImpl extends StoreWithID<Image> implements ImageStore {

    public ImageStoreImpl() {
        super();
    }

    public ImageStoreImpl( int start ) {
        super(start);
    }

    @Override
    public Image getImage(int ID) {
        return super.get(ID);
    }

    @Override
    public void addImage( Image newImage ) {
        super.add( newImage.getImageID(), newImage );
    }

    @Override
    public boolean deleteImage(int ID) {
        return super.delete(ID);
    }

    @Override
    public Image addImage( Image path, int postID, int uid ) {

        // Create image
        int imageID = super.getFreeID();
        Image tempImage = new Image(imageID, postID);

        // Add image
        this.addImage(tempImage);
        return tempImage;

    }

    @Override
    public String uploadImage( Image newImage ) {

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter filepath: ");

        String path = myObj.nextLine();  // Read user input
        String extension = "";
        int i = path.lastIndexOf('.');
        if ( i >= 0 ) {
            extension = path.substring(i + 1);
        }

        String validTypes = "jpg png jpeg";
        if ( !(validTypes.contains(extension)) ) {
            System.out.println("Invalid Input, Please Input a Valid Image!");
            path = uploadImage(newImage);
        }
        newImage.setPath(path);
        return path;

    }

    @Override
    public void moveImage( Image newImage ) {

        FileSystem fileSys = FileSystems.getDefault();
        Path srcPath = fileSys.getPath(uploadImage(newImage));
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
