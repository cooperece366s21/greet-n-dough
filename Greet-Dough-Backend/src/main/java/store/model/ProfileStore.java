package store.model;

import model.Profile;

public interface ProfileStore {

    Profile getProfile( int uid );

    /**
     * The method creates a profile with an empty bio
     * and no profile picture.
     */
    Profile addProfile( int uid );

    /**
     * The method creates a profile with the specified bio
     * and no profile picture.
     */
    Profile addProfile( int uid, String bio );

    /**
     * The method creates a profile with the specified bio
     * and profile picture.
     *
     * @param   deleteOriginalImage a boolean specifying whether the original file
     *                              at {@code path} should be deleted after being copied
     */
    Profile addProfile( int uid, String bio, String path, boolean deleteOriginalImage );

    void changeBio( int uid, String newBio );

    void deleteBio( int uid );

    /**
     * @param   deleteOriginalImage a boolean specifying whether the original file
     *                              at {@code newPath} should be deleted after being copied
     */
    void changeProfilePicture( int uid, String newPath, boolean deleteOriginalImage );

    void deleteProfilePicture( int uid );

    /**
     * Deletes all soft deleted profile pictures.
     * Removes the profile pictures from the
     * profile_pictures directory and from the database.
     */
    void clearDeleted();

}
