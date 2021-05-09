package store.postgres;

import model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserStorePostgresTest extends UserStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    private static UserStorePostgres userStorePostgres;
    private static ImageStorePostgres imageStorePostgres;

    public UserStorePostgresTest() {
        super(jdbi);
    }

    @BeforeAll
    static void setUpAll() {

        // Delete all the databases (only use the relevant ones)
        ResetDao.deleteAll(jdbi);

        userStorePostgres = new UserStorePostgres(jdbi);
        imageStorePostgres = new ImageStorePostgres(jdbi);

    }

    @AfterAll
    static void tearDownAll() {
        ResetDao.reset(jdbi);
    }

    @BeforeEach
    void setUpEach() {

        // Delete the databases
        imageStorePostgres.delete();
        userStorePostgres.delete();


        // Initialize the databases
        userStorePostgres.init();
        imageStorePostgres.init();

    }

    @Test
    void testAddUser() {

        // Test adding and retrieving a user
        User josh = userStorePostgres.addUser("Josh");
        User userAfterWrite = userStorePostgres.getUser( josh.getID() );
        assert ( userAfterWrite.getID() == josh.getID() );
        assert ( userAfterWrite.getName().equals("Josh") );
        assert ( userStorePostgres.hasUser( josh.getID() ) );

    }

    @Test
    void testSearchUser() {

        // Add users
        userStorePostgres.addUser("Josh");
        userStorePostgres.addUser("Jon");

        // Prints the names of the users given a list of users
        //      Playing around with mapping a list
        LinkedList<String> correctList = new LinkedList<>();
        correctList.add("Josh"); correctList.add("Jon");
        assert ( userStorePostgres.searchUsers("Jo").stream().map(User::getName).collect(Collectors.toList())
                .equals(correctList) );
        assert ( userStorePostgres.searchUsers("jo").stream().map(User::getName).collect(Collectors.toList())
                .equals(correctList) );

    }

    @Test
    void testChangeUser() {

        // Add a user
        User newUser = userStorePostgres.addUser("Jon");

        // Test changing the user's name
        userStorePostgres.changeName( newUser.getID(), "John" );
        assert ( userStorePostgres.getUser(newUser.getID()).getName().equals("John") );

    }

    @Test
    void testDeleteUser() {

        // Add a user
        User josh = userStorePostgres.addUser("Josh");

        // Test deleting the user
        userStorePostgres.deleteUser( josh.getID() );
        assertNull( userStorePostgres.getUser( josh.getID() ) );

    }

}