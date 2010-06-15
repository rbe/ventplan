import com.westaflex.wac.*

onBootstrapEnd = { app ->
	println "onBootstrapEnd: start"
	// DataSource
	def dataSource = new ConfigSlurper().parse(DataSource).dataSource
	WacModelService.instance.initDataSource(dataSource)
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
	// Double, Double.toString2: format a float with german notation
	Integer.metaClass.toString2 = toString2
	Long.metaClass.toString2 = toString2
	Float.metaClass.toString2 = toString2
	Double.metaClass.toString2 = toString2
	BigDecimal.metaClass.toString2 = toString2
	// String.toDouble2: parse a string with german notation to a float value
	String.metaClass.toDouble2 = { digits = 2 ->
		def d = delegate
		def r = 0.0f
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
	println "onBootstrapEnd: finished"
}

onStartupStart = { app ->
	println "onStartupStart: start"
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
	println "onStartupStart: finished"
}