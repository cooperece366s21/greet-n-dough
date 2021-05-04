package store.postgres;

import model.User;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserStorePostgresTest extends UserStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    public UserStorePostgresTest() {
        super(jdbi);
    }

    @Test
    void test() {

        UserStorePostgres userStorePostgres = new UserStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        // Test adding and retrieving a user
        User josh = userStorePostgres.addUser("Josh");
        User userAfterWrite = userStorePostgres.getUser( josh.getID() );
        assert ( userAfterWrite.getID() == josh.getID() );
        assert ( userAfterWrite.getName().equals("Josh") );
        assert ( userStorePostgres.hasUser( josh.getID() ) );

        // Test searching for a user given the first portion of their name
        User newUser = userStorePostgres.addUser("Jon");

        // Prints the names of the users given a list of users
        //      Playing around with mapping a list
        LinkedList<String> correctList = new LinkedList<>();
        correctList.add("Josh"); correctList.add("Jon");
        assert ( userStorePostgres.searchUsers("Jo").stream().map(User::getName).collect(Collectors.toList())
                    .equals(correctList) );
        assert ( userStorePostgres.searchUsers("jo").stream().map(User::getName).collect(Collectors.toList())
                    .equals(correctList) );

        // Test changing the user's name
        userStorePostgres.changeName( newUser.getID(), "John" );
        assert ( userStorePostgres.getUser(newUser.getID()).getName().equals("John") );

        // Test deleting the user
        userStorePostgres.deleteUser( josh.getID() );
        assertNull( userStorePostgres.getUser( josh.getID() ) );

    }

}