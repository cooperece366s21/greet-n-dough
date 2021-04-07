package store.postgres;

import model.Likes;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface LikeDao {

    // columns
    // post_id, user_id

    // users who like will show up
    // if ids not in a set, to get like count
        // SELECT post_id, COUNT(userid) AS likeCount
            // FROM like
        // WHERE post_id = (:post_id)
        // GROUP BY post_id;

    // in pure backend
        // SELECT post_id, ARRAY_AGG(user_id) AS userLike
            // FROM like
        // WHERE post_id = (:post_id)
        // GROUP BY post_id;

    // in a set
    // SELECT * FROM like WHERE post_id = (:post_id);
        // then get the length of set

    // add like if not a set
        // INSERT INTO like (user_id, post_id) VALUES (:user_id, :post_id);

    // how to insert a set, is it even necessary???

    // remove like
        // DELETE FROM like WHERE user_id = (:user_id);

    @SqlUpdate("DROP TABLE IF EXISTS likes;")
    void resetTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS likes( " +
            "post_id INT " +        "NOT NULL, " +
            "user_id INT[] " +      "NOT NULL, " +
            "PRIMARY KEY(user_id), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
            "CONSTRAINT fk_post " + "FOREIGN KEY(post_id) " +
                "REFERENCES post(post_id) " + "ON DELETE CASCADE " +
            ");")
    void createTable();


}
