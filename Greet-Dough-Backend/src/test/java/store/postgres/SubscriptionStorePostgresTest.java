package store.postgres;

import model.User;
import model.UserTier;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionStorePostgresTest extends SubscriptionStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    private static UserStorePostgres userStorePostgres;
    private static ImageStorePostgres imageStorePostgres;
    private static SubscriptionStorePostgres subscriptionStorePostgres;

    private static User newUser_1;
    private static User newUser_2;
    private static User newUser_3;
    private static User newUser_4;

    public SubscriptionStorePostgresTest() {
        super(jdbi);
    }

    @BeforeAll
    static void setupAll() {

        //Delete all the database
        ResetDao.deleteAll(jdbi);

        userStorePostgres = new UserStorePostgres(jdbi);
        imageStorePostgres = new ImageStorePostgres(jdbi);
        subscriptionStorePostgres = new SubscriptionStorePostgres(jdbi);

    }

    @AfterAll
    static void tearDownAll() {
        ResetDao.reset(jdbi);
    }

    @BeforeEach
    void setUpEach() {

        // Delete the database
        subscriptionStorePostgres.delete();
        imageStorePostgres.delete();
        userStorePostgres.delete();

        //Initialize the database
        userStorePostgres.init();
        imageStorePostgres.init();
        subscriptionStorePostgres.init();

        // Create Users
        newUser_1 = userStorePostgres.addUser("Ban Dim");
        newUser_2 = userStorePostgres.addUser("Bill Bluemint");
        newUser_3 = userStorePostgres.addUser("Vim Kim");
        newUser_4 = userStorePostgres.addUser("Poki Vim");

    }

    @Test
    void testAddSubscription() {

        // Test Subscribing with positive tier values
        subscriptionStorePostgres.addSubscription( newUser_1.getID(), newUser_2.getID(), 3 );

        // Test retrieving the list of subscribers
        List<UserTier> newUser_1_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_1.getID() );
        assert ( newUser_1_subscriptions.size() == 1 );

    }

    @Test
    void testGetFollowers() {

        // Subscribing with positive tier values
        subscriptionStorePostgres.addSubscription( newUser_1.getID(), newUser_2.getID(), 3 );

        // Test retrieving the empty list of followers
        assert ( subscriptionStorePostgres.getFollowers( newUser_1.getID() ).isEmpty() );

        // Test retrieving the list of followers
        List<UserTier> newUser_2_followers = subscriptionStorePostgres.getFollowers( newUser_2.getID() );
        assert ( newUser_2_followers.size() == 1 );

    }

    @Test
    void testGetSubscribers() {

        // Subscribing with positive tier values
        subscriptionStorePostgres.addSubscription( newUser_1.getID(), newUser_2.getID(), 3 );

        // Test retrieving the list of subscribers
        List<UserTier> newUser_1_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_1.getID() );
        assert ( newUser_1_subscriptions.size() == 1 );

        // Test subscribing back to a follower
        subscriptionStorePostgres.addSubscription( newUser_4.getID(), newUser_3.getID() );

        List<UserTier> newUser_4_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_4.getID() );
        assert ( newUser_4_subscriptions.size() == 1 );

    }

    @Test
    void testFreeTierSub() {

        // Test Subscribing with free tier (tier 0)
        subscriptionStorePostgres.addSubscription( newUser_3.getID(), newUser_4.getID() );

        // Test retrieving the list of subscribers
        List<UserTier> newUser_3_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_3.getID() );
        assert ( newUser_3_subscriptions.size() == 1 );

        // Test retrieving the list of followers
        List<UserTier> newUser_4_followers = subscriptionStorePostgres.getFollowers( newUser_4.getID() );
        assert ( newUser_4_followers.size() == 1 );

    }

    @Test
    void testSubBack() {

        // Test Subscribing with free tier (tier 0)
        subscriptionStorePostgres.addSubscription( newUser_3.getID(), newUser_4.getID() );

        // Test subscribing back to a follower
        subscriptionStorePostgres.addSubscription( newUser_4.getID(), newUser_3.getID() );
        List<UserTier> newUser_3_followers = subscriptionStorePostgres.getFollowers( newUser_3.getID() );
        assert ( newUser_3_followers.size() == 1 );

        List<UserTier> newUser_4_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_4.getID() );
        assert ( newUser_4_subscriptions.size() == 1 );

    }

    @Test
    void testMultiple() {

        // Test subscribing to multiple users
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_1.getID(), 3 );
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_3.getID(), 1 );
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_4.getID(), 2 );
        List<UserTier> newUser_2_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_2.getID() );
        assert ( newUser_2_subscriptions.size() == 3 );

    }

    @Test
    void testHasSub() {

        // Subscribe to multiple users
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_1.getID(), 3 );
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_3.getID(), 1 );
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_4.getID(), 2 );
        List<UserTier> newUser_2_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_2.getID() );
        assert ( newUser_2_subscriptions.size() == 3 );

        // Test hasSubscription()
        assertNotNull( subscriptionStorePostgres.hasSubscription( newUser_2.getID(), newUser_1.getID() ) );
        assertNull( subscriptionStorePostgres.hasSubscription( newUser_2.getID(), -1 ) );

    }

    @Test
    void testChangeSub() {

        // Subscribe to multiple users
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_1.getID(), 3 );
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_3.getID(), 1 );
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_4.getID(), 2 );
        List<UserTier> newUser_2_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_2.getID() );
        assert ( newUser_2_subscriptions.size() == 3 );

        // Test changeSubscription()
        subscriptionStorePostgres.changeSubscription( newUser_2.getID(), newUser_1.getID(), 4 );
        assert ( subscriptionStorePostgres.hasSubscription( newUser_2.getID(), newUser_1.getID() ) == 4 );

    }

    @Test
    void testDeleteSub() {

        // Subscribe with free tier (tier 0)
        subscriptionStorePostgres.addSubscription( newUser_3.getID(), newUser_4.getID() );

        // Test deleting the subscription
        //      Should delete cascade the subscribers and followers
        subscriptionStorePostgres.deleteSubscription( newUser_3.getID(), newUser_4.getID() );
        assert ( subscriptionStorePostgres.getSubscriptions( newUser_3.getID() ).isEmpty() );

    }

    @Test
    void testDeleteUser() {

        // Subscribe to multiple users
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_1.getID(), 3 );
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_3.getID(), 1 );
        subscriptionStorePostgres.addSubscription( newUser_2.getID(), newUser_4.getID(), 2 );
        List<UserTier> newUser_2_subscriptions = subscriptionStorePostgres.getSubscriptions( newUser_2.getID() );
        assert ( newUser_2_subscriptions.size() == 3 );

        // Test deleting the user
        userStorePostgres.deleteUser( newUser_1.getID() );
        assert ( subscriptionStorePostgres.getSubscriptions( newUser_2.getID() ).size() == 2 );

    }

}