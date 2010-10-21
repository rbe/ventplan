/**
 * /Users/rbe/project/wac2/griffon-app/util/com/westaflex/wac/CheckUpdate.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

/**
 * Check for an update.
 */
class CheckUpdate implements java.lang.Runnable {
	
	def static unzip = { String dest ->
		//in metaclass added methods, 'delegate' is the object on which 
		//the method is called. Here it's the file to unzip
		def result = new java.util.zip.ZipInputStream(new java.io.FileInputStream(delegate))
		def destFile = new File(dest)
		if (!destFile.exists()) {
			destFile.mkdir()
		}
		result.withStream{
			def entry
			while (entry = result.nextEntry) {
				if (!entry.isDirectory()) {
					new java.io.File("${dest}/${entry.name}").parentFile?.mkdirs()
					def output = new java.io.FileOutputStream("${dest}/${entry.name}")
					output.withStream {
						int len = 0
						byte[] buffer = new byte[32 * 1024]
						while ((len = result.read(buffer)) > 0){
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
			version = new java.io.File("conf/version").text.trim()
			def u = "http://service.bensmann.com/update/wac/${version}/wacupdate.zip"
			println "update: trying to download ${u}"
			def buf = new byte[512 * 1024]
			// Destination for download
			def dest = java.io.File.createTempFile("wacupdate", ".tmp")
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
			println "update: downloaded into ${dest}"
			// And copy it to update/ folder
			def dest2 = new java.io.File("update", "wacupdate.zip")
			dest2.parentFile.mkdirs()
			dest.renameTo(dest2)
			// Unzip it
			dest2.unzip(dest2.parent)
			println "update: unzipped into ${dest2.parent}"
			// Delete
			dest2.deleteOnExit()
			dest2.delete()
			//
			println "update: done"
		} catch (java.io.FileNotFoundException e) {
			println "update: nothing found for version ${version}"
		} catch (e) {
			e.printStackTrace()
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
