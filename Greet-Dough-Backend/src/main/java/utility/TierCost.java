package utility;

import java.util.HashMap;

public class TierCost {

    private static final HashMap<Integer,Integer> tiers = new HashMap<>();
    private static final int maxTier = 5;

    static {

        for ( int i=0; i<=maxTier; i++ ) {
            tiers.put(i,5*i);
        }

    }

    /**
     * @return  an int representing the cost of the specified tier;
     *          null if the tier is greater than the max tier
     */
    public static Integer getCost( int tier ) {

        if ( tier > maxTier ) {
            return null;
        } else {
            return tiers.get(tier);
        }

    }

}
