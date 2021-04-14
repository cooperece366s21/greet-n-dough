package utility;

import org.jdbi.v3.core.Jdbi;
import store.postgres.*;

public class ResetDao {

    public static void reset(Jdbi jdbi) {

        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        WalletStorePostgres WalletStorePostgres = new WalletStorePostgres(jdbi);
        ImageStorePostgres ImageStorePostgres = new ImageStorePostgres(jdbi);
        PostStorePostgres PostStorePostgres = new PostStorePostgres(jdbi);
        CommentStorePostgres CommentStorePostgres = new CommentStorePostgres(jdbi);
        LikeStorePostgres LikeStorePostgres = new LikeStorePostgres(jdbi);
        LoginStorePostgres LoginStorePostgres = new LoginStorePostgres(jdbi);
        PasswordStorePostgres PasswordStorePostgres = new PasswordStorePostgres(jdbi);

        // Delete the databases
        PasswordStorePostgres.delete();
        LoginStorePostgres.delete();
        LikeStorePostgres.delete();
        CommentStorePostgres.delete();
        PostStorePostgres.delete();
        ImageStorePostgres.delete();
        WalletStorePostgres.delete();
        UserStorePostgres.delete();

        // Initialize the databases
        UserStorePostgres.init();
        WalletStorePostgres.init();
        ImageStorePostgres.init();
        PostStorePostgres.init();
        CommentStorePostgres.init();
        LikeStorePostgres.init();
        LoginStorePostgres.init();
        PasswordStorePostgres.init();

    }

}
