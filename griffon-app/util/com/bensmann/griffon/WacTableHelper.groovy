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

/**
 * Several helpers for Griffon.
 */
class WacTableHelper {

    /**
	 * Statischer DefaultCellEditor für raumBezeichnung Combobox
	 */
	def static raumdatenBezeichnungEditor
	def static raumdatenBezeichnungItems

	/**
	 * Statischer DefaultCellEditor für raumLuftart Combobox
	 */
	def static raumdatenLuftartEditor
	def static raumdatenLuftartItems

	/**
	 * Statischer DefaultCellEditor für raumGeschoss/Verteilebene Combobox
	 */
	def static raumdatenGeschossEditor
	def static raumdatenGeschossItems
	
	
	/**
	 * Liefert einen DefaultCellEditor für "Bezeichnung Zuluftventile" in RaumVsView
	 * für die Tabelle "raumVsZuAbluftventileTabelle" zurück
	 */
	def static getRaumVsBezeichnungZuluftventileCellEditor = { builder, model ->
		def raumVsBezeichnungZuluftventileItems = model.meta.raumVsBezeichnungZuluftventile
		def editor = new javax.swing.DefaultCellEditor(builder.comboBox(id: 'raumVsBezeichnungZuluftventileCombo', items: raumVsBezeichnungZuluftventileItems))
		editor
	}
	
	/**
	 * Liefert einen DefaultCellEditor für "Bezeichnung Abluftventile" in RaumVsView
	 * für die Tabelle "raumVsZuAbluftventileTabelle" zurück
	 */
	def static getRaumVsBezeichnungAbluftventileCellEditor = { builder, model ->
		def raumVsBezeichnungAbluftventileItems = model.meta.raumVsBezeichnungAbluftventile
		def editor = new javax.swing.DefaultCellEditor(builder.comboBox(id: 'raumVsBezeichnungAbluftventileCombo', items: raumVsBezeichnungAbluftventileItems))
		editor
	}
	
	/**
	 * Liefert einen DefaultCellEditor für "Verteilebene" in RaumVsView für die
	 * Tabelle "raumVsZuAbluftventileTabelle" zurück
	 */
	def static getRaumVsVerteilebeneCellEditor = { builder ->
		def raumVsVerteilebeneItems = ["KG", "EG", "OG", "DG", "SB"]
		//def raumVsVerteilebeneItems = model.meta.raumVsVerteilebene
		def editor = new javax.swing.DefaultCellEditor(builder.comboBox(id: 'raumVsVerteilebeneCombo', items: raumVsVerteilebeneItems))
		editor
	}
	
	/**
	 * Liefert einen DefaultCellEditor für "Überström-Element" in RaumVsView
	 * für die Tabelle zurück (raumVsUberstromventileTabelle)
	 */
	def static getRaumVsUberstromElementCellEditor = { builder, model ->
		def druckverlustEinstellungRaumItems = ["Element 1", "Element 2", "Element 3"]
		def druckverlustEinstellungRaumEditor = new javax.swing.DefaultCellEditor(builder.comboBox(id: 'raumVsUberstromElementCombo', items: druckverlustEinstellungRaumItems))
		druckverlustEinstellungRaumEditor
	}
	
	/**
	 * Liefert einen TableModelListener für die Tabelle "raumVsZuAbluftventileTabelle" zurück
	 */
	def static getRaumVsZuAbluftventileTableModelListener = {
		// Initialisiere TableModelListener
		def raumVsZuAbluftventileTableModelListener = { e ->
			println "getRaumVsZuAbluftventileTableModelListener: ${e.firstRow} ${e.column} ${e.type}"
		} as javax.swing.event.TableModelListener
		raumVsZuAbluftventileTableModelListener
	}
	
	/**
	 * Liefert einen TableModelListener für die Tabelle "raumVsUberstromventileTabelle" zurück
	 */
	def static getRaumVsUberstromTableModelListener = {
		// Initialisiere TableModelListener
		def raumVsUberstromTableModelListener = { e ->
			println "getRaumVsUberstromTableModelListener: ${e.firstRow} ${e.column} ${e.type}"
		} as javax.swing.event.TableModelListener
		raumVsUberstromTableModelListener
	}
	
	/**
	 * Liefert einen TableModelListener für die Tabelle "raumTabelle" zurück
	 */
	def static getRaumdatenTableModelListener = {
		// Initialisiere TableModelListener
		def raumdatenTableModelListener = { e ->
			println "getRaumdatenTableModelListener: ${e.firstRow} ${e.column} ${e.type}"
		} as javax.swing.event.TableModelListener
		raumdatenTableModelListener
	}

