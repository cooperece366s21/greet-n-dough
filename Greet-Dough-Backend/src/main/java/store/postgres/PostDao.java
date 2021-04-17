package store.postgres;

import model.Post;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import java.util.List;
import java.util.Optional;

public interface PostDao {

    @SqlUpdate("DROP TABLE IF EXISTS posts;")
    void deleteTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS posts( " +
            "post_id SERIAL " +         "NOT NULL, " +
            "user_id INT " +            "NOT NULL, " +
            "image_id INT " +           "NULL, " +
            "post_title TEXT " +        "NOT NULL, " +
            "post_contents TEXT " +     "NULL, " +
            "time_created TIMESTAMP " + "NOT NULL " + "DEFAULT NOW(), " +
            "PRIMARY KEY(post_id), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE, " +
            "CONSTRAINT fk_image " + "FOREIGN KEY(image_id) " +
                "REFERENCES images(image_id) " +
            ");")
    void createTable();

    @SqlUpdate("INSERT INTO posts (user_id, image_id, post_title, post_contents) " +
            "VALUES (:user_id, :image_id, :title, :contents);")
    @GetGeneratedKeys("post_id")
    int addPost(@Bind("title") String title,
                @Bind("contents") String contents,
                @Bind("user_id") int user_id,
                @Bind("image_id") Integer image_id);

    @SqlUpdate("DELETE FROM posts " +
            "WHERE post_id = (:post_id);")
    void deletePost(@Bind("post_id") int post_id);

    @SqlQuery("SELECT EXISTS( " +
            "SELECT * from users " +
            "WHERE post_id = (:post_id));")
    Boolean containsPost(@Bind("post_id") int post_id);

    @SqlQuery("SELECT * FROM posts " +
            "ORDER BY user_id")
    List<Post> listPosts();

    @SqlQuery("SELECT * FROM posts " +
            "WHERE post_id = (:post_id)")
    Optional<Post> getPost(@Bind("post_id") int post_id);

    @SqlQuery("SELECT * FROM posts " +
            "WHERE user_id = (:user_id)")
    List<Post> getFeed(@Bind("user_id") int user_id);

}