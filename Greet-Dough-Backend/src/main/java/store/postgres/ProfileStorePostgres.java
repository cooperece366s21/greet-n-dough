package store.postgres;

import store.model.ProfileStore;

import org.jdbi.v3.core.Jdbi;

import store.model.ProfileStore;

public class ProfileStorePostgres implements ProfileStore {

    private final Jdbi jdbi;

    public ProfileStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void init() {
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).deleteTable() );
    }

    public void delete() {
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).createTable() );
    }

    @Override
    public void addBio( int uid, String bio ) {
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).addBio( uid, bio, null ) );
    }

    // add bio with profile pic
        // will need to add constraint on the original table on a profile image store
            // might be necessary since profile image is not a post image

    // getBio
        // query on user_id

    // changeBio
        // sqlupdate with UPDATE profiles SET bio = new_bio WHERE user_id = user_id
    @Override
    public void changeBio( int uid, String newBio ) {
        jdbi.useHandle( handle -> handle.attach(ProfileDao.class).changeBio( uid, newBio ) );
    }

}
