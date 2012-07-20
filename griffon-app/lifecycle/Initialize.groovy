/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/7/12 5:23 PM
 */

/*
 * This script is executed inside the UI thread, so be sure to  call 
 * long running code in another thread.
 *
 * You have the following options
 * - execOutside { // your code }
 * - execFuture { // your code }
 * - Thread.start { // your code }
 *
 * You have the following options to run code again inside the UI thread
 * - execAsync { // your code }
 * - execSync { // your code }
 */

import groovy.swing.SwingBuilder
import static griffon.util.GriffonApplicationUtils.isMacOSX
import com.ventplan.desktop.VentplanSplash

//griffon.util.groovy.swing.SwingBuilder.lookAndFeel('mac', 'nimbus', 'gtk', ['metal', [boldFonts: false]])
SwingBuilder.lookAndFeel((isMacOSX ? 'system' : 'nimbus'), 'gtk', ['metal', [boldFonts: false]])
VentplanSplash.instance.setup()
VentplanSplash.instance.initializing()

println "Initialize: Working directory is ${System.getProperty('user.dir')}"
