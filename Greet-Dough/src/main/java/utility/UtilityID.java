package utility;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class UtilityID implements Serializable {

    ////////////////// Members //////////////////
    private static AtomicInteger freeUserIDs;
    private static AtomicInteger freePostIDs;
    private static AtomicInteger freeImageIDs;

    ////////////////// Functions //////////////////
    public UtilityID() {
        this(0,0,0);
    }

    public UtilityID( int userStart, int postStart, int imageStart ) {

        this.freeUserIDs = new AtomicInteger( userStart );
        this.freePostIDs = new AtomicInteger( postStart );
        this.freeImageIDs = new AtomicInteger( imageStart );

    }

    public int getUnusedUserID() {
        return UtilityID.freeUserIDs.getAndIncrement();
    }

    public int getUnusedPostID() {
        return UtilityID.freePostIDs.getAndIncrement();
    }

    public int getUnusedImageID() {
        return UtilityID.freeImageIDs.getAndIncrement();
    }

}
