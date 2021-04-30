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

    /**
     * Copies the image file specified by {@code path}. Can optionally delete the
     * original file after copying.
     *
     * @param   deleteOriginalImage a boolean specifying whether the original file
     *                              at {@code path} should be deleted after being copied
     * @return                      an Image object associated with the specified file
     */
    Image addImage( int uid, String path, boolean deleteOriginalImage );

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
