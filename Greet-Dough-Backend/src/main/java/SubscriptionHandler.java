import model.UserTier;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import store.model.SubscriptionStore;
import com.google.gson.Gson;
import store.model.WalletStore;
import utility.Tiers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

public class SubscriptionHandler {

    private final SubscriptionStore subStore;
    private final WalletStore walletStore;
    private final Gson gson = new Gson();

    public SubscriptionHandler(SubscriptionStore subStore, WalletStore walletStore) {
        this.subStore = subStore;
        this.walletStore = walletStore;
    }

    public int addSubscription( Request req, Response res ) {

        Properties data = gson.fromJson( req.body(), Properties.class );
        int cuid = Integer.parseInt( req.attribute("cuid").toString() );
        int uid = Integer.parseInt( req.params(":uid") );
        int tier = Integer.parseInt( data.getProperty("tier") );

        if ( cuid == uid ) {
            res.status(404);
            return res.status();
        }
        if ( !Tiers.isValidTier(tier) ) {
            res.status(404);
            return res.status();
        }

        List<UserTier> currentSubscriptions = subStore.getSubscriptions(cuid);

        for( UserTier sub : currentSubscriptions ) {
            if ( sub.getUserID() == uid ) {

                // User is already subscribed but is a higher tier (Dont spend money)
                if ( sub.getTier()>tier ) {
                    subStore.deleteSubscription(cuid, uid);
                    subStore.addSubscription(cuid, uid, tier);
                    // money stuff
                    res.status(200);
                }

                // User is subscribed but is a lower tier (Spend the remainder)
                if ( sub.getTier()<tier ) {
                    subStore.deleteSubscription(cuid, uid);
                    subStore.addSubscription(cuid, uid, tier);
                    // money stuff
                    res.status(200);
                }

                // User is subscribed but sent a request for the same subscription level?
                if ( sub.getTier()==tier ) {
                    res.status(200);
                }
                return res.status();
            }
        }

        subStore.addSubscription(cuid, uid, tier);
        // money stuff

        res.status(200);
        return res.status();
    }

    public int deleteSubscription( Request req, Response res ) {

        Properties data = gson.fromJson( req.body(), Properties.class );
        int cuid = Integer.parseInt( req.attribute("cuid").toString() );
        int uid = Integer.parseInt( data.getProperty("uid") );

        subStore.deleteSubscription( cuid, uid );

        res.status(200);
        return res.status();
    }

    public JSONObject getSubscriptions( Request req, Response res ) {

        int uid = Integer.parseInt( req.params(":uid") );

        List<UserTier> users = subStore.getSubscriptions(uid);

        res.status(200);
        return new JSONObject(users);

    }

    // Add a route to this
    public JSONObject getFollowers( Request req, Response res ) {

        int uid = Integer.parseInt( req.params(":uid") );

        List<UserTier> users = subStore.getFollowers(uid);

        res.status(200);
        return new JSONObject(users);

    }

}
