package utility;

import org.jdbi.v3.core.Jdbi;
import store.postgres.GreetDoughJdbi;
import store.postgres.ImageStorePostgres;
import store.postgres.ProfileStorePostgres;

/**
 * The class is used to periodically clean up the database.
 */
public class Cleaner implements Runnable {

    private final ImageStorePostgres imageStorePostgres;
    private final ProfileStorePostgres profileStorePostgres;

    public Cleaner() {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        imageStorePostgres = new ImageStorePostgres(jdbi);
        profileStorePostgres = new ProfileStorePostgres(jdbi);

    }

    @Override
    public void run() {

        imageStorePostgres.clearDeleted();
        profileStorePostgres.clearDeleted();

    }

}
