package handler;

import store.model.*;

import spark.Response;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UtilityHandler {

    private final ProfileStore profileStore;
    private final ImageStore imageStore;

    public UtilityHandler( ProfileStore profileStore, ImageStore imageStore ) {

        this.profileStore = profileStore;
        this.imageStore = imageStore;

    }

    // Defines the accepted file extensions for images
    private static final HashSet<String> validImageFileExtensions = Stream
            .of(".png",".jpg",".gif")
            .collect( Collectors.toCollection(HashSet::new) );

    /**
     * The method checks if the given file extension is valid.
     *
     * @return  true if the file extension is valid; false otherwise
     */
    boolean isValidImageFile( String fileExtension, Response res ) {

        // Check if the extension is valid
        if ( validImageFileExtensions.contains(fileExtension) ) {

            res.status(200);
            return true;

        } else {

            res.status(403);
            return false;

        }

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
