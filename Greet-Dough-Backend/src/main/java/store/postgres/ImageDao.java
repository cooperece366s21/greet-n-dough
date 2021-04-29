package store.postgres;

import model.Image;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface ImageDao {

    @SqlUpdate("DROP TABLE IF EXISTS images;")
    void deleteTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS images( " +
            "image_id SERIAL " +    "NOT NULL, " +
            "user_id INT " +        "NOT NULL, " +
            "path TEXT " +          "NOT NULL, " +
            "is_deleted BOOLEAN " + "NOT NULL " + "DEFAULT FALSE, " +
            "PRIMARY KEY(image_id) " +
            ");")
    void createTable();

    @SqlUpdate("INSERT INTO images (user_id, path) " +
            "VALUES (:user_id, :path);")
    @GetGeneratedKeys("image_id")
    int addImage(@Bind("user_id") int user_id,
                 @Bind("path") String path);

    @SqlUpdate("UPDATE images " +
            "SET is_deleted = true " +
            "WHERE image_id = (:image_id);")
    void deleteImage(@Bind("image_id") int image_id);

    /**
     * @return a list of images deleted from the table
     */
    @SqlQuery("DELETE FROM images " +
            "WHERE is_deleted = true " +
            "RETURNING *;")
    List<Image> clearDeleted();

    @SqlQuery("SELECT * FROM images " +
            "ORDER BY user_id")
    List<Image> listImages();

    @SqlQuery("SELECT * FROM images " +
            "WHERE image_id = (:image_id) AND " +
            "is_deleted = false")
    Optional<Image> getImage(@Bind("image_id") int image_id);

    @SqlQuery("SELECT * FROM images " +
            "WHERE user_id = (:user_id) AND " +
            "is_deleted = false")
    List<Image> getGallery(@Bind("user_id") int user_id);

}
