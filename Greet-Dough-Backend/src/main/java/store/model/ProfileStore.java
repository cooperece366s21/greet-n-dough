package store.model;

import model.Profile;

public interface ProfileStore {

    Profile getProfile( int uid );

    Profile addProfile( int uid, String bio, String path );

    // getBio
        // query on user_id
    // Profile getBio(int uid);


    // changeBio
        // sqlupdate with UPDATE profiles SET bio = new_bio WHERE user_id = user_id
    void changeBio( int uid, String newBio );

    void changeProfilePicture( int uid, String path );

}
