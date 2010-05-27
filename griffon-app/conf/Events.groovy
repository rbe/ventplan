import com.westaflex.wac.*

onBootstrapEnd = { app ->
	println "onBootstrapEnd: start"
	// DataSource
	def dataSource = new ConfigSlurper().parse(DataSource).dataSource
	WacModelService.instance.initDataSource(dataSource)
	//
	def toString2 = { digits = 2 ->
		def d = delegate
		def r = "0," + "0" * digits
		if (d) {
			r = java.text.NumberFormat.getInstance(java.util.Locale.GERMAN).with {
				minimumFractionDigits = digits
				maximumFractionDigits = digits
				roundingMode = java.math.RoundingMode.HALF_UP
				format(d)
			}
		}
		//println "toString2(): ${d} -> ${r}"
		r
	}
	// Float, Double.toString2: format a float with german notation
	Integer.metaClass.toString2 = toString2
	Long.metaClass.toString2 = toString2
	Float.metaClass.toString2 = toString2
	Double.metaClass.toString2 = toString2
	BigDecimal.metaClass.toString2 = toString2
	// String.toFloat2: parse a string with german notation to a float value
	String.metaClass.toFloat2 = { digits = 2 ->
		def d = delegate
		def r = 0.0f
		if (d) {
			r = java.text.NumberFormat.getInstance(java.util.Locale.GERMAN).with {
				minimumFractionDigits = digits
				maximumFractionDigits = digits
				roundingMode = java.math.RoundingMode.HALF_UP
				parse(d) as Float
			}
		}
		//println "toFloat2(): ${d} -> ${r}"
		r
	}
	// String.multiply
	String.metaClass.multiply = { m ->
		def a = delegate.toFloat2()
		def b = m.toFloat2()
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