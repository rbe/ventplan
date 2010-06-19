/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/conf/Events.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
import com.westaflex.wac.*

onBootstrapEnd = { app ->
	def startTime = System.currentTimeMillis()
	println "Events.onBootstrapEnd: start"
	/* DataSource
	def dataSource = new ConfigSlurper().parse(DataSource).dataSource
	WacModelService.instance.initDataSource(dataSource)
	*/
	// Number -> Formatted String
	def toString2 = { digits = 2 ->
		def d = delegate
		def r = "0," + "0" * digits
		// Check against NaN, Infinity
		if (d in [Float.NaN, Double.NaN]) {
			r = "NaN"
		} else if (d in [Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY]) {
			r = "Inf"
		} else if (d) {
			r = java.text.NumberFormat.getInstance(java.util.Locale.GERMAN).with {
				minimumFractionDigits = digits
				maximumFractionDigits = digits
				//roundingMode = java.math.RoundingMode.HALF_UP
				format(d)
			}
		}
		//println "toString2(): ${d?.dump()} -> ${r?.dump()}"
		r
	}
	// Double, Double.toString2: format a float/double value with german notation
	Integer.metaClass.toString2 = toString2
	Long.metaClass.toString2 = toString2
	Float.metaClass.toString2 = toString2
	Double.metaClass.toString2 = toString2
	BigDecimal.metaClass.toString2 = toString2
	// String.toDouble2: parse a string with german notation to a float value
	String.metaClass.toDouble2 = { digits = 2 ->
		def d = delegate
		def r = 0.0d
		if (d) {
			r = java.text.NumberFormat.getInstance(java.util.Locale.GERMAN).with {
				minimumFractionDigits = digits
				maximumFractionDigits = digits
				//roundingMode = java.math.RoundingMode.HALF_EVEN
				try {
					parse(d) as Double
				} catch (e) {
					e.printStackTrace()
				}
			}
		}
		//println "toDouble2(): ${d?.dump()} -> ${r?.dump()}"
		r
	}
	// String.multiply
	String.metaClass.multiply = { m ->
		def a = delegate.toDouble2()
		def b = m.toDouble2()
		delegate = (a * b).toString2()
	}
	// String.toString2
	String.metaClass.toString2 = {
		delegate //.toString()
	}
	// Map.flatten
	Map.metaClass.flatten = { String prefix = '' ->
		delegate.inject([:]) { map, v ->
			def kstr = "${prefix${prefix ? '.' : ''}$v.key}"
			if (v.value instanceof Map) {
				map += v.value.flatten(kstr)
			} else {
				map[kstr] = v.value
			}
			map
		}
	}
	//
	def stopTime = System.currentTimeMillis()
	println "Events.onBootstrapEnd: finished in ${stopTime - startTime} ms"
}

onStartupStart = { app ->
	def startTime = System.currentTimeMillis()
	println "Events.onStartupStart: start"
	// Load data from database
	/*
	withSql { sql ->
		def tmpList = []
		sql.eachRow("SELECT * FROM persons") {
			tmpList << [id: it.id,
				name: it.name,
				lastname: it.lastname
			]
		}
		edt { model.personsList.addAll(tmpList) }
	}
	*/
	//
	def stopTime = System.currentTimeMillis()
	println "Events.onStartupStart: finished in ${stopTime - startTime} ms"
}

onStartupEnd = { app ->
	def startTime = System.currentTimeMillis()
	println "Events.onStartupEnd: start"
	//
	def stopTime = System.currentTimeMillis()
	println "Events.onStartupEnd: finished in ${stopTime - startTime} ms"
}

onReadyStart = { app ->
	def startTime = System.currentTimeMillis()
	println "Events.onReadyStart: start"
	//
	def stopTime = System.currentTimeMillis()
	println "Events.onReadyStart: finished in ${stopTime - startTime} ms"
}

onReadyEnd = { app ->
	def startTime = System.currentTimeMillis()
	println "Events.onReadyEnd: start"
	//
	def stopTime = System.currentTimeMillis()
	println "Events.onReadyEnd: finished in ${stopTime - startTime} ms"
}

onShutdownStart = { app ->
	def startTime = System.currentTimeMillis()
	println "Events.onShutdownStart: start"
	//
	def stopTime = System.currentTimeMillis()
	println "Events.onShutdownStart: finished in ${stopTime - startTime} ms"
}

onNewInstance = { clazz, type, instance ->
	println "Events.onNewInstance: clazz=${clazz} type=${type} instance=${instance}"
}

onCreateMVCGroup = { mvcId, model, view, controller, mvcType, instances ->
	println "Events.onCreateMVCGroup: mvcId=${mvcId}"
}

onDestroyMVCGroup = { mvcId ->
	println "Events.onDestroyMVCGroup: mvcId=${mvcId}"
}
