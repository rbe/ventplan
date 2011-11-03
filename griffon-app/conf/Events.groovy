/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/conf/Events.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
import com.bensmann.griffon.GriffonHelper as GH
import javax.swing.JOptionPane

/**
 * 
 */
onBootstrapEnd = { app ->
    
    app.config.shutdown.proceed = false
    
	def startTime = System.currentTimeMillis()
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
	//
	def stopTime = System.currentTimeMillis()
    
    
    app.addShutdownHandler([
        canShutdown: { a ->
            app.config.shutdown.proceed = app.controllers["wac2"].exitApplication(a)
            /*def choice = app.controllers["Dialog"].showCloseProjectDialog()
            switch (choice) {
                case 0: // Save: save and close project
                	println "projektSchliessen: save and close"
                	controller.aktivesProjektSpeichern(evt)
                	//clacpr(mvc)
                	app.config.shutdown.proceed = true
                    break
                case 1: // Cancel: do nothing...
                    println "projektSchliessen: cancel"
                	app.config.shutdown.proceed = false
                    break
                case 2: // Close: just close the tab...
                	println "projektSchliessen: close without save"
               		//clacpr(mvc)
                	app.config.shutdown.proceed = true
                    break
            }*/
            return app.config.shutdown.proceed
        },
        onShutdown: { a -> }
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
	if (clazz.name ==~ /Wac2.*/) Wac2Splash.instance.creatingUI()
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
