package store.postgres;

import model.Profile;
import store.model.ProfileStore;
import utility.ImageHandler;

import org.jdbi.v3.core.Jdbi;

import java.util.LinkedList;
import java.util.List;

public class ProfileStorePostgres implements ProfileStore {

    private final Jdbi jdbi;
    private final ImageHandler imageHandler;

    public ProfileStorePostgres( final Jdbi jdbi ) {

        this.jdbi = jdbi;
        this.imageHandler = new ImageHandler("profile_pictures");

    }

    public void delete() {
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).deleteTable() );
    }

    public void init() {
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).createTable() );
    }

    @Override
    public Profile getProfile( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(ProfileDao.class).getProfile(uid) ).orElse( new Profile(uid) );
    }

    /**
     * Currently only used for testing.
     *
     * @return all profiles in the database
     */
    public LinkedList<Profile> getAllProfiles() {
        return jdbi.withHandle( handle -> handle.attach(ProfileDao.class).getAllProfiles() );
    }

    @Override
    public Profile addProfile( int uid ) {
        return addProfile( uid, null );
    }

    @Override
    public Profile addProfile( int uid, String bio ) {
        return addProfile( uid, bio, null, false );
    }

    @Override
    public Profile addProfile( int uid, String bio, String path, boolean deleteOriginalImage ) {

        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).addProfile( uid, bio ) );
        changeProfilePicture( uid, path, deleteOriginalImage );
        return getProfile(uid);

    }

    @Override
    public void changeBio( int uid, String newBio ) {
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).changeBio( uid, newBio ) );
    }

    @Override
    public void changeProfilePicture( int uid, String newPath, boolean deleteOriginalImage ) {

        String savedPath = imageHandler.copyImage(newPath);
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).changeProfilePicture( uid, savedPath ) );

        if ( deleteOriginalImage ) {
            imageHandler.deleteImage(newPath);
        }

    }

    @Override
    public void deleteBio( int uid ) {
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).deleteBio(uid) );
    }

    @Override
    public void deleteProfilePicture( int uid ) {

        // Delete the profile picture from the profile_pictures directory
        Profile tempProfile = getProfile(uid);
        imageHandler.deleteImage( tempProfile.getPath() );

        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).deleteProfilePicture(uid) );

    }

    @Override
    public void clearDeleted() {

        List<Profile> deletedProfiles = jdbi.withHandle(handle -> handle.attach(ProfileDao.class).clearDeleted() );
        for ( Profile profile : deletedProfiles ) {
            imageHandler.deleteImage( profile.getPath() );
        }

    }

}
