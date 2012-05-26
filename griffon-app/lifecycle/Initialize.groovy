/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
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

//griffon.util.groovy.swing.SwingBuilder.lookAndFeel('mac', 'nimbus', 'gtk', ['metal', [boldFonts: false]])
SwingBuilder.lookAndFeel((isMacOSX ? 'system' : 'nimbus'), 'gtk', ['metal', [boldFonts: false]])
com.ventplan.desktop.Wac2Splash.instance.setup()
com.ventplan.desktop.Wac2Splash.instance.initializing()
