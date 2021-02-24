import model.Post;
import model.User;

public class Main {

    public static void main(String[] args) {

        User me = new User("Tony");
        System.out.println( "My name is " + me.getName() );

        me.makePost( "Hi guys, make sure to Belladonate to my channel" );
        me.makePost( "Hi guys, now wouldn't be a bad time to Belladonate to my channel" );
        me.checkFeed();

        Post myPost = new Post( "Hi", 0 );
        System.out.println( myPost.getImageID() );

//        model.User him = new model.User( "Josh" );
//        System.out.println( me.getFollowers().size() );
//        me.subscribe( him.getID() );
//        System.out.println( me.getFollowers().size() );

    }

}
