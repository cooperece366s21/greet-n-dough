package store.postgres;

import model.Post;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import java.util.List;
import java.util.Optional;

public interface PostDao {

    @SqlUpdate( "DROP TABLE IF EXISTS post_images;" +
                "DROP TABLE IF EXISTS posts;")
    void deleteTable();

    @SqlUpdate( "CREATE TABLE IF NOT EXISTS posts( " +
                    "post_id SERIAL " +         "NOT NULL, " +
                    "user_id INT " +            "NOT NULL, " +
                    "post_title TEXT " +        "NOT NULL, " +
                    "post_contents TEXT " +     "NULL, " +
                    "time_created TIMESTAMP " + "NOT NULL " + "DEFAULT NOW(), " +
                    "PRIMARY KEY(post_id), " +
                    "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                        "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
                ");" +
                "CREATE TABLE IF NOT EXISTS post_images( " +
                    "post_id INT " +            "NOT NULL, " +
                    "image_id INT " +           "NOT NULL, " +
                    "CONSTRAINT fk_post " + "FOREIGN KEY(post_id) " +
                        "REFERENCES posts(post_id)" + "ON DELETE CASCADE, " +
                    "CONSTRAINT fk_image " + "FOREIGN KEY(image_id) " +
                        "REFERENCES images(image_id) " + "ON DELETE CASCADE " +
                ");"
            )
    void createTable();

    @SqlUpdate( "INSERT INTO posts (user_id, post_title, post_contents) " +
                    "VALUES (:user_id, :title, :contents);")
    @GetGeneratedKeys("post_id")
    int addPost(@Bind("title") String title,
                @Bind("contents") String contents,
                @Bind("user_id") int user_id);

    @SqlUpdate( "INSERT INTO post_images (post_id, image_id) " +
                    "VALUES (:post_id, :image_id);")
    void addPostImage(@Bind("post_id") int post_id,
                      @Bind("image_id") int image_id);

    @SqlUpdate( "DELETE FROM posts " +
                    "WHERE post_id = (:post_id);")
    void deletePost(@Bind("post_id") int post_id);

    @SqlQuery(  "SELECT EXISTS( " +
                    "SELECT * from users " +
                    "WHERE post_id = (:post_id));")
    Boolean containsPost(@Bind("post_id") int post_id);

    @SqlQuery(  "SELECT * FROM posts " +
                    "JOIN " +
                        "(SELECT post_id, ARRAY_AGG(image_id) AS image_id_agg FROM post_images " +
                        "GROUP BY post_id) AS x " +
                    "USING(post_id) " +
                    "ORDER BY post_id;")
    List<Post> getAllPosts();

    @SqlQuery(  "SELECT * FROM posts " +
                    "LEFT JOIN " +
                        "(SELECT post_id, ARRAY_AGG(image_id) AS image_id_agg FROM post_images " +
                        "GROUP BY post_id)" +
                        "AS x " +
                    "USING(post_id)" +
                    "WHERE post_id=(:post_id) " +
                    "ORDER BY post_id;")
    Optional<Post> getPost(@Bind("post_id") int post_id);

    @SqlQuery(  "SELECT * FROM posts " +
                    "LEFT JOIN " +
                        "(SELECT post_id, ARRAY_AGG(image_id) AS image_id_agg FROM post_images " +
                        "GROUP BY post_id)" +
                        "AS x " +
                    "USING(post_id)" +
                    "WHERE user_id = (:user_id) " +
                    "ORDER BY user_id;")
    List<Post> getFeed(@Bind("user_id") int user_id);

    @SqlUpdate( "UPDATE posts " +
                    "SET post_title = (:new_title) " +
                    "WHERE post_id = (:post_id);")
    void changeTitle(@Bind("post_id") int post_id, @Bind("new_title") String new_title );

    @SqlUpdate( "UPDATE posts " +
                    "SET post_contents = (:new_contents) " +
                    "WHERE post_id = (:post_id);")
    void changeContents(@Bind("post_id") int post_id, @Bind("new_contents") String new_contents );

}