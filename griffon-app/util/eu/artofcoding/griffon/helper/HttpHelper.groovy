/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 01.04.13 21:08
 */

package eu.artofcoding.griffon.helper

import java.util.zip.ZipInputStream

public class HttpHelper {

    def static unzip = { String dest ->
        // In metaclass added methods, 'delegate' is the object on which the method is called.
        // Here it's the file to unzip.
        def result = new ZipInputStream(new FileInputStream(delegate))
        def destFile = new File(dest)
        if (!destFile.exists()) {
            destFile.mkdir()
        }
        result.withStream {
            def entry
            while (entry = result.nextEntry) {
                if (!entry.isDirectory()) {
                    new File("${dest}/${entry.name}").parentFile?.mkdirs()
                    def output = new FileOutputStream("${dest}/${entry.name}")
                    output.withStream {
                        int len = 0
                        byte[] buffer = new byte[32 * 1024]
                        while ((len = result.read(buffer)) > 0) {
                            output.write(buffer, 0, len)
                        }
                    }
                } else {
                    new File("${dest}/${entry.name}").mkdir()
                }
            }
        }
    }
    
    public static void downloadAndUnzip(String url) {
        /*
        File.metaClass.unzip = HttpHelper.unzip
        // Download ZIP from webserver
        //println "update: trying to download ${u}"
        def buf = new byte[512 * 1024]
        // Destination for download
        def dest = File.createTempFile('ventplan', '.tmp')
        dest.deleteOnExit()
        // Download data and write into temporary file
        dest.withOutputStream { ostream ->
            def r
            new URL(u).withInputStream { istream ->
                while ((r = istream.read(buf, 0, buf.length)) > -1) {
                    ostream.write(buf, 0, r)
                }
            }
        }
        //println "update: downloaded into ${dest}"
        // And copy it to update/ folder
        def dest2 = new File('ventplan', 'update.zip')
        dest2.parentFile.mkdirs()
        dest.renameTo(dest2)
        // Unzip it
        dest2.unzip(dest2.parent)
        //println "update: unzipped into ${dest2.parent}"
        // Delete
        dest2.deleteOnExit()
        dest2.delete()
        //println "update: done"
        */
    }

    public static String download(String url) {
        OutputStream stream = new ByteArrayOutputStream()
        BufferedOutputStream out = new BufferedOutputStream(stream)
        out << new URL(url).openStream()
        out.close()
        return new String(stream.toByteArray())
    }

    public static void download(String url, File filename) {
        String fname = filename ?: url.tokenize("/")[-1]
        OutputStream file = new FileOutputStream(fname)
        BufferedOutputStream out = new BufferedOutputStream(file)
        out << new URL(url).openStream()
        out.close()
    }

}
