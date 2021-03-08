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
    public void addImage(Image newImage) {
        super.add( newImage.getImageID(), newImage );
    }

    @Override
    public boolean deleteImage(int ID) {
        return super.delete(ID);
    }

}