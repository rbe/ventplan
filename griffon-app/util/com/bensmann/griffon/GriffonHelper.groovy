/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/util/com/bensmann/griffon/GriffonHelper.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.bensmann.griffon

/**
 * Several helpers for Griffon.
 */
class GriffonHelper {
	
	/**
	 * Cache for created dialog instances.
	 */
	private static dialogCache = [:]
	
	/**
	 * Colors.
	 */
	private static final java.awt.Color MY_YELLOW = new java.awt.Color(255, 255, 180)
	private static final java.awt.Color MY_RED = new java.awt.Color(255, 80, 80)
	
	/**
	 * Establish private EventPublisher relationship between two classes.
	 */
	def static tieEventListener = { me, klass, props = [:] ->
		println "tieEventListener: setting up eventlistener relationship with ${klass}"
		def el = klass.newInstance(props)
		me.addEventListener(el)
		el.addEventListener(me)
	}
	
	/**
	 * Dump a change.
	 */
	def static dumpPropertyChange = { name, evt, k ->
		println "${name}.${k}.${evt.propertyName}: value changed: ${evt.oldValue?.dump()} -> ${evt.newValue?.dump()}"
	}
	
	/**
	 * Recursively add PropertyChangeListener to the map itself and all nested maps.
	 */
	def static addMapPropertyChange = { name, map, closure = {} ->
		map.each { k, v ->
			if (v instanceof ObservableMap) {
				//println "addMapPropertyChange: adding PropertyChangeListener to ${name} for ${k}"
				v.addPropertyChangeListener({ evt ->
					GriffonHelper.dumpPropertyChange.delegate = v
					GriffonHelper.dumpPropertyChange(name, evt, k)
					closure()
				} as java.beans.PropertyChangeListener)
				GriffonHelper.addMapPropertyChange(name, v)
			}
		}
	}
	
	/**
	 * Show a dialog.
	 */
	def static showDialog = { builder, dialogClass, dialogProp = [:] ->
		def dialog = dialogCache[dialogClass]
		if (!dialog) {
			// Properties for dialog
			def prop = [
					title: "Ein Dialog",
					visible: false,
					modal: true,
					pack: true,
					locationByPlatform: true
				] + dialogProp
			// Create dialog instance
			dialog = builder.dialog(prop) {
					build(dialogClass)
				}
			// Cache dialog instance
			dialogCache[dialogClass] = dialog
		}
		// Show dialog
		dialog.show()
		// Return dialog instance
		dialog
	}
	
	/**
	 * Check row to select in a table.
	 */
	def static checkRow = { row, table ->
		//println "checkRow: row=${row}, table=${table}"
		if (0 <= row && row < table.rowCount) return row
		else if (row < 0) return 0
		else if (row >= table.rowCount) return table.rowCount - 1
	}
	
	/**
	 * Execute code with disabled ListSelectionListeners on a table.
	 * The given table is set as the delegate for the closure.
	 */
	def static withDisabledListSelectionListeners = { table, closure ->
		def lsm = table.selectionModel
		// Save existing ListSelectionListeners
		def lsl = lsm.listSelectionListeners
		lsl.each { lsm.removeListSelectionListener(it) }
		// Execute closure
		closure.delegate = table
		closure()
		// Re-add ListSelectionListeners
		lsl.each { lsm.addListSelectionListener(it) }
		// Repaint, as default decorator is removed
		table.repaint()
	}
	
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
	 * Is a double value in a component 'empty'?
	 * Yes for two cases:
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
	 * Set behaviour for Double-TextFields.
	 */
	def static doubleTextField = { component ->
		//println "doubleTextField: ${component.class}"
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
							//println "doubleTextField: selecting all: component.text = " + component.text + " -> isEmptyDouble=" + isEmptyDouble(component)
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
	 * Auto-format a Double-textfield when focus is lost.
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
			//println "toString2Converter: ${v?.dump()} -> ${v2?.dump()}"
			v2
		} else if (v) {
			throw new IllegalStateException("toString2Converter: You tried to convert a String to a String: ${v?.dump()}")
		}
	}
	
	/**
	 * 
	 */
	def static toString3Converter = { v ->
		if (v instanceof Number) {
			def v3 = v?.toString2(3)
			//println "toString3Converter: ${v?.dump()} -> ${v3?.dump()}"
			v3
		} else if (v) {
			throw new IllegalStateException("toString3Converter: You tried to convert a String to a String: ${v?.dump()}")
		}
	}
	
}
