package store.postgres;

import model.Likes;
import store.model.LikeStore;

import org.jdbi.v3.core.Jdbi;

public class LikeStorePostgres implements LikeStore {

    private final Jdbi jdbi;

    public LikeStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle(handle -> handle.attach(LikeDao.class).deleteTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(LikeDao.class).createTable());
    }

    @Override
    public Likes getLikes( int pid ) {
        return jdbi.withHandle(handle -> handle.attach(LikeDao.class).getUserLikes(pid) ).orElse( new Likes(pid) );
    }

    @Override
    public void addUserLike( int pid, int uid ) {
        jdbi.useHandle( handle -> handle.attach(LikeDao.class).addUserLike(pid, uid) );
    }

    @Override
    public void deleteUserLike( int pid, int uid ) {
        jdbi.useHandle( handle -> handle.attach(LikeDao.class).deleteUserLike(pid, uid) );
    }

    @Override
    public boolean hasUserLike( int pid, int uid ){
        return jdbi.withHandle( handle -> handle.attach(LikeDao.class).hasUserLike(pid, uid) );
    }

}
