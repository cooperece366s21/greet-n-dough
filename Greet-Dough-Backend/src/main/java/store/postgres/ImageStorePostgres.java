package store.postgres;

import model.Image;
import store.model.ImageStore;
import utility.ImageHandler;

import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class ImageStorePostgres implements ImageStore {

    private final Jdbi jdbi;
    private final ImageHandler imageHandler;
    private static final String imageDir = "images";

    public ImageStorePostgres( final Jdbi jdbi ) {

        this.jdbi = jdbi;
        this.imageHandler = new ImageHandler(imageDir);

    }

    public void delete() {
        jdbi.useHandle( handle -> handle.attach(ImageDao.class).deleteTable() );
    }

    public void init() {
        jdbi.useHandle( handle -> handle.attach(ImageDao.class).createTable() );
    }

    @Override
    public String getImageDir() {
        return ImageStorePostgres.imageDir;
    }

    @Override
    public Image getImage( int iid ) {
        return jdbi.withHandle( handle -> handle.attach(ImageDao.class).getImage(iid) ).orElse(null);
    }

    /**
     * Currently only used for testing.
     *
     * @return all images in the database
     */
    protected List<Image> getAllImages() {
        return jdbi.withHandle( handle -> handle.attach(ImageDao.class).getAllImages() );
    }

    @Override
    public List<Image> makeGallery( int uid ) {
        return jdbi.withHandle( handle -> handle.attach(ImageDao.class).getGallery(uid) );
    }

    @Override
    public boolean hasImage( int iid ) {
        return getImage(iid) != null;
    }

    @Override
    public Image addImage( int uid, String path ) {

        String savedPath = imageHandler.copyImage(path);
        int ID = jdbi.withHandle( handle -> handle.attach(ImageDao.class).addImage( uid, savedPath ) );
        return getImage(ID);

    }

    @Override
    public void deleteImage( int iid ) {
        jdbi.useHandle( handle -> handle.attach(ImageDao.class).deleteImage(iid) );
    }

    @Override
    public void clearDeleted() {

        List<Image> deletedImages = jdbi.withHandle( handle -> handle.attach(ImageDao.class).clearDeleted() );
        for ( Image img : deletedImages ) {
            imageHandler.deleteImage( img.getPath() );
        }

    }

}
