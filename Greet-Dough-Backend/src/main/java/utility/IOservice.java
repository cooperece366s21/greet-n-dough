package utility;

import java.io.*;

public class IOservice {

    private IOservice(){
    }
    // IO Helper Functions
    public static Integer saveObject( Object objToSave, String fileName ){

        try {

            FileOutputStream fo = new FileOutputStream(new File(fileName), false);
            ObjectOutputStream oo = new ObjectOutputStream(fo);

            oo.writeObject( objToSave );
            oo.flush();
            oo.close();
            fo.close();

        } catch ( Exception ex ) {
            ex.printStackTrace();
            return -1;
        }

        return 0;

    }

    // Generalized Load Function. Requires casting on call.
    public static Object loadObject( String fileName ){

        Object objToLoad = new Object();

        try {

            FileInputStream fi = new FileInputStream( new File( fileName ) );
            ObjectInputStream oi = new ObjectInputStream(fi);

            if ( fi.available() != 0 ) {
                objToLoad = oi.readObject();
                oi.close();
                fi.close();
            }
            else {
                System.out.println("Empty file " + fileName);
            }

        }
        catch ( EOFException e ){
            System.out.println( fileName + " Is empty");
        }
        catch ( Exception ex ) {
            ex.printStackTrace();
        }

        return objToLoad;
    }

}
