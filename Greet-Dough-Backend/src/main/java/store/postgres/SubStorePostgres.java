package store.postgres;

import store.model.SubStore;

import org.jdbi.v3.core.Jdbi;

import java.util.LinkedList;
import java.util.List;

public class SubStorePostgres implements SubStore{
    private final Jdbi jdbi;

    public SubStorePostgres(final Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle( handle -> handle.attach(SubscribeDao.class).deleteTable() );
    }

    public void init() {
        jdbi.useHandle( handle -> handle.attach(SubscribeDao.class).createTable() );
    }

    @Override
    public ArrayList<Integer> getSubscriptions (int uid) {
        return jdbi.withHandle(handle -> handle.attach(SubscribeDao.class).getSubscriber(uid));
    }

    @Override
    public void addSubscription( int curUser, int targetUser ){
        jdbi.useHandle( handle -> handle.attach(SubscribeDao.class).addSubscription(targetUser, curUser) );
    }

    @Override
    public void removeSubscription( int curUser, int targetUser ){
        jdbi.useHandle( handle -> handle.attach(SubscribeDao.class).removeSubscription(targetUser, curUser) );
    }

    @Override
    public boolean deleteUser(int uid) {
        return false;
    }

}
