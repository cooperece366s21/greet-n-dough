package store.postgres;

import model.Image;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface ImageDao {

    @SqlUpdate("DROP TABLE IF EXISTS images;")
    void resetTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS images( " +
            "image_id SERIAL " +    "NOT NULL, " +
            "user_id INT " +        "NOT NULL, " +
            "image BYTEA " +        "NOT NULL, " +
            "PRIMARY KEY(image_id), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
            ");")
    void createTable();

    @SqlUpdate("INSERT INTO images (user_id, path) VALUES (:user_id, :path);")
    @GetGeneratedKeys("image_id")
    int insertImage(@Bind("path") String path,
                   @Bind("user_id") int user_id);

    @SqlUpdate("DELETE FROM images WHERE image_id = (:image_id);")
    void deleteImage(@Bind("image_id") int image_id);

    @SqlQuery("SELECT EXISTS( " +
            "SELECT * from images WHERE image_id = (:image_id));")
    Boolean containsImage(@Bind("image_id") int image_id);

    @SqlQuery("SELECT * FROM images ORDER BY user_id")
    List<Image> listImages();

    @SqlQuery("SELECT * FROM images WHERE image_id = (:image_id)")
    Image getImage(@Bind("image_id") int image_id);

    @SqlQuery("SELECT * FROM images WHERE user_id = (:user_id)")
    List<Image> getGallery(@Bind("user_id") int user_id);

}
