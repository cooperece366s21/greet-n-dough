package store.postgres;

import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface ImageDao {

    @SqlUpdate("DROP TABLE images;")
    void resetTable();

    @SqlUpdate("CREATE TABLE images( " +
            "image_id SERIAL NOT NULL, " +
            "user_id INT NOT NULL, " +
            "path TEXT NOT NULL, " +
            "PRIMARY KEY(image_id), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) + " +
            ");")
    void createTable();
}
