package store.postgres;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;

public interface PasswordDao {

    @SqlUpdate( "DROP TABLE IF EXISTS passwords;")
    void deleteTable();

    @SqlUpdate( "CREATE TABLE IF NOT EXISTS passwords( " +
                    "user_email TEXT " +    "NOT NULL, " +
                    "user_id INT " +        "NOT NULL, " +
                    "user_pass BYTEA " +    "NOT NULL, " +
                    "PRIMARY KEY(user_email), " +
                    "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                        "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
                ");")
    void createTable();

    @SqlUpdate( "INSERT INTO passwords (user_email, user_id, user_pass) " +
                    "VALUES ( LOWER(:user_email), :user_id, SHA256( CAST(:user_pass AS BYTEA) ) ) " +
                    "ON CONFLICT (user_email) DO NOTHING;")
    int addPassword(@Bind("user_email") String user_email,
                    @Bind("user_id") int user_id,
                    @Bind("user_pass") String user_pass);

    @SqlQuery(  "SELECT user_id FROM passwords " +
                    "WHERE (user_email, user_pass) = " +
                        "( LOWER(:user_email), SHA256( CAST(:user_pass AS BYTEA) ) );")
    Optional<Integer> isCorrectPassword(@Bind("user_email") String user_email,
                                        @Bind("user_pass") String user_pass);

    @SqlQuery(  "SELECT EXISTS( " +
                    "SELECT * FROM passwords " +
                    "WHERE user_email = LOWER(:user_email));")
    boolean hasEmail(@Bind("user_email") String user_email);

    @SqlUpdate( "UPDATE passwords " +
                    "SET user_email = LOWER(:new_email) " +
                    "WHERE user_email = LOWER(:old_email);")
    void changeEmail(@Bind("old_email") String old_email,
                     @Bind("new_email") String new_email);

    @SqlUpdate( "UPDATE passwords " +
                    "SET user_pass = SHA256( CAST(:new_pass AS BYTEA) ) " +
                    "WHERE user_email = LOWER(:user_email);")
    void changePassword(@Bind("user_email") String user_email,
                        @Bind("new_pass") String new_pass);

}
