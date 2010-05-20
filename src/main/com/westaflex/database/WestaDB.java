
/*
 * WestaDB.java
 *
 * Created on 26. Juli 2006, 17:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.westaflex.database;

import com.westaflex.resource.Strings.Strings;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author Oliver
 */
public class WestaDB {

    // ---Pseudo constants----------------------------------------
    static String FILESEP = System.getProperty("file.separator");
    static int nextdid = 1;
    static int nextfid = 1;
    // ---Static Variables----------------------------------------
    static Connection con;
    static DatabaseMetaData dmd;
    private static WestaDB instance;
    static Statement sql;
    String replace = "";
    List vLocalDBFiles = null;

    private WestaDB() {
    }

    public static WestaDB getInstance() {
        if (instance == null) {
            instance = new WestaDB();
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return instance;
    }

    public void close() {
        try {
            if (sql != null) {
                sql.close();
                con.close();
            }
            if (vLocalDBFiles != null) {
                for (int len = 0; len < vLocalDBFiles.size(); len++) {
                    (new File((String) vLocalDBFiles.get(len))).delete();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean open() {

        boolean ret = false;
        try {
            if ((con == null) || ((con != null) && con.isClosed())) {
                System.out.println(Strings.TRYING_TO_CONNECT);
                try {
                    con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Westa", "westa", "");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    /*if (con == null) {
                        connectLocalOODatabase();
                    }*/
                }
                if (con != null) {
                    sql = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ret = true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public synchronized ResultSet queryDB(String s) {
        ResultSet res;

        res = null;

        open();
        try {
            res = sql.executeQuery(s.replace("~", replace));
        } catch (SQLException ex) {
            System.err.println(s);
            ex.printStackTrace();
        }
        return res;
    }

    public String[][] queryDBResultArray(String sSQL) {

        String ret[][] = null;
        int rows, columns;
        ResultSet r = queryDB(sSQL);
        if (r != null) {
            try {
                columns = r.getMetaData().getColumnCount();
                r.last();
                rows = r.getRow();
                r.beforeFirst();
                ret = new String[rows][columns];
                for (int i = 0; i < rows; i++) {
                    r.next();
                    for (int k = 0; k < columns; k++) {
                        ret[i][k] = r.getString(k + 1);
                    }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public String[] queryDBResultList(String s) {

        String ret[] = null;
        int rSize, i;

        ResultSet r = queryDB(s);
        if (r != null) {
            try {
                r.last();
                rSize = r.getRow();
                r.beforeFirst();
                ret = new String[rSize];
                for (i = 0; i < rSize; i++) {
                    r.next();
                    ret[i] = r.getString(1);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public String[] queryDBResultRow(String s) {

        String ret[] = null;

        ResultSet r = queryDB(s);
        if (r != null) {
            try {
                if (r.next()) {
                    ret = new String[r.getMetaData().getColumnCount()];
                    for (int i = 0; i < ret.length; i++) {
                        ret[i] = r.getString(i + 1);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public float[] queryDBResultRowAsFloat(String s) {

        float ret[] = {0f};
        ResultSet r = queryDB(s);
        if (r != null) {
            try {
                if (r.next()) {
                    ret = new float[r.getMetaData().getColumnCount()];
                    for (int i = 0; i < ret.length; i++) {
                        ret[i] = r.getFloat(i + 1);
                    }
                }
            } catch (SQLException ex) {
                System.out.println("SQL-Fehler bei: " + s);
                System.out.println(String.format("SQL-State = %s\nSQL-Error = %s", ex.getSQLState(), ex.getMessage()));
            }
        }
        return ret;
    }

    public int updateDB(String s) {
        int res = 0;

        open();
        try {
            res = sql.executeUpdate(s);
        } catch (SQLException ex) {
            System.err.println(s);
            ex.printStackTrace();
        }

        return res;
    }

    private void connectLocalOODatabase() {

        //from http://digiassn.blogspot.com/2006/07/java-creating-jdbc-connection-to.html
        int len;
        BufferedOutputStream out;
        InputStream in;
        ZipEntry ent;
        Enumeration en = null;
        File f;
        ZipFile file;
        String tmppath;

        if (System.getProperty("os.name").contains("Windows")) {
            tmppath = System.getenv("TEMP") + "\\";
        } else {
            tmppath = "/tmp/";
        }
        //Open the zip file that holds the OO.Org Base file
        try {
//            String userInst = System.getenv("UserInstallation");
//            if (userInst != null){
//                URI uri = new URI(userInst);
//                file = new ZipFile(Tools.searchFile(uri, "WestaHSQL.odb"));
//            } else {
            file = new ZipFile(getClass().getResource("/com/westaflex/resource/WestaHSQL.odb").getPath());
//            }
            vLocalDBFiles = new ArrayList();
            //Create a generic temp file. I only need to get the filename from
            //the tempfile to prefix the extracted files for OO Base
            f = File.createTempFile("ooTempDatabase", "tmp");
            f.deleteOnExit();

            //Get file entries from the zipfile and loop through all of them
            en = file.entries();
            while (en.hasMoreElements()) {
                //Get the current element
                ent = (ZipEntry) en.nextElement();

                //If the file is in the database directory, extract it to our
                //temp folder using the temp filename above as a prefix
                if (ent.getName().startsWith("database/")) {
                    System.out.println("Extracting File: " + ent.getName());
                    byte[] buffer = new byte[1024];

                    //Create an input stream file the file entry
                    in = file.getInputStream(ent);

                    //Create a output stream to write out the entry to, using the
                    //temp filename created above
                    out = new BufferedOutputStream(new FileOutputStream(tmppath + f.getName() + "." + ent.getName().substring(9)));

                    //Add the newly created temp file to the tempfile vector for deleting
                    //later on
                    vLocalDBFiles.add(tmppath + f.getName() + "." + ent.getName().substring(9));

                    //Read the input file into the buffer, then write out to
                    //the output file
                    while ((len = in.read(buffer)) >= 0) {
                        out.write(buffer, 0, len);
                    }

                    //close both the input stream and the output stream
                    out.close();
                    in.close();
                }
            }
            //Close the zip file since the temp files have been created
            file.close();

            //Create our JDBC connection based on the temp filename used above
            con = DriverManager.getConnection("jdbc:hsqldb:file:///" + tmppath.replace('\\', '/') + f.getName(), "SA", "");
            replace = "\"";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}