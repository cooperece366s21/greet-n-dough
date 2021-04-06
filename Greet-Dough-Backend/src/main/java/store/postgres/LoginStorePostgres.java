package store.postgres;

import model.User;
import org.jdbi.v3.core.Jdbi;

// INSTEAD OF USING HAS(), USE IF( GET() )?
public class LoginStorePostgres {

    // For testing purposes
    public static void main(String[] args) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        LoginStorePostgres LoginStorePostgres = new LoginStorePostgres(jdbi);

        // Used to DROP and CREATE the posts table
        LoginStorePostgres.reset();
        LoginStorePostgres.init();

        // Create a user and get a token for that user
        User yeet = UserStorePostgres.addUser("yeet");
        String token = LoginStorePostgres.addInstance( yeet.getID() );

        // Get the user ID using the token
        int uid = LoginStorePostgres.getUserID( token );

        System.out.println( LoginStorePostgres.hasInstance(token) );
        System.out.println(uid);
        System.out.println(token);

    }

    private final Jdbi jdbi;

    public LoginStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void reset() {
        jdbi.useHandle(handle -> handle.attach(LoginDao.class).resetTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(LoginDao.class).createTable());
    }

    public String addInstance( int userID ) {
        return jdbi.withHandle( handle -> handle.attach(LoginDao.class).insertInstance(userID) );
    }

    public boolean hasInstance( String token ) {
        return jdbi.withHandle( handle -> handle.attach(LoginDao.class).containsInstance(token) );
    }

    public int getUserID( String token ) {
        return jdbi.withHandle( handle -> handle.attach(LoginDao.class).getUserID(token) );
    }

}