package store.postgres;

import model.User;
import org.jdbi.v3.core.Jdbi;

import java.util.Optional;

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
        User newUser = UserStorePostgres.addUser("Dan");
        String token = LoginStorePostgres.addSession( newUser.getID() );

        // Get the user ID using the token
        Integer uid = LoginStorePostgres.getUserID( token );

        System.out.println( LoginStorePostgres.hasSession(token) );
        System.out.println( LoginStorePostgres.getUserID("abc") );
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

    public String addSession( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(LoginDao.class).insertInstance(uid) );
    }

    public boolean hasSession( String token ) {
        return getUserID(token) != null;
    }

    public Integer getUserID( String token ) {
        return jdbi.withHandle( handle -> handle.attach(LoginDao.class).getUserID(token) ).orElse(null);
    }

}