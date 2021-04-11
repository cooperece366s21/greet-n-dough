package store.model;

import model.Image;

public interface ImageStore {

    Image getImage( int iid );

    boolean hasImage( int iid );

    Image addImage( String path, int uid );

    void deleteImage( int iid );
}
