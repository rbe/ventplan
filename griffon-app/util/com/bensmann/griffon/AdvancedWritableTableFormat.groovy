/*
 * WAC
 *
 * Copyright (C) 2005      Informationssysteme Ralf Bensmann.
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.bensmann.griffon

import javax.swing.JCheckBox
import javax.swing.JTable
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import javax.swing.event.TableModelListener
import java.awt.Component
import ca.odell.glazedlists.GlazedLists
import ca.odell.glazedlists.gui.AdvancedTableFormat
import ca.odell.glazedlists.gui.TableFormat
import ca.odell.glazedlists.gui.WritableTableFormat

/**
 * 
 */
interface AdvancedWritableTableFormat extends AdvancedTableFormat, WritableTableFormat {
	
	// WritableTableFormat
	public int getColumnCount()
	public String getColumnName(int columnIndex)
	public Object getColumnValue(Object baseObject, int column)
	public boolean isEditable(Object baseObject, int column)
	public Object setColumnValue(Object baseObject, Object editedValue, int column)
	public Object getValueAt(int rowIndex, int columnIndex)
	
	// AdvancedTableFormat
	public Class getColumnClass(int i)
	
	// AdvancedTableFormat
	public Comparator getColumnComparator(int i)
	
}
