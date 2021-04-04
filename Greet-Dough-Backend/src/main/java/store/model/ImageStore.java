package store.model;

import model.Image;

import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;

public interface ImageStore {

    Image getImage( int ID );

    void addImage( Image newImage );

    Image addImage( Image path, int postID, int uid);

    boolean deleteImage( int ID );

    String uploadImage( Image newImage );

    void moveImage( Image newImage );

}
