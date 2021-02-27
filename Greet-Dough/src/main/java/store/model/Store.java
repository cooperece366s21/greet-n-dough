package store.model;

import store.StorageRetrieval;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Store<T> extends StorageRetrieval<T> {

    private AtomicInteger freeIDs;

    protected Store() {
        this(0);
    }

    protected Store( int start ) {

        super();
        this.freeIDs = new AtomicInteger(start);

    }

    public int getFreeID() {
        return this.freeIDs.getAndIncrement();
    }

}
