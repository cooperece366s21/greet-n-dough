package store.postgres;

import model.Profile;
import store.model.ProfileStore;
import utility.ImageHandler;

import org.jdbi.v3.core.Jdbi;

import store.model.ProfileStore;

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

    // add bio with profile pic
        // will need to add constraint on the original table on a profile image store
            // might be necessary since profile image is not a post image
    //@Override
    //public

    // getBio
        // query on user_id
    @Override
    public Profile getBio(int uid ) {
        return jdbi.withHandle( handle -> handle.attach(ProfileDao.class).getBio(uid) );
    }

    @Override
    public Profile getProfile( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(ProfileDao.class).getProfile(uid) );
    }

    @Override
    public Profile addProfile( int uid, String bio, String path ) {

        String savedPath = imageHandler.copyImage(path);
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).addProfile( uid, bio, savedPath ) );
        return getProfile(uid);

    }

    // changeBio
        // sqlupdate with UPDATE profiles SET bio = new_bio WHERE user_id = user_id
    @Override
    public void changeBio( int uid, String newBio ) {
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).changeBio( uid, newBio ) );
    }

    @Override
    public void changeProfilePicture( int uid, String newPath ) {

        String savedPath = imageHandler.copyImage(newPath);
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).changeProfilePicture( uid, savedPath ) );

    }

}
