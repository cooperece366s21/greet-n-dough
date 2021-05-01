package store.postgres;

import model.Image;
import store.model.ImageStore;
import utility.ImageHandler;

import utility.PathDefs;

import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class ImageStorePostgres implements ImageStore {

    private final Jdbi jdbi;
    private final ImageHandler imageHandler;
    private final String imageDir;

    /**
     * Uses {@link PathDefs#IMAGE_DIR} as the default directory.
     */
    public ImageStorePostgres( final Jdbi jdbi ) {
        this( jdbi, PathDefs.IMAGE_DIR );
    }

    /**
     * Used for ProfileStorePostgres.
     *
     * @param customDir specifies the directory to copy images to
     */
    public ImageStorePostgres( final Jdbi jdbi, String customDir ) {

        this.jdbi = jdbi;
        this.imageHandler = new ImageHandler(customDir);
        this.imageDir = customDir;

    }

    public void delete() {
        jdbi.useHandle( handle -> handle.attach(ImageDao.class).deleteTable() );
    }

    public void init() {
        jdbi.useHandle( handle -> handle.attach(ImageDao.class).createTable() );
    }

    @Override
    public String getImageDir() {
        return this.imageDir;
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

    private Image addImage( int uid, String path ) {

        String savedPath = imageHandler.copyImage(path);
        int ID = jdbi.withHandle( handle -> handle.attach(ImageDao.class).addImage( uid, savedPath ) );
        return getImage(ID);

    }

    @Override
    public Image addImage( int uid, String path, boolean deleteOriginalImage ) {

        Image tempImage = addImage( uid, path );

        if ( deleteOriginalImage ) {
            imageHandler.deleteImage(path);
        }

        return tempImage;

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
