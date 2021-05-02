package store.postgres;

import model.Profile;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.LinkedList;
import java.util.Optional;

public interface ProfileDao {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS profiles( " +
            "user_id INT " +                "NOT NULL, " +
            "user_bio TEXT " +              "NULL, " +
            "image_id INT " +               "NULL, " +
            "PRIMARY KEY(user_id), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE, " +
            "CONSTRAINT fk_image " + "FOREIGN KEY(image_id) " +
                "REFERENCES images(image_id) " + "ON DELETE SET DEFAULT " +
            ");")
    void createTable();

    @SqlUpdate("DROP TABLE IF EXISTS profiles;")
    void deleteTable();

    @SqlQuery("SELECT * FROM profiles " +
            "WHERE user_id = (:user_id);")
    Optional<Profile> getProfile(@Bind("user_id") int user_id);

    @SqlQuery("SELECT * FROM profiles " +
            "ORDER BY user_id;")
    LinkedList<Profile> getAllProfiles();

    @SqlUpdate("INSERT INTO profiles (user_id, user_bio) " +
            "VALUES (:user_id, :user_bio);")
    void addProfile(@Bind("user_id") int user_id,
                    @Bind("user_bio") String user_bio);

    @SqlUpdate("UPDATE profiles " +
            "SET user_bio = (:new_bio) " +
            "WHERE user_id = (:user_id);")
    void changeBio(@Bind("user_id") int user_id,
                   @Bind("new_bio") String new_bio);

    @SqlUpdate("UPDATE profiles " +
            "SET image_id = (:new_image_id) " +
            "WHERE user_id = (:user_id);")
    void changeProfilePicture(@Bind("user_id") int user_id,
                              @Bind("new_image_id") int new_image_id);

    @SqlUpdate("UPDATE profiles " +
            "SET user_bio = NULL " +
            "WHERE user_id = (:user_id);")
    void deleteBio(@Bind("user_id") int user_id);

    @SqlUpdate("UPDATE profiles " +
            "SET image_id = NULL " +
            "WHERE user_id = (:user_id);")
    void deleteProfilePicture(@Bind("user_id") int user_id);

}
