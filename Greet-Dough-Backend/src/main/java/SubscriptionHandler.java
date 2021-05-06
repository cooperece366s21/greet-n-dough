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

    public SubscriptionHandler( SubscriptionStore subStore, WalletStore walletStore ) {

        this.subStore = subStore;
        this.walletStore = walletStore;

    }

    public int addSubscription( Request req, Response res ) {

        Properties data = gson.fromJson( req.body(), Properties.class );
        int cuid = Integer.parseInt( req.attribute("cuid").toString() );
        int uid = Integer.parseInt( req.params(":uid") );
        int tier = Integer.parseInt( data.getProperty("tier") );

        // Check if user is attempting to subscribe to themself
        if ( cuid == uid ) {

            res.status(404);
            return res.status();

        }

        Integer curTier = subStore.hasSubscription( cuid, uid );
        int tierIncrease;

        // Check how many tiers to upgrade the subscription by
        if ( curTier == null ) {
            tierIncrease = tier;
        } else {
            tierIncrease = tier - curTier;
        }

        // Check if the specified tier is valid
        if ( !Tiers.isValidTier(tierIncrease) || tierIncrease == 0 ) {

            System.err.println("Error: Invalid Tier");
            res.status(404);
            return res.status();

        }

        // Check if the user's balance is high enough to subscribe
        BigDecimal curBal = walletStore.getBalance(cuid);
        if ( curBal.compareTo( Tiers.getCost(tierIncrease) ) == -1 ) {

            System.err.println("Error: User does not have enough money to subscribe.");
            res.status(402);
            return res.status();

        } else {

            // Update the subscription
            if ( curTier == null ) {
                subStore.addSubscription( cuid, uid, tier );
            } else {
                subStore.changeSubscription( cuid, uid, tier );
            }

            walletStore.subtractFromBalance( cuid, Tiers.getCost(tierIncrease) );

        }

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

    // Probably unneeded for now
//    public JSONObject getFollowers( Request req, Response res ) {
//
//        int uid = Integer.parseInt( req.params(":uid") );
//
//        List<UserTier> users = subStore.getFollowers(uid);
//
//        res.status(200);
//        return new JSONObject(users);
//
//    }

}
