/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/util/com/bensmann/griffon/GriffonHelper.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.bensmann.griffon

import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import javax.swing.event.TableModelListener
import java.awt.Component
//import java.awt.GridLayout

/**
 * Several helpers for Griffon.
 * TODO Migrate to grootils
 */
class GriffonHelper {
	
	public static boolean DEBUG = false
	
	/**
	 * Standard rounding mode.
	 */
	public static ROUNDING_MODE = java.math.RoundingMode.HALF_UP
	
	/**
	 * Cache for created dialog instances.
	 */
	private static dialogCache = [:]
	
	/**
	 * Colors.
	 */
	private static final java.awt.Color MY_YELLOW = new java.awt.Color(255, 255, 180)
	private static final java.awt.Color MY_RED = new java.awt.Color(255, 0, 0)
	private static final java.awt.Color MY_GREEN = new java.awt.Color(51, 153, 0)
	
	/**
	 * Number -> Formatted German String
	 */
	def static toString2 = { digits = 2, roundingMode = null ->
		def d = delegate
		def r = "0,00"
		// Check against NaN, Infinity
		if (d in [Float.NaN, Double.NaN]) {
			//r = "NaN"
		} else if (d in [Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY]) {
			//r = "Inf"
		} else if (d) {
			def nf = java.text.NumberFormat.getInstance(java.util.Locale.GERMAN)
			// Use fraction digits?
			if (d instanceof Integer) {
				r = "0"
				nf.minimumFractionDigits = 0
				nf.maximumFractionDigits = 0
			} else {
				r = "0," + "0" * digits
				nf.minimumFractionDigits = digits
				nf.maximumFractionDigits = digits
				nf.roundingMode = roundingMode ?: GriffonHelper.ROUNDING_MODE
			}
			try {
				r = nf.format(d)
			} catch (e) {
				if (GriffonHelper.DEBUG) println "toString2(): Exception while converting number ${d?.dump()}: ${e}"
			}
		}
		r
	}
	
	/**
	 * Show number with 2 fraction digits
	 */
	def static toString2Converter = { v ->
		if (v && v instanceof Number) {
			v.toString2()
		} else {
			"0,00"
		}
	}
	
	/**
	 * Convert number to rounded value, shown with 2 fraction digits
	 */
	def static toString2Round5Converter = { v ->
		if (v && v instanceof Number) {
			round5(v).toString2()
		} else {
			"0,00"
		}
	}
	
	/**
	 * Show number with 3 fraction digits
	 */
	def static toString3Converter = { v ->
		if (v && v instanceof Number) {
			v.toString2(3)
		} else {
			"0,000"
		}
	}
	
	/**
	 * Parse a string with german notation to a double value.
	 */
	def static toDouble2 = { digits = 2, roundingMode = null ->
		def d = delegate
		def r = 0.0d
		// Stop in case of we got a float/double
		if (!(d.class in [java.lang.String]) || d.class in [java.lang.Float, java.lang.Double, java.math.BigDecimal]) {
			return d
		}
		// Does String contain a character?
		def charList = (["a".."z"] + ["A".."Z"]).flatten()
		if (d.any { it in charList }) return d
		// Parse number
		if (d in ["NaN", "Inf"]) {
			//r = 0.0d
		} else if (d) {
			def nf = java.text.NumberFormat.getInstance(java.util.Locale.GERMAN)
			nf.minimumFractionDigits = digits
			nf.maximumFractionDigits = digits
			if (roundingMode) nf.roundingMode = roundingMode ?: GriffonHelper.ROUNDING_MODE
			try {
				r = nf.parse(d) as Double
			} catch (e) {
				println "toDouble2: d=${delegate} digits=${digits} e=${e}"
				//e.printStackTrace()
				return d
			}
		}
		//if (GriffonHelper.DEBUG) println "toDouble2(): ${d?.dump()} -> ${r?.dump()}"
		r
	}
	
	/**
	 * Map.flatten
	 */
	def static installMapFlatten = {
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
	}
	
