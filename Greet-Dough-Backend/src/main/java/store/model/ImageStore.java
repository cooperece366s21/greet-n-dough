package store.model;

import model.Image;

import java.util.List;

public interface ImageStore {

    Image getImage( int iid );

    // Returns a list of images owned by a user
    List<Image> makeGallery( int uid );

    boolean hasImage( int iid );

    Image addImage( String path, int uid );

    void deleteImage( int iid );

}
