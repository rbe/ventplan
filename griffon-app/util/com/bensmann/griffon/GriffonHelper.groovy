package com.bensmann.griffon

/**
 * 
 */
class GriffonHelper {
	
	/**
	 * 
	 */
	private static final java.awt.Color MY_YELLOW = new java.awt.Color(255, 255, 180)
	
	/**
	 * Apply a closure to a component or recurse component's components.
	 */
	def static recurse(component, closure) {
		//println "recurseComponent: ${component.class}"
		if (component instanceof java.awt.Container) {
			component.components.each { recurse(it, closure) }
		}
		try {
			closure(component)
		} catch (e) {
			println "recurse(${component}): EXCEPTION=${e}"
		}
	}
	
	/**
	 * Intelligently parse a float.
	 */
	def static parseFloat = { f ->
		def r = 0.0f
		if (f) {
			f = f.replaceAll(",", ".")
			//try {
				r = Float.parseFloat(f)
			//} catch (e) {
			//	println "pf(${f.inspect()}): EXCEPTION=${e}"
			//}
		}
		r
	}
	
	/**
	 * Get a float as formatted text.
	 */
	def static setFloat = { f ->
		def r = 0.0f
		if (f) {
			//try {
				r = java.text.NumberFormat.getInstance(java.util.Locale.GERMAN).with {
					minimumFractionDigits = 2
					maximumFractionDigits = 2
					format(f)
				}
			//} catch (e) {
			//	println "sf(${f.inspect()}): EXCEPTION=${e}"
			//}
		}
		r
	}
	
	/**
	 * Composition: sf o pf.
	 */
	def static spf = { f -> sf(pf(f)) }
	
	/**
	 * 
	 */
	def static floatTextField = { component ->
		//println "floatTextField: ${component.class}"
		if (component instanceof javax.swing.JTextField) {
			component.addFocusListener({ evt ->
				if (evt.id == java.awt.event.FocusEvent.FOCUS_LOST) {
					if (component.text) {
						try {
							def f = Float.parseFloat(component.text?.replaceAll(",", "."))
							component.text = java.text.NumberFormat.getInstance(java.util.Locale.GERMAN).with {
								minimumFractionDigits = 2
								format(f)
							}
						} catch (e) {
							println "floatTextField: component=${component}): EXCEPTION=${e}"
						}
					}
				}
			} as java.awt.event.FocusListener)
		}
	}
	
	/**
	 * Set textfield to have a yellow background when focused.
	 */
	def static yellowTextField = { component ->
		//println "yellowTextField: ${component.class}"
		if (component instanceof javax.swing.JTextField && component.editable) {
			component.addFocusListener({ evt ->
				component.background = (evt.id == java.awt.event.FocusEvent.FOCUS_GAINED ? MY_YELLOW : java.awt.Color.WHITE)
			} as java.awt.event.FocusListener)
		}
	}
	
	/**
	 * Right align text.
	 */
	def static rightAlignTextField = { component ->
		//println "rightAlignTextField: ${component.class}"
		if (component instanceof javax.swing.JTextField) {
			component.horizontalAlignment = javax.swing.JTextField.RIGHT
		}
	}
	
	/**
	 * Get all values from components as a map.
	 */
	def static getValuesFromView = { view, prefix ->
		def map = [:]
		view.binding.variables.each { k, v ->
			if (k.startsWith(prefix)) {
				if (v instanceof javax.swing.JTextField) {
					//println "getValuesFromView: JTextField: ${k}"
					map["${k}"] = v.text
				} else if (v instanceof javax.swing.JComboBox) {
					//println "getValuesFromView: JComboBox: ${k}"
					map["${k}"] = v.selectedItem
				}
			}
		}
		map
	}
	
}
