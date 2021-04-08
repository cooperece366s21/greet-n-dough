package store.postgres;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface PasswordDao {

    @SqlUpdate("DROP TABLE IF EXISTS passwords;")
    void resetTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS passwords( " +
            "user_id INT " +        "NOT NULL, " +
            "user_pass TEXT " +     "NOT NULL, " +
            "PRIMARY KEY(user_id), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
            ");")
    void createTable();

    @SqlUpdate("INSERT INTO passwords (user_id, user_pass) VALUES (:user_id, SHA256(:user_pass) );")
    void insertPassword(@Bind("user_id") int user_id, @Bind("user_pass") String user_pass);

    @SqlQuery("SELECT EXISTS( SELECT * FROM passwords WHERE (user_id, user_pass) = (:user_id, SHA256(:user_pass) ));")
    boolean isCorrectPassword(@Bind("user_id") int user_id, @Bind("user_pass") String user_pass);

    @SqlQuery("SELECT EXISTS( SELECT * FROM passwords WHERE user_id = (:user_id);")
    boolean hasPassword(@Bind("user_id") int user_id);

}
