/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/RaumVsView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011 art of coding UG (haftungsbeschränkt)
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout
import com.bensmann.griffon.GriffonHelper as GH

// Raumvolumenströme
panel(layout: new MigLayout("wrap", "[center]", "[fill]"), constraints: "grow") {


    label(icon: imageIcon("/icon/westawac.png"), constraints: "wrap")

    label(id: "aboutDialogVersion", text: "Version: " + GH.localVersion())
    label("Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann")
    label("Copyright (C) 2011 art of coding UG (haftungsbeschränkt)")


}

