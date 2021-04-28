package store.postgres;

import model.Comment;
import store.model.CommentStore;

import org.jdbi.v3.core.Jdbi;

import java.util.LinkedList;

public class CommentStorePostgres implements CommentStore {

    private final Jdbi jdbi;

    public CommentStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle( handle -> handle.attach(CommentDao.class).deleteTable() );
    }

    public void init() {
        jdbi.useHandle( handle -> handle.attach(CommentDao.class).createTable() );
    }

    @Override
    public Comment getComment( int cid ) {
        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).getComment(cid) );
    }

    @Override
    public LinkedList<Comment> getReplies( int parentID ) {
        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).getReplies(parentID) );
    }

    @Override
    public LinkedList<Comment> getParents( int pid ) {
        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).getParents(pid) );
    }

    @Override
    public Comment addComment( String contents, int uid, int pid, Integer parent_id ) {

        int ID = jdbi.withHandle( handle -> handle.attach(CommentDao.class).addComment( contents, uid, pid, parent_id ) );
        return getComment(ID);

    }

    @Override
    public Comment addComment( String contents, int uid, int pid ) {
        return addComment( contents, uid, pid, null );
    }

    @Override
    public boolean hasComment( int cid ) {
        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).hasComment(cid) );
    }

    @Override
    public boolean isParent( int cid ) {
        return jdbi.withHandle( handle -> handle.attach(CommentDao.class).isParent(cid) );
    }

    @Override
    public void deleteComment( int cid ) {
        jdbi.useHandle( handle -> handle.attach(CommentDao.class).deleteComment(cid) );
    }

}


