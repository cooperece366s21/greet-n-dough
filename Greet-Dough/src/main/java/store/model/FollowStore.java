package store.model;

import java.util.ArrayList;

public interface FollowStore {

    ArrayList<Integer> getFollowers( int ID) ;

    void addFollower( int curUser, int targetUser );

    void removeFollower( int curUser, int targetUser );

    // Deletes user along with all followers
    boolean deleteUser( int ID );

}
