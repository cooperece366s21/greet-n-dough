package store.model;

import model.Profile;

public interface ProfileStore {

    void addBio( int uid, String bio );

    // getBio
        // query on user_id
    //Profile getBio(int uid);

    // getBio
        // query on user_id
    Profile getBio(int uid);

    // changeBio
        // sqlupdate with UPDATE profiles SET bio = new_bio WHERE user_id = user_id
    void changeBio( int uid, String newBio );

}
