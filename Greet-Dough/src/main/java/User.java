import java.util.ArrayList;
import java.util.HashSet;
import java.io.Serializable;

public class User implements Serializable{

    private static final long serialVersionUID = 1L;
    ////////////////// Members //////////////////
    private String name;
    protected int id;                   // Stores unique id for a given user
    private Feed userFeed;

    // Stores a list of a user's subscriptions
    // < Content_Creator_ID, Subscription_Tier >
    protected ArrayList<Pair> subscribedTo;

    // Stores a list of a user's followers
    // Used to send notifications, emails, etc.
    protected HashSet<Integer> subscribers;

    ////////////////// Constructor //////////////////
    User( String name ) {

        this.name = name;
        this.id = User.hashName(name);
        this.userFeed = new Feed();
        this.subscribedTo = new ArrayList<>();
        this.subscribers = new HashSet<>();

    }

    ////////////////// Functions //////////////////
    // NEED TO WRITE THIS
    private static int hashName( String name ) {
        return 0;
    }

    public String getName() {
        return this.name;
    }

    public void makePost( String contents ) {
        this.userFeed.addPost( contents );
    }

    public void checkFeed() {
        this.userFeed.display();
    }
}
