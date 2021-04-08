package store.postgres;

import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;

public interface PasswordDao {

    @SqlUpdate("DROP TABLE IF EXISTS passwords;")
    void resetTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS passwords( " +
            "user_email TEXT " +    "NOT NULL, " +
            "user_id INT " +        "NOT NULL, " +
            "user_pass TEXT " +     "NOT NULL, " +
            "PRIMARY KEY(user_email), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
            ");")
    void createTable();

    @SqlUpdate("INSERT INTO passwords (user_email, user_id, user_pass) VALUES (:user_email, :user_id, MD5(:user_pass) );")
    void insertPassword(@Bind("user_email") String user_email, @Bind("user_id") int user_id, @Bind("user_pass") String user_pass) throws UnableToExecuteStatementException;

    @SqlQuery("SELECT user_id FROM passwords WHERE (user_email, user_pass) = (:user_email, MD5(:user_pass) );")
    Optional<Integer> isCorrectPassword(@Bind("user_email") String user_email, @Bind("user_pass") String user_pass);

    @SqlQuery("SELECT EXISTS( SELECT * FROM passwords WHERE user_email = (:user_email) );")
    boolean hasEmail(@Bind("user_email") String user_email);

}
