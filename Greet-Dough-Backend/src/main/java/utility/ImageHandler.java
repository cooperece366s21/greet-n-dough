package utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Random;

public class ImageHandler {

    private final Path imageDir;
    private final Random filenameGen;
    private static final int MAX_FILENAME_SIZE = 15;
    private static final FileSystem fileSys = FileSystems.getDefault();

    /**
     * @param   folderName  the desired name of the folder to save images to
     */
    public ImageHandler( String folderName ) {

        this.imageDir = setImageDir(folderName);
        this.filenameGen = new Random();

    }

    /**
     * @param   folderName  the desired name of the folder to save images to
     * @return              a path to /Greet-Dough-Backend/data/{folderName}
      */
    private Path setImageDir( String folderName ) {

        // Should be /Greet-Dough-Backend/
        Path tempPath = fileSys.getPath( System.getProperty("user.dir") );

        // Should be /Greet-Dough-Backend/data/{folderName}
        Path dirPath = fileSys.getPath( tempPath.toString() + File.separator + "data" + File.separator + folderName );

        // Creates the directory if it doesn't exist
        new File( dirPath.toString() ).mkdir();

        return dirPath;

    }

    // From https://stackoverflow.com/a/21974043
    /**
     * @return  the extension of the provided file including the dot
     *          (.png)
     */
    public static String getFileExtension( String filename ) {

        String extension = "";

        int i = filename.lastIndexOf('.');
        int p = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));

        if (i > p) {
            extension = filename.substring(i);
        }

        return extension;

    }

    /**
     * Generates a random alphanumeric filename containing
     * characters from '0' to 'z'.
     * From https://www.baeldung.com/java-random-string
     *
     * @return  a string with length {@value MAX_FILENAME_SIZE}
     */
    public String genRandomName() {

        return filenameGen.ints(48,122+1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit( MAX_FILENAME_SIZE )
                .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                .toString();

    }

    /**
     * Copies the image at the specified path.
     * @return  the path to the copied image;
     *          if path is null, returns null
     */
    public String copyImage( String path ) {

        if ( path == null ) {
            return null;
        }

        Path srcPath = fileSys.getPath(path);
        String extension = ImageHandler.getFileExtension(path);

        // Get a random name for the file
        String filename = genRandomName();

        // Writes to imageDir/{RANDOM_NAME}
        Path destPath = fileSys.getPath( imageDir.toString() + File.separator + filename + extension );

        // Attempt to save the image
        try {

            // Creates the file to write to
            // Checks if creation was successful
            if ( !new File( destPath.toString() ).createNewFile() ) {
                return null;
            }

            // Copies the file
            Files.copy( srcPath, destPath, StandardCopyOption.REPLACE_EXISTING );
            return destPath.toString();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return null;

    }

    // From https://docs.oracle.com/javase/tutorial/essential/io/delete.html
    public void deleteImage( String path ) {

        if ( path == null ) {
            return;
        }

        try {
            Files.delete( fileSys.getPath(path) );
        } catch ( NoSuchFileException x ) {
            System.err.format("%s: no such" + " file or directory%n", path);
        } catch ( DirectoryNotEmptyException x ) {
            System.err.format("%s not empty%n", path);
        } catch ( IOException x ) {
            // File permission problems are caught here.
            System.err.println(x);
        }

    }

}
