package utility;

import org.jdbi.v3.core.Jdbi;
import store.postgres.GreetDoughJdbi;
import store.postgres.ImageStorePostgres;

/**
 * The class is used to periodically clean up the database.
 */
public class Cleaner implements Runnable {

    private final ImageStorePostgres imageStorePostgres;

    public Cleaner() {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        imageStorePostgres = new ImageStorePostgres(jdbi);

    }

    @Override
    public void run() {
        imageStorePostgres.clearDeleted();
    }

}
