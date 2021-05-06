package store.postgres;

import model.User;
import model.UserTier;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionStorePostgresTest extends SubscriptionStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    public SubscriptionStorePostgresTest() {
        super(jdbi);
    }

    @Test
    void test() {

        UserStorePostgres userStorePostgres = new UserStorePostgres(jdbi);
        SubscriptionStorePostgres subscriptionStorePostgres = new SubscriptionStorePostgres(jdbi);
        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        // Create Users
        User newUser_1 = userStorePostgres.addUser("Ban Dim");
        User newUser_2 = userStorePostgres.addUser("Bill Bluemint");
        User newUser_3 = userStorePostgres.addUser("Vim Kim");
        User newUser_4 = userStorePostgres.addUser("Poki Vim");

        // Test Subscribing with positive tier values
        subscriptionStorePostgres.addSubscription( newUser_1.getID(), newUser_2.getID(), 3 );

        // Test retrieving the list of subscribers
        List<UserTier> newUser_1_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_1.getID() );
        assert ( newUser_1_subscriptions.size() == 1 );

        // Test retrieving the empty list of followers
        assert ( subscriptionStorePostgres.getFollowers( newUser_1.getID() ).isEmpty() );

        // Test retrieving the list of followers
        List<UserTier> newUser_2_followers = subscriptionStorePostgres.getFollowers( newUser_2.getID() );
        assert ( newUser_2_followers.size() == 1 );

        // Test Subscribing with free tier (tier 0)
        subscriptionStorePostgres.addSubscription( newUser_3.getID(), newUser_4.getID() );

        // Test retrieving the list of subscribers
        List<UserTier> newUser_3_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_3.getID() );
        assert ( newUser_3_subscriptions.size() == 1 );

        // Test retrieving the list of followers
        List<UserTier> newUser_4_followers = subscriptionStorePostgres.getFollowers( newUser_4.getID() );
        assert ( newUser_4_followers.size() == 1 );

        // Test subscribing back to a follower
        subscriptionStorePostgres.addSubscription( newUser_4.getID(), newUser_3.getID() );
        List<UserTier> newUser_3_followers = subscriptionStorePostgres.getFollowers( newUser_3.getID() );
        assert ( newUser_3_followers.size() == 1 );

        List<UserTier> newUser_4_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_4.getID() );
        assert ( newUser_4_subscriptions.size() == 1 );

        // Test subscribing to multiple users
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_1.getID(), 3 );
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_3.getID(), 1 );
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_4.getID(), 2 );
        List<UserTier> newUser_2_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_2.getID() );
        assert ( newUser_2_subscriptions.size() == 3 );

        // Test deleting the subscription
        //      Should delete cascade the subscribers and followers
        subscriptionStorePostgres.deleteSubscription( newUser_3.getID(), newUser_4.getID() );
        assert ( subscriptionStorePostgres.getSubscriptions( newUser_3.getID() ).isEmpty() );

        // Test deleting the user
        userStorePostgres.deleteUser( newUser_1.getID() );
        assert ( subscriptionStorePostgres.getSubscriptions( newUser_2.getID() ).size() == 2 );

        // Users can't subscribe to themselves (to do in handler)

    }

}