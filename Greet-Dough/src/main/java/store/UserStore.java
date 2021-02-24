package store;

import model.User;
import utility.TrackerID;

public interface UserStore extends TrackerID {

    User getUser( int userID );

    void addUser( User newPost );

    boolean deleteUser( int userID );

}
