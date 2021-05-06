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
    public Integer hasSubscription( int cuid, int tuid ) {
        return jdbi.withHandle( handle -> handle.attach(SubscriptionDao.class).hasSubscription( cuid, tuid ) ).orElse(null);
    }

    @Override
    public void addSubscription( int cuid, int tuid ) {
        addSubscription( cuid, tuid, 0 );
    }

    @Override
    public void addSubscription( int cuid, int tuid, int tier ) {
        jdbi.useHandle( handle -> handle.attach(SubscriptionDao.class).addSubscription( cuid, tuid, tier ) );
    }

    @Override
    public void deleteSubscription( int cuid, int tuid ) {
        jdbi.useHandle( handle -> handle.attach(SubscriptionDao.class).deleteSubscription( cuid, tuid ) );
    }

    @Override
    public void changeSubscription( int cuid, int tuid, int newTier ) {
        jdbi.useHandle( handle -> handle.attach(SubscriptionDao.class).changeSubscription( cuid, tuid, newTier ) );
    }

}
