package store;

import model.Image;
import utility.Store;

public class ImageStoreImpl extends Store<Image> {

    public ImageStoreImpl() {
        super();
    }

    public ImageStoreImpl( int start ) {
        super(start);
    }

    public Image getImage( int ID ) {
        return super.get(ID);
    }

    public void addImage( Image newImage ) {
        super.add( newImage.getID(), newImage );
    }

    public boolean deleteImage( int ID ) {
        return super.delete(ID);
    }

}
