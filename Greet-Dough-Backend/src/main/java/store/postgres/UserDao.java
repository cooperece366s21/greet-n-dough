package store.postgres;

import model.User;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import store.postgres.GreetDoughJdbi.UserRowMapper;

import java.util.List;
import java.util.Map;

public interface UserDao {

    @SqlUpdate("DROP TABLE users;")
    void resetTable();

    @SqlUpdate("CREATE TABLE users( " +
            "id SERIAL PRIMARY KEY     NOT NULL, " +
            "name TEXT              NOT NULL);")
    void createTable();

    @SqlUpdate("INSERT INTO users (name) VALUES (:name);")
    @GetGeneratedKeys("id")
    int insertUser(@Bind("name") String name);

    @SqlUpdate("INSERT INTO users(id, name) VALUES (:id, :name)")
    void insertNamed(@Bind("id") int id, @Bind("name") String name);

    @SqlUpdate("INSERT INTO users(id, name) VALUES (:id, :name)")
    void insertBean(@BindBean User user);

    @SqlQuery("SELECT * FROM users ORDER BY name")
    @RegisterBeanMapper(User.class)
    List<User> listUsers();

    @SqlQuery("SELECT * FROM users WHERE id IN (:id)")
    User getUser(@Bind("id") int id);

}