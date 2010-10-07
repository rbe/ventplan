/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/conf/Events.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
import com.bensmann.griffon.GriffonHelper as GH

/**
 * 
 */
onBootstrapEnd = { app ->
	def startTime = System.currentTimeMillis()
	//println "Events.onBootstrapEnd: start"
	// Add .toDouble2 and .toString2 to all types to have a convenient API
	[Integer, Long, Float, Double, BigDecimal, String].each {
		it.metaClass.toDouble2 = GH.toDouble2
		it.metaClass.toString2 = GH.toString2
	}
	// String.multiply
	String.metaClass.multiply = { m ->
		def a = delegate.toDouble2()
		def b = m.toDouble2()
		delegate = (a * b).toString2()
	}
	// Override String.toString2
	String.metaClass.toString2 = {
		delegate
	}
	//
	def stopTime = System.currentTimeMillis()
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
onNewInstance = { clazz, type, instance ->
	// Nur Anzeigen, wenn Applikation erstmalig started (see conf/Application, startup groups)
	if (clazz.name ==~ /Wac2.*/) Wac2Splash.instance.creatingUI()
	//println "Events.onNewInstance: clazz=${clazz} type=${type} instance=${instance}"
}

/**
 * 
 */
onCreateMVCGroup = { mvcId, model, view, controller, mvcType, instances ->
	//println "Events.onCreateMVCGroup: mvcId=${mvcId}"
}

/**
 * 
 */
onDestroyMVCGroup = { mvcId ->
	//println "Events.onDestroyMVCGroup: mvcId=${mvcId}"
}