	/**
	 * Invert a map. Taken from http://jira.codehaus.org/browse/GROOVY-4294.
	 * http://jira.codehaus.org/secure/attachment/49994/patchfile.txt
	 */
	public static <K, V> Map<V, K> invertMap(Map<K, V> self) {
		Map<V, K> answer = new HashMap<V, K>();
		Iterator<Map.Entry<K, V>> it = self.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry entry = it.next();
			answer.put((V)entry.getValue(), (K)entry.getKey());
		}
		return answer;
	}
	
	/**
	 * Recursively add PropertyChangeListener to the map itself and all nested maps.
	 */
	def static addMapPropertyChangeListener = { name, map, closure = null ->
		// This map
		if (GriffonHelper.DEBUG) println "addMapPropertyChangeListener: adding PropertyChangeListener for ${name}"
		map.addPropertyChangeListener({ evt ->
				// TODO rbe print if debug flag is set
				if (GriffonHelper.DEBUG) println "C! ${name}.${evt.propertyName}: ${evt.oldValue?.dump()} -> ${evt.newValue?.dump()}"
				if (closure) closure(evt)
			} as java.beans.PropertyChangeListener)
		// All nested maps
		map.each { k, v ->
			if (v instanceof ObservableMap) {
				GriffonHelper.addMapPropertyChangeListener("${name}.${k}", v, closure)
			}
		}
	}
	
	/**
	 * Copy all values from a map taking nested maps into account.
	 */
	def static deepCopyMap = { m, x ->
		x.each { k, v ->
			if (v instanceof Map) {
				// TODO Create a nested map if missing? m[k] = [:] as ObservableMap
				GriffonHelper.deepCopyMap m[k], v
			} else {
				try {
					m[k] = v
				} catch (e) {
					println "deepCopyMap: else; v=$v k=$k m=$m"
					println "deepCopyMap: ${e}"
				}
			}
		}
	}
	
	/**
	 * Dezimalzahl auf 5 runden.
	 */
	def static round5(factor) {
		5.0d * (Math.round(factor / 5.0d))
	}
	
	/**
	 * Wrap text in HTML and substitute every space character with HTML-breaks.
	 * TODO Rename to wrapInHTML
	 */
	def static ws = { t, threshold = 0 ->
		def n = t
		if (threshold) {
			def i = 0
			n = t.collect { c ->
				if (i++ > threshold && c == " ") "<br/>"
				else c
			}.join()
		}
		"<html><div align=\"center\">${n}</div></html>" as String
	}
	
	/**
	 * Establish private EventPublisher relationship between two classes.
	 */
	def static tieEventListener = { Object me, Class klass, Map props = [:] ->
		//if (GriffonHelper.DEBUG) println "tieEventListener: setting up eventlistener relationship with ${klass}"
		def el = klass.newInstance(props)
		me.addEventListener(el)
		el.addEventListener(me)
	}
	
	/**
	 * Create a dialog. Please call .show() yourself as this call blocks until the dialog is closed.
	 */
	def static createDialog = { builder, dialogClass, dialogProp = [:] ->
		def dialog = dialogCache[dialogClass]
		//if (!dialog) {
			// Properties for dialog
			def prop = [
					title: "Ein Dialog",
					visible: false,
					modal: true,
					pack: false,
					locationByPlatform: true
				] + dialogProp
			// Create dialog instance
			dialog = builder.dialog(prop) {
					build(dialogClass)
				}
			// Cache dialog instance
			//dialogCache[dialogClass] = dialog
		//}
		// Return dialog instance
		dialog
	}
	
	/**
	 * Check row to select in a table.
	 */
	def static checkRow = { row, table ->
		//if (GriffonHelper.DEBUG) println "checkRow: row=${row}, table=${table}"
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
		// Save existing ListSelectionListener(s)
		def lsl = lsm.listSelectionListeners
		lsl.each {
				// Will throw UnsupportedOperationException!
				//if (GriffonHelper.DEBUG) println "withDisabledListSelectionListeners: removing ${it}"
				lsm.removeListSelectionListener(it)
			}
		// Execute closure
		closure.delegate = table
		closure()
		// Re-add ListSelectionListener(s)
		lsl.each {
				// Will throw UnsupportedOperationException!
				//if (GriffonHelper.DEBUG) println "withDisabledListSelectionListeners: re-adding ${it}"
				lsm.addListSelectionListener(it)
			}
		// Repaint, as default decorator was removed and e.g. table model has changed
		table.repaint()
	}
	
	/**
	 * Execute a closure with disabled ActionListener(s).
	 */
	def static withDisabledActionListeners = { component, closure ->
		// Save existing ActionListener(s)
		def actionListeners = component.actionListeners
		actionListeners.each {
			if (GriffonHelper.DEBUG) println "withDisabledActionListener: removing ${it}"
			component.removeActionListener(it)
		}
		// Execute closure
		closure.delegate = component
		closure()
		// Re-add ActionListener(s)
		actionListeners.each {
				if (GriffonHelper.DEBUG) println "withDisabledActionListener: re-adding ${it}"
				component.addActionListener(it)
			}
	}
	
	/**
	 * Apply a closure to a component or recurse component's components and apply closure.
	 */
	def static recurse(component, closure) {
		//if (GriffonHelper.DEBUG) println "recurseComponent: ${component.class}"
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
		//if (GriffonHelper.DEBUG) println "yellowTextField: ${component.class}"
		if (component instanceof javax.swing.JTextField) {
			// Editable: set yellow background when focused
			if (component.editable) {
				component.addFocusListener({ evt ->
					component.background = (evt.id == java.awt.event.FocusEvent.FOCUS_GAINED ? MY_YELLOW : java.awt.Color.WHITE)
				} as java.awt.event.FocusListener)
			}
			// Not editable: set red background when focused
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
		//if (GriffonHelper.DEBUG) println "rightAlignTextField: ${component.class}"
		if (component instanceof javax.swing.JTextField) {
			component.horizontalAlignment = javax.swing.JTextField.RIGHT
		}
	}
	
	/**
	 * Set behaviour for Double-TextFields:
	 * yellow background + right align, select all on focus gained
	 */
	def static doubleTextField = { component ->
		//if (GriffonHelper.DEBUG) println "doubleTextField: ${component.class}"
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
							//if (GriffonHelper.DEBUG) println "doubleTextField: selecting all: component.text = " + component.text + " -> isEmptyDouble=" + isEmptyDouble(component)
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
	 * Auto-format a Double-textfield when focus is lost:
	 * doubleTextField plus: convert value to formatted double on focus lost
	 */
	def static autoformatDoubleTextField = { component ->
		//if (GriffonHelper.DEBUG) println "autoformatDoubleTextField: ${component.class}"
		if (component instanceof javax.swing.JTextField) {
			GriffonHelper.doubleTextField(component)
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
	 * Get all values from components as a map: [view-id: value].
	 * Filter view IDs by prefix, if given.
	 */
	def static getValuesFromView = { view, prefix = null ->
		def map = [:]
		def bindings
		// Find bindings
		if (prefix) {
			bindings = view.binding.variables.findAll { k, v -> k.startsWith(prefix) }
		} else {
			bindings = view.binding.variables
		}
		// Extract values from components
		bindings.each { k, v ->
			if (v instanceof javax.swing.JTextField) {
				//if (GriffonHelper.DEBUG) println "getValuesFromView: JTextField: ${k} -> ${v.text}"
				map["${k}"] = v.text
			} else if (v instanceof javax.swing.JComboBox) {
				//if (GriffonHelper.DEBUG) println "getValuesFromView: JComboBox: ${k} -> ${v.selectedItem}"
				map["${k}"] = v.selectedItem
			}
		}
		map
	}
	
	/**
	 * 
	 */
	def static makeComboboxCellEditor = { column, list ->
		def eventList = ca.odell.glazedlists.GlazedLists.eventList(list) as ca.odell.glazedlists.EventList
		javax.swing.DefaultCellEditor cellEditor = ca.odell.glazedlists.swing.AutoCompleteSupport.createTableCellEditor(eventList)
		column.setCellEditor(cellEditor)
	}
	
}
