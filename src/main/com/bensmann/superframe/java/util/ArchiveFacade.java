/*
 * Created on 09.02.2004
 *
 */
package com.bensmann.superframe.java.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Easy create a ArchiveFacade file:
 *
 * <p>
 *
 * <pre>
 * ArchiveFacade z = new ArchiveFacade("file.zip");
 * z.addFile(String, File);
 * z.create();
 * </pre>
 * </p>
 *
 *
 * @author rb
 * @version $Id: ArchiveFacade.java,v 1.1 2005/07/19 15:51:39 rb Exp $
 */
public class ArchiveFacade {
    
    /**
     * Buffer length for stream operations
     */
    private final int BUFFER = 8192;
    
    /**
     *
     */
    private File zipFile;
    
    /**
     *
     */
    private ZipOutputStream archiveOutputStream;
    
    /**
     *
     */
    private HashMap<String, Object> files;
    
    /**
     * Constructor
     */
    private ArchiveFacade() {
        files = new HashMap<String, Object>();
    }
    
    /**
     * Constructor
     *
     * @param zipFileName Name of the resulting zip archive
     */
    public ArchiveFacade(String zipFileName) {
        this();
        zipFile = new File(zipFileName);
    }
    
    /**
     * Constructor
     *
     * @param zipFile
     */
    public ArchiveFacade(File zipFile) {
        this();
        this.zipFile = zipFile;
    }
    
    /**
     * Add a file to archive
     *
     * @param file File object to be included in archive
     */
    public void addFile(File file) {
        files.put(file.getName(), file);
    }
    
    /**
     * Add a file to archive
     *
     * @param fileName Name of file in archive
     * @param file File object to be included in archive
     */
    public void addFile(String fileName, File file) {
        files.put(fileName, file);
    }
    
    /**
     * Add a file to archive
     *
     * @param fileName Name of file in archive
     * @param fin InputStream to be included in archive
     * @throws IOException
     */
    public void addFile(String fileName, FileInputStream fin)
    throws IOException {
        
        files.put(fileName, fin);
        
    }
    
    /**
     * Remove file from file list
     *
     * @param fileName
     */
    public void removeFile(String fileName) {
        files.remove(fileName);
    }
    
    private void openArchive(ArchiveFileMode archiveFileMode)
    throws IOException {
        
        if (archiveOutputStream == null) {
            
            if (archiveFileMode == ArchiveFileMode.WRITE || archiveFileMode == ArchiveFileMode.READ_WRITE) {
                archiveOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            }
            
        }
        
    }
    
    private void closeArchive() throws IOException {
        
    }
    
    /**
     * Create the archive
     *
     * @throws IOException
     */
    public void createArchive() throws IOException {
        
        FileInputStream fin = null;
        openArchive(ArchiveFileMode.WRITE);
        
        Iterator i = files.entrySet().iterator();
        while (i.hasNext()) {
            
            Map.Entry e = (Map.Entry) i.next();
            String fn = (String) e.getKey();
            Object fc = (Object) e.getValue();
            
            archiveOutputStream.putNextEntry(new ZipEntry(fn));
            
            if (fc instanceof FileInputStream) {
                fin = (FileInputStream) fc;
            } else if (fc instanceof File) {
                fin = new FileInputStream(fn);
            } else {
                continue;
            }
            
            byte[] b = new byte[BUFFER];
            int len = 0;
            while ((len = fin.read(b)) > 0) {
                archiveOutputStream.write(b, 0, len);
            }
            
            fin.close();
            archiveOutputStream.closeEntry();
            
        }
        
        archiveOutputStream.close();
        
    }
    
    /**
     * Get a file from Zip archive and save it into the filesystem (in
     * current working directory)
     *
     * @param fileName
     * @throws IOException
     * @return
     */
    public File getFile(String fileName) throws IOException {
        
        byte[] b = new byte[BUFFER];
        int bytesRead;
        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
        File file = new File(fileName);
        FileOutputStream fout = new FileOutputStream(file);
        
        while (zin.available() > 0) {
            
            ZipEntry ze = zin.getNextEntry();
            if (ze.getName().equals(fileName)) {
                while ((bytesRead = zin.read(b, 0, BUFFER)) != -1) {
                    fout.write(b, 0, bytesRead);
                }
            }
            
            fout.close();
            
        }
        
        return file;
        
    }
    
    /**
     *
     * @return
     * @param filename
     * @throws java.io.IOException
     */
    public ByteArrayOutputStream getFileAsByteArrayOutputStream(File file)
    throws IOException {
        
        InputStream in = null;
        String tmpFilename = null;
        OutputStream out = null;
        byte[] buf = new byte[BUFFER];
        int len = 0;
        
        // Open the file
        in = new FileInputStream(file);
        
        // Open the output byte array
        out = new ByteArrayOutputStream();
        
        // Transfer bytes from the ZIP file to the output file
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, BUFFER);
        }
        
        // Close the streams
        out.close();
        in.close();
        
        return (ByteArrayOutputStream) out;
        
    }
    
    /**
     *
     * @param filename
     * @return
     */
    public byte[] getFileAsByteArray(File file)
    throws IOException {
        
        return getFileAsByteArrayOutputStream(file).toByteArray();
        
    }
    
    /**
     *
     * @return
     * @param filename
     * @throws java.io.IOException
     */
    public ByteArrayOutputStream getFileAsByteArrayOutputStreamFromZip(
            File zipFile, String filename)
            throws IOException {
        
        ZipInputStream in = null;
        String tmpFilename = null;
        ByteArrayOutputStream out = null;
        byte[] buf = new byte[BUFFER];
        int len = 0;
        
        try {
            
            // Open the ZIP file
            in = new ZipInputStream(new FileInputStream(zipFile));
            
            // Look for ZIP entry
            tmpFilename = in.getNextEntry().getName();
            while (tmpFilename != null &&
                    !tmpFilename.toLowerCase().equals(filename.toLowerCase())) {
                
                tmpFilename = in.getNextEntry().getName();
                
            }
            
            // Open the output byte array
            out = new ByteArrayOutputStream();
            
            // Transfer bytes from the ZIP file to the output file
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            
            // Close the streams
            out.close();
            in.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return out;
        
    }
    
    /**
     *
     * @param filename
     * @return
     */
    public byte[] getFileAsByteArrayFromZip(String filename)
    throws IOException {
        
        return getFileAsByteArrayOutputStreamFromZip(zipFile, filename).
                toByteArray();
        
    }
    
    /**
     *
     * @param filename
     * @throws java.io.IOException
     * @return
     */
    public char[] getFileAsCharArrayFromZip(String filename)
    throws IOException {
        
        return new String(
                getFileAsByteArrayFromZip(filename)).toCharArray();
        
    }
    
}