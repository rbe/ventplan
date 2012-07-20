/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/7/12 3:42 PM
 */

import com.bensmann.griffon.GriffonHelper as GH

/**
 *
 */
onBootstrapEnd = { app ->
    app.config.shutdown.proceed = false
    //def startTime = System.currentTimeMillis()
    //println "Events.onBootstrapEnd: start"
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
    //def stopTime = System.currentTimeMillis()
    // WAC-8: shutdown handler to abort application closing
    app.addShutdownHandler([
            canShutdown: { a ->
                app.config.shutdown.proceed = app.controllers['MainFrame'].canExitApplication(a)
                //println "addShutdownHandler: canShutdown"
                return app.config.shutdown.proceed
            },
            onShutdown: { a ->
                //println "addShutdownHandler: onShutdown"
            }
    ] as griffon.core.ShutdownHandler)
    //println "Events.onBootstrapEnd: finished in ${stopTime - startTime} ms"
}

onStartupStart = { app ->
    /*
     def startTime = System.currentTimeMillis()
     //println "Events.onStartupStart: start"
     //
     def stopTime = System.currentTimeMillis()
     //println "Events.onStartupStart: finished in ${stopTime - startTime} ms"
     */
}

/**
 *
 */
onStartupEnd = { app ->
    /*
     def startTime = System.currentTimeMillis()
     //println "Events.onStartupEnd: start"
     //
     def stopTime = System.currentTimeMillis()
     //println "Events.onStartupEnd: finished in ${stopTime - startTime} ms"
     */
}

/**
 *
 */
onReadyStart = { app ->
    /*
     def startTime = System.currentTimeMillis()
     //println "Events.onReadyStart: start"
     //
     def stopTime = System.currentTimeMillis()
     //println "Events.onReadyStart: finished in ${stopTime - startTime} ms"
     */
}

/**
 *
 */
onReadyEnd = { app ->
    /*
     def startTime = System.currentTimeMillis()
     //println "Events.onReadyEnd: start"
     //
     def stopTime = System.currentTimeMillis()
     //println "Events.onReadyEnd: finished in ${stopTime - startTime} ms"
     */
}

/**
 *
 */
onShutdownStart = { app ->
    def startTime = System.currentTimeMillis()
    //println "Events.onShutdownStart: start"
    //
    def stopTime = System.currentTimeMillis()
    //println "Events.onShutdownStart: finished in ${stopTime - startTime} ms"
}

/**
 *
 */
onShutdownAbort = { app ->
    app.config.shutdown.proceed = false
}

/**
 *
 */
onNewInstance = { clazz, type, instance ->
    // Nur Anzeigen, wenn Applikation erstmalig started (see conf/Application, startup groups)
    //println "Events.onNewInstance: clazz=${clazz} type=${type} instance=${instance}"
    if (clazz.name ==~ /Wac2.*/)
        Wac2Splash.instance.creatingUI()
    //println "Events.onNewInstance: clazz=${clazz} type=${type} instance=${instance}"
}

/**
 *
 */
//onCreateMVCGroup = { mvcId, map ->
onCreateMVCGroup = { mvcId ->
    //println "Events.onCreateMVCGroup: mvcId=${mvcId.dump()}"
}

/**
 *
 */
onDestroyMVCGroup = { mvcId ->
    //println "Events.onDestroyMVCGroup: mvcId=${mvcId}"
}
