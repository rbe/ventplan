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

import net.miginfocom.swing.MigLayout

import javax.swing.filechooser.FileFilter

//<editor-fold desc="FileChooser">
vpxFileChooserWindow = fileChooser(
        dialogTitle: 'Bitte wählen Sie eine Ventplan-Datei',
        multiSelectionEnabled: false,
        fileFilter: [
                getDescription: {-> 'Ventplan Projekt XML' },
                accept: { file ->
                    //println "wpxFileChooser: filtering ${file.dump()} isDirectory=${file.isDirectory()} endsWith(wpx)=${file.name.endsWith(".wpx")}"
                    return file.isDirectory() || file.name.toLowerCase().endsWith('.vpx') || file.name.toLowerCase().endsWith('.wpx')
                }
        ] as FileFilter
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
        ] as FileFilter
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
//</editor-fold>

ventplanFrame = application(
        title: "Ventplan ${VentplanResource.ventplanVersion}",
        size: [1280, 800], //[screen.width as int, screen.height as int],
        pack: false,
        locationByPlatform: true,
        iconImage: imageIcon('/image/ventplan_signet_48x48.png').image,
        iconImages: [
                imageIcon('/image/ventplan_signet_48x48.png').image,
                imageIcon('/image/ventplan_signet_32x32.png').image,
                imageIcon('/image/ventplan_signet_16x16.png').image
        ],
        layout: new MigLayout('fill', '[grow,200::]'),
        defaultCloseOperation: WindowConstants.DO_NOTHING_ON_CLOSE, // Our window close listener
        windowClosing: { evt -> app.shutdown() }
) {
    // Build menu bar
    build(VentplanMenubar)
    // Build toolbar
    toolBar(build(VentplanToolBar))
    // Content
    widget(
            // Set scrollpane for all projects
            jideScrollPane(id: 'mainScrollPane', constraints: 'grow') {
                build(VentplanMainPane)
            }
    )
    // set visibility of scrollbars
    mainScrollPane.setHorizontalScrollBarPolicy(com.jidesoft.swing.JideScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    mainScrollPane.setVerticalScrollBarPolicy(com.jidesoft.swing.JideScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    // Bindings
    build(VentplanBindings)
    // The status bar
    statusBar(id: 'mainStatusBar') {
        progressBar(id: 'mainStatusProgressBar', minimum: 0, maximum: 100, indeterminate: bind { model.statusProgressBarIndeterminate })
        label(id: 'mainStatusBarText', text: bind { model.statusBarText })
    }
    // WAC-161: Zuletzt geöffnete Projekte in das Menu laden
    controller.buildRecentlyOpenedMenuItems()
}
