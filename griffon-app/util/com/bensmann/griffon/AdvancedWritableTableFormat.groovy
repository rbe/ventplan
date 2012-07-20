/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */
package com.bensmann.griffon

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
