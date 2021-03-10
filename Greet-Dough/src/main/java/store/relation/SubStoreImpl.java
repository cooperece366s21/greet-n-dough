package store.relation;

import store.model.SubStore;

import java.util.ArrayList;

// create table users
// create table subscriptions ( ... foreign key blah(users) refers ...)
// delete from users where id = 1 --> cascade the delete to anywhere this user was referenced
// "soft delete" -- if deleting a user, set a boolean flag indicating the account is inactive
// count subs --> select * from subs join users where user.active = true

// Store subscriptions as ArrayList or HashMap?
// A single user's subscriptions probably won't be so large that a linear search will take very long
public class SubStoreImpl extends Relation implements SubStore {

    public SubStoreImpl() {
        super();
    }

    @Override
    public ArrayList<Integer> getSubscriptions( int ID ) {
        return super.get(ID);
    }

    @Override
    public void addSubscription( int curUser, int targetUser ) {
        super.add( curUser, targetUser );
    }

    // Removes targetUser from curUser's subscriptions
    @Override
    public void removeSubscription( int curUser, int targetUser ) {
        super.remove( curUser, targetUser );
    }

    // Deletes user along with all subscriptions
    @Override
    public boolean deleteUser( int ID ) {
        return super.delete(ID);
    }

}
