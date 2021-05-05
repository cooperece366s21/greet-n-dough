import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface SubscribeDao {
    // two columns, column 1 for creator and column 2 is follower
    // creator is the person you are folowing
    // follower are the people who is following the creator
    // Creator_id | Follower_id

    @SqlUpdate("DROP TABLE IF EXISTS subscribe;")
    void deleteTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS subscribe( " +
            "creator_id INT " + "NOT NULL, " +
            "follower_id INT " + "NOT NULL, " +
            "CONSTRAINT fk_creator " + "FOREIGN KEY(creator_id) " +
            "REFERENCES users(user_id) " + "ON DELETE CASCADE, " +
            "CONSTRAINT fk_follower " + "FOREIGN_KEY(post_id) " +
            "REFERENCES users(user_id) " + "ON DELETE CASCADE " +
            ");")
    void createTable();

    @SqlUpdate("INSERT INTO subscribe (creator_id, follower_id) VALUES (:creator_id, :follower_id);")
    int addSubscription(@Bind("creator_id") int creator_id,
                        @Bind("follower_id") int follower_id);

    @SqlUpdate("DELETE FROM subscribe WHERE creator_id = (:creator_id) AND follower_id = (:follower_id);")
    int removeSubscription(@Bind("creator_id") int creator_id,
                           @Bind("follower_id") int follower_id);

    // getfollowing return a list of users that the designated follows
    @SqlQuery("SELECT creator_id FROM subscribe WHERE follower_id = (:follower_id);")
    ArrayList<Integer> getSubscriber(@Bind("follower_id") int follower_id);

    @SqlQuery("SELECT follower_id FROM subscribe WHERE creator_id = (:creator_id);")
    ArrayList<Integer> getFollower(@Bind("creator_id") int creator_id);

}