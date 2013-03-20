/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 15:14
 */

import eu.artofcoding.griffon.helper.GriffonHelper as GH

onBootstrapEnd = { app ->
    app.config.shutdown.proceed = false
    // Add .toDouble2 and .toString2 to all types to have a convenient API
    // Integer, Long, Float, Double, BigDecimal, String
    [java.lang.Object].each {
        it.metaClass.toDouble2 = GH.toDouble2
        it.metaClass.toString2 = GH.toString2
    }
    // Override String.toString2
    String.metaClass.toString2 = {
        delegate
    }
    // String.multiply
    String.metaClass.multiply = { m ->
        def a = delegate.toDouble2()
        def b = m.toDouble2()
        delegate = (a * b).toString2()
    }
    // WAC-8: shutdown handler to abort application closing
    app.addShutdownHandler([
            canShutdown: { a ->
                app.config.shutdown.proceed = app.controllers['MainFrame'].canExitApplication(a)
                return app.config.shutdown.proceed
            },
            onShutdown: { a ->
            }
    ] as griffon.core.ShutdownHandler)
}

onStartupStart = { app ->
}

onStartupEnd = { app ->
}

onReadyStart = { app ->
}

onReadyEnd = { app ->
}

onShutdownStart = { app ->
}

onShutdownAbort = { app ->
    app.config.shutdown.proceed = false
}

onNewInstance = { clazz, type, instance ->
    /*
    // Nur Anzeigen, wenn Applikation erstmalig started (see conf/Application, startup groups)
    if (clazz.name ==~ /Wac2./) {
        Wac2Splash.instance.creatingUI()
    }
    */
}

//onCreateMVCGroup = { mvcId, map ->
onCreateMVCGroup = { mvcId ->
}

onDestroyMVCGroup = { mvcId ->
}
