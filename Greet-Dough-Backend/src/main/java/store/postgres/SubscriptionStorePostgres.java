package store.postgres;

import model.UserTier;
import store.model.SubscriptionStore;

import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class SubscriptionStorePostgres implements SubscriptionStore {

    private final Jdbi jdbi;

    public SubscriptionStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle( handle -> handle.attach(SubscriptionDao.class).deleteTable() );
    }

    public void init() {
        jdbi.useHandle( handle -> handle.attach(SubscriptionDao.class).createTable() );
    }

    @Override
    public List<UserTier> getSubscriptions( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(SubscriptionDao.class).getSubscriptions(uid) );
    }

    @Override
    public List<UserTier> getFollowers( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(SubscriptionDao.class).getFollowers(uid) );
    }

    @Override
    public void addSubscription( int uidCurrent, int uidTarget ) {
        jdbi.useHandle( handle -> handle.attach(SubscriptionDao.class).addSubscription( uidCurrent, uidTarget ) );
    }

    @Override
    public void deleteSubscription( int uidCurrent, int uidTarget ) {
        jdbi.useHandle( handle -> handle.attach(SubscriptionDao.class).deleteSubscription( uidCurrent, uidTarget ) );
    }

}
