package store.model;

import model.Image;

import java.util.List;

public interface ImageStore {

    Image getImage( int iid );

    /**
     * @return a list of images owned by a user
      */
    List<Image> makeGallery( int uid );

    boolean hasImage( int iid );

    Image addImage( String path, int uid );

    /**
     * Soft deletes the image associated with the provided iid.
     * Image should not appear in subsequent queries.
     */
    void deleteImage( int iid );

    /**
     * Deletes all soft deleted images.
     * Removes the images from the image directory and from the database.
     */
    void clearDeleted();

}
