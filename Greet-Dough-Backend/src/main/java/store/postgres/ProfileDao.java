package store.postgres;

import model.Profile;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public interface ProfileDao {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS profiles( " +
            "user_id INT " +                "NOT NULL, " +
            "user_bio TEXT " +              "NULL, " +
            "profile_picture_path TEXT " +  "NULL, " +
            "is_deleted BOOLEAN " +         "NOT NULL " + "DEFAULT FALSE, " +
            "PRIMARY KEY(user_id) " +
            ");")
    void createTable();

    @SqlUpdate("DROP TABLE IF EXISTS profiles;")
    void deleteTable();

    @SqlQuery("SELECT * FROM profiles " +
            "WHERE user_id = (:user_id) AND " +
                "is_deleted = FALSE;")
    Optional<Profile> getProfile(@Bind("user_id") int user_id);

    @SqlQuery("SELECT * FROM profiles " +
            "ORDER BY user_id;")
    LinkedList<Profile> getAllProfiles();

    @SqlUpdate("INSERT INTO profiles (user_id, user_bio, profile_picture_path) " +
            "VALUES (:user_id, :user_bio, :profile_picture_path);")
    void addProfile(@Bind("user_id") int user_id,
                    @Bind("user_bio") String user_bio,
                    @Bind("profile_picture_path") String profile_picture_path);

    @SqlUpdate("UPDATE profiles " +
            "SET user_bio = (:new_bio) " +
            "WHERE user_id = (:user_id) AND " +
                "is_deleted = FALSE;")
    void changeBio(@Bind("user_id") int user_id,
                   @Bind("new_bio") String new_bio);

    @SqlUpdate("UPDATE profiles " +
            "SET profile_picture_path = (:new_profile_picture_path) " +
            "WHERE user_id = (:user_id) AND " +
                "is_deleted = FALSE;")
    void changeProfilePicture(@Bind("user_id") int user_id,
                              @Bind("new_profile_picture_path") String new_profile_picture_path);

    @SqlUpdate("UPDATE profiles " +
            "SET user_bio = NULL " +
            "WHERE user_id = (:user_id);")
    void deleteBio(@Bind("user_id") int user_id);

    @SqlUpdate("UPDATE profiles " +
            "SET profile_picture_path = NULL " +
            "WHERE user_id = (:user_id);")
    void deleteProfilePicture(@Bind("user_id") int user_id);

    /**
     * @return a list of profiles deleted from the table
     */
    @SqlQuery("DELETE FROM profiles " +
            "WHERE is_deleted = true " +
            "RETURNING *;")
    List<Profile> clearDeleted();

}
