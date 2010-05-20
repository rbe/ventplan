/*
 * Created on May 30, 2003
 *
 */
package com.bensmann.superframe.java.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

/**
 *
 * @author rb
 * @version $Id: IOUtil.java,v 1.1 2005/07/19 15:51:38 rb Exp $
 */
public final class IOUtil {
    
    /**
     *
     */
    private static Logger logger;
    
    static {
        logger = Logger.getLogger(IOUtil.class.getName());
    }
    
    /** You cannot create an instance */
    private IOUtil() {
    }
    
    /**
     * 
     * @param file 
     * @throws java.io.FileNotFoundException 
     * @throws java.io.IOException 
     * @return 
     */
    public static ByteArrayInputStream convertFileToOutputStream(File file)
    throws FileNotFoundException, IOException {
        
        byte[] tmp = new byte[32 * 1024];
        int len = 0;
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while ((len = fis.read(tmp)) != -1) {
            outputStream.write(tmp);
            outputStream.flush();
        }
        
        return new ByteArrayInputStream(outputStream.toByteArray());
        
    }
    
    /**
     *
     * @return
     * @param filename
     * @throws java.io.IOException
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
     */
    public static byte[] getFileAsByteArray(File file)
    throws IOException {
        
        return getFileAsByteArrayOutputStream(file).toByteArray();
        
    }
    
    /**
     * @param inFileName
     * @param outFileName
     * @throws IOException
     */
    public static void copyFile(String inFileName, String outFileName)
    throws IOException {
        
        File inputFile = new File(inFileName);
        File outputFile = new File(outFileName);
        
        FileReader in = new FileReader(inputFile);
        FileWriter out = new FileWriter(outputFile);
        int c;
        
        while ((c = in.read()) != -1)
            out.write(c);
        
        in.close();
        out.close();
        
    }
    
    /**
     * copy data, streams will not be closed
     */
    public static void copyStream(InputStream in, OutputStream out,
            int bufSize) throws IOException {
        
        byte buf[] = new byte[bufSize];
        int r;
        while ((r = in.read(buf)) != -1) {
            out.write(buf, 0, r);
        }
    }
    
    /**
     * copy data, streams will be closed after successful copy process
     */
    public static void copyStream(InputStream in, OutputStream out,
            int bufSize, boolean closeStreams) throws IOException {
        
        byte buf[] = new byte[bufSize];
        int r;
        while ((r = in.read(buf)) != -1) {
            out.write(buf, 0, r);
        }
        
        in.close();
        out.close();
    }
    
    /**
     * Copy an InputStream into a byte[] using a certain buffer size for the
     * copy process.
     *
     * @param in
     *            InputStream that should be read
     * @param bufferSize
     *            Size of buffer for copy process
     */
    public static byte[] copyStream(InputStream in, int bufferSize)
    throws IOException {
        
        // Buffer for reading InputStream
        byte[] buffer = new byte[bufferSize];
        // Result (all bytes read from InputStream)
        byte[] result = new byte[0];
        byte[] temp = null;
        // Count of read bytes
        int i = 0;
        
        while ((i = in.read(buffer, 0, bufferSize)) != -1) {
            
            // Create new buffer that content is 'existing result + read bytes'
            temp = new byte[result.length + buffer.length];
            if (result.length > 0) {
                System.arraycopy(result, 0, temp, 0, result.length);
            }
            System.arraycopy(buffer, 0, temp, result.length, buffer.length);
            
            // Reassign result
            result = temp;
            
        }
        
        return result;
        
    }
    
    /**
     * Write the content of a byte array into a file at a given URL.
     *
     * @param b
     * @param url
     */
    public static void writeByteArrayToURL(byte[] b, URL url)
    throws IOException {
        
        File file = null;
        
        try {
            file = new File(new URI("file://" + url.getFile()));
        }
        // URISyntaxException, IllegalArgumentException
        catch (Exception e) {
            file = null;
        }
        
        // Write byte array to file
        if (file != null) {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(b);
            fos.close();
        }
        
    }
    
    /**
     * Write contents of a buffer into a file
     *
     * @param br
     * @param f
     * @throws IOException
     */
    public static void writeBufferToFile(BufferedReader br, File f)
    throws IOException {
        
        String line;
        FileWriter fw = new FileWriter(f);
        
        while ((line = br.readLine()) != null) {
            fw.write(line + "\n");
        }
        
        fw.close();
        
    }
    
}