package utility;

import org.jdbi.v3.core.Jdbi;
import store.postgres.*;

public class ResetDao {

    /**
     * Resets the databases associated with this project.
     * DROP's and CREATE's all the tables.
     */
    public static void reset( Jdbi jdbi ) {

        UserStorePostgres userStorePostgres = new UserStorePostgres(jdbi);
        WalletStorePostgres walletStorePostgres = new WalletStorePostgres(jdbi);
        ProfileStorePostgres profileStorePostgres = new ProfileStorePostgres(jdbi);
        ImageStorePostgres imageStorePostgres = new ImageStorePostgres(jdbi);
        PostStorePostgres postStorePostgres = new PostStorePostgres(jdbi);
        CommentStorePostgres commentStorePostgres = new CommentStorePostgres(jdbi);
        LikeStorePostgres likeStorePostgres = new LikeStorePostgres(jdbi);
        LoginStorePostgres loginStorePostgres = new LoginStorePostgres(jdbi);
        PasswordStorePostgres passwordStorePostgres = new PasswordStorePostgres(jdbi);

        // Delete the databases
        passwordStorePostgres.delete();
        loginStorePostgres.delete();
        likeStorePostgres.delete();
        commentStorePostgres.delete();
        postStorePostgres.delete();
        imageStorePostgres.delete();
        profileStorePostgres.delete();
        walletStorePostgres.delete();
        userStorePostgres.delete();

        // Initialize the databases
        userStorePostgres.init();
        walletStorePostgres.init();
        profileStorePostgres.init();
        imageStorePostgres.init();
        postStorePostgres.init();
        commentStorePostgres.init();
        likeStorePostgres.init();
        loginStorePostgres.init();
        passwordStorePostgres.init();

    }

}
