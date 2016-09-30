package ch.hasselba.xpages.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

import com.ibm.designer.domino.napi.NotesAPIException;
import com.ibm.designer.domino.napi.NotesDatabase;
import com.ibm.designer.domino.napi.NotesNote;
import com.ibm.designer.domino.napi.NotesSession;
import com.ibm.designer.domino.napi.design.FileAccess;

public class Toolbox {

    /**
     * loads the properties from a file
     * 
     * @param dbPath full path of the database
     * @param fileName name of the file to load
     * @return the properties object
     */
    public Properties loadProperties(final String dbPath, final String fileName) {
        try {
            // load the file
            InputStream inStream = getFile( dbPath, fileName );

            // if file exists, init a properties object
            if (inStream != null) {
                Properties props = new Properties();
                props.load( inStream );
                return props;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * saves a property file to a database
     * 
     * @author Sven Hasselbach
     * 
     * @param dbPath full path of the database
     * @param fileName name of the file to load
     * @param props the properties object
     */
    public void saveProperties(final String dbPath, final String fileName, final Properties props) {
        try {
            // init Notes objects
            NotesSession nSession = new NotesSession();
            NotesDatabase nDB = nSession.getDatabaseByPath(dbPath);
            nDB.open();

            // store properties in byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            props.store(bos, "My XSP Properties");

            // save the property file
            NotesNote nFile = FileAccess.getFileByPath(nDB, fileName);
            FileAccess.saveData(nFile, fileName, bos.toByteArray() );

            // recycle the objects
            nFile.recycle();
            nDB.recycle();
            nSession.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * loads a property file from a database
     * 
     * @author Sven Hasselbach
     * @param dbPath full path of the database
     * @param fileName name of the file to load
     * @return InputStream content of the file
     */
    private InputStream getFile(final String dbPath, final String fileName) {
        try {
            // init Notes objects
            NotesSession nSession = new NotesSession();
            NotesDatabase nDB = nSession.getDatabaseByPath(dbPath);
            nDB.open();

            // get the file
            NotesNote nNote = FileAccess.getFileByPath(nDB, fileName);
            InputStream inStream = FileAccess.readFileContentAsInputStream(nNote);

            // recycle the objects
            nNote.recycle();
            nDB.recycle();
            nSession.recycle();

            return inStream;
        } catch (NotesAPIException apiEx) {
            apiEx.printStackTrace();
        }
        return null;
    }

}