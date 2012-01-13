/**
 * WAC
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. All rights reserved.
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout
import com.bensmann.griffon.GriffonHelper as GH

// About view
panel(layout: new MigLayout("wrap", "[center]", "[fill]"), constraints: "grow") {


    label(icon: imageIcon("/icon/westawac.png"), constraints: "wrap")

    label(id: "aboutDialogVersion", text: "Version: " + GH.localVersion())
    label("Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann")
    label("Copyright (C) 2011 art of coding UG (haftungsbeschränkt)")


}
