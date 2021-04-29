package store.postgres;

import model.Profile;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface ProfileDao {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS profiles( " +
            "user_id INT " +                "NOT NULL, " +
            "user_bio TEXT " +              "NULL, " +
            "profile_picture_path TEXT " +  "NULL, " +
            "PRIMARY KEY(user_id), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
            ");")
    void createTable();

    @SqlUpdate("DROP TABLE IF EXISTS profiles;")
    void deleteTable();

    @SqlQuery("SELECT * FROM profiles " +
            "WHERE user_id = (:user_id);")
    Profile getProfile(@Bind("user_id") int user_id);

    @SqlUpdate("INSERT INTO profiles (user_id, user_bio, profile_picture_path) " +
            "VALUES (:user_id, :user_bio, :profile_picture_path);")
    void addProfile(@Bind("user_id") int user_id,
                    @Bind("user_bio") String user_bio,
                    @Bind("profile_picture_path") String profile_picture_path);

    @SqlUpdate("UPDATE profiles " +
            "SET user_bio = (:new_bio) " +
            "WHERE user_id = (:user_id);")
    void changeBio(@Bind("user_id") int user_id,
                   @Bind("new_bio") String new_bio);

    @SqlUpdate("UPDATE profiles " +
            "SET profile_picture_path = (:new_profile_picture_path) " +
            "WHERE user_id = (:user_id);")
    void changeProfilePicture(@Bind("user_id") int user_id,
                              @Bind("new_profile_picture_path") String new_profile_picture_path);

}
