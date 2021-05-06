package store.model;

import model.UserTier;

import java.util.List;

public interface SubscriptionStore {

    /**
     * @return  a list representing the users that {@code uid}
     *          has a subscription to along with the tier of that subscription
     */
    List<UserTier> getSubscriptions( int uid );

    /**
     * @return  a list representing the users that are subscribed
     *          to {@code uid} along with the tier of their subscriptions
     */
    List<UserTier> getFollowers( int uid );

    /**
     * Checks if {@code cuid} is subscribed to {@code tuid}.
     *
     * @return  the subscription tier, or null if the subscription
     *          does not exist
     */
    Integer hasSubscription( int cuid, int tuid );

    /**
     * Adds a tier 0 subscription from {@code cuid} to {@code tuid}.
     */
    void addSubscription( int cuid, int tuid );

    /**
     * Adds a subscription from {@code cuid} to {@code tuid} with
     * the specified tier level.
     */
    void addSubscription( int cuid, int tuid, int tier );

    void deleteSubscription( int cuid, int tuid );

    void changeSubscription( int cuid, int tuid, int newTier );

}
