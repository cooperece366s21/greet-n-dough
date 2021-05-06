import model.UserTier;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import store.model.SubscriptionStore;
import com.google.gson.Gson;

import java.util.List;
import java.util.Properties;

public class SubscriptionHandler {
    private final SubscriptionStore subStore;
    private final Gson gson = new Gson();

    public SubscriptionHandler(SubscriptionStore subStore) {
        this.subStore = subStore;
    }

    public int AddSubscription( Request req, Response res ) {

        Properties data = gson.fromJson( req.body(), Properties.class );
        int cuid = Integer.parseInt( req.attribute("uid").toString() );
        int uid = Integer.parseInt( data.getProperty("uid") );

        // ENUM this string to a tier number
        String tier = data.getProperty("tier");

//        Do the subscription SHITE here
//        subStore.addSubscription(cuid, uid, tier);

        res.status(200);
        return res.status();
    }

    public int DeleteSubscription( Request req, Response res ) {

        Properties data = gson.fromJson( req.body(), Properties.class );
        int cuid = Integer.parseInt( req.attribute("uid").toString() );
        int uid = Integer.parseInt( data.getProperty("uid") );

        subStore.deleteSubscription( cuid, uid );

        res.status(200);
        return res.status();
    }

    public JSONObject GetSubscriptions( Request req, Response res ) {

        int uid = Integer.parseInt( req.params(":uid") );

        List<UserTier> users = subStore.getFollowers(uid);

        res.status(200);
        return new JSONObject( users );

    }

}
