package store.model;

import model.Image;

public interface ImageStore {

    Image getImage( int ID );

    boolean hasImage( int ID );

    Image addImage( String path, int uid );

    void deleteImage( int ID );
}
