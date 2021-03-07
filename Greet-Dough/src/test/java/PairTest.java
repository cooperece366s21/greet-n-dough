import org.junit.jupiter.api.Test;
import utility.Pair;

class PairTest extends Pair {

    @Test
    void test() {

        int leftVal = 1, rightVal = -1;
        Pair myPair = new Pair( leftVal, rightVal );

        // Check that left and right values are correct
        assert myPair.getLeft() == leftVal;
        assert myPair.getRight() == rightVal;

    }

}