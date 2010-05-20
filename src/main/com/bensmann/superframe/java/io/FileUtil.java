/*
 * FileUtil.java
 *
 * Created on 5. Juni 2006, 16:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.bensmann.superframe.java.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author rb
 */
public class FileUtil {
    
    /** Creates a new instance of FileUtil */
    public FileUtil() {
    }
    
    /**
     *
     * @return
     * @param filename
     * @throws java.io.IOException
     * @deprecated Used IOUtil.getFileAsByteArrayOutputStream
     */
    public static ByteArrayOutputStream getFileAsByteArrayOutputStream(
            File file)
            throws IOException {
        
        InputStream in = null;
        String tmpFilename = null;
        OutputStream out = null;
        byte[] buf = new byte[1024 * 8];
        int len = 0;
        
        try {
            
            // Open the file
            in = new FileInputStream(file);
            
            // Open the output byte array
            out = new ByteArrayOutputStream();
            
            // Transfer bytes from the ZIP file to the output file
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            
            // Close the streams
            out.close();
            in.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return (ByteArrayOutputStream) out;
        
    }
    
    /**
     *
     * @param filename
     * @return
     * @deprecated Used IOUtil.getFileAsByteArrayOutputStream
     */
    public static byte[] getFileAsByteArray(File file)
    throws IOException {
        
        return getFileAsByteArrayOutputStream(file).toByteArray();
        
    }
        
}
