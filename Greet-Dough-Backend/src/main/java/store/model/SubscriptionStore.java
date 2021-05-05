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

    void addSubscription( int uidCurrent, int uidTarget );

    void deleteSubscription( int uidCurrent, int uidTarget );

}
