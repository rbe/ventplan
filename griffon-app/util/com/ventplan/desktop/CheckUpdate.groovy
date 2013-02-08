/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */
package com.ventplan.desktop

import griffon.swing.SwingApplication
import java.util.zip.ZipInputStream

/**
 * Check for an update.
 */
class CheckUpdate implements Runnable {

    private static boolean userAcknowledged = false

    /**
     * Griffon app.
     */
    SwingApplication app

    def static unzip = { String dest ->
        //in metaclass added methods, 'delegate' is the object on which 
        //the method is called. Here it's the file to unzip
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

    boolean update() {
        boolean b = false
        try {
            String url = String.format(VentplanResource.updateUrl, VentplanResource.ventplanVersion)
            URI.create(url).toURL().withInputStream {}
            /*
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
            b = true
        } catch (FileNotFoundException e) {
            //println "${this}.update: nothing found for version ${VentplanResource.ventplanVersion}"
        } catch (Exception e) {
            //println "${this}.update: ${e}"
        } finally {
            //println "${this}.update: b=${b}"
        }
        return b
    }

    void run() {
        File.metaClass.unzip = CheckUpdate.unzip
        // Check for updates as long as: no updates were found or user acknowledged a new update
        while (!userAcknowledged) {
            if (update() && !userAcknowledged) {
                // Bitte Dialog mit Link einbauen zu http://www.ventplan.com/latest/
                app.controllers['MainFrame'].checkUpdateDialogOeffnen()
                userAcknowledged = true
            }
            // Check every 10 minutes
            try { Thread.sleep(10 * 60 * 1000) } catch (e) {}
        }
    }

}
