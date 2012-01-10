/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/util/com/bensmann/griffon/GriffonHelper.groovy
 * Last modified at 09.03.2011 17:46:39 by rbe
 */
package com.bensmann.griffon

import com.westaflex.wac.*
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import javax.swing.event.TableModelListener
import java.awt.Component
//import java.awt.GridLayout
import griffon.transform.Threading

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
     * Key codes: Backspace, Comma, 0 .. 9.
     */
    def static final NUMBER_KEY_CODES =  48..57 //(([8, 44] as List) << (48..57 as Integer[])).flatten() as Integer[]

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
				println "toString2(): Exception while converting number ${d?.dump()} to string: ${e}"
			}
		}
        ////println "toString2: ${d?.dump()} -> ${r?.dump()}"
		r
	}
	
	/**
	 * Show number with 2 fraction digits
	 */
	def static toString0Converter = { v ->
		if ((v && v instanceof Number) || (v instanceof String && v.isNumber())) {
			v.toString2(0)
		} else {
			"0"
		}
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
        // Null?
        if (d == null) return r
		// Stop in case of we got a float/double
		if (!(d.getClass() in [java.lang.String]) || d.getClass() in [java.lang.Float, java.lang.Double, java.math.BigDecimal]) {
			return d
		}
		// Does String contain a character?
		def charList = (["a".."z"] + ["A".."Z"]).flatten()
		if (d.any { it in charList }) return d
        // TODO HACK: Convert dot into comma (we expect german format, not english)
        d = d.collect { it == "." ? "," : it }.join()
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
				println "toDouble2: Exception while converting string ${d?.dump()} to double: d=${delegate} digits=${digits} e=${e}"
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
	 * @param m Destination map.
	 * @param x Source map.
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
					if (DEBUG) {
						println "deepCopyMap: else; v=$v k=$k m=$m"
                        println "deepCopyMap: ${e}"
					}
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
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
	def static createDialog = { builder, dialogClass, dialogProp = [:] -> 
        //javax.swing.SwingUtilities.invokeLater {
            def dialog// = dialogCache[dialogClass]
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
                //println "createDialog -> builder=${builder?.dump()}"
                
                
                dialog = builder.dialog(prop) {
                    jideScrollPane() {
                        build(dialogClass)
                    }
                }
                // Cache dialog instance
                //dialogCache[dialogClass] = dialog
            //}
            // Return dialog instance
            dialog
        //}
	}
    
    /**
	 * Create a dialog. Please call .show() yourself as this call blocks until the dialog is closed.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
	def static createDialogNoScrollPane = { builder, dialogClass, dialogProp = [:] ->
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
        //javax.swing.SwingUtilities.invokeLater {
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
        //}
	}
	
	/**
	 * Execute a closure with disabled ActionListener(s).
	 */
	def static withDisabledKeyListeners = { component, closure ->
		// Save existing KeyListener(s)
		def keyListeners = component.keyListeners
		keyListeners.each {
			if (GriffonHelper.DEBUG) println "withDisabledKeyListeners: removing ${it}"
			component.removeKeyListener(it)
		}
		// Execute closure
		closure.delegate = component
		closure()
		// Re-add ActionListener(s)
		keyListeners.each {
				if (GriffonHelper.DEBUG) println "withDisabledKeyListeners: re-adding ${it}"
				component.addKeyListener(it)
			}
	}
	
	/**
	 * Apply a closure to a component (JTextField/JTextArea) or recurse component's components and apply closure.
	 */
	def static recurse(component, closure) {
		//if (GriffonHelper.DEBUG) println "recurse: ${component.getClass()} ${component instanceof javax.swing.JTextField} ${component instanceof javax.swing.JPanel}"
        if (component instanceof javax.swing.JTextField || component instanceof javax.swing.JTextArea || component instanceof javax.swing.JComboBox) {
            try {
                closure(component)
            } catch (e) {
                println "recurse(${component.class}): EXCEPTION=${e}"
                e.printStackTrace()
            }
        } else if (component instanceof javax.swing.JPanel || component instanceof javax.swing.JTabbedPane) { /*java.awt.Container*/
			component.components.each { GriffonHelper.recurse(it, closure) }
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
	 * Select all when textfield is focussed.
	 */
	def static selectAllTextField = { component ->
		// Add focus listener
		component.addFocusListener({ evt ->
			if (evt.id == java.awt.event.FocusEvent.FOCUS_GAINED) {
				// If component is editable and 'is empty', select entire contents for easy editing
				if (component.editable && isEmptyDouble(component)) {
					//javax.swing.SwingUtilities.invokeLater {
						if (GriffonHelper.DEBUG) println "selectAllTextField: selecting all: component.text = " + component.text + " -> isEmptyDouble=" + isEmptyDouble(component)
						GriffonHelper.withDisabledKeyListeners component, { component.selectAll() }
					//}
				}
			}
		} as java.awt.event.FocusListener)
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
			// Select all
			GriffonHelper.selectAllTextField(component)
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
	 * Make a CellEditor with a ComboBox for GlazedLists.
	 */
	static void makeComboboxCellEditor(column, list) {
        javax.swing.SwingUtilities.invokeLater {
            def eventList = ca.odell.glazedlists.GlazedLists.eventList(list) as ca.odell.glazedlists.EventList
            def threadEventList = ca.odell.glazedlists.GlazedLists.threadSafeList(eventList)
            javax.swing.DefaultCellEditor cellEditor = ca.odell.glazedlists.swing.AutoCompleteSupport.createTableCellEditor(threadEventList)
            column.setCellEditor(cellEditor)
        }
	}

    /**
     * Install a key listener/adapter: execute code when a key was released.
     * @param closure Takes one argument: evt
     */
    def static installKeyAdapter = { component, keyCodes = null, closure = null ->
        component.addKeyListener(
            [
                keyReleased: { evt ->
                    // Calculate if: closure is not null and (if no keycodes given or certain keys are pressed)
                    //println evt.keyCode
                    if (closure && (!keyCodes || (keyCodes && evt.keyCode in keyCodes))) {
                        //println "installKeyAdapter: executing"
                        closure(evt)
                    }
                }
            ] as java.awt.event.KeyAdapter)
    }
	
    /**
     * Install a focus listener/adapter: execute code when focus is lost.
     * @param closure Takes one argument: evt
     */
    def static installFocusLostAdapter = { component, closure = null ->
        component.addFocusListener(
            [
                focusLost: { evt ->
                    // Calculate if focus lost
                    if (closure) {
                        //println "installFocusLostAdapter: executing"
                        closure(evt)
                    }
                }
            ] as java.awt.event.FocusAdapter)
    }

    /**
     * Execute code on change of component:
     *   textField: focusLost, keyReleased
     *   combobox: stateChanged
     */
    def static onChange = { component, keyCodes = null, closure = null ->
        if (closure) {
            switch (component) {
                case { it instanceof javax.swing.JTextArea || it instanceof javax.swing.JTextField }:
                    GriffonHelper.installKeyAdapter(component, keyCodes, closure)
                    break
                case { it instanceof javax.swing.JComboBox || it instanceof javax.swing.JRadioButton}:
                    component.addActionListener(
                        [
                            actionPerformed: { evt ->
                                closure(evt)
                            }
                        ] as java.awt.event.ActionListener)
                    break
            }
        }
    }

    /**
     * Returns the current installed version.
     */
    def static localVersion = {
        Properties properties = new Properties()
        def p = Wac2Resource.getConfVersion()
        properties.load(p)
        return properties.get("version") as String
    }

    /**
     * 
     */
    def static getWacwsUrl = {
        Properties properties = new Properties()
        def p = Wac2Resource.getWacwsProperties()
        properties.load(p)
        return properties.get("webservice.url") as String
        //return "http://localhost:8080/wacws/services/wpxUpload?wsdl"
    }
    
    
    /**
     * 
     */
    def static getOdiseeRestUrl = {
        Properties properties = new Properties()
        def p = Wac2Resource.getWacwsProperties()
        properties.load(p)
        return properties.get("odisee.rest.url") as String
        //return "http://localhost:8080/wacws/services/wpxUpload?wsdl"
    }
    
    
    /**
     * 
     */
    def static getOdiseeRestPath = {
        Properties properties = new Properties()
        def p = Wac2Resource.getWacwsProperties()
        properties.load(p)
        return properties.get("odisee.rest.path") as String
        //return "http://localhost:8080/wacws/services/wpxUpload?wsdl"
    }
    
}
