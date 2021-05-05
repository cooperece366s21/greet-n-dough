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
     * Adds a tier 0 subscription from {@code uidCurrent} to {@code uidTarget}.
     */
    void addSubscription( int uidCurrent, int uidTarget );

    /**
     * Adds a subscription from {@code uidCurrent} to {@code uidTarget} with
     * the specified tier level.
     */
    void addSubscription( int uidCurrent, int uidTarget, int tier );

    void deleteSubscription( int uidCurrent, int uidTarget );

}
