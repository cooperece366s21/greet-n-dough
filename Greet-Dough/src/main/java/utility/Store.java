package utility;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Store<T> implements Serializable {

    private AtomicInteger freeIDs;
    private HashMap<Integer, T> items;

    protected Store() {
        this(0);
    }

    protected Store( int start ) {

        this.freeIDs = new AtomicInteger(start);
        this.items = new HashMap<>();

    }

    public int getFreeID() {
        return this.freeIDs.getAndIncrement();
    }

    protected T get( int ID ) {
        return this.items.get(ID);
    }

    protected void add( int key, T newItem ) {
        this.items.put( key, newItem );
    }

    protected boolean delete( int ID ) {
        return ( this.items.remove( ID ) != null );
    }

}
