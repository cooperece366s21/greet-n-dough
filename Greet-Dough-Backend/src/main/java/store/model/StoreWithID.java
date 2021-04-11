package store.model;

import store.StorageRetrieval;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class StoreWithID<T> extends StorageRetrieval<T> {

    private AtomicInteger freeIDs;

    protected StoreWithID() {
        this(0);
    }

    protected StoreWithID( int start ) {

        super();
        this.freeIDs = new AtomicInteger(start);

    }

    protected int getFreeID() {
        return this.freeIDs.getAndIncrement();
    }

}
