package store.postgres;

import model.User;
import store.postgres.GreetDoughJdbi.UserRowMapper;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import java.util.List;

public interface UserDao {

    @SqlUpdate("DROP TABLE users;")
    void resetTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS users( " +
            "user_id SERIAL PRIMARY KEY " + "NOT NULL, " +
            "name TEXT " +                  "NOT NULL" +
            ");")
    void createTable();

    @SqlUpdate("INSERT INTO users (name) VALUES (:name);")
    @GetGeneratedKeys("user_id")
    int insertUser(@Bind("name") String name);

    @SqlUpdate("DELETE FROM users WHERE user_id = (:user_id);")
    void deleteUser(@Bind("user_id") int user_id);

    @SqlQuery("SELECT EXISTS( " +
            "SELECT * from users WHERE user_id = (:user_id));")
    Boolean containsUser(@Bind("user_id") int user_id);

    @SqlQuery("SELECT * FROM users ORDER BY name")
    List<User> listUsers();

    @SqlQuery("SELECT * FROM users WHERE user_id = (:user_id)")
    User getUser(@Bind("user_id") int user_id);

}