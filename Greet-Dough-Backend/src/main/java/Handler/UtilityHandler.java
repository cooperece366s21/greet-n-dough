package Handler;

import store.model.*;

public class UtilityHandler {

    private final ProfileStore profileStore;
    private final ImageStore imageStore;

    public UtilityHandler( ProfileStore profileStore, ImageStore imageStore ) {

        this.profileStore = profileStore;
        this.imageStore = imageStore;

    }

    /**
     * @return  a string representing a url to the profile picture if exists;
     *          an empty string ("") otherwise
     */
    String getUrlToPFP( int uid ) {

        Integer iid = profileStore.getProfile(uid).getImageID();
        return (iid != null) ? imageStore.getImage(iid).getPath() : "";

    }

}
