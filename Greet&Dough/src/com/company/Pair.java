package com.company;

// Custom Pair class b/c apparently Java doesn't natively support Tuples
public class Pair {

    private int left, right;

    Pair( int left, int right ) {

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
