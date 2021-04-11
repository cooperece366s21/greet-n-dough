package store.postgres;

import store.model.LoginStore;
import model.User;
import org.jdbi.v3.core.Jdbi;
import utility.ResetDao;

public class LoginStorePostgres implements LoginStore {

    // For testing purposes
    public static void main(String[] args) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        LoginStorePostgres LoginStorePostgres = new LoginStorePostgres(jdbi);

        // Used to DROP and CREATE all tables
        ResetDao.reset(jdbi);

        // Create a user and get a token for that user
        User newUser = UserStorePostgres.addUser("Dan");
        String token = LoginStorePostgres.addSession( newUser.getID() );

        // Get the user ID using the token
        Integer uid = LoginStorePostgres.getUserID(token);

        System.out.println( LoginStorePostgres.hasSession(token) );
        System.out.println( LoginStorePostgres.getUserID("abc") );
        System.out.println(uid);
        System.out.println(token);

        // Test deleting a session
        LoginStorePostgres.deleteSession(token);
        System.out.println( LoginStorePostgres.hasSession(token) );

    }

    private final Jdbi jdbi;

    public LoginStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void delete() {
        jdbi.useHandle(handle -> handle.attach(LoginDao.class).deleteTable());
    }

    public void init() {

        jdbi.useHandle(handle -> handle.attach(LoginDao.class).createTable());
        jdbi.useHandle(handle -> handle.attach(LoginDao.class).createTrigger());
        jdbi.useHandle(handle -> handle.attach(LoginDao.class).setTrigger());

    }

    @Override
    public String addSession( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(LoginDao.class).insertSession(uid) );
    }

    @Override
    public boolean hasSession( String token ) {
        return getUserID(token) != null;
    }

    @Override
    public void deleteSession( String token ) {
        jdbi.useHandle(handle -> handle.attach(LoginDao.class).deleteSession(token));
    }

    @Override
    public Integer getUserID( String token ) {
        return jdbi.withHandle( handle -> handle.attach(LoginDao.class).getUserID(token) ).orElse(null);
    }

}