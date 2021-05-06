package utility;

import java.math.BigDecimal;
import java.util.HashMap;

public class Tiers {

    private static final HashMap<Integer,BigDecimal> tiers = new HashMap<>();
    private static final BigDecimal costPerTier = BigDecimal.valueOf(5);
    private static final int maxTier = 5;

    static {

        for ( int i=0; i<=maxTier; i++ ) {
            tiers.put( i, costPerTier.multiply( BigDecimal.valueOf(i) ) );
        }

    }

    public static boolean isValidTier( int tier ) {
        return (0 <= tier) && (tier <= maxTier);
    }

    /**
     * @return  a BigDecimal representing the cost of the specified tier;
     *          null if the tier is greater than the max tier
     */
    public static BigDecimal getCost( int tier ) {

        if ( !isValidTier(tier) ) {
            return null;
        } else {
            return tiers.get(tier);
        }

    }

    /**
     * Can potentially return a negative number if {@code oldTier} > {@code newTier}.
     *
     * @return  a BigDecimal representing the cost of the going from
     *          {@code oldTier} to {@code newTier};
     *          null if either tier is invalid
     */
    public static BigDecimal getCost( int oldTier, int newTier ) {

        if ( !isValidTier(oldTier) || !isValidTier(newTier) ) {
            return null;
        } else {
            return tiers.get(newTier).subtract( tiers.get(oldTier) );
        }

    }

}
