package utility;

import java.io.Serializable;

// Custom Pair class b/c apparently Java doesn't natively support Tuples
public class Pair implements Serializable {

    private int left, right;

    public Pair() {}

    public Pair( int left, int right ) {

        this.left = left;
        this.right = right;

    }

    public int getLeft() {
        return this.left;
    }

    public int getRight() {
        return this.right;
    }

}
