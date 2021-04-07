package store.postgres;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;
import java.util.OptionalInt;

public interface LoginDao {

    @SqlUpdate("DROP TABLE IF EXISTS login;")
    void resetTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS login( " +
            "user_token TEXT " +    "NOT NULL, " +
            "user_id INT " +        "NOT NULL, " +
            "PRIMARY KEY(user_token), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
            ");")
    void createTable();

    @SqlUpdate("INSERT INTO login (user_token, user_id) VALUES (MD5(random()::text), :user_id);")
    @GetGeneratedKeys("user_token")
    String insertInstance(@Bind("user_id") int user_id);

    @SqlQuery("SELECT EXISTS( SELECT * FROM login WHERE user_token = (:user_token) );")
    boolean containsInstance(@Bind("user_token") String user_token );

    @SqlQuery("SELECT user_id FROM login WHERE user_token = (:user_token);")
    Optional<Integer> getUserID(@Bind("user_token") String user_token );

}
