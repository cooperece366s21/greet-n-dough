package store.relation;

import store.model.FollowStore;

import java.util.ArrayList;

public class FollowStoreImpl extends Relation implements FollowStore {

    public FollowStoreImpl() {
        super();
    }

    @Override
    public ArrayList<Integer> getFollowers( int ID ) {
        return super.get(ID);
    }

    @Override
    public void addFollower( int curUser, int targetUser ) {
        super.add( curUser, targetUser );
    }

    @Override
    public void removeFollower( int curUser, int targetUser ) {
        super.remove( curUser, targetUser );
    }

    // Deletes user along with all followers
    @Override
    public boolean deleteUser( int ID ) {
        return super.delete(ID);
    }

}
