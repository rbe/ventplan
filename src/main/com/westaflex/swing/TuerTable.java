/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.westaflex.swing;

import com.bensmann.superswing.component.util.JTableUtil;
import com.westaflex.component.classes.ContentAware;
import com.westaflex.component.classes.RaumItem;
import com.westaflex.component.classes.Tuer;
import java.util.Vector;
import javax.swing.JTable;

/**
 *
 * @author seebass
 */
public class TuerTable extends JTable implements ContentAware {

    private RaumItem.PROP prop = null;

    @Override
    public Vector<Tuer> getValue() {

        Vector<Tuer> vt = new Vector<Tuer>(getRowCount());
        for (int i = 0; i < getRowCount(); i++) {
            Tuer t = new Tuer();
            t.setBezeichnung((String) getValueAt(i, 0));
            if (!t.equals(null)) {
                t.setBreite((Integer) JTableUtil.parseIntegerFromTableCell(this, i, 1));
            } else {
                t.setBreite(0);
            }
            t.setQuerschnittsflaeche(JTableUtil.parseFloatFromTableCell(this, i, 2));
            t.setSpalthoehe(JTableUtil.parseFloatFromTableCell(this, i, 3));
            t.setDichtung((Boolean) getValueAt(i, 4));
            vt.add(t);
        }
        return vt;
    }

    @Override
    public String getString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                Object o = getValueAt(i, j);
                sb.append((o != null ? o : "") + ";");
            }
        }
        if (sb.length() > -1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public void setValue(Object value) {
        int rc = 0;
        // "
        // tür1;610.0;0.0;0.0;true;
        // tür2;1110 ;   ;   ;true;
        //     ;     ;   ;   ;true;
        //     ;     ;   ;   ;true
        // "
        if (value instanceof String) {
            String[] cont = ((String) value).split(";");
            for (int i = 0; i < cont.length; i += 5) {
                setValueAt(cont[i + 0], rc, 0);
                setValueAt((cont[i + 1].isEmpty() ? 0 : Float.parseFloat(cont[i + 1])), rc, 1);
                setValueAt((cont[i + 2].isEmpty() ? 0 : Float.parseFloat(cont[i + 2])), rc, 2);
                setValueAt((cont[i + 3].isEmpty() ? 0 : Float.parseFloat(cont[i + 3])), rc, 3);
                setValueAt((cont[i + 4].isEmpty() ? false : Boolean.parseBoolean(cont[i + 4])), rc, 4);
                rc++;
            }
        } else {
            Vector<Tuer> vec = (Vector<Tuer>) value;
            if (vec != null) {
                for (Tuer tuer : vec) {
                    setValueAt(tuer.getBezeichnung(), rc, 0);
                    setValueAt(tuer.getBreite(), rc, 1);
                    setValueAt(tuer.getQuerschnittsflaeche(), rc, 2);
                    setValueAt(tuer.getSpalthoehe(), rc, 3);
                    setValueAt(tuer.getDichtung(), rc, 4);
                    rc++;
                }
            }
        }
    }

    @Override
    public void setRaumItemProp(RaumItem.PROP prop) {
        this.prop = prop;
    }

    @Override
    public RaumItem.PROP getRaumItemProp() {
        return this.prop;
    }

}
