package store.postgres;

import model.User;
import model.UserTier;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import utility.GreetDoughJdbi;
import utility.ResetDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionStorePostgresTest extends SubscriptionStorePostgres {

    private static final Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");

    public SubscriptionStorePostgresTest() {
        super(jdbi);
    }

    @Test
    void test() {

        UserStorePostgres userStorePostgres = new UserStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        // Create Users
        User newUser_1 = userStorePostgres.addUser("Ban Dim");
        User newUser_2 = userStorePostgres.addUser("Bill Bluemint");
        User newUser_3 = userStorePostgres.addUser("Vim Kim");



    }

}