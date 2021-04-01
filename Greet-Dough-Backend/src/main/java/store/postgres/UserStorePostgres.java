package store.postgres;

import model.User;
import store.model.UserStore;
import org.jdbi.v3.core.Jdbi;

public class UserStorePostgres implements UserStore {

    // for testing purposes
    public static void main( String[] args ) {

        UserStorePostgres UserStorePostgres =
                new UserStorePostgres(
                        GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough"));

        //    String userId = UUID.randomUUID().toString();
        //    User user = userStoreMysql.get(userId);
        //    System.out.println(user);

        UserStorePostgres.reset();
        UserStorePostgres.init();
        User yeet = UserStorePostgres.addUser("yeet");

        User userAfterWrite = UserStorePostgres.getUser( yeet.getID() );
        System.out.println( userAfterWrite.getID() + " " + userAfterWrite.getName() );

    }

    private final Jdbi jdbi;

    public UserStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void reset() {
        jdbi.useHandle(handle -> handle.attach(UserDao.class).resetTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(UserDao.class).createTable());
    }

    @Override
    public User getUser(int ID) {
        return jdbi.withHandle( handle -> handle.attach(UserDao.class).getUser(ID) );
    }

    @Override
    public boolean hasUser(int ID) {
        return false;
    }

    @Override
    public void addUser(User newUser) {

    }

    @Override
    public User addUser(String name) {

        int ID = jdbi.withHandle(
            handle -> handle.attach(UserDao.class).insertUser(name)
        );
        System.out.println(ID);
//        jdbi.useHandle(
//                handle -> {
//                    handle.attach(UserDao.class).insertUser(ID, name);
//                });
        return getUser(ID);

    }

    @Override
    public boolean deleteUser(int ID) {
        return false;
    }

}
