package store.postgres;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;

public interface WalletDao {

    @SqlUpdate("DROP TABLE IF EXISTS wallet;")
    void deleteTable();

    // Can store 100 digits on both sides of decimal
    //      Limited to 2 digits on the right side of the decimal (cents)
    @SqlUpdate("CREATE TABLE IF NOT EXISTS wallet( " +
            "user_id INT " +                    "NOT NULL, " +
            "user_balance NUMERIC(1000,2) " +   "NOT NULL " + "DEFAULT 0, " +
            "PRIMARY KEY(user_id), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
            ");")
    void createTable();

    @SqlUpdate("INSERT INTO wallet (user_id, user_balance) VALUES (:user_id, :balance);")
    void insertUser(@Bind("user_id") int user_id,
                    @Bind("balance") BigDecimal balance);

    @SqlQuery("SELECT user_balance FROM wallet WHERE user_id = (:user_id);")
    BigDecimal getBalance(@Bind("user_id") int user_id);

    // From https://stackoverflow.com/a/48648915
    @SqlUpdate("UPDATE wallet SET user_balance = (user_balance + :amount) " +
            "WHERE (user_balance + :amount) >= 0 " +
            "AND user_id = (:user_id);")
    void addToBalance(@Bind("user_id") int user_id,
                      @Bind("amount") BigDecimal amount);

}
