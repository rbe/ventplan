/*
 * WestaWacBerechnungen.java
 *
 * Created on 30. Juli 2006, 13:27
 *
 */
package com.westaflex;

import com.bensmann.superswing.component.util.ConversionUtil;
import com.bensmann.superswing.component.util.JTableUtil;
import com.seebass.tools.Tools;
import com.westaflex.component.ProjectInternalFrame;
import com.westaflex.component.classes.Raum;
import com.westaflex.component.classes.RaumItem;
import com.westaflex.component.classes.Rooms;
import com.westaflex.component.classes.SeeTable;
import com.westaflex.database.WestaDB;
import com.westaflex.resource.Strings.Strings;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * $Header$
 * @author rb
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public final class WestaWacBerechnungen {

    private ProjectInternalFrame project;
    private JTable wohnTabelle;
    private DefaultTableModel wohnTableModel;
    private JTable lmeTabelle;
    private DefaultTableModel lmeTableModel;
    private JTable lmeTabelleUeberstroem;
    private DefaultTableModel lmeTableUeberstroemModel;
    public static final int BEZEICHNUNG = 0;
    public static final int BELUEFTUNG = 1;
    public static final int VOLUMEN = 2;
    public static final int LUFTWECHSEL = 3;
    public static final int ANZABLUFTVENTILE = 4;
    public static final int ANZZULUFTVENTILE = 7;
    public static final int ABLUFTMENGEJEVENTIL = 5;
    public static final int ZULUFTMENGEJEVENTIL = 11;
    public static final int SOLLLUFTMENGEWERT = 6;
    public static final int ISTLUFTMENGEWERT = 6;
    public static final int ANZAHLUEBERSTROEM = 3;
    public static final int UEBERSTROEMELEMENT = 5;
    public static final int ZULUFTTYPENBEZEICHNUNG = 8;
    public static final int ABLUFTTYPENBEZEICHNUNG = 9;
    static final int VERTEILEBENE = 10;
    private JTable dvbTeilstreckeTabelle;
    private SeeTable dvbVentileinstellungTabelle;
    private HashMap<String, Integer> hmMaxVolumenstrom = new HashMap();
//    private static final HashMap<String, Float> mindestRaumluftwechsel;
    public static boolean bKeineNotwendigkeit = false;
    private Rooms raeume = null;

//    static {
//        mindestRaumluftwechsel = new HashMap(5);
//        mindestRaumluftwechsel.put("Eltern", 35f);
//        mindestRaumluftwechsel.put("Schlafen", 35f);
//        mindestRaumluftwechsel.put("Küche", 40f);
//        mindestRaumluftwechsel.put("Bad", 40f);
//        mindestRaumluftwechsel.put("WC", 20f);
//    }
    /**
     * Creates a new instance of WestaWacBerechnungen
     *
     * @param project
     */
    public WestaWacBerechnungen(ProjectInternalFrame project) {
        this.project = project;
    }

    public float getGesAussenluft(float fGesAbR) {
        // Gesamt-Außenluft-Volumenstrom
        float fGesAussenluft = Math.max(berechneGesAU_NE(), fGesAbR);
        fGesAussenluft = Math.max(fGesAussenluft, berechneGesAU_Pers());
        return fGesAussenluft;
    }

    /**
     * Aktualisiere Daten (TableModel müssen immer neu geholt werden!
     */
    private void updateData() {

        wohnTabelle = project.getWfTabelleTable();
        wohnTableModel = (DefaultTableModel) wohnTabelle.getModel();

        lmeTabelle = project.getLmeTabelleTable();
        lmeTableModel = (DefaultTableModel) lmeTabelle.getModel();

        lmeTabelleUeberstroem = project.getLmeTabelleUeberstroemTable();
        lmeTableUeberstroemModel = (DefaultTableModel) lmeTabelleUeberstroem.getModel();

        dvbTeilstreckeTabelle = project.getDvbTeilstreckenTabelleTable();
        dvbVentileinstellungTabelle = project.getDvbVentileinstellungTabelleTable();
    }

    /**
     *
     * @param row
     * @return
     */
    public float getLuftmengeVolumen(int row) {
        return JTableUtil.parseFloatFromTableCell(lmeTabelle, row, VOLUMEN);
    }

    /**
     *
     * @param row
     * @return
     */
    public String getLuftmengeBelueftung(int row) {
        updateData();
        return (String) lmeTableModel.getValueAt(row, BELUEFTUNG);
    }

    public int getMaxVolumenstrom(String artikel) {

        int ret = 0;

        if (hmMaxVolumenstrom.containsKey(artikel)) {
            ret = hmMaxVolumenstrom.get(artikel);
        } else {
            String[] sMV = WestaDB.getInstance().queryDBResultRow("select ~MaxVolumenstrom~ from ~artikelstamm~ where ~Artikelnummer~ = '" + artikel + "'");
            if (sMV != null && sMV.length > 0) {
                ret = Integer.parseInt(sMV[0]);
            }
            hmMaxVolumenstrom.put(artikel, ret);
        }
        return ret;
    }

    /**
     *
     * Update der Tabelle Luftmengenermittlung aus den Angaben
     * aus der Tabelle "Wohnfläche"
     *
     * @param flag
     * @param index
     */
    public void luftmengenUpdate(int zeile) {

        updateData();

        float flaeche = JTableUtil.parseFloatFromTableCell(wohnTabelle, zeile, 3);
        float hoehe = JTableUtil.parseFloatFromTableCell(wohnTabelle, zeile, 4);
        lmeTableModel.addRow(new Object[]{});
        lmeTableModel.setValueAt(wohnTableModel.getValueAt(zeile, 0), zeile, 0);
        lmeTableModel.setValueAt(wohnTableModel.getValueAt(zeile, 2), zeile, 1);
        JTableUtil.setFormattedFloatInTableCell(lmeTabelle, zeile, VOLUMEN, flaeche * hoehe);
        luftmengeBerechnen();
    }

    /**
     */
    public void luftmengeBerechnen() {

        int row = -1;
        float fZuluftMenge = 0f;
        float fAbluftMenge = 0f;
        String typ = null;
        int divisor = 1;
        float volumen = 0f;
        float luftwechsel = 0f;
        int anzahlVentile = 0;
        float luftmengeJeVentil = 0f;
        HashMap<String, Integer> hmVol = new HashMap();

        updateData();

        row = lmeTabelle.getSelectedRow();
        if (row > -1) {
            volumen = getLuftmengeVolumen(row);
            fZuluftMenge = getLuftmengeIstLuftmenge(row);
            fAbluftMenge = getLuftmengeSollLuftmenge(row);
            typ = getLuftmengeTypenbezeichnung(row);

            if (fAbluftMenge > 0) {
                if (volumen != 0) {
                    luftwechsel = fAbluftMenge / volumen;
                }
                setLuftmengeLuftwechsel(row, luftwechsel);
            } else {
                if (fZuluftMenge > 0) {
                    if (volumen != 0) {
                        luftwechsel = fZuluftMenge / volumen;
                    }
                    setLuftmengeLuftwechsel(row, luftwechsel);
                }
            }

            if (fZuluftMenge > 0 && typ != null) {
                if (hmVol.containsKey(typ)) {
                    divisor = hmVol.get(typ);
                } else {
                    divisor = getMaxVolumenstrom(typ);//String[] sMV = WestaDB.getInstance().
                    if (divisor == 0) {
                        divisor = Math.round(fZuluftMenge);
                    }
                    hmVol.put(typ, divisor);
                }
                // Anzahl Ventile
                anzahlVentile = (int) Math.ceil(fZuluftMenge / divisor);
                JTableUtil.setFormattedFloatInTableCell(lmeTabelle, row, ANZABLUFTVENTILE, anzahlVentile);

                // Luftmenge je Ventile
                luftmengeJeVentil = fZuluftMenge / anzahlVentile;
                JTableUtil.setFormattedFloatInTableCell(lmeTabelle, row, ZULUFTMENGEJEVENTIL, luftmengeJeVentil);

            } else {
                if (fAbluftMenge > 0 && typ != null) {
                    if (hmVol.containsKey(typ)) {
                        divisor = hmVol.get(typ);
                    } else {
                        divisor = getMaxVolumenstrom(typ);
                        if (divisor == 0) {
                            divisor = Math.round(fAbluftMenge);
                        }
                        hmVol.put(typ, divisor);
                    }
                    // Anzahl Ventile
                    anzahlVentile = (int) Math.ceil(fAbluftMenge / divisor);
                    JTableUtil.setFormattedFloatInTableCell(lmeTabelle, row, ANZABLUFTVENTILE, anzahlVentile);

                    // Luftmenge je Ventile
                    luftmengeJeVentil = fAbluftMenge / anzahlVentile;
                    JTableUtil.setFormattedFloatInTableCell(lmeTabelle, row, ABLUFTMENGEJEVENTIL, luftmengeJeVentil);

                } else {
                    lmeTableModel.setValueAt(null, row, ANZABLUFTVENTILE);
                    lmeTableModel.setValueAt(null, row, ABLUFTMENGEJEVENTIL);
                }
            }
        }

        float vol = 0;
        if (project.getGelueftetVolumen() != 0) {
            vol = project.getGelueftetVolumen();
        } else {
            vol = sumVolumen();
        }
        luftmengeAutoBerechnen(false);
        float fGesAU_Luft = 0f;
        if (project.getWirkInfiltration()) {
            fGesAU_Luft = project.round5(project.getVsGrundlueftungWertLabel() + berechneInfiltration(true));
        } else {
            fGesAU_Luft = project.getVsGrundlueftungWertLabel();
        }
        // Setze Ergebnis "Gesamtvolumen"
        project.setLmeGesamtvolumenWertLabel(vol);

        project.setLmeGesAussenluftVolumenWertLabel(fGesAU_Luft);
        project.setLmeGebaeudeluftwechselWertLabel(fGesAU_Luft / vol);
    }

    /**
     * automatische Berechnung der Luftmenge und Luftwechsel pro Stunde
     * getrennt nach Abluft und Zuluft im Verhältnis der einzelnen Raumluftvolumenströme
     * zum Gesamt-Raumluftvolumenstrom
     * @param b true=Raumvolumenströme, false=Gesamtraumvolumenstrom
     */
    public void luftmengeAutoBerechnen(boolean b) {

        int rows = lmeTabelle.getRowCount();
        float fLTMZuluftRaum = 0, fLTMAbluftRaum = 0;
        float fGesamtAb = 0f, fGesamtZu = 0f, fLTM_GL = 0;
        // fGesamtAb:gesamter ursprünglicher Abluft-Vol.Strom
        // fGesamtZu:gesamter ursprünglicher Zuluftfaktor
        float fSollAbluft[] = new float[rows];// Abluftvolumenstrom pro Raum
        float fSollZuluft[] = new float[rows];// Zuluftvolumenstrom pro Raum
        float fGesAussenluft;
        Raum r = null;
        raeume = project.getRaeume();

        updateData();

        //Sind lüftungstechnische Maßnahmen erforderlich?
        if (!berechneLTMerforderlich(project.getWaermeschutz())) {
            // Fehlermeldung
            if (!bKeineNotwendigkeit) {
                Tools.msgbox("Es sind keine\nlüftungstechnischen Maßnahmen\nerforderlich!");
                bKeineNotwendigkeit = true;
            }
        } else {
            bKeineNotwendigkeit = false;
        }
        //LTM: erste Berechnung für Raumvolumenströme
        for (int i = 0; i < rows; i++) {
            if (!((String) lmeTabelle.getValueAt(i, BELUEFTUNG)).contains("ÜB")) {
                // AbluftRäume
                if (((String) lmeTabelle.getValueAt(i, BELUEFTUNG)).contains("AB") && !((String) wohnTabelle.getValueAt(i, 6)).isEmpty()) {
                    fSollAbluft[i] = JTableUtil.parseFloatFromTableCell(project.getWfTabelleTable(), i, 6);// 6=Abluftvolumen
                    fGesamtAb = fGesamtAb + fSollAbluft[i];//Gesamt-Abluft-Volumenstrom
                }
                // ZuluftRäume
                if (((String) lmeTabelle.getValueAt(i, BELUEFTUNG)).contains("ZU") && !((String) wohnTabelle.getValueAt(i, 5)).isEmpty()) {
                    fSollZuluft[i] = JTableUtil.parseFloatFromTableCell(project.getWfTabelleTable(), i, 5);// 5=Zuluftfaktor
                    fGesamtZu = fGesamtZu + fSollZuluft[i];//Gesamt-Zuluft-Faktor
                }
            }
        }
        // Gesamt - Außenluft - Volumenstrom bestimmen!
        fGesAussenluft = getGesAussenluft(fGesamtAb);
        if (project.getBesAnforderFaktor() != 1) {
            fGesAussenluft *= project.getBesAnforderFaktor();
        }
        // Gesamt-Außenluft-Volumenstrom für lüftungstechnische Maßnahmen
        if (project.getWirkInfiltration() && b) {
            fLTM_GL = fGesAussenluft - berechneInfiltration(true);
        } else {
            fLTM_GL = fGesAussenluft;
        }

        float fSumLTMAbluft = 0f, fSumLTMZuluft = 0f;

        for (int i = 0; i < rows; i++) {
            r = raeume.get(i);
            if (fSollAbluft[i] != 0) {
                fLTMAbluftRaum = Math.round(fLTM_GL / fGesamtAb * fSollAbluft[i]);
                if (b) {
                    setLuftmengeSollLuftmenge(i, fLTMAbluftRaum);
                    r.setRaumItemValue(RaumItem.PROP.ABLUFTVOLUMENSTROM, fLTMAbluftRaum);
                    if (getLuftmengeVolumen(i) != 0) {
                        setLuftmengeLuftwechsel(i, fLTMAbluftRaum / getLuftmengeVolumen(i));
                    } else {
                        setLuftmengeLuftwechsel(i, 0);
                    }
                    if (((String) lmeTabelle.getValueAt(i, BELUEFTUNG)).contains("ZU/AB")) {
                        fLTMZuluftRaum = Math.round(fLTM_GL * fSollZuluft[i] / fGesamtZu);
                        r.setRaumItemValue(RaumItem.PROP.ZULUFTVOLUMENSTROM, fLTMZuluftRaum);
                        r.setRaumItemValue(RaumItem.PROP.ABLUFTVOLUMENSTROM, fLTMAbluftRaum);
                        if (fLTMZuluftRaum > fLTMAbluftRaum) {
                            setLuftmengeIstLuftmenge(i, fLTMZuluftRaum);
                            setLuftmengeLuftwechsel(i, fLTMZuluftRaum / getLuftmengeVolumen(i));
                        } else {
                            setLuftmengeSollLuftmenge(i, fLTMAbluftRaum);
                            setLuftmengeLuftwechsel(i, fLTMAbluftRaum / getLuftmengeVolumen(i));
                        }
                        fSumLTMZuluft += fLTMAbluftRaum;
                    }
                } else {
                    fSumLTMAbluft += fLTMAbluftRaum;
                }
            }
        }
        /**
        Raum r = null;
        raeume = project.getRaeume();
        for (int i = 0; i < row; i++) {
        r = raeume.get(i);
         */
        //LTM: zweite Berechnung für Raumvolumenströme
        for (int i = 0; i < rows; i++) {
            r = raeume.get(i);
            if (fSollZuluft[i] != 0 && !((String) lmeTabelle.getValueAt(i, BELUEFTUNG)).contains("ZU/AB")) {
                fLTMZuluftRaum = Math.round(fLTM_GL * fSollZuluft[i] / fGesamtZu);
                if (b) {
                    setLuftmengeIstLuftmenge(i, fLTMZuluftRaum);
                    r.setRaumItemValue(RaumItem.PROP.ZULUFTVOLUMENSTROM, fLTMZuluftRaum);
                    setLuftmengeLuftwechsel(i, fLTMZuluftRaum / getLuftmengeVolumen(i));
                } else {
                    fSumLTMZuluft += fLTMZuluftRaum;
                }
            }
        }
        if (!b) {
            project.setLmeSummeAbluftmengeWertLabel(fSumLTMAbluft);
            project.setLmeSummeZuluftmengeWertLabel(fSumLTMZuluft);
        }
        //Neue Festlegung ENDE!
    }

    /**
     *
     * @param a
     * @return
     */
    public float diffToDelta(float a) {

        final float ft[] = {3f, 2.8f, 2.5f, 2.3f, 2.1f, 1.9f, 1.8f, 1.6f, 1.5f, 1.3f, 1.2f, 1.1f, 1f,
            0.9f, 0.8f, 0.7f, 0.6f, 0.55f, 0.5f, 0.45f, 0.4f, 0.35f, 0.3f, 0.275f, 0.25f, 0.225f, 0.2f
        };

        if ((0f <= a) && (a <= 13f) && (a % 0.5f == 0)) {
            return (ft[(int) a * 2]);
        }

        return 0f;
    }

    /**
     *
     * @param table
     * @param label
     */
    public void berechneMittlererSchalldruckpegel(JTable table, JLabel label) {

        int i = 1;
        int h = 0;
        float j = 0f;
        float[] l = new float[6];

        updateData();

        for (int z = 0; z < 6; z++) {
            l[z] = JTableUtil.parseFloatFromTableCell(table, 12, z);
        }

        while (i <= 5) {
            h = i;
            while (h >= 1 && (l[h]) > l[h - 1]) {
                j = l[h - 1];
                l[h - 1] = l[h];
                l[h] = j;
                h--;
                i = 1;
            }
            if (h == i) {
                i++;
            }
        }
        i = 0;
        j = l[0];
        while (i <= 4) {
            if ((j - l[i + 1] < 0) || (j - l[i + 1] > 13)) {
                i = 99;
            } else {
                j += diffToDelta(Math.round(j - l[i + 1]));
            }
            i++;
        }

        // getrickst!!
        if (j < 20) {
            j = 20;
        }
        ConversionUtil.setFormattedFloatInComponent(label, j, Locale.GERMAN);
    }

    /**
     *
     */
    public void berechneBewerteterSchallpegel(JTable table) {

        updateData();

        for (int column = 0; column < 6; column++) {
            JTableUtil.setFormattedFloatInTableCell(table, 12, column,
                    JTableUtil.summarizeFloatInTableColumn(table, column, 0, 12));
        }
    }

    /**
     *
     * @return
     */
    public float sumLuftMenge(String luftart) {

        float sum = 0f;

        updateData();

        for (int row = 0; row < lmeTabelle.getRowCount(); row++) {
            if (getLuftmengeBelueftung(row).contains(luftart)) {
                sum += getLuftmengeSollLuftmenge(row);
            }
        }
        return sum;
    }

    /**
     *
     * @return
     */
    public float sumVolumen() {

        float sum = 0f;

        updateData();

        // Addiere alle Volumen (m³) Spalten aus der
        // Tabelle für Luftmengenermittlung
        for (int row = 0; row < lmeTabelle.getRowCount(); row++) {
            sum += getLuftmengeVolumen(row);
        }
        if (sum < 30.0f * project.getHoehe()) {
            sum = 30.0f * project.getHoehe();
        }

        return sum;
    }

    public float sumVolumen(String luftart) {

        float sum = 0f;

        updateData();

        // Addiere alle Volumen (m³) Spalten aus der
        // Tabelle für Luftmengenermittlung
        for (int row = 0; row < lmeTabelle.getRowCount(); row++) {
            if (getLuftmengeBelueftung(row).contains(luftart)) {
                sum += getLuftmengeVolumen(row);
            }
        }

        return sum;
    }

    /**
     *
     * @return
     */
    public float sumFlaeche() {

        float sum = 0f;

        updateData();

        // Addiere alle Flächen (m³) Spalten aus der
        // Tabelle für Raumdaten
        for (int row = 0; row < wohnTabelle.getRowCount(); row++) {
            sum += JTableUtil.parseFloatFromTableCell(wohnTabelle, row, 3);
        }
        if (sum < 30) {
            sum = 30;
        }
        return sum;
    }

    /**
     *
     * @param row
     * @param sollLuftmenge
     */
    public void setLuftmengeSollLuftmenge(int row, float sollLuftmenge) {

        updateData();

        // Wert in versteckter Spalte im TableModel schreiben
        JTableUtil.setFormattedFloatInTableCell(lmeTabelle, row, SOLLLUFTMENGEWERT, sollLuftmenge);
    }

    /**
     *
     * @param row
     * @return
     */
    public float getLuftmengeSollLuftmenge(int row) {
        updateData();
        return JTableUtil.parseFloatFromTableCell(lmeTabelle, row, SOLLLUFTMENGEWERT);
    }

    /**
     *
     * @param row
     * @param istluftmenge
     */
    public void setLuftmengeIstLuftmenge(int row, float istluftmenge) {

        updateData();

        // Wert in versteckter Spalte im TableModel schreiben
        JTableUtil.setFormattedFloatInTableCell(lmeTabelle, row, ISTLUFTMENGEWERT, istluftmenge);
    }

    /**
     *
     * @param row
     * @return
     */
    public float getLuftmengeIstLuftmenge(int row) {
        updateData();
        return JTableUtil.parseFloatFromTableCell(lmeTabelle, row, ISTLUFTMENGEWERT);
    }

    /**
     *
     * @param row
     * @param typ
     */
    public void setLuftmengeTypenbezeichnung(int row, String typ) {
        updateData();
        lmeTableModel.setValueAt(typ, row, ABLUFTTYPENBEZEICHNUNG);
    }

    /**
     *
     * @param row
     * @return
     */
    public String getLuftmengeTypenbezeichnung(int row) {
        updateData();
        return (String) lmeTableModel.getValueAt(row, ABLUFTTYPENBEZEICHNUNG);
    }

    public void setLuftmengeVerteilebene(int row, String typ) {
        updateData();
        lmeTableModel.setValueAt(typ, row, VERTEILEBENE);
    }

    public String getLuftmengeVerteilebene(int row) {
        updateData();
        return (String) lmeTableModel.getValueAt(row, VERTEILEBENE);
    }

    /**
     *
     * @param row
     * @param luftwechsel
     */
    public void setLuftmengeLuftwechsel(int row, float luftwechsel) {
        updateData();
        JTableUtil.setFormattedFloatInTableCell(lmeTabelle, row, LUFTWECHSEL, luftwechsel);
//        Raum r = new Raum();
    }

    /**
     *
     * @param row
     */
    public void removeLuftmengeLuftwechsel(int row) {
        updateData();
        lmeTableModel.setValueAt(null, row, LUFTWECHSEL);
    }

    /**
     *
     * @param row
     * @param label
     * @param f
     */
    public void setAkkustikTableRow(int row, JTable table, JLabel label, float[] f) {

        float[] tmp = null;

        updateData();
        try {
            // Wenn ein Label übergeben wurde, Wert setzen und Array verkürzen
            // damit keine Multiplikation gemacht wird
            if (label != null) {

                // Setze Wert in Label
                ConversionUtil.setFormattedFloatInComponent(label, f[6], Locale.GERMAN);

                // Lösche letztes Element aus Array, damit f.length == 6 ist
                tmp = f;
                f = new float[6];
                System.arraycopy(tmp, 0, f, 0, 6);

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.getLogger(WestaWacBerechnungen.class.getName()).log(Level.SEVERE, null, e);
        }

        // Keine Werte
        if (f.length == 0) {
            for (int i = 0; i < 6; i++) {
                table.setValueAt("", row, i);
            }
        } // a; 0
        else if (f.length == 1) {
            for (int i = 0; i < 6; i++) {
                JTableUtil.setFormattedFloatInTableCell(table, row, i, f[0], 1);
            }
        } // a-f; 0-5
        else if (f.length == 6) {
            for (int i = 0; i < f.length; i++) {
                JTableUtil.setFormattedFloatInTableCell(table, row, i, f[i], 1);
            }
        } // a-g; 0-6
        else if (f.length == 7) {
            for (int i = 0; i < f.length - 1; i++) {
                JTableUtil.setFormattedFloatInTableCell(table, row, i, f[i] * f[6], 1);
            }
        }
    }

    /**
     *
     * @param teilstreckenName
     */
    public void berechneTeilstrecke(String teilstreckenName) {


        updateData();
        int lastRowNumber = dvbTeilstreckeTabelle.getRowCount() - 1;
        float luftmenge = JTableUtil.parseFloatFromTableCell(dvbTeilstreckeTabelle, lastRowNumber, 2);//50f
        String artikel = (String) dvbTeilstreckeTabelle.getValueAt(lastRowNumber, 3);
        float laenge = JTableUtil.parseFloatFromTableCell(dvbTeilstreckeTabelle, lastRowNumber, 4);//5f

        double druckverlust = 0f;
        double lambda = 0f;
        float geschwindigkeit = 0f;
        int klasse = 0;

        ResultSet res = WestaDB.getInstance().queryDB("select * from ~rohrwerte~ where ~Artikelnummer~ = '" + artikel.trim() + "'");
        try {
            if (res.next()) {
                klasse = res.getInt("Klasse");
                long flaeche = res.getLong("Flaeche");
                int durchmesser = res.getInt("Durchmesser");
                int seiteA = res.getInt("SeiteA");
                int seiteB = res.getInt("seiteB");

                geschwindigkeit = luftmenge * 1000000 / (flaeche * 3600);

                switch (klasse) {
                    case 4:
                        lambda = calcDruckverlustKlasse4(durchmesser, seiteA, seiteB);
                        break;
                    case 5:
                        lambda = calcDruckverlustKlasse5(geschwindigkeit, durchmesser);
                        break;
                    case 6:
                        lambda = calcDruckverlustKlasse6(geschwindigkeit, durchmesser);
                        break;
                    case 7:
                        lambda = calcDruckverlustKlasse78(geschwindigkeit, durchmesser);
                        break;
                    case 8:
                        lambda = calcDruckverlustKlasse78(geschwindigkeit, durchmesser);
                        break;
                    default:
                        Logger.getLogger(WestaWacBerechnungen.class.getName()).log(Level.WARNING, "switch clause runs into default. Klasse " + klasse + " not defined ==> lambda not set");
                }
                druckverlust = lambda * laenge * 1.2 * Math.pow(geschwindigkeit, 2) / (2 * (durchmesser + 0f) / 1000);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        JTableUtil.setFormattedFloatInTableCell(dvbTeilstreckeTabelle, lastRowNumber, 5, geschwindigkeit);
        JTableUtil.setFormattedFloatInTableCell(dvbTeilstreckeTabelle, lastRowNumber, 6, Float.parseFloat(Double.toString(druckverlust)));
    }

    private double calcDruckverlustKlasse4(int durchmesser, int seiteA, int seiteB) {

        double k1 = 0.0255 * Math.pow((seiteA / seiteB), 2) - 0.1393 * (seiteA / seiteB) + 1.1485;
        double lambda1 = Math.log10(50 * Math.sqrt(0.674 * durchmesser));
        double lambda2 = lambda1 * lambda1;
        double lambda = k1 * 0.25 / lambda2;

        return lambda;
    }

    private double calcDruckverlustKlasse5(float geschwindigkeit, int durchmesser) {

        double re = geschwindigkeit * (durchmesser + 0f) / (0.015); //1000 * 15 hoch -6
        double lambda = 0.22 / Math.pow(re, 0.2);

        return lambda;
    }

    private double calcDruckverlustKlasse6(float geschwindigkeit, int durchmesser) {

        double lambda;

        double re = geschwindigkeit * (durchmesser + 0f) / 0.015;
        float f1 = 1.2f;
        float f2 = 2.4f;

        if (re < 2300) {
            lambda = 64 / re;
        } else {

            if (re >= 2300 && re < 20000) {

                lambda = 1.14 - 2 * Math.log10(f1 / durchmesser);

                // Iteration
                for (int i = 0; i < 3; i++) {
                    lambda = -2 * Math.log10(f1 / (durchmesser * 3.71) + 2.51 / re * lambda);
                }

                lambda = Math.pow(1 / lambda, 2);

            } else {

                lambda = 1.14 - 2 * Math.log10(f2 / durchmesser);
                lambda = Math.pow(1 / lambda, 2);

            }
        }
        return lambda;
    }

    private double calcDruckverlustKlasse78(float geschwindigkeit, int durchmesser) {

        double lambda = 0d;
        double re = geschwindigkeit * durchmesser / 0.015;
        final float f1 = 0.09f;

        if (re < 2300) {
            lambda = 64 / re;
        } else {
            if (re >= 2300 && re < 20000) {
                //Iteration
                lambda = 1.14 - 2 * Math.log10(f1 / durchmesser);
                for (int i = 0; i < 3; i++) {
                    lambda = -2 * Math.log10(f1 / (durchmesser * 3.71) + 2.51 / re * lambda);
                }
                lambda = Math.pow(1 / lambda, 2);
            } else {
                lambda = 1.14 - 2 * Math.log10(0.8 / durchmesser);
                lambda = Math.pow(1 / lambda, 2);
            }
        }
        return lambda;
    }

    /**
     *
     * @param str1
     * @param str2
     * @return
     */
    public float getPaSum(String teilstrecken, String belueftung) {

        float result = 0f;
        String[] teilstreckenTmp = teilstrecken.split(";");
        String belueftungTmp = null;
        String teilstreckeTmp = null;

        for (int ts = 0; ts < teilstreckenTmp.length; ts++) {

            for (int row = 0; row < dvbTeilstreckeTabelle.getRowCount(); row++) {

                belueftungTmp = (String) dvbTeilstreckeTabelle.getValueAt(row, 0);
                teilstreckeTmp = (String) dvbTeilstreckeTabelle.getValueAt(row, 1);

                if (belueftungTmp.equals(belueftung) && teilstreckeTmp.equals(teilstreckenTmp[ts].trim())) {

                    result +=
                            JTableUtil.parseFloatFromTableCell(dvbTeilstreckeTabelle, row, 6)
                            + JTableUtil.parseFloatFromTableCell(dvbTeilstreckeTabelle, row, 8);
                }

            }

        }

        return result;
    }

    /**
     *
     * @param str1
     * @param str2
     * @param luft
     * @return
     */
    public float searchFullOpen(String ventil, String luftart, float luftmenge) {

        float f = 0f;
        final String sql1 = "select distinct ~Luftmenge~ from ~druckverlust~ where ~Artikelnummer~ "
                + "= '$1' and ~Luftart~ = '$2' and ~Luftmenge~ >= $3 order by ~Luftmenge~ asc";
        final String sql2 = "select min(~Druckverlust~) from ~druckverlust~ where ~Artikelnummer~ = "
                + "'$1' and ~Luftart~ = '$2' and ~Luftmenge~ = $3";
        String sql = "";

        sql = sql1.replace("$1", ventil);
        sql = sql.replace("$2", luftart);
        sql = sql.replace("$3", Float.toString(luftmenge));
        float[] res = WestaDB.getInstance().queryDBResultRowAsFloat(sql);
        if (res[0] != 0f) {
            sql = sql2.replace("$1", ventil);
            sql = sql.replace("$2", luftart);
            sql = sql.replace("$3", Float.toString(res[0]));
            res = WestaDB.getInstance().queryDBResultRowAsFloat(sql);
            f = res[0];
        }
        return f;
    }

    /**
     *
     * @param str1
     * @param str2
     * @return
     */
    public float getLastLuftmenge(String teilstrecken, String belueftung) {

        String belueftungTmp = null; // cell 0
        String teilstrecke = null; // cell 1
        float luftmenge = 0f; // cell 2
        String tmp = null;
        StringTokenizer st = null;

        updateData();
        // Letzte Teilstrecke holen
        st = new StringTokenizer(teilstrecken.trim(), ";");
        while (st.hasMoreElements()) {
            tmp = st.nextToken().trim();
        }
        for (int row = 0; row < dvbTeilstreckeTabelle.getRowCount(); row++) {

            belueftungTmp = (String) dvbTeilstreckeTabelle.getValueAt(row, 0);
            teilstrecke = (String) dvbTeilstreckeTabelle.getValueAt(row, 1);

            if (belueftungTmp.equals(belueftung) && teilstrecke.equals(tmp)) {
                luftmenge = JTableUtil.parseFloatFromTableCell(dvbTeilstreckeTabelle, row, 2);
            }
        }

        return luftmenge;
    }

    /**
     *
     */
    public boolean calculateVentileinstellung() {

        String belueftung = null; // cell 0
        String teilstrecken = null; // cell 2
        String ventiltyp = null; // cell 3
        float lastLuftmenge = 0f;
        float cell4 = 0f;
        float cell5 = 0f;
        float cell6 = 0f;
        float cell7 = 0f;
        int cell8 = 0;
        float maxZU = 0f;
        float maxAB = 0f;
        boolean result = true;

        updateData();

        for (int row = 0; row < dvbVentileinstellungTabelle.getRowCount(); row++) {
            belueftung = (String) dvbVentileinstellungTabelle.getValueAt(row, 0);
            teilstrecken = (String) dvbVentileinstellungTabelle.getValueAt(row, 2);
            ventiltyp = ((String) dvbVentileinstellungTabelle.getValueAt(row, 3)).trim();
            lastLuftmenge = getLastLuftmenge(teilstrecken, belueftung);
            if (lastLuftmenge != 0) {
                cell4 = searchFullOpen(ventiltyp, belueftung, lastLuftmenge);
                cell5 = getPaSum(teilstrecken, belueftung) + cell4;

                JTableUtil.setFormattedFloatInTableCell(dvbVentileinstellungTabelle, row, 4, cell4);
                JTableUtil.setFormattedFloatInTableCell(dvbVentileinstellungTabelle, row, 5, cell5);

                if (belueftung.equals("ZU")) {
                    if (cell5 > maxZU) {
                        maxZU = cell5;
                    }
                } else if (belueftung.equals("AB")) {
                    if (cell5 > maxAB) {
                        maxAB = cell5;
                    }
                }
            } else {
                Tools.errbox("Luftmenge 0 bei Artikel " + ventiltyp);
                result = false;
            }
        }
        // Differenzen
        for (int row = 0; row < dvbVentileinstellungTabelle.getRowCount(); row++) {

            belueftung = (String) dvbVentileinstellungTabelle.getValueAt(row, 0);
            teilstrecken = (String) dvbVentileinstellungTabelle.getValueAt(row, 2);
            ventiltyp = ((String) dvbVentileinstellungTabelle.getValueAt(row, 3)).trim();
            cell4 = JTableUtil.parseFloatFromTableCell(dvbVentileinstellungTabelle, row, 4);
            cell5 = JTableUtil.parseFloatFromTableCell(dvbVentileinstellungTabelle, row, 5);

            if (belueftung.equals("ZU")) {
                cell6 = maxZU - cell5;
                JTableUtil.setFormattedFloatInTableCell(dvbVentileinstellungTabelle, row, 6, cell6);
            } else if (belueftung.equals("AB")) {
                cell6 = maxAB - cell5;
                JTableUtil.setFormattedFloatInTableCell(dvbVentileinstellungTabelle, row, 6, cell6);
            }

            cell7 = cell6 + cell4;
            JTableUtil.setFormattedFloatInTableCell(dvbVentileinstellungTabelle, row, 7, cell7);
            cell8 = searchEinstellung(ventiltyp, belueftung, getLastLuftmenge(teilstrecken, belueftung), cell7);
            result = (cell8 != 0);
            // 080118-SK Hinweisbox eingebaut
            if (!result) {
                Tools.errbox(Strings.VALVE_NULL);
            }
            dvbVentileinstellungTabelle.setValueAt(cell8, row, 8);
        }
        return result;
    }

    /**
     *
     * @param str1
     * @param str2
     * @param luft
     * @param abgleich
     * @return
     */
    public int searchEinstellung(String ventil, String luftart, float luftmenge, float abgleich) {
        final String sql = "select distinct ~Einstellung~, ~Druckverlust~, ~Luftmenge~ from "
                + "~druckverlust~ where ~Artikelnummer~ = '$1' and ~Luftart~ = '$2' and ~Luftmenge~ >= $3 and "
                + "( ~Ausblaswinkel~ = 360 or ~Ausblaswinkel~ = 0 ) order by ~Luftmenge~ asc, ~Einstellung~ asc";
        String s;
        int einst = 0;
//      Tools.msgbox(String.format("ventil=%s, luftart=%s, luftmenge=%f, abgleich=%f", ventil, luftart, luftmenge, abgleich));
        s = sql.replace("$1", ventil);
        s = s.replace("$2", luftart);
        s = s.replace("$3", Float.toString(luftmenge));
        String[][] res = WestaDB.getInstance().queryDBResultArray(s);
        float wert = Float.MAX_VALUE;
        if (res.length > 0) {
            int firstLuftmenge = Integer.parseInt(res[0][2]), i = 0;
            do { // durchsuchen der Tabelle innerhalb der Luftmenge
                if (Math.abs(abgleich - Float.parseFloat(res[i][1])) < wert) {
                    wert = Math.abs(abgleich - Float.parseFloat(res[i][1]));
                    einst = Integer.parseInt(res[i][0]);
//                    Tools.msgbox(String.format("wert=%f, einst=%d", wert, einst));
                }
            } while (firstLuftmenge == Integer.parseInt(res[i][2]) && i++ < res.length);
        }
        return einst;
    }

    // GZ: never used
    /*private float getNearestLuftmenge(float[] ventilElement, float luft) {
    
    float luftmenge = 0;
    float tmp = 0f;
    float luftmengeTmp = Float.MAX_VALUE;
    float returnElement = 0f;
    
    for (int i = 0; i < ventilElement.length; i++) {
    luftmenge = ventilElement[i];
    tmp = Math.abs(luftmenge - luft);
    if (tmp < luftmengeTmp) {
    luftmengeTmp = tmp;
    returnElement = luftmenge;
    }
    }
    return returnElement;
    }*/
    /**
     * Ermittelt, ob lüftungstechnische Maßnahmen erforderlich sind
     * 
     * @param dWaermeschutz
     * @return
     */
    public boolean berechneLTMerforderlich(float dWaermeschutz) {


        float fInfil, fVolFL;

        fInfil = berechneInfiltration(false);// Infiltration berechnen!!!
        fVolFL = dWaermeschutz * berechneGesAU_NE();//Aussenluftvolumenstrom für Feuchteschutz
        if (fVolFL > fInfil) {
            return true;    // lüftungstechnische Maßnahmen erforderlich
        }

        return false;
    }

    /**
     * Wirksamer Infiltrationsanteil berechnen
     * 
     * @param bVent (=true: Ventilatoranlage) (=false: freie Lüftung)
     * @return
     */
    public float berechneInfiltration(boolean bVent) {

        float fInfil, fWirk;
        float fFlaeche, fVolumen;
        float fSys = 0.6f, fInf = 1.0f, fFl;
        float fDiffDruck = 0f, n50 = 1.5f, fDruckExpo = 2.0f / 3;
        String sGebtyp = project.getGebaeudetyp();
        String sGeblag = project.getGebaeudelage();
        float fDDruck = project.getDiffDruck();
        float fLuftwechsel = project.getLuftwechsel();

        if (bVent) {
            fInf = 0.9f;
            n50 = fLuftwechsel;
            if (sGebtyp.contains("MFH")) {
                fSys = 0.45f;
            }
        } else {
            if (sGebtyp.contains("MFH")) {
                fSys = 0.5f;
            }
            if (fLuftwechsel != 0) {
                n50 = 1.5f;
            }
        }
        if (project.getLuftdicht().contains("M")) {
            fDiffDruck = fDDruck;
            n50 = fLuftwechsel;
            if (project.getDruckExp() != 0.666) {
                fDruckExpo = project.getDruckExp();
            }
        } else {
            if (!bVent) {
                if (sGebtyp.contains("MFH")) {
                    if (sGeblag.contains("schwach")) {
                        fDiffDruck = 2f;
                    } else {
                        fDiffDruck = 4f;
                    }
                } else {
                    if (sGeblag.contains("schwach")) {
                        fDiffDruck = 5f;
                    } else {
                        fDiffDruck = 7f;
                    }
                }
            } else {
                if (sGeblag.contains("schwach")) {
                    fDiffDruck = 2f;
                } else {
                    fDiffDruck = 4f;
                }
            }
        }
        if (project.getGelueftetVolumen() == 0) {
            fFlaeche = sumFlaeche();
            fVolumen = sumVolumen();
        } else {
            fFlaeche = project.getGelueftetVolumen() / project.getHoehe2();
            fVolumen = project.getGelueftetVolumen();
        }
        fFl = (float) (-fFlaeche / 1600 + 1.025);
        fWirk = fSys * fInf * fFl;
        // neu: fWirk wird nach Tabelle 11 Seite 29 ermittelt
        if (bVent) {
            // Norm 1946-6 endgültige Fassung
            fWirk = 0.45f;
//            if (sGebtyp.contains("MFH")) {
//                fWirk = 0.35f;
//            } else {
//                fWirk = 0.5f;
//            }
        } else {
            // Norm 1946-6 endgültige Fassung
            fWirk = 0.5f;
//            if (sGebtyp.contains("MFH")) {
//                fWirk = 0.2f;
//            } else {
//                fWirk = 0.35f;
//            }
        }
        // neu: fWirk
        fInfil = (float) (fWirk * fVolumen * n50 * Math.pow(fDiffDruck / 50, fDruckExpo));
        return fInfil;
    }

    /**
     * Gesamt-Außenluft-Volumenstrom berechnen
     * 
     * @return
     */
    public float berechneGesAU_NE() {


        float fGesAU, fFlaeche;
        if (project.getGelueftetVolumen() != 0) {
            fFlaeche = project.getGelueftetVolumen() / project.getHoehe2();
        } else {
            fFlaeche = sumFlaeche();
        }
        fGesAU = (float) (-0.001 * fFlaeche * fFlaeche + 1.15 * fFlaeche + 20);
        return fGesAU;
    }

    /**
     * Gesamt-Außenluft-Volumenstrom nach Personenbelegung berechnen
     * 
     * @return
     */
    public float berechneGesAU_Pers() {
        float fGesAU;
        fGesAU = project.getMindestluftwert();
        return fGesAU;
    }

    /**
     *
     * @param row
     * @return
     */
    public String getZuluftmengeTypenbezeichnung(int row) {
        updateData();
        return (String) lmeTableModel.getValueAt(row, 8);
    }

    /**
     *
     * @param row
     * @return
     */
    public String getAbluftmengeTypenbezeichnung(int row) {
        updateData();
        return (String) lmeTableModel.getValueAt(row, 9);
    }

    /**
     * Anzahl der Ventile und Luftmenge pro Ventil aktualisieren
     */
    public void aktualisiereVentile() {
        int row = 0, divisorZU = 1, divisorAB = 1, anzahlZuluftVentile = 0, anzahlAbluftVentile = 0;
        String typZU = null;
        String typAB = null;
        raeume = project.getRaeume();
        float abluftmengeJeVentil = 0f, zuluftmengeJeVentil = 0f;
        float fZuluftMenge = 0f, fAbluftMenge = 0f;
        HashMap<String, Integer> hmVol = new HashMap();
        row = lmeTabelle.getRowCount();

        for (int i = 0; i < row; i++) {
            fZuluftMenge = getLuftmengeIstLuftmenge(i);
            try {
                fZuluftMenge = (Float) raeume.get(i).getRaumItemValue(RaumItem.PROP.ZULUFTVOLUMENSTROM);
            } catch (Exception e) {
            }
            fAbluftMenge = getLuftmengeSollLuftmenge(i);
            try {
                fAbluftMenge = (Float) raeume.get(i).getRaumItemValue(RaumItem.PROP.ABLUFTVOLUMENSTROM);
            } catch (Exception e) {
            }
            typZU = getZuluftmengeTypenbezeichnung(i);
            typAB = getAbluftmengeTypenbezeichnung(i);
            if (((String) lmeTabelle.getValueAt(i, BELUEFTUNG)).contains("ÜB")) {
                lmeTableModel.setValueAt(null, i, ANZABLUFTVENTILE);
                lmeTableModel.setValueAt(null, i, ABLUFTMENGEJEVENTIL);
                lmeTableModel.setValueAt(null, i, ANZZULUFTVENTILE);
                lmeTableModel.setValueAt(null, i, ZULUFTMENGEJEVENTIL);
                lmeTableModel.setValueAt("", i, ZULUFTTYPENBEZEICHNUNG);
                lmeTableModel.setValueAt("", i, ABLUFTTYPENBEZEICHNUNG);
            } else {
                if (((String) lmeTabelle.getValueAt(i, BELUEFTUNG)).contains("ZU")) {
                    if (fZuluftMenge > 0 && typZU != null && !typZU.equals("")) {
                        if (hmVol.containsKey(typZU)) {
                            divisorZU = hmVol.get(typZU);
                        } else {
                            divisorZU = getMaxVolumenstrom(typZU);
                            if (divisorZU == 0) {
                                divisorZU = Math.round(fZuluftMenge);
                            }
                            hmVol.put(typZU, divisorZU);
                        }
                        // Anzahl Ventile
                        anzahlZuluftVentile = (int) Math.ceil(fZuluftMenge / divisorZU);
                        JTableUtil.setFormattedFloatInTableCell(lmeTabelle, i, ANZZULUFTVENTILE, anzahlZuluftVentile);

                        // Luftmenge je Ventile
                        zuluftmengeJeVentil = fZuluftMenge / anzahlZuluftVentile;
                        JTableUtil.setFormattedFloatInTableCell(lmeTabelle, i, ZULUFTMENGEJEVENTIL, zuluftmengeJeVentil);
                    }
                    if (!((String) lmeTabelle.getValueAt(i, BELUEFTUNG)).contains("AB")) {
                        lmeTableModel.setValueAt(null, i, ANZABLUFTVENTILE);
                        lmeTableModel.setValueAt("", i, ABLUFTMENGEJEVENTIL);
                        lmeTableModel.setValueAt("", i, ABLUFTTYPENBEZEICHNUNG);
                    }

                }
                if (((String) lmeTabelle.getValueAt(i, BELUEFTUNG)).contains("AB")) {
                    if (fAbluftMenge > 0 && typAB != null && !typAB.equals("")) {
                        if (hmVol.containsKey(typAB)) {
                            divisorAB = hmVol.get(typAB);
                        } else {
                            divisorAB = getMaxVolumenstrom(typAB);
                            if (divisorAB == 0) {
                                divisorAB = Math.round(fAbluftMenge);
                            }
                            hmVol.put(typAB, divisorAB);
                        }
                        // Anzahl Ventile
                        anzahlAbluftVentile = (int) Math.ceil(fAbluftMenge / divisorAB);
                        JTableUtil.setFormattedFloatInTableCell(lmeTabelle, i, ANZABLUFTVENTILE, anzahlAbluftVentile);

                        // Luftmenge je Ventile
                        abluftmengeJeVentil = fAbluftMenge / anzahlAbluftVentile;
                        JTableUtil.setFormattedFloatInTableCell(lmeTabelle, i, ABLUFTMENGEJEVENTIL, abluftmengeJeVentil);
                    }
                    if (!((String) lmeTabelle.getValueAt(i, BELUEFTUNG)).contains("ZU")) {
                        lmeTableModel.setValueAt(null, i, ANZZULUFTVENTILE);
                        lmeTableModel.setValueAt("", i, ZULUFTMENGEJEVENTIL);
                        lmeTableModel.setValueAt("", i, ZULUFTTYPENBEZEICHNUNG);
                    }

//                } else {
//                    lmeTableModel.setValueAt(null, i, ANZABLUFTVENTILE);
//                    lmeTableModel.setValueAt(null, i, ABLUFTMENGEJEVENTIL);
                }
            }
        }
    }

    public void aktualisiereUeberstroemelemente() {
        int row = 0, divisor = 1, anzahlUerberstroem = 0;
        String typ = null;//getLuftmengeUeberstroem(row);
        float fLuftMenge = 0f, fUeberLuftMenge = 0f;
//        float luftmengeJeVentil = 0f;
        HashMap<String, Integer> hmVol = new HashMap();
        row = lmeTabelleUeberstroem.getRowCount();
        Raum r = null;
        raeume = project.getRaeume();
        for (int i = 0; i < row; i++) {
            r = raeume.get(i);
            fUeberLuftMenge = r.getUeberstroemMenge();
            fLuftMenge = getLuftmengeIstLuftmenge(i);
            typ = getLuftmengeUeberstroem(i);
            if (fLuftMenge > 0 && typ != null && !typ.equals("")) {
                if (hmVol.containsKey(typ)) {
                    divisor = hmVol.get(typ);
                } else {
                    divisor = getMaxVolumenstrom(typ);
                    if (divisor == 0) {
                        divisor = Math.round(fLuftMenge);
                    }
                    hmVol.put(typ, divisor);
                }
                // Anzahl Ventile
                fLuftMenge -= fUeberLuftMenge;
                if (fLuftMenge < 0) {
                    fLuftMenge = 0;
                }
                anzahlUerberstroem = (int) Math.ceil(fLuftMenge / divisor);

                JTableUtil.setFormattedFloatInTableCell(lmeTabelleUeberstroem, i, ANZAHLUEBERSTROEM, anzahlUerberstroem);

            } else {
                JTableUtil.setTableCell(lmeTabelleUeberstroem, i, ANZAHLUEBERSTROEM, "");
            }
        }
    }

    /**
     *
     * @param row
     * @return
     */
    public String getLuftmengeUeberstroem(int row) {
        updateData();
        return lmeTableUeberstroemModel.getRowCount() >= 0
                ? (String) lmeTableUeberstroemModel.getValueAt(row, UEBERSTROEMELEMENT)
                : null;
    }
}
