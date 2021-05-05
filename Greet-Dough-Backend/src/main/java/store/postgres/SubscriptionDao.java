package store.postgres;

import model.UserTier;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface SubscriptionDao {
    // two columns, column 1 for creator and column 2 is follower
    // creator is the person you are folowing
    // follower are the people who is following the creator
    // Creator_id | Follower_id

    @SqlUpdate("DROP TABLE IF EXISTS subscriptions;")
    void deleteTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS subscriptions( " +
                    "creator_id INT " +     "NOT NULL, " +
                    "follower_id INT " +    "NOT NULL, " +
                    "tier INT " +           "NOT NULL " + "DEFAULT 0, " +
                    "CONSTRAINT fk_creator " + "FOREIGN KEY(creator_id) " +
                        "REFERENCES users(user_id) " + "ON DELETE CASCADE, " +
                    "CONSTRAINT fk_follower " + "FOREIGN_KEY(follower_id) " +
                        "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
                ");")
    void createTable();

    @SqlQuery("SELECT creator_id, tier FROM subscriptions " +
            "WHERE follower_id = (:follower_id);")
    List<UserTier> getSubscriptions(@Bind("follower_id") int follower_id);

    @SqlQuery("SELECT follower_id, tier FROM subscriptions " +
            "WHERE creator_id = (:creator_id);")
    List<UserTier> getFollowers(@Bind("creator_id") int creator_id);

    @SqlUpdate("INSERT INTO subscriptions (creator_id, follower_id) " +
                    "VALUES (:creator_id, :follower_id);")
    void addSubscription(@Bind("creator_id") int creator_id,
                        @Bind("follower_id") int follower_id);

    @SqlUpdate("DELETE FROM subscriptions " +
                    "WHERE creator_id = (:creator_id) AND " +
                        "follower_id = (:follower_id);")
    void deleteSubscription(@Bind("creator_id") int creator_id,
                           @Bind("follower_id") int follower_id);

}