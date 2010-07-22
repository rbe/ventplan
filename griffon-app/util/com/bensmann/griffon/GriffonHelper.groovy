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
 */
class GriffonHelper {
	
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
	private static final java.awt.Color MY_RED = new java.awt.Color(255, 80, 80)
	
	/**
	 * Number -> Formatted German String
	 */
	def static toString2 = { digits = 2, roundingMode = null ->
		def d = delegate
		def r = "0," + "0" * digits
		// Check against NaN, Infinity
		if (d in [Float.NaN, Double.NaN]) {
			//r = "NaN"
		} else if (d in [Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY]) {
			//r = "Inf"
		} else if (d) {
			def nf = java.text.NumberFormat.getInstance(java.util.Locale.GERMAN)
			nf.minimumFractionDigits = digits
			nf.maximumFractionDigits = digits
			if (roundingMode) nf.roundingMode = roundingMode ?: GriffonHelper.ROUNDING_MODE
			r = nf.format(d)
		}
		//println "toString2(): ${d?.dump()} -> ${r?.dump()}"
		r
	}
	
	/**
	 * Parse a string with german notation to a float value
	 */
	def static toDouble2 = { digits = 2, roundingMode = null ->
		def d = delegate
		def r = 0.0d
		// Stop in case of we got a float/double
		if (d.class in [Float, Double]) {
			return d
		}
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
				e.printStackTrace()
			}
		}
		//println "toDouble2(): ${d?.dump()} -> ${r?.dump()}"
		r
	}
	
	/**
	 * Map.flatten
	 */
	def installMapFlatten = {
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
	 * Recursively add PropertyChangeListener to the map itself and all nested maps.
	 */
	def static addMapPropertyChangeListener = { name, map, closure = {} ->
		// This map
		println "addMapPropertyChangeListener: adding PropertyChangeListener for ${name}"
		map.addPropertyChangeListener({ evt ->
				println "C! ${name}.${evt.propertyName}: ${evt.oldValue?.dump()} -> ${evt.newValue?.dump()}"
				if (closure) closure(evt)
			} as java.beans.PropertyChangeListener)
		// All nested maps
		map.each { k, v ->
			if (v instanceof ObservableMap) {
				/*
				println "addMapPropertyChangeListener 2: adding PropertyChangeListener for ${name}.${k} (${v})"
				v.addPropertyChangeListener({ evt ->
						println "C! ${name}.${k}.${evt.propertyName}: ${evt.oldValue?.dump()} -> ${evt.newValue?.dump()}"
						closure()
					} as java.beans.PropertyChangeListener)
				*/
				GriffonHelper.addMapPropertyChangeListener("${name}.${k}", v, closure)
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
	 * Show number with 2 fraction digits
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
	 * Convert number to rounded value, shown with 2 fraction digits
	 */
	def static toString2Round5Converter = { v ->
		if (v && v instanceof Number) {
			def v2 = round5(v).toString2()
			//println "toString2Converter: ${v?.dump()} -> ${v2?.dump()}"
			v2
		} else if (v) {
			throw new IllegalStateException("toString2Converter: You tried to convert a String to a String: ${v?.dump()}")
		}
	}
	
	/**
	 * Show number with 3 fraction digits
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
	
	/**
	 * Wrap text in HTML and substitute every space character with HTML-breaks.
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
	def static tieEventListener = { me, klass, props = [:] ->
		println "tieEventListener: setting up eventlistener relationship with ${klass}"
		def el = klass.newInstance(props)
		me.addEventListener(el)
		el.addEventListener(me)
	}
	
	/**
	 * Show a dialog.
	 */
	def static showDialog = { builder, dialogClass, dialogProp = [:] ->
		def dialog = dialogCache[dialogClass]
		//if (!dialog) {
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
			//dialogCache[dialogClass] = dialog
		//}
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
		// Save existing ListSelectionListener(s)
		def lsl = lsm.listSelectionListeners
		lsl.each {
				// Will throw UnsupportedOperationException! println "withDisabledListSelectionListeners: removing ${it}"
				lsm.removeListSelectionListener(it)
			}
		// Execute closure
		closure.delegate = table
		closure()
		// Re-add ListSelectionListener(s)
		lsl.each {
				// Will throw UnsupportedOperationException! println "withDisabledListSelectionListeners: re-adding ${it}"
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
			println "withDisabledActionListener: removing ${it}"
			component.removeActionListener(it)
		}
		// Execute closure
		closure.delegate = component
		closure()
		// Re-add ActionListener(s)
		actionListeners.each {
				println "withDisabledActionListener: re-adding ${it}"
				component.addActionListener(it)
			}
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
				//println "getValuesFromView: JTextField: ${k} -> ${v.text}"
				map["${k}"] = v.text
			} else if (v instanceof javax.swing.JComboBox) {
				//println "getValuesFromView: JComboBox: ${k} -> ${v.selectedItem}"
				map["${k}"] = v.selectedItem
			}
		}
		map
	}

    // Leeres TableModel initialisieren.
    def static initTableModelBuilder = { builder, model ->
        def items1 = model.meta.zuluftventile
        def items2 = model.meta.abluftventile
        def items3 = [' 1', ' 2', ' 3', ' 4']

        def editor1 = new javax.swing.DefaultCellEditor(builder.comboBox(id: 'raumBezeichnungZuluftventileCombo', items: items1))
        def editor2 = new javax.swing.DefaultCellEditor(builder.comboBox(id: 'raumBezeichnungAbluftventileCombo', items: items2))
        def editor3 = new javax.swing.DefaultCellEditor(builder.comboBox(id: 'raumVentilebeneCombo', items: items3))

        def tableModel = builder.tableModel() {
            propertyColumn(header: 'Raum', propertyName: 'raumBezeichnung')
            propertyColumn(header: 'Luftart', propertyName: 'raumLuftart')
            propertyColumn(header: GriffonHelper.ws("Raumvolumen<br/>(m³)"), propertyName: 'raumVolumen')
            propertyColumn(header: GriffonHelper.ws("Luftwechsel<br/>(1/h)"), propertyName: 'raumLuftwechsel')
            propertyColumn(header: GriffonHelper.ws("Anzahl<br/>Abluftventile"), propertyName: "raumBezeichnungAbluftventile")
            propertyColumn(header: GriffonHelper.ws("Abluftmenge<br/>je Ventil"), propertyName: "raumAnzahlAbluftventile")
            propertyColumn(header: GriffonHelper.ws("Volumenstrom<br/>(m³/h)"), propertyName: 'raumVolumenstrom')
            propertyColumn(header: GriffonHelper.ws("Anzahl<br/>Zuluftventile"), propertyName: 'raumAnzahlZuluftventile')
            propertyColumn(header: GriffonHelper.ws("Bezeichnung<br/>Zuluftventile"),
                propertyName: 'raumBezeichnungZuluftventileCombo',
                cellEditor: editor1,
                cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
            ) // combo
            propertyColumn(header: GriffonHelper.ws("Bezeichnung<br/>Abluftventile"),
                propertyName: 'raumBezeichnungAbluftventileCombo',
                cellEditor: editor2,
                cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
            ) // combo
            propertyColumn(header: "Ventilebene",
                propertyName: 'raumVentilebeneCombo',
                cellEditor: editor3,
                cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
            ) // combo
            propertyColumn(header: GriffonHelper.ws("Zuluftmenge<br/>je Ventil"), propertyName: 'raumZuluftmengeJeVentil')
        }
        tableModel
    }

    // TableModel updaten. Neue Row hinzufügen.
    def static addRowToTableModel = { r, builder, raumVsZuAbluftventileTabelle ->
        println "addRowToTableModel ${r}}"
        def dataList = createDataList(r)

        // Let's add a new row to the table
        def rows = raumVsZuAbluftventileTabelle.getModel().getRowsModel().getValue()
        rows.add( dataList )
        raumVsZuAbluftventileTabelle.getModel().getRowsModel().setValue( rows )
        raumVsZuAbluftventileTabelle.getModel().fireTableDataChanged()
    }

    // TableModel updaten. Row löschen!
    def static removeRowToTableModel = { r, builder, raumVsZuAbluftventileTabelle ->
        println "removeRowToTableModel"
        def dataList = createDataList(r)

        // Let's remove a row from the table
        def rows = raumVsZuAbluftventileTabelle.getModel().getRowsModel().getValue()
        rows.remove( dataList )
        raumVsZuAbluftventileTabelle.getModel().getRowsModel().setValue( rows )
        raumVsZuAbluftventileTabelle.getModel().fireTableDataChanged()
    }

    // Erstelle eine Liste mit den aktuellen Raumdaten
    def static createDataList = {r ->
        def dataList = [raumBezeichnung: r.raumBezeichnung,
                        raumLuftart: r.raumLuftart,
                        raumVolumen: r.raumVolumen,
                        raumLuftwechsel: r.raumLuftwechsel,
                        raumBezeichnungAbluftventile: r.raumBezeichnungAbluftventile,
                        raumAnzahlAbluftventile: r.raumAnzahlAbluftventile,
                        raumVolumenstrom: r.raumVolumenstrom,
                        raumAnzahlZuluftventile: r.raumAnzahlZuluftventile,
                        raumBezeichnungZuluftventileCombo: r.raumBezeichnungZuluftventileCombo,
                        raumBezeichnungAbluftventileCombo: r.raumBezeichnungAbluftventileCombo,
                        raumVentilebeneCombo: r.raumVentilebeneCombo,
                        raumZuluftmengeJeVentil: r.raumZuluftmengeJeVentil]
         dataList
    }
}
