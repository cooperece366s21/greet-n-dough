package store.model;

import model.Image;

import java.util.List;

public interface ImageStore {

    /**
     * @return  the directory where the image files are saved
     */
    String getImageDir();

    Image getImage( int iid );

    /**
     * @return  a list of images owned by a user
      */
    List<Image> makeGallery( int uid );

    boolean hasImage( int iid );

    Image addImage( int uid, String path );

    /**
     * Soft deletes the image associated with the provided iid.
     * The specified Image should not appear in subsequent queries.
     */
    void deleteImage( int iid );

    /**
     * Deletes all soft deleted images.
     * Removes the images from the image directory and from the database.
     */
    void clearDeleted();

}
