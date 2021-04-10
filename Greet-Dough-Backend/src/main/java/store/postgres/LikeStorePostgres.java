package store.postgres;

import model.Likes;
import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import store.model.LikeStore;

public class LikeStorePostgres implements LikeStore {

    // test function
    public static void main( String[] args ) {

        Jdbi jdbi = GreetDoughJdbi.create("jdbc:postgresql://localhost:4321/greetdough");
        UserStorePostgres UserStorePostgres = new UserStorePostgres(jdbi);
        PostStorePostgres PostStorePostgres = new PostStorePostgres(jdbi);
        LikeStorePostgres LikeStorePostgres = new LikeStorePostgres(jdbi);

        PostStorePostgres.reset();
        LikeStorePostgres.reset();
        //UserStorePostgres.reset();
        //UserStorePostgres.init();
        LikeStorePostgres.init();
        PostStorePostgres.init();

        User newUser = UserStorePostgres.addUser("Felipe");

        // Make two posts
        Post newPost = PostStorePostgres.addPost( "first!", newUser.getID() );
        Post secondPost = PostStorePostgres.addPost( "haha very cool!", newUser.getID() );

        // Like one post
        int temp = newPost.getID();
        int chata = newUser.getID();
        LikeStorePostgres.insertLikes(temp, chata);

        // Check if the user liked a specific post
        System.out.println(LikeStorePostgres.containsLike(temp, chata));

        // Delete post => delete all likes
        LikeStorePostgres.deleteLikes(temp);
    }

    private final Jdbi jdbi;

    public LikeStorePostgres( final Jdbi jdbi ) {
        this.jdbi = jdbi;
    }

    public void reset() {
        jdbi.useHandle(handle -> handle.attach(LikeDao.class).resetTable());
    }

    public void init() {
        jdbi.useHandle(handle -> handle.attach(LikeDao.class).createTable());
    }

    @Override
    public Likes getID(int ID) {
        return jdbi.withHandle( handle -> handle.attach(LikeDao.class).getID(ID) );
    }

    // From hashtable store
    @Override
    public void addLikes(Likes newLikes) {

    }

    // From hashtable store (can replace with insertLikes)
    @Override
    public Likes addLikes(int postID, int uid) {
        return null;
    }

    @Override
    public void deleteLikes(Integer ID) {
        jdbi.useHandle( handle -> handle.attach(LikeDao.class).deleteLikes(ID));
    }

    @Override
    public void insertLikes(int postID, int uid) {
        jdbi.useHandle( handle -> handle.attach(LikeDao.class).insertLikes(postID, uid) );
    }

    @Override
    public boolean containsLike(int postID, int uid){
        return jdbi.withHandle( handle -> handle.attach(LikeDao.class).containsLike(postID, uid) );
    }

}
