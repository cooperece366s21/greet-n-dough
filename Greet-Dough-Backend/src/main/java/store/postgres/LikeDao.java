package store.postgres;

import model.Likes;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import java.util.Optional;

public interface LikeDao {

    @SqlUpdate("DROP TABLE IF EXISTS likes;")
    void deleteTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS likes( " +
            "post_id INT " +        "NOT NULL, " +
            "user_id INT " +        "NOT NULL, " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE, " +
            "CONSTRAINT fk_post " + "FOREIGN KEY(post_id) " +
                "REFERENCES posts(post_id) " + "ON DELETE CASCADE " +
            ");")
    void createTable();

    @SqlUpdate("INSERT INTO likes (post_id, user_id) " +
            "VALUES (:post_id, :user_id);")
    int addUserLike(@Bind("post_id") int post_id,
                    @Bind("user_id") int user_id);

    @SqlUpdate("DELETE FROM likes " +
            "WHERE post_id = (:post_id) AND " +
                    "user_id = (:user_id);")
    void deleteUserLike(@Bind("post_id") int post_id,
                        @Bind("user_id") int user_id);

    @SqlQuery("SELECT post_id, ARRAY_AGG(user_id) as user_id_agg FROM likes " +
            "WHERE post_id = (:post_id) " +
            "GROUP BY post_id")
    Optional<Likes> getUserLikes(@Bind("post_id") int post_id);

    @SqlQuery("SELECT EXISTS( " +
            "SELECT * from likes " +
            "WHERE post_id = (:post_id) AND " +
                    "user_id = (:user_id));")
    Boolean hasUserLike(@Bind("post_id") int post_id,
                        @Bind("user_id") int user_id);

}
