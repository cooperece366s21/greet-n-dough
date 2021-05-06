package store.postgres;

import model.UserTier;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface SubscriptionDao {

    @SqlUpdate("DROP TABLE IF EXISTS subscriptions;")
    void deleteTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS subscriptions( " +
                    "follower_id INT " +    "NOT NULL, " +
                    "creator_id INT " +     "NOT NULL, " +
                    "tier INT " +           "NOT NULL, " +
                    "CONSTRAINT fk_follower " + "FOREIGN KEY(follower_id) " +
                        "REFERENCES users(user_id) " + "ON DELETE CASCADE, " +
                    "CONSTRAINT fk_creator " + "FOREIGN KEY(creator_id) " +
                        "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
                ");")
    void createTable();

    @SqlQuery("SELECT creator_id AS user_id, tier FROM subscriptions " +
                    "WHERE follower_id = (:follower_id);")
    List<UserTier> getSubscriptions(@Bind("follower_id") int follower_id);

    @SqlQuery("SELECT follower_id AS user_id, tier FROM subscriptions " +
                    "WHERE creator_id = (:creator_id);")
    List<UserTier> getFollowers(@Bind("creator_id") int creator_id);

    @SqlQuery("SELECT tier FROM subscriptions " +
                    "WHERE follower_id = (:follower_id) AND " +
                        "creator_id = (:creator_id);")
    Optional<Integer> hasSubscription( int follower_id, int creator_id );

    @SqlUpdate("INSERT INTO subscriptions (follower_id, creator_id, tier) " +
                    "VALUES (:follower_id, :creator_id, :tier);")
    void addSubscription(@Bind("follower_id") int follower_id,
                         @Bind("creator_id") int creator_id,
                         @Bind("tier") int tier);

    @SqlUpdate("DELETE FROM subscriptions " +
                    "WHERE follower_id = (:follower_id) AND " +
                        "creator_id = (:creator_id);")
    void deleteSubscription(@Bind("follower_id") int follower_id,
                            @Bind("creator_id") int creator_id);

    @SqlUpdate( "UPDATE subscriptions " +
                    "SET tier = (:new_tier) " +
                    "WHERE follower_id = (:follower_id) AND" +
                        "creator_id = (:creator_id);")
    void changeSubscription(@Bind("follower_id") int follower_id,
                            @Bind("creator_id") int creator_id,
                            @Bind("new_tier") int new_tier );

}