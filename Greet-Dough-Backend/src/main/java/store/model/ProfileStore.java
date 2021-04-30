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
     */
    Profile addProfile( int uid, String bio, String path );

    void changeBio( int uid, String newBio );

    void deleteBio( int uid );

    void changeProfilePicture( int uid, String path );

    void deleteProfilePicture( int uid );

    /**
     * Deletes all soft deleted profile pictures.
     * Removes the profile pictures from the
     * profile_pictures directory and from the database.
     */
    void clearDeleted();

}