	/**
	 * Liefert einen DefaultCellEditor für "Raumbezeichnung" in RaumdatenView
	 * für die Tabelle "raumTabelle" zurück
	 */
	def static getRaumdatenBezeichnungCellEditor = { builder, model ->
		if (null == raumdatenBezeichnungEditor) {
			raumdatenBezeichnungItems = model.meta.raum.typ
			raumdatenBezeichnungEditor = new javax.swing.DefaultCellEditor(builder.comboBox(id: 'raumBezeichnungCombo', items: raumdatenBezeichnungItems))
		}
		raumdatenBezeichnungEditor
	}
	
	/**
	 * Liefert einen DefaultCellEditor für "Raumbezeichnung" in RaumdatenView
	 * für die Tabelle "raumTabelle" zurück
	 */
	def static getRaumdatenGeschossCellEditor = { builder, model ->
		if (null == raumdatenGeschossEditor) {
			raumdatenGeschossItems = model.meta.raum.geschoss
			raumdatenGeschossEditor = new javax.swing.DefaultCellEditor(builder.comboBox(id: 'raumGeschossCombo', items: raumdatenGeschossItems))
		}
		raumdatenGeschossEditor
	}
	
	/**
	 * Liefert einen DefaultCellEditor für "Raumbezeichnung" in RaumdatenView
	 * für die Tabelle "raumTabelle" zurück
	 */
	def static getRaumdatenLuftartCellEditor = { builder, model ->
		if (null == raumdatenLuftartEditor) {
			raumdatenLuftartItems = model.meta.raum.luftart
			raumdatenLuftartEditor = new javax.swing.DefaultCellEditor(builder.comboBox(id: 'raumLuftartCombo', items: raumdatenLuftartItems))
		}
		raumdatenLuftartEditor
	}
	
	/**
	 * Liefert einen TableModelListener für die Tabelle "raumTabelle" zurück
	 */
	def static getDvbKanalnetzTableModelListener = {
		// Initialisiere TableModelListener
		def dvbKanalnetzTableModelListener = { e ->
			println "getRaumdatenTableModelListener: ${e.firstRow} ${e.column} ${e.type}"
		} as javax.swing.event.TableModelListener
		dvbKanalnetzTableModelListener
	}
	
	/**
	 * Liefert einen DefaultCellEditor für "Luftart" in DruckverlustView
	 * für die Tabelle zurück (Kanalnetz)
	 */
	def static getDruckverlustLuftartEditor = { builder, model ->
		def druckverlustLuftartItems = ["ZU", "AB"]
		new javax.swing.DefaultCellEditor(builder.comboBox(id: 'dvbkLuftart', items: druckverlustLuftartItems))
	}
	
	/**
	 * Liefert einen DefaultCellEditor für "Kanalbezeichnung" in DruckverlustView
	 * für die Tabelle zurück (Kanalnetz)
	 */
	def static getDruckverlustKanalbezeichnungEditor = { builder, model ->
		def druckverlustKanalbezeichnungItems = model.meta.dvbKanalbezeichnung
		new javax.swing.DefaultCellEditor(builder.comboBox(id: 'kanalbezeichnung', items: druckverlustKanalbezeichnungItems))
	}
	
	/**
	 * Liefert einen DefaultCellEditor für "Luftart" in DruckverlustView
	 * für die Tabelle zurück (Ventileinstellung)
	 */
	def static getDruckverlustEinstellungLuftartEditor = { builder, model ->
		def druckverlustEinstellungLuftartItems = ["ZU", "AB", "AU", "FO"]
		new javax.swing.DefaultCellEditor(builder.comboBox(id: 'dvbvLuftart', items: druckverlustEinstellungLuftartItems))
	}
	
	/**
	 * Liefert einen DefaultCellEditor für "Raum" in DruckverlustView
	 * für die Tabelle zurück (Ventileinstellung)
	 */
	def static getDruckverlustEinstellungRaumEditor = { builder, model ->
		def druckverlustEinstellungRaumItems = model.meta.raum.typ
		new javax.swing.DefaultCellEditor(builder.comboBox(id: 'dvbvRaum', items: druckverlustEinstellungRaumItems))
	}

    /**
	 * Liefert einen DefaultCellEditor für "Ventilbezeichnung" in DruckverlustView
	 * für die Tabelle zurück (Ventileinstellung)
	 */
    def static getDruckverlustEinstellungVentilbezeichnungEditor = { builder, model ->
		def druckverlustVentilbezeichnungItems = model.meta.dvbVentileinstellung
		new javax.swing.DefaultCellEditor(builder.comboBox(id: 'ventilbezeichnung', items: druckverlustVentilbezeichnungItems))
    }
	
}
