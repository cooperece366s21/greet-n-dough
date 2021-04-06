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
    public Image getImage( int ID ) {
        return super.get(ID);
    }

    @Override
    public boolean hasImage( int ID ) {
        return super.has(ID);
    }

    @Override
    public void deleteImage( int ID ) {
        super.delete(ID);
    }

    @Override
    public Image addImage( String path, int uid ) {

        // Create image
        int ID = super.getFreeID();
        String newPath = copyImage(path);   // Copies the image to a default folder
        Image tempImage = new Image( newPath, ID, uid );

        // Add image
        this.add( tempImage.getID(), tempImage );
        return tempImage;

    }

    public String copyImage( String path ) {

        FileSystem fileSys = FileSystems.getDefault();
        Path srcPath = fileSys.getPath(path);
        //change this abomination if you are testing it (for now)
        //NEED TO EDIT LATER
        Path destPath = fileSys.getPath("c:\\Users\\brian\\OneDrive\\Documents\\Github\\Lee-Ko\\Greet-Dough\\data\\images.png");
        try {
            //COPY image from source to destination folder
            Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
            return destPath.toString();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return null;

    }

}
