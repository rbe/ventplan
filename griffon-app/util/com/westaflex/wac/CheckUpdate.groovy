/*
 * WAC
 *
 * Copyright (C) 2005      Informationssysteme Ralf Bensmann.
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

/**
 * Check for an update.
 */
class CheckUpdate implements java.lang.Runnable {

    private static final String version = GH.localVersion()

    def static unzip = { String dest ->
        //in metaclass added methods, 'delegate' is the object on which 
        //the method is called. Here it's the file to unzip
        def result = new java.util.zip.ZipInputStream(new java.io.FileInputStream(delegate))
        def destFile = new File(dest)
        if (!destFile.exists()) {
            destFile.mkdir()
        }
        result.withStream {
            def entry
            while (entry = result.nextEntry) {
                if (!entry.isDirectory()) {
                    new java.io.File("${dest}/${entry.name}").parentFile?.mkdirs()
                    def output = new java.io.FileOutputStream("${dest}/${entry.name}")
                    output.withStream {
                        int len = 0
                        byte[] buffer = new byte[32 * 1024]
                        while ((len = result.read(buffer)) > 0) {
                            output.write(buffer, 0, len)
                        }
                    }
                } else {
                    new java.io.File("${dest}/${entry.name}").mkdir()
                }
            }
        }
    }

    /**
     *
     */
    def update = {
        def version
        try {
            // Download ZIP from webserver
            //def u = "http://files.art-of-coding.eu/westaflex/wac/update/${version}/wacupdate.zip"
            def u = String.format(GH.getUpdateUrl(), GH.localVersion())
            //println "update: trying to download ${u}"
            def buf = new byte[512 * 1024]
            // Destination for download
            def dest = java.io.File.createTempFile("ventplan", ".tmp")
            dest.deleteOnExit()
            // Download data and write into temporary file
            dest.withOutputStream { ostream ->
                def r
                new java.net.URL(u).withInputStream { istream ->
                    while ((r = istream.read(buf, 0, buf.length)) > -1) {
                        ostream.write(buf, 0, r)
                    }
                }
            }
            //println "update: downloaded into ${dest}"
            // And copy it to update/ folder
            def dest2 = new java.io.File("ventplan", "update.zip")
            dest2.parentFile.mkdirs()
            dest.renameTo(dest2)
            // Unzip it
            dest2.unzip(dest2.parent)
            //println "update: unzipped into ${dest2.parent}"
            // Delete
            dest2.deleteOnExit()
            dest2.delete()
            //println "update: done"
        } catch (java.io.FileNotFoundException e) {
            //println "update: nothing found for version ${version}"
        } catch (e) {
            println "${this}.update: ${e}"
        }
    }

    /**
     *
     */
    void run() {
        File.metaClass.unzip = CheckUpdate.unzip
        while (true) {
            update()
            try { Thread.sleep(10 * 60 * 1000) } catch (e) {}
        }
    }

}
