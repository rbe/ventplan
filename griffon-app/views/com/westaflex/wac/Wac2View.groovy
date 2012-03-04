/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout
import com.bensmann.griffon.GriffonHelper as GH
import javax.swing.JFileChooser

def screen = java.awt.Toolkit.defaultToolkit.screenSize

wpxFileChooserWindow = fileChooser(
        dialogTitle: 'Bitte wählen Sie eine VentPlan-Datei',
        multiSelectionEnabled: false,
        fileFilter: [
                getDescription: {-> 'VentPlan Projekt XML' },
                accept: { file ->
                    //println "wpxFileChooser: filtering ${file.dump()} isDirectory=${file.isDirectory()} endsWith(wpx)=${file.name.endsWith(".wpx")}"
                    return file.isDirectory() || file.name.toLowerCase().endsWith('.vpx') || file.name.toLowerCase().endsWith('.wpx')
                }
        ] as javax.swing.filechooser.FileFilter
)

projektSuchenFolderChooserWindow = fileChooser(
        dialogTitle: 'Bitte wählen Sie einen Ordner aus',
        multiSelectionEnabled: false,
        fileSelectionMode: JFileChooser.DIRECTORIES_ONLY,
        fileFilter: [
                getDescription: {-> 'Ordner' },
                accept: { file ->
                    return file.isDirectory()
                }
        ] as javax.swing.filechooser.FileFilter
)

/*
// WAC-177: Angebotsverfolgung
angebotsverfolgungChooserWindow = fileChooser(
        dialogTitle: "Bitte wählen Sie eine WPX-Datei oder Verzeichnis mit WPX-Dateien aus",
        multiSelectionEnabled: true,
        fileSelectionMode: javax.swing.JFileChooser.FILES_AND_DIRECTORIES,
        fileFilter: [
                getDescription: { -> "WestaWAC Projekt XML" },
                accept: { file ->
                    //println "wpxFileChooser: filtering ${file.dump()} isDirectory=${file.isDirectory()} endsWith(wpx)=${file.name.endsWith(".wpx")}"
                    return file.isDirectory() || file.name.toLowerCase().endsWith('.vpx') || file.name.toLowerCase().endsWith(".wpx")
                } ] as javax.swing.filechooser.FileFilter
    )
*/

wac2Frame = application(
        title: "VentPlan ${GH.localVersion()}",
        size: [1280, 800], //[screen.width as int, screen.height as int],
        pack: false,
        locationByPlatform: true,
        iconImage: imageIcon('/image/ventplan_signet_48x48.png').image,
        iconImages: [
                imageIcon('/image/ventplan_signet_48x48.png').image,
                imageIcon('/image/ventplan_signet_32x32.png').image,
                imageIcon('/image/ventplan_signet_16x16.png').image
        ],
        layout: new MigLayout("fill", "[grow,200::]"),
        // Our window close listener
        defaultCloseOperation: javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE,
        windowClosing: { evt ->
            //println "windowClosing -> ${evt.dump()}"
            //controller.exitApplication
            app.shutdown()
        }
) {
    // Build menu bar
    build(Wac2MenuBar)
    // Build toolbar
    toolBar(build(Wac2ToolBar))
    // Content
    widget(
            // set scrollpane for all projects
            jideScrollPane(id: "mainScrollPane", constraints: "grow") {
                build(Wac2MainPane)
            }
    )
    // set visibility of scrollbars
    mainScrollPane.setHorizontalScrollBarPolicy(com.jidesoft.swing.JideScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    mainScrollPane.setVerticalScrollBarPolicy(com.jidesoft.swing.JideScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    // Bindings
    build(Wac2Bindings)
    // The status bar
    statusBar(id: "mainStatusBar") {
        progressBar(id: "mainStatusProgressBar", minimum: 0, maximum: 100, indeterminate: bind { model.statusProgressBarIndeterminate })
        label(id: "mainStatusBarText", text: bind { model.statusBarText })
    }

    // WAC-161: Zuletzt geöffnete Projekte in das Menu laden
    controller.buildRecentlyOpenedMenuItems()
}
