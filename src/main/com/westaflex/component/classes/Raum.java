/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.westaflex.component.classes;

import com.seebass.tools.Tools;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author seebass
 */
public class Raum implements XRaum {

    //private Object[] data = null;
    private HashMap<RaumItem.PROP, Object> data;

    public Raum() {
        data = new HashMap<RaumItem.PROP, Object>(RaumItem.PROP.values().length);
    }

    /**
     *
     * @return size of dataarray
     */
    public final int getSize() {
        return data.size();
    }

    public Object getRaumItemValue(RaumItem.PROP prop) {
        return this.data.get(prop);
    }

    public void setRaumItemValue(RaumItem.PROP prop, Object value) {
        this.data.put(prop, value);

    }

    public boolean berechneTuerspalt(boolean bUeberstroem) {
        int iSumTuerbreite = 0;
        boolean bFlag = false, bDurchgang = false;
        float fQuerschnitt = 0f, fVolumenstrom = 0f;
        float fSpalthoehe = 0f;
        try {
            fSpalthoehe = (Float) getRaumItemValue(RaumItem.PROP.MAX_TUERSPALTHOEHE);
        } catch (Exception e) {
            fSpalthoehe = Float.parseFloat((String) getRaumItemValue(RaumItem.PROP.MAX_TUERSPALTHOEHE));
        }
        Vector<Tuer> t = getTueren();
        int kDichtung = 2500, iOhneDichtung = 0;
        if (t != null) {
            for (Tuer tuer : t) {
                if (!tuer.getDichtung()) {
                    iOhneDichtung++;
                }
                iSumTuerbreite += tuer.getBreite();
                if (tuer.getBezeichnung().contains("Dur")) {
                    bDurchgang = true;
                }
            }
        }
        if (!bDurchgang) {
            if (iSumTuerbreite > 0) {
                if (data.get(RaumItem.PROP.LUFTART).toString().equals("ZU")) {
                    fVolumenstrom = (Float) data.get(RaumItem.PROP.ZULUFTVOLUMENSTROM);
                } else if (data.get(RaumItem.PROP.LUFTART).toString().equals("AB")) {
                    fVolumenstrom = (Float) data.get(RaumItem.PROP.ABLUFTVOLUMENSTROM);
                } else if (data.get(RaumItem.PROP.LUFTART).toString().equals("ZU/AB")) {
                    fVolumenstrom = Math.abs((Float) data.get(RaumItem.PROP.ABLUFTVOLUMENSTROM) - (Float) data.get(RaumItem.PROP.ZULUFTVOLUMENSTROM));
                }

                fQuerschnitt = (float) (100 * 3.1f * fVolumenstrom / Math.sqrt(1.5f)) - kDichtung * iOhneDichtung;
                // mal 100, da Umrechnung von cm auf mm
                fQuerschnitt = Math.round(fQuerschnitt);
                if (fQuerschnitt < 0) {
                    fQuerschnitt = 0;
                }
                float hoehe = fQuerschnitt / iSumTuerbreite;

                if (hoehe > fSpalthoehe && !bUeberstroem) {
                    Tools.msgbox("Die Spalthöhe ist bei dem Raum '" + data.get(RaumItem.PROP.RAUMNAME)
                            + "' im '" + data.get(RaumItem.PROP.GESCHOSS) + "' größer als " + fSpalthoehe + " mm."
                            + "\nBitte wählen Sie ein Überströmelement aus!");
                }
                for (Tuer tuer : t) {
                    tuer.setQuerschnittsflaeche(Math.round(fQuerschnitt * tuer.getBreite() / iSumTuerbreite));
                    if (tuer.getBreite() != 0) {
                        tuer.setSpalthoehe(hoehe);
                    }
                }
            } else {
                bFlag = true;
            }
        }
        return bFlag;
    }

    public float getUeberstroemMenge() {
        float fQuerschnitt = 0f, fLuftMenge = 0f;
        Vector<Tuer> t = getTueren();
        int kDichtung = 2500, iOhneDichtung = 0;
        if (t != null) {
            for (Tuer tuer : t) {
                if (!tuer.getDichtung()) {
                    iOhneDichtung++;
                }
                try {
                    fQuerschnitt += tuer.getBreite() * (Float) data.get(RaumItem.PROP.MAX_TUERSPALTHOEHE);
                } catch (Exception e) {
                    fQuerschnitt += tuer.getBreite() * Float.parseFloat((String) data.get(RaumItem.PROP.MAX_TUERSPALTHOEHE));
                }
            }
            fLuftMenge = (float) ((fQuerschnitt + kDichtung * iOhneDichtung) / 100 / 3.1f * Math.sqrt(1.5f));
        }
        return fLuftMenge;
    }

    public Vector<Tuer> getTueren() {
        Vector<Tuer> doors = (Vector<Tuer>) data.get(RaumItem.PROP.TUEREN);
        if (doors == null) {
            doors = new Vector<Tuer>();
        }

        return doors;
    }

    void setValueFromString(int columnIndex, String value) {

        //Class type = RaumDataProviderSingleton.getInstance().getItemClassByName( key );
        RaumItem raumItem = RaumDataProviderSingleton.getInstance().getRaumItemByColumnIndex(columnIndex);

        if (value == null || value.equals("null")) {
            this.data.put(raumItem.getProp(), null);
            //setValue(enumOrdinal, null);
        } else if (raumItem.getPropClass().equals(String.class)) {
            this.data.put(raumItem.getProp(), value);
            //setValue(enumOrdinal, value);
        } else if (raumItem.getPropClass().equals(Float.class)) {
            String s = value.replaceFirst(",", ".");
            this.data.put(raumItem.getProp(), value.isEmpty() ? 0f : Float.parseFloat(s));
            //setValue(enumOrdinal, value.isEmpty() ? 0f : Float.parseFloat(value));
        } else if (raumItem.getPropClass().equals(Integer.class)) {
            this.data.put(raumItem.getProp(), value.isEmpty() ? 0 : Integer.parseInt(value));
            //setValue(enumOrdinal, value.isEmpty() ? 0 : Integer.parseInt(value));
        } else {
            System.out.println("Severe Error. No applicable Class found");
        }

    }
}
