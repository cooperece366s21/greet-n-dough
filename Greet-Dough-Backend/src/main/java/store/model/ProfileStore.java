package store.model;

public interface ProfileStore {


    void addBio(int uid, String bio);


    // changeBio
        // sqlupdate with UPDATE profiles SET bio = new_bio WHERE user_id = user_id
    void changeBio(int uid, String new_bio);
}
