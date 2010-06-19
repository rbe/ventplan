/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/services/com/westaflex/wac/WacModelService.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

import groovy.sql.*
import org.javanicus.gsql.*

/**
 * Communicate with WestaWAC database.
 */
//@Singleton(lazy = true)
class WacModelService {
	
	/**
	 * Cache MaxVolumenstrom
	 */
	def maxVolumenstrom = [:]
	
	/**
	 * Hole Liste mit Zentralgeräten (Raumvolumenströme).
	 */
	List getZentralgerat() {
		def r = withSql { sql ->
			sql.rows("SELECT Artikelnummer FROM artikelstamm WHERE Kategorie = ? AND Gesperrt = ? AND MaxVolumenstrom <> ?", [1, false, 0])
		}?.collect {
			it.Artikelnummer
		}
		println "getZentralgerat: ${r?.dump()}"
		r
	}
	
	/**
	 * Hole Volumenströme für ein bestimmtes Zentralgerät (Raumvolumenströme).
	 */
	List getVolumenstromFurZentralgerat(String artikel) {
		def r = withSql { sql ->
			sql.rows("SELECT DISTINCT Volumenstrom FROM schalleistungspegel WHERE Artikelnummer = ?", [artikel])
		}?.collect {
			it.Volumenstrom
		}
		println "getVolumenstromFurZentralgerat: ${r?.dump()}"
		r
	}
	
	/**
	 * 
	public void setGeraeteauswahl(int fLueftung) {
		String sSQL = "select Artikelnummer from artikelstamm where Kategorie = 1 and MaxVolumenstrom >= " + fLueftung;
		if (!bZentralgeraet) {
			if (lmeZentralgeraetCombobox.getItemCount() > 0) {
				String[] fVS = WestaDB.getInstance().queryDBResultList(sSQL);
				if (fVS != null && fVS.length != 0) {
					lmeZentralgeraetCombobox.setSelectedItem(fVS[0]);
				}
			}
			lmeZentralgeraetComboboxActionPerformed(new java.awt.event.ActionEvent(lmeZentralgeraetCombobox, 0, null));
			int idx = -1;
			for (int i = 0; i
					< lmeVolumenstromCombobox.getItemCount() - 1 && idx < 0; i++) {
				if (Integer.parseInt(lmeVolumenstromCombobox.getItemAt(i).toString()) >= fLueftung) {
					idx = i;
				}
			}
			lmeVolumenstromCombobox.setSelectedIndex(idx);
		}
		abZuSchallleistungspegelZuluftstutzenComboBox.setModel(lmeVolumenstromCombobox.getModel());
		abAbSchallleistungspegelAbluftstutzenComboBox.setModel(lmeVolumenstromCombobox.getModel());
	}
	 */
	
	/**
	 * 
	 */
	Integer getMaxVolumenstrom(String artikel) {
		Integer ret = 0
		// Cache?
		if (maxVolumenstrom.containsKey(artikel)) {
			ret = maxVolumenstrom[artikel]
		}
		// Lookup from database and store in cache
		else {
			def mv = withSql { sql ->
				sql.firstRow("SELECT MaxVolumenstrom FROM artikelstamm WHERE Artikelnummer = ?", [artikel])
			} as Integer
			maxVolumenstrom[artikel] = ret
		}
		ret
	}

}
