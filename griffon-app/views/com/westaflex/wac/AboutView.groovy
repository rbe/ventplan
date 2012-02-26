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

import net.miginfocom.swing.MigLayout
import com.bensmann.griffon.GriffonHelper as GH

// About view
panel(layout: new MigLayout("wrap", "[center]", "[fill]"), constraints: "grow") {


    label(icon: imageIcon("/icon/westawac.png"), constraints: "wrap")

    label(id: "aboutDialogVersion", text: "Version: " + GH.localVersion())
    label("Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann")
    label("Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt)")


}
