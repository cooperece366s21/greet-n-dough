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

    public static boolean isValidTier( int tier ) {
        return (0 <= tier) && (tier <= maxTier);
    }

    /**
     * @return  an int representing the cost of the specified tier;
     *          null if the tier is greater than the max tier
     */
    public static Integer getCost( int tier ) {

        if ( isValidTier(tier) ) {
            return null;
        } else {
            return tiers.get(tier);
        }

    }

}
