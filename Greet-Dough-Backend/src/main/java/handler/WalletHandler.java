package Handler;

import store.model.WalletStore;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Properties;

public class WalletHandler {

    private final WalletStore walletStore;
    private final Gson gson = new Gson();

    public WalletHandler( WalletStore walletStore ) {
        this.walletStore = walletStore;
    }

    /**
     * The method returns the user's balance if the user is in the database.
     * The method returns an empty string otherwise.
     * The string returned has 2 digits after the decimal.
     *
     *
     * @throws ArithmeticException  if the user's balance has more than 2 digits
     *                              after the decimal (excluding trailing zeros).
     *                              Should never happen.
     * @return                      the user's balance
     */
    public String getBalance( Request req, Response res ) throws ArithmeticException {

        int uid = Integer.parseInt( req.attribute("cuid") );

        BigDecimal bal = walletStore.getBalance(uid);

        if ( bal == null ) {

            res.status(404);
            return "";

        } else {

            // Set bal to 2 decimal places
            bal = bal.setScale(2, RoundingMode.UNNECESSARY);

            res.status(200);
            return bal.toString();

        }

    }

    private int modifyBalance( Request req, Response res, boolean isAdd ) {

        Properties data = gson.fromJson(req.body(), Properties.class);
        int uid = Integer.parseInt( req.attribute("cuid").toString() );

        // Parse the request
        String amountQuery = data.getProperty("amount");
        BigDecimal amount = new BigDecimal(amountQuery).stripTrailingZeros();

        // Check if the amount is not positive or the number of digits after the decimal is greater than 2
        //      E.g. 0 or -1 or 1.005
        if ( amount.compareTo(BigDecimal.ZERO) != 1 || amount.scale() > 2 ) {

            res.status(401);
            return res.status();

        }

        if ( WalletStore.verifyPurchase() ) {

            if ( isAdd ) {
                walletStore.addToBalance( uid, amount );
            } else {
                walletStore.subtractFromBalance( uid, amount );
            }

            res.status(200);

        } else {
            res.status(401);
        }

        return res.status();

    }

    /**
     * Adds the amount specified to the user's balance.
     * Operation can fail if user doesn't have a balance or if verifyPurchase() failed.
     *
     * @param req   contains the amount to be added;
     *              amount must be positive (greater than 0) and have at most 2 digits after the decimal
     * @return      the HTTP status code
     * @see         WalletStore#verifyPurchase()
     */
    public int addToBalance( Request req, Response res ) {
        return modifyBalance( req, res, true );
    }

    /**
     * Subtracts the amount specified from the user's balance.
     * Operation can fail if user doesn't have a balance or if verifyPurchase() failed.
     *
     * @param req   contains the amount to be subtracted;
     *              amount must be positive (greater than 0) and have at most 2 decimal places
     * @return      the HTTP status code
     * @see         WalletStore#verifyPurchase()
     */
    public int subtractFromBalance( Request req, Response res ) {
        return modifyBalance( req, res, false );
    }

}
