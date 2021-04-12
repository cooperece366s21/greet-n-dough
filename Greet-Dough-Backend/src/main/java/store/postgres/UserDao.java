package store.postgres;

import model.User;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    @SqlUpdate("DROP TABLE IF EXISTS users;")
    void deleteTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS users( " +
            "user_id SERIAL PRIMARY KEY " + "NOT NULL, " +
            "user_name TEXT " +             "NOT NULL" +
            ");")
    void createTable();

    @SqlUpdate("INSERT INTO users (user_name) VALUES (:user_name);")
    @GetGeneratedKeys("user_id")
    int insertUser(@Bind("user_name") String user_name);

    @SqlUpdate("DELETE FROM users WHERE user_id = (:user_id);")
    void deleteUser(@Bind("user_id") int user_id);

    @SqlQuery("SELECT * FROM users ORDER BY user_name")
    List<User> listUsers();

    @SqlQuery("SELECT * FROM users WHERE user_id = (:user_id)")
    Optional<User> getUser(@Bind("user_id") int user_id);

    @SqlQuery("SELECT * FROM users WHERE user_name " +
            "SIMILAR TO :name || '%';" )
    List<User> searchUsers(@Bind("name") String name);

}