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
	 * 
	 */
	def static floatTextField = { component ->
		//println "floatTextField: ${component.class}"
		if (component instanceof javax.swing.JTextField) {
			component.addFocusListener({ evt ->
				if (evt.id == java.awt.event.FocusEvent.FOCUS_LOST) {
					if (component.text) {
						component.text = component.text.toFloat2().toString2()
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
