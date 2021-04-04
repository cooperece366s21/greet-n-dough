package store.impl;

import model.Image;
import store.model.StoreWithID;
import store.model.ImageStore;

public class ImageStoreImpl extends StoreWithID<Image> implements ImageStore {

    public ImageStoreImpl() {
        super();
    }

    public ImageStoreImpl( int start ) {
        super(start);
    }

    @Override
    public Image getImage(int ID) {
        return super.get(ID);
    }

    @Override
    public boolean deleteImage(int ID) {
        return super.delete(ID);
    }

    @Override
    public Image addImage( Image path, int postID, int uid ) {

        // Create image
        int ID = super.getFreeID();
        Image tempImage = new Image(ID);

        // Add image
        this.add( tempImage.getID(), tempImage );
        return tempImage;

    }

}
