package store.postgres;

import model.Profile;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface ProfileDao {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS profiles( " +
            "user_id INT " +            "NOT NULL, " +
            "bio TEXT " +               "NOT NULL, " +
            "profile_picture_id INT " + "NULL, " +
            "PRIMARY KEY(user_id), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
            ");")
    void createTable();

    @SqlUpdate("DROP TABLE IF EXISTS profiles;")
    void deleteTable();

    @SqlUpdate("INSERT INTO profiles (user_id, bio, profile_picture_id) " +
            "VALUES (:user_id, :bio, :profile_picture_id);")
    void addBio(@Bind("user_id") int user_id,
                @Bind("bio") String bio,
                @Bind("profile_picture_id") Integer profile_picture_id);

    @SqlQuery("SELECT bio FROM profiles WHERE user_id = (:user_id);")
    Profile getBio(@Bind("user_id") int user_id);

    @SqlUpdate("UPDATE profiles " +
            "SET bio = (:new_bio) " +
            "WHERE user_id = (:user_id);")
    void changeBio(@Bind("user_id") int user_id,
                   @Bind("new_bio") String new_bio);
}
