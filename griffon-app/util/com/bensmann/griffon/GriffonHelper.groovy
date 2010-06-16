package com.bensmann.griffon

/**
 * 
 */
class GriffonHelper {
	
	/**
	 * 
	 */
	private static final java.awt.Color MY_YELLOW = new java.awt.Color(255, 255, 180)
	private static final java.awt.Color MY_RED = new java.awt.Color(255, 80, 80)
	
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
			e.printStackTrace()
			println "recurse(${component.class}): EXCEPTION=${e}"
		}
	}
	
	/**
	 * Is a float empty? Yes for two cases:
	 * - no text
	 * - text is "0,00"
	 */
	def static isEmptyDouble(component) {
		!component.text || component.text == "0,00"
	}
	
	/**
	 * Set textfield to have a yellow background when focused.
	 */
	def static yellowTextField = { component ->
		//println "yellowTextField: ${component.class}"
		if (component instanceof javax.swing.JTextField) {
			// Set yellow background when focused
			if (component.editable) {
				component.addFocusListener({ evt ->
					component.background = (evt.id == java.awt.event.FocusEvent.FOCUS_GAINED ? MY_YELLOW : java.awt.Color.WHITE)
				} as java.awt.event.FocusListener)
			}
			// Set red background when focused
			else {
				component.addFocusListener({ evt ->
					//component.setBorder(evt.id == java.awt.event.FocusEvent.FOCUS_GAINED ? javax.swing.BorderFactory.createLineBorder(java.awt.Color.RED) : null)
					component.background = (evt.id == java.awt.event.FocusEvent.FOCUS_GAINED ? MY_RED : java.awt.Color.WHITE)
				} as java.awt.event.FocusListener)
			}
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
	 * Set behaviour for float TextFields.
	 */
	def static floatTextField = { component ->
		//println "floatTextField: ${component.class}"
		if (component instanceof javax.swing.JTextField) {
			// Set yellow background while editing
			GriffonHelper.yellowTextField(component)
			// Right align the textfield
			GriffonHelper.rightAlignTextField(component)
			// Add focus listener
			component.addFocusListener({ evt ->
				if (evt.id == java.awt.event.FocusEvent.FOCUS_GAINED) {
					// If component is editable and 'is empty', select entire contents for easy editing
					if (component.editable && isEmptyDouble(component)) {
						javax.swing.SwingUtilities.invokeLater {
							//println "floatTextField: selecting all: component.text = " + component.text + " -> isEmptyDouble=" + isEmptyDouble(component)
							component.selectAll()
						}
					}
				}
				/* Is done via binding-converter-closure now (see **Binding scripts)
				if (evt.id == java.awt.event.FocusEvent.FOCUS_LOST) {
					if (component.text) {
						javax.swing.SwingUtilities.invokeLater {
							component.text = component.text.toDouble2().toString2()
						}
					}
				}
				*/
			} as java.awt.event.FocusListener)
		}
	}
	
	/**
	 * Auto-format a float textfield when focus is lost.
	 */
	def static autoformatDoubleTextField = { component ->
		//println "autoformatDoubleTextField: ${component.class}"
		if (component instanceof javax.swing.JTextField) {
			// Right-align
			GriffonHelper.rightAlignTextField(component)
			// Add focus listener
			component.addFocusListener({ evt ->
				if (evt.id == java.awt.event.FocusEvent.FOCUS_LOST) {
					if (component.text) {
						javax.swing.SwingUtilities.invokeLater {
							component.text = component.text.toDouble2().toString2()
						}
					}
				}
			} as java.awt.event.FocusListener)
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
	
	/**
	 * 
	 */
	def static toString2Converter = { v ->
		if (v instanceof Number) {
			def v2 = v?.toString2()
			println "toStringConverter: ${v?.dump()} -> ${v2?.dump()}"
			v2
		} else if (v) {
			throw new IllegalStateException("toString2Converter: You tried to convert a String: ${v?.dump()}")
		}
	}
	
	/**
	 * 
	 */
	def static toString3Converter = { v ->
		if (v instanceof Number) {
			def v3 = v?.toString2(3)
			println "toStringConverter: ${v?.dump()} -> ${v3?.dump()}"
			v3
		} else if (v) {
			throw new IllegalStateException("toString3Converter: You tried to convert a String: ${v?.dump()}")
		}
	}
	
}
