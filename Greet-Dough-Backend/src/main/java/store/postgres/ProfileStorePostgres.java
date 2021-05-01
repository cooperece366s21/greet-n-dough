package store.postgres;

import model.Profile;
import store.model.ImageStore;
import store.model.ProfileStore;
import utility.ImageHandler;
import utility.PathDefs;

import org.jdbi.v3.core.Jdbi;

import java.util.LinkedList;

public class ProfileStorePostgres implements ProfileStore {

    private final Jdbi jdbi;
    private final ImageHandler imageHandler;
    private final ImageStore imageStore;
    private static final String imageDir = PathDefs.PFP_DIR;

    public ProfileStorePostgres( final Jdbi jdbi ) {

        this.jdbi = jdbi;
        this.imageHandler = new ImageHandler(imageDir);
        this.imageStore = new ImageStorePostgres(jdbi, imageDir);

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

        if ( path != null ) {
            changeProfilePicture( uid, path, deleteOriginalImage );
        }

        return getProfile(uid);

    }

    @Override
    public void changeBio( int uid, String newBio ) {
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).changeBio( uid, newBio ) );
    }

    @Override
    public void changeProfilePicture( int uid, String newPath, boolean deleteOriginalImage ) {

        // Get the iid of the old profile picture (if exists)
        Integer oldPFP = getProfile(uid).getImageID();

        int iid = imageStore.addImage( uid, newPath, deleteOriginalImage ).getID();
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).changeProfilePicture( uid, iid ) );

        // Delete the old profile picture after successfully adding the new profile picture
        if ( oldPFP != null ) {
            imageStore.deleteImage(oldPFP);
        }

    }

    @Override
    public void deleteBio( int uid ) {
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).deleteBio(uid) );
    }

    /**
     * Assumes the profile has an existing profile picture.
     */
    @Override
    public void deleteProfilePicture( int uid ) {

        // Delete the profile picture from the profile_pictures directory
        Integer iid = getProfile(uid).getImageID();
        imageStore.deleteImage(iid);

        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).deleteProfilePicture(uid) );

    }

}
