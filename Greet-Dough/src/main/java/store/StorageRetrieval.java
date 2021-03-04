package store;

import java.io.Serializable;
import java.util.HashMap;

public abstract class StorageRetrieval<T> implements Serializable {
    // changed the line below so implementations can access items
    private HashMap<Integer, T> items;

    protected StorageRetrieval() {
        this.items = new HashMap<>();
    }

    protected T get( int ID ) {
        return this.items.get(ID);
    }

    protected HashMap<Integer, T> getItems() { return this.items; }

    protected void add( int key, T newItem ) {
        this.items.put( key, newItem );
    }

    // Returns:
    //      1   if successful
    //      0   if unsuccessful
    protected boolean delete( int ID ) {
        return ( this.items.remove(ID) != null );
    }

}
