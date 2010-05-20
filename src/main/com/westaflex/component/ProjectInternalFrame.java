/*
 * ProjectInternalFrame.java
 *
 * Created on 11. Juli 2006, 20:25
 */
package com.westaflex.component;

import com.bensmann.superswing.component.util.ConversionUtil;
import com.bensmann.superswing.component.util.JTableUtil;
import com.bensmann.superswing.observer.ShutdownObservable;
import com.bensmann.superswing.observer.ShutdownObserver;
import com.seebass.tools.Tools;
import com.sun.star.beans.NamedValue;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.comp.beans.OfficeDocument;
import com.sun.star.text.XTextFieldsSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XModifiable;
import com.sun.star.util.XRefreshable;
import com.westaflex.component.classes.DocumentAware;
import com.westaflex.component.classes.SeeComboBox;
import com.westaflex.component.classes.SeeTable;
import com.westaflex.dialogs.TeilstreckenAuswahl;
import com.westaflex.WestaWacBerechnungen;
import com.westaflex.component.classes.Raum;
import com.westaflex.component.classes.RaumItem;
import com.westaflex.component.classes.Rooms;
import com.westaflex.component.classes.Tuer;
import com.westaflex.database.WestaDB;
import com.westaflex.dialogs.RaumDialog;
import com.westaflex.image.Description;
import com.westaflex.resource.Strings.Strings;
import com.westaflex.util.WestaWacApplHelper;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author os
 */
public class ProjectInternalFrame extends javax.swing.JInternalFrame
        implements ShutdownObserver {

    private static final Logger logger;
    private static NumberFormat nfDE;
    private static NumberFormat nfUS;    // Static initializer

    static {

        // Java Logging API
        logger = Logger.getLogger(ProjectInternalFrame.class.getName());

        nfDE = NumberFormat.getInstance(Locale.GERMAN);
        nfDE.setGroupingUsed(true);
        nfDE.setMinimumFractionDigits(2);
        nfDE.setMaximumFractionDigits(2);

        nfUS = NumberFormat.getInstance(Locale.US);
        nfUS.setGroupingUsed(true);
        nfUS.setMinimumFractionDigits(2);
        nfUS.setMaximumFractionDigits(2);

    }
    private static int counter;
    public static final int ventCount = 13;
    public static final int luftMax = 7;
    public static final int einsMax = 15;
    public static boolean bZentralgeraet = false;
    public static boolean bUeberstroemVolumenstrom = false;
    private WestaWacApplHelper applHelper;
    private WacMainFrame wacMainFrame;
    private OfficeDocument officeDocument = null;
    private File wpxFile;
    private WestaWacBerechnungen berechnungen;
    private long changed = 0;
    private Rooms raeume = null;

    /**
     * Creates new form ProjectInternalFrame
     *
     * private - soll nur über den Constructor ProjectInternalFrame(WacMainFrame)
     * gerufen werden
     */
    private ProjectInternalFrame() {

        counter++;
        applHelper = WestaWacApplHelper.getInstance();
        raeume = new Rooms();
        initComponents();
        initLayout();

        lmeTabelleTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateLuftmengeTab(false);
            }
        });

        lmeTabelleUeberstroemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateLuftmengeTab(false);
            }
        });

        class DocList implements DocumentListener {

            @Override
            public void changedUpdate(DocumentEvent evt) {
                toggleUebernehmenEnabled();
            }

            @Override
            public void insertUpdate(DocumentEvent evt) {
                toggleUebernehmenEnabled();
            }

            @Override
            public void removeUpdate(DocumentEvent evt) {
                toggleUebernehmenEnabled();
            }
        }
        addPropertyChangeListener("Changed", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                changed = (Long) evt.getNewValue();
                if (!getTitle().endsWith("*")) {
                    setTitle(getTitle() + "*");
                }
            }
        });
    }

    /**
     *
     * @param wacMainFrame
     */
    public ProjectInternalFrame(WacMainFrame wacMainFrame) {

        this();
        this.wacMainFrame = wacMainFrame;
        // Für Shutdown Event registrieren
        wacMainFrame.registerShutdownObserver(this);

        // Titel des InternalFrames setzen
        setProjektTitel();

        // Initialisiere Klasse für Berechnungen
        berechnungen = new WestaWacBerechnungen(this);
    }

    void initLayout() {

        JTableUtil.setColumnSize(dvbTeilstreckenTabelleTable, 10, 0);
//        JTableUtil.setColumnSize(lmeTabelleTable, 7, 0);
//        JTableUtil.setColumnSize(lmeTabelleTable, 8, 0);
        JTableUtil.setColumnSize(lmeTabelleUeberstroemTable, 2, 0);
        getBZentralgeraet();
        updateAbZuTexte();
        setEKZWertLabel();
        setHKZWertLabel();
        setKennzeichen();
        setLuftwechsel();
        setDiffDruck();
        setEnableEnergie();

        //für Auswahl der Überströmelemente in der Überströmtabelle über ComboBox
        SeeComboBox sc02 = new SeeComboBox();
        sc02.mySearch = "select ~Artikelnummer~ from ~artikelstamm~ where ~Klasse~ = 14";
        sc02.fromDatabase();
        sc02.addItem("");
        lmeTabelleUeberstroemTable.getColumnModel().getColumn(5).setCellEditor(
                new DefaultCellEditor(sc02));

        //für Auswahl der Zuluftventil-Bezeichnung in der Tabelle über ComboBox
        SeeComboBox scb2 = new SeeComboBox(dvbVentileinstellungVentilbezeichnungComboBox.makeModelFromComboBox());
        scb2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateLuftmengeTab(false);
            }
        });
        lmeTabelleTable.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(scb2));

        //für Auswahl der Abluftventil-Bezeichnung in der Tabelle über ComboBox
        SeeComboBox scb = new SeeComboBox(dvbVentileinstellungVentilbezeichnungComboBox.makeModelFromComboBox());
        scb.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateLuftmengeTab(false);
            }
        });
        lmeTabelleTable.getColumnModel().getColumn(9).setCellEditor(new DefaultCellEditor(scb));

        //für Auswahl der Verteilebene in der Tabelle über ComboBox
        lmeTabelleTable.getColumnModel().getColumn(10).setCellEditor(
                new DefaultCellEditor(
                new SeeComboBox(
                new javax.swing.DefaultComboBoxModel(
                new String[]{"", "KG", "EG", "OG", "DG", "SB"}))));

        seebassScrollPane.setVisible(false);
    }

    /**
     *
     * @param shutdownObservable
     */
    @Override
    public void processShutdown(ShutdownObservable shutdownObservable) {

        if (queryClosing()) {
            // Projekt verwerfen
            wacMainFrame.removeProject(this);
            wacMainFrame.unregisterShutdownObserver(this);
            dispose();
        }
    }

    /**
     * Zeigt alle Komponenten mit ihren aktuellen Werten an
     */
    public void updateAllComponents() {

        setProjektTitel();

        updateLuftmengeTab(true);
        updateDvbTeilstreckeTab();
        updateAkkustikberechnungZuluft();
        updateAkkustikberechnungAbluft();
    }

    /**
     * berechnet die Gesamtfläche aus den Raumdaten und
     * trägt diese in die Geometriedaten ein
     */
    private void berechneGesamtflaeche() {
        float fFlaeche = 0f;
        if (wfTabelleTable.getRowCount() > -1) {
            for (int i = 0; i < wfTabelleTable.getRowCount(); i++) {
                fFlaeche += JTableUtil.parseFloatFromTableCell(wfTabelleTable, i, 3);
            }
//            if (geoGelueftetesVolumenTextField.getString().isEmpty()) {
            ConversionUtil.setFormattedFloatInComponent(geoWohnflaecheTextField, fFlaeche, Locale.GERMAN);
//            }
            berechneGelueftetesVolumen();
        }
    }

    /**
     * berechnet die mittlere Raumhöhe aus den Raumdaten und
     * trägt diese in die Geometriedaten ein
     */
    private void berechneMittlereRaumhoehe() {
        float fHoehe = 0f;
        if (wfTabelleTable.getRowCount() > -1) {
            for (int i = 0; i < wfTabelleTable.getRowCount(); i++) {
                fHoehe += JTableUtil.parseFloatFromTableCell(wfTabelleTable, i, 4);
            }
            fHoehe = fHoehe / wfTabelleTable.getRowCount();
//            if (geoGelueftetesVolumenTextField.getString().isEmpty()) {
            ConversionUtil.setFormattedFloatInComponent(geoHoeheTextField, fHoehe, Locale.GERMAN);
//            }
        }
    }

    /**
     * ermittelt das gelüftete Volumen aus den vorhandenen Werten
     */
    private void berechneGelueftetesVolumen() {
        float fFlaeche = 0f, fHoehe = 0f, fVol = 0f;

        if (!geoWohnflaecheTextField.getText().isEmpty() && !geoHoeheTextField.getText().isEmpty()) {
            fFlaeche = ConversionUtil.parseFloatFromComponent(geoWohnflaecheTextField);
            fHoehe = ConversionUtil.parseFloatFromComponent(geoHoeheTextField);
            fVol = fFlaeche * fHoehe;
            setGesamtVolumen(fVol);
            if (geoGeluefteteflaecheTextField.getText().isEmpty()) {
                setGelueftetVolumen(fVol);
            }
        }

        if (!geoGeluefteteflaecheTextField.getText().isEmpty() && !geoHoeheTextField.getText().isEmpty()) {
            fFlaeche = ConversionUtil.parseFloatFromComponent(geoGeluefteteflaecheTextField);
            fHoehe = ConversionUtil.parseFloatFromComponent(geoHoeheTextField);
            fVol = fFlaeche * fHoehe;
            setGelueftetVolumen(fVol);
        }

        if (!geoVolumenTextField.getText().isEmpty() && geoGelueftetesVolumenTextField.getText().isEmpty()) {
            setGelueftetVolumen(ConversionUtil.parseFloatFromComponent(geoVolumenTextField));
        }
    }

    /**
     * Berechnung der freien Überström-Querschnittflache und der Spalthöhen der
     * einzelnen Türen
     */
    private void berechneTueren() {
        boolean bUeberstroem = false, bB = false;
        for (int i = 0; i < raeume.getRowCount(); i++) {
            if (!raeume.get(i).getRaumItemValue(RaumItem.PROP.LUFTART).equals("ÜB")) {
                try {
                    if (JTableUtil.parseFloatFromTableCell(lmeTabelleUeberstroemTable, i, 3) > 0) {
                        bUeberstroem = true;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                Raum raum = raeume.get(i);
                if (raum.berechneTuerspalt(bUeberstroem)) {
                    bB = true;
                }
                bUeberstroem = false;
            }
        }
        if (bB) {
            Tools.errbox("Bitte geben Sie alle notwendigen Türdaten ein!");
        }
    }

    /**
     * Energie-Kennzeichen setzen
     */
    private void setEnableEnergie() {
        ekBemessungCheckbox.setEnabled(ekZuAbluftCheckbox.isSelected());
        ekRueckgewinnungCheckbox.setEnabled(ekZuAbluftCheckbox.isSelected());
        ekRegelungCheckbox.setEnabled(ekZuAbluftCheckbox.isSelected());
    }

    /**
     *
     */
    private void updateAkkustikberechnungZuluft() {

        final Object controls[] = {
            abZuTabelleTable, //0
            abZuTabelleDezibelWertLabel, //1
            abZuKanalnetzComboBox, //2
            abZuFilterverschmutzungComboBox, //3
            abZuHauptschalldaempfer1ComboBox, //4
            abZuHauptschalldaempfer2ComboBox, //5
            abZuAnzahlUmlenkungenTextField, //6
            abZuLuftverteilerkastenTextField, //7
            abZuLaengsdaempfungKanalComboBox, //8
            abZuSchalldaempferVentilComboBox, //9
            abZuEinfuegungswertLuftdurchlassComboBox, //00
            abZuRaumabsorptionTextField, //11
            abZuTabelleMittlererSchalldruckpegelWertLabel, //12
            abZuLaengsdaempfungKanalTextField //13
        };
        updateAkkustikberechnung("zu", controls);
    }

    private void updateAkkustikberechnungAbluft() {

        final Object controls[] = {
            abAbTabelleTable, //0
            abAbTabelleDezibelWertLabel, //1
            abAbKanalnetzComboBox, //2
            abAbFilterverschmutzungComboBox, //3
            abAbHauptschalldaempfer1ComboBox, //4
            abAbHauptschalldaempfer2ComboBox, //5
            abAbAnzahlUmlenkungenTextField, //6
            abAbLuftverteilerkastenTextField, //7
            abAbLaengsdaempfungKanalComboBox, //8
            abAbSchalldaempferVentilComboBox, //9
            abAbEinfuegungswertLuftdurchlassComboBox, //00
            abAbRaumabsorptionTextField, //11
            abAbTabelleMittlererSchalldruckpegelWertLabel, //12
            abAbLaengsdaempfungKanalTextField //13
        };
        updateAkkustikberechnung("ab", controls);
    }

    private void updateAkkustikberechnung(String modus, Object[] control) {

        float[] oktavmf = null;
        String sZentralgeraet = "";
        float[] fPegelerhoehungExternerDruck = null;
        float[] fCalcPegelerhoehung = null;
        float f = 0f;

        SeeTable theTable = (SeeTable) control[0];

        // 1. Zuluftstutzen
        oktavmf = applHelper.getOktavmittenfrequenz(
                lmeZentralgeraetCombobox.getString(), lmeVolumenstromCombobox.getString(), modus);
        berechnungen.setAkkustikTableRow(0, theTable, (JLabel) control[1], oktavmf);

        // Für Kanalnetz und Filterverschmutzung
        if (!sZentralgeraet.equals(lmeZentralgeraetCombobox.getString())) {
            sZentralgeraet = lmeZentralgeraetCombobox.getString();
            fPegelerhoehungExternerDruck = applHelper.getPegelerhoehungExternerDruck(sZentralgeraet);
        }

        // 2. Kanalnetz
        fCalcPegelerhoehung = fPegelerhoehungExternerDruck.clone();
        f = Float.valueOf(((DocumentAware) control[2]).getString()) / 100;
        for (int i = 0; i < fCalcPegelerhoehung.length; i++) {
            fCalcPegelerhoehung[i] *= f;
        }
        berechnungen.setAkkustikTableRow(1, theTable, null, fCalcPegelerhoehung);

        // 3. Filterverschmutzung
        fCalcPegelerhoehung = fPegelerhoehungExternerDruck.clone();
        f = Float.valueOf(((DocumentAware) control[3]).getString()) / 100;
        for (int i = 0; i < fCalcPegelerhoehung.length; i++) {
            fCalcPegelerhoehung[i] *= f;
        }
        berechnungen.setAkkustikTableRow(2, theTable, null, fCalcPegelerhoehung);

        // 4. Hauptschalldämpfer 1
        int[] iTab = {4, 5, 9};
        for (int i = 0; i < iTab.length; i++) {
            String s = ((DocumentAware) control[iTab[i]]).getString();
            if (!s.isEmpty()) {
                oktavmf = applHelper.getOktavmittenfrequenz(s, -1);
                berechnungen.setAkkustikTableRow(iTab[i] - 1, theTable, null, oktavmf);
            } else {
                berechnungen.setAkkustikTableRow(iTab[i] - 1, theTable, null, new float[]{0f});
            }
        }

        // 5. Hauptschalldämpfer 2 (siehe 4.)

        // 6. Umlenkungen
        try {
            berechnungen.setAkkustikTableRow(5, theTable, null,
                    new float[]{-0.9f * Float.valueOf(((DocumentAware) control[6]).getString())});
        } catch (NumberFormatException e) {
            berechnungen.setAkkustikTableRow(5, theTable, null, new float[]{});
        }

        // 7. Luftverteilerkasten
        if (((DocumentAware) control[7]).getString().equals("0")) {
            berechnungen.setAkkustikTableRow(6, theTable, null, new float[]{});
        } else {
            berechnungen.setAkkustikTableRow(6, theTable, null, new float[]{-3f});
        }
        // 8. Längsdämpfung Kanal
        @SuppressWarnings("deprecation")
        int i = Integer.parseInt(((JTextField) control[13]).getText());
        oktavmf = applHelper.getOktavmittenfrequenz(((DocumentAware) control[8]).getString(), -i);
        berechnungen.setAkkustikTableRow(7, theTable, null, oktavmf);

        // 9. Schalldäpmfer Ventil (siehe 4.)

        // 10. Einfügungsdämmwert Luftdurchlass
        oktavmf = applHelper.getOktavmittenfrequenz(((DocumentAware) control[10]).getString(), -1);
        berechnungen.setAkkustikTableRow(9, theTable, null, oktavmf);

        // 11. Raumabsorption
        if (((DocumentAware) control[11]).getString().equals("0")) {
            berechnungen.setAkkustikTableRow(10, theTable, null, new float[]{});
        } else {
            berechnungen.setAkkustikTableRow(10, theTable, null, new float[]{-4f});
        }

        // Bewerteter Schallpegel
        berechnungen.berechneBewerteterSchallpegel(theTable);

        // Mittlerer Schalldruckpegel
        berechnungen.berechneMittlererSchalldruckpegel(theTable, (JLabel) control[12]);
    }

    /**
     *
     */
    public void updateDvbTeilstreckeTab() {

        float summe = 0f;
        float cell5 = 0f;

        for (int row = 0; row < dvbTeilstreckenTabelleTable.getRowCount(); row++) {

            summe = JTableUtil.parseFloatFromTableCell(dvbTeilstreckenTabelleTable, row, 7);
            cell5 = JTableUtil.parseFloatFromTableCell(dvbTeilstreckenTabelleTable, row, 5);

            // Einzelwiderstände Pa
            JTableUtil.setFormattedFloatInTableCell(
                    dvbTeilstreckenTabelleTable, row, 8,
                    0.6f * summe * cell5 * cell5);
            JTableUtil.setFormattedFloatInTableCell(
                    dvbTeilstreckenTabelleTable, row, 9,
                    JTableUtil.parseFloatFromTableCell(dvbTeilstreckenTabelleTable, row, 6)
                    + JTableUtil.parseFloatFromTableCell(dvbTeilstreckenTabelleTable, row, 8));

        }
    }

    /**
     * Zeigt alle (meist errechneten) Komponenten mit ihren akutellen Werten
     * an: Luftmenge
     */
    public void updateLuftmengeTab(boolean b) {

        berechnungen.luftmengeBerechnen();
//        String sStufe = lmeVolumenstromCombobox.getString();
        float fZuluftmenge = ConversionUtil.parseFloatFromComponent(lmeSummeZuluftmengeWertLabel);
        float fAbluftmenge = ConversionUtil.parseFloatFromComponent(lmeSummeAbluftmengeWertLabel);
//        float fGrundlueftung1 = (fZuluftmenge > fAbluftmenge ? fZuluftmenge : fAbluftmenge);
        float fGrundlueftung1 = Math.max(fAbluftmenge, fZuluftmenge);
        float fInfiltration = 0f;
        if (getWirkInfiltration()) {
            fInfiltration = berechnungen.berechneInfiltration(true);
        }
        float fGrundlueftung = fGrundlueftung1 - fInfiltration;
        float fMindestlueftung = 0.7f * fGrundlueftung1 - fInfiltration;
        float fIntensivlueftung = 1.3f * fGrundlueftung1 - fInfiltration;
        float fFeuchtelueftung = getWaermeschutz() * fGrundlueftung1 - fInfiltration;
        float fMinimum = Float.parseFloat(lmeVolumenstromCombobox.getItemAt(0).toString());
        if (lmeZentralgeraetCombobox.getSelectedItem().toString().contains("400WAC")) {
            fMinimum = 75;
        }
        if (fMindestlueftung < fMinimum) {
            fMindestlueftung = fMinimum;
        }
        if (fFeuchtelueftung < fMinimum) {
            fFeuchtelueftung = fMinimum;
        }
        ConversionUtil.setFormattedFloatInComponent(lmeGrundlueftungWertLabel, round5(fGrundlueftung), Locale.GERMAN);
        ConversionUtil.setFormattedFloatInComponent(lmeMindestlueftungWertLabel, round5(fMindestlueftung), Locale.GERMAN);
        ConversionUtil.setFormattedFloatInComponent(lmeFeuchteschutzWertLabel, round5(fFeuchtelueftung), Locale.GERMAN);
        ConversionUtil.setFormattedFloatInComponent(lmeIntensivlueftungWertLabel, round5(fIntensivlueftung), Locale.GERMAN);
        setGeraeteauswahl(round5(fGrundlueftung));
        // Versteckte *SummeWertLabel setzen
        ConversionUtil.setFormattedFloatInComponent(lmeAbSummeWertLabel, berechnungen.sumVolumen("AB"), Locale.GERMAN);
        ConversionUtil.setFormattedFloatInComponent(lmeZuSummeWertLabel, berechnungen.sumVolumen("ZU"), Locale.GERMAN);
        ConversionUtil.setFormattedFloatInComponent(lmeUebSummeWertLabel, berechnungen.sumVolumen("ÜB"), Locale.GERMAN);
        berechnungen.aktualisiereVentile();
        setSumLTMZuluftmengeWertLabel();
        setSumLTMAbluftmengeWertLabel();
        berechnungen.aktualisiereUeberstroemelemente();
    }

    /**
     * 
     * Dezimalzahl auf 5 runden
     * 
     * @param aFactor
     * @return
     */
    public int round5(float fFactor) {
        return 5 * (Math.round(fFactor / 5));
    }

    /**
     * Ausgabe der Gesamtvolumenströme
     */
    public void berechneAussenluftvolumenstroeme() {
        float fGesAU = round5(berechnungen.berechneGesAU_NE() * getWaermeschutz() * getBesAnforderFaktor());
        float fVolIn = round5(berechnungen.berechneInfiltration(false));
        // Ausgabe der Notwendigkeit der LTM
        setVsGesamtAUVolumenWertLabel(fGesAU);
        setVsVolumenstromInfilWertLabel(fVolIn);
        if (fGesAU > fVolIn) {
            vsLTMerforderlichWertLabel.setText("Lüftungstechnische Maßnahmen erforderlich!");
        } else {
            vsLTMerforderlichWertLabel.setText("");
        }
        float fZuluftmenge = 0f, fAbluftmenge = 0f;
        berechnungen.luftmengeAutoBerechnen(false);
        if (!lmeSummeZuluftmengeWertLabel.getText().isEmpty()) {
            fZuluftmenge = ConversionUtil.parseFloatFromComponent(lmeSummeZuluftmengeWertLabel);
        }
        if (!lmeSummeAbluftmengeWertLabel.getText().isEmpty()) {
            fAbluftmenge = ConversionUtil.parseFloatFromComponent(lmeSummeAbluftmengeWertLabel);
        }
        float fGrundlueftung1 = berechnungen.berechneGesAU_NE();
        float fVol = 0f, fInfiltration = 0f;
        if (getGelueftetVolumen() != 0) {
            fVol = getGelueftetVolumen();
        } else {
            fVol = berechnungen.sumVolumen();
        }
        // Ausgabe der Gesamt-Außenluftvolumenströme
        float fGrundlueftung = fGrundlueftung1;
        setVsAUGrundlueftungWertLabel(round5(fGrundlueftung));
        setVsAUGLLuftwechselWertLabel(fGrundlueftung / fVol);
        float fMindestlueftung = 0.7f * fGrundlueftung1;
        setVsAUMindestlueftungWertLabel(round5(fMindestlueftung));
        setVsAUMLLuftwechselWertLabel(fMindestlueftung / fVol);
        float fIntensivlueftung = 1.3f * fGrundlueftung1;
        setVsAUIntensivlueftungWertLabel(round5(fIntensivlueftung));
        setVsAUILLuftwechselWertLabel(fIntensivlueftung / fVol);
        float fFeuchtelueftung = getWaermeschutz() * fGrundlueftung1;
        setVsAUFeuchteschutzWertLabel(round5(fFeuchtelueftung));
        setVsAUFELuftwechselWertLabel(fFeuchtelueftung / fVol);
        // Ausgabe der Gesamt-Raumabluft-Volumenströme
        int rows = lmeTabelleTable.getRowCount();
        float fRaumAbluft = 0f;
        for (int i = 0; i < rows; i++) {
            if (!((String) lmeTabelleTable.getValueAt(i, 1)).contains("ÜB")) {
                if (((String) lmeTabelleTable.getValueAt(i, 1)).contains("AB")) {
                    fRaumAbluft += JTableUtil.parseFloatFromTableCell(getWfTabelleTable(), i, 6);
                }
            }//Gesamt-Abluft-Volumenstrom
        }
        fGrundlueftung1 = fRaumAbluft;
        fGrundlueftung = fGrundlueftung1;
        setVsRAbGrundlueftungWertLabel(round5(fGrundlueftung));
        setVsRAbGLLuftwechselWertLabel(fGrundlueftung / fVol);
        fMindestlueftung = 0.7f * fGrundlueftung1;
        setVsRAbMindestlueftungWertLabel(round5(fMindestlueftung));
        setVsRAbMLLuftwechselWertLabel(fMindestlueftung / fVol);
        fIntensivlueftung = 1.3f * fGrundlueftung1;
        setVsRAbIntensivlueftungWertLabel(round5(fIntensivlueftung));
        setVsRAbILLuftwechselWertLabel(fIntensivlueftung / fVol);
        fFeuchtelueftung = getWaermeschutz() * fGrundlueftung1;
        setVsRAbFeuchteschutzWertLabel(round5(fFeuchtelueftung));
        setVsRAbFELuftwechselWertLabel(fFeuchtelueftung / fVol);
        // Ausgabe der personenbezogenen Gesamtaußenluftvolumenströme
        fGrundlueftung1 = berechnungen.berechneGesAU_Pers();
        fGrundlueftung = fGrundlueftung1;
        setVsPersGrundlueftungWertLabel(round5(fGrundlueftung));
        setVsPersGLLuftwechselWertLabel(fGrundlueftung / fVol);
        fMindestlueftung = 0.7f * fGrundlueftung1;
        setVsPersMindestlueftungWertLabel(round5(fMindestlueftung));
        setVsPersMLLuftwechselWertLabel(fMindestlueftung / fVol);
        fIntensivlueftung = 1.3f * fGrundlueftung1;
        setVsPersIntensivlueftungWertLabel(round5(fIntensivlueftung));
        setVsPersILLuftwechselWertLabel(fIntensivlueftung / fVol);
        fFeuchtelueftung = getWaermeschutz() * fGrundlueftung1;
        setVsPersFeuchteschutzWertLabel(round5(fFeuchtelueftung));
        setVsPersFELuftwechselWertLabel(fFeuchtelueftung / fVol);
        // Ausgabe der Volumenströme für LTM
        fGrundlueftung1 = Math.max(fAbluftmenge, fZuluftmenge);
        fGrundlueftung1 = Math.max(berechnungen.berechneGesAU_NE(), fGrundlueftung1);
        fGrundlueftung1 = Math.max(berechnungen.berechneGesAU_Pers(), fGrundlueftung1);
        if (getWirkInfiltration()) {
            fInfiltration = berechnungen.berechneInfiltration(true);
        }
        fGrundlueftung = fGrundlueftung1 - fInfiltration;
        setVsGrundlueftungWertLabel(round5(fGrundlueftung));
        setVsGLLuftwechselWertLabel(fGrundlueftung / fVol);
        fMindestlueftung = 0.7f * fGrundlueftung1 - fInfiltration;
        setVsMindestlueftungWertLabel(round5(fMindestlueftung));
        setVsMLLuftwechselWertLabel(fMindestlueftung / fVol);
        fIntensivlueftung = 1.3f * fGrundlueftung1 - fInfiltration;
        setVsIntensivlueftungWertLabel(round5(fIntensivlueftung));
        setVsILLuftwechselWertLabel(fIntensivlueftung / fVol);
    }

    public float checkZuluftfaktor(float fFak) {
        float zuluftfaktor = fFak;
        if (wfBezeichnungCombobox.getString().contains("Wohn")) {
            if (zuluftfaktor < 2.5) {
                Tools.msgbox("Der Zuluftfaktor wird von " + zuluftfaktor + " auf 2.5 (laut Norm-Tolerenz) geändert!");
                zuluftfaktor = 2.5f;
            }
            if (zuluftfaktor > 3.5) {
                Tools.msgbox("Der Zuluftfaktor wird von " + zuluftfaktor + " auf 3.5 (laut Norm-Tolerenz) geändert!");
                zuluftfaktor = 3.5f;
            }
        } else if (wfBezeichnungCombobox.getString().contains("Kind")
                || wfBezeichnungCombobox.getString().contains("Schlaf")) {
            if (zuluftfaktor < 1.0) {
                Tools.msgbox("Der Zuluftfaktor wird von " + zuluftfaktor + " auf 1.0 (laut Norm-Tolerenz) geändert!");
                zuluftfaktor = 1.0f;
            }
            if (zuluftfaktor > 3.0) {
                Tools.msgbox("Der Zuluftfaktor wird von " + zuluftfaktor + " auf 3.0 (laut Norm-Tolerenz) geändert!");
                zuluftfaktor = 3.0f;
            }
        } else if (wfBezeichnungCombobox.getString().contains("Ess")
                || wfBezeichnungCombobox.getString().contains("Arbeit")
                || wfBezeichnungCombobox.getString().contains("Gäste")) {
            if (zuluftfaktor < 1.0) {
                Tools.msgbox("Der Zuluftfaktor wird von " + zuluftfaktor + " auf 1.0 (laut Norm-Tolerenz) geändert!");
                zuluftfaktor = 1.0f;
            }
            if (zuluftfaktor > 2.0) {
                Tools.msgbox("Der Zuluftfaktor wird von " + zuluftfaktor + " auf 2.0 (laut Norm-Tolerenz) geändert!");
                zuluftfaktor = 2.0f;
            }
        }
        return zuluftfaktor;
    }

    public void setZuluftfaktor(float fFak) {
        float zuluftfaktor = 0f;
        if (wfBezeichnungCombobox.getString().contains("Wohn")) {
            zuluftfaktor = 3f;
            ConversionUtil.setFormattedFloatInComponent(wfZuluftfaktorTextField, zuluftfaktor, Locale.GERMAN);
        }
        if (wfBezeichnungCombobox.getString().contains("Kind")
                || wfBezeichnungCombobox.getString().contains("Schlaf")) {
            zuluftfaktor = 2f;
            ConversionUtil.setFormattedFloatInComponent(wfZuluftfaktorTextField, zuluftfaktor, Locale.GERMAN);
        }
        if (wfBezeichnungCombobox.getString().contains("Ess")
                || wfBezeichnungCombobox.getString().contains("Arbeit")
                || wfBezeichnungCombobox.getString().contains("Gäste")) {
            zuluftfaktor = 1.5f;
            ConversionUtil.setFormattedFloatInComponent(wfZuluftfaktorTextField, zuluftfaktor, Locale.GERMAN);
        }
    }

    public void aktualisiereTabelle() {

        Raum raum = null;
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
        nf.setGroupingUsed(true);
        nf.setMinimumFractionDigits(2);
        for (int i = 0; i < wfTabelleTable.getRowCount(); i++) {
            raum = raeume.get(i);
            // Raumtabelle aktualisieren
            JTableUtil.setTableCell(wfTabelleTable, i, 0,
                    (String) raum.getRaumItemValue(RaumItem.PROP.RAUMNAME));
            JTableUtil.setTableCell(wfTabelleTable, i, 1,
                    (String) raum.getRaumItemValue(RaumItem.PROP.GESCHOSS));
            JTableUtil.setTableCell(wfTabelleTable, i, 2,
                    (String) raum.getRaumItemValue(RaumItem.PROP.LUFTART));
            try {
                JTableUtil.setFormattedFloatInTableCell(wfTabelleTable, i, 3,
                        (Float) raum.getRaumItemValue(RaumItem.PROP.RAUMFLAECHE));
            } catch (Exception e) {
                JTableUtil.setTableCell(wfTabelleTable, i, 3,
                        (String) raum.getRaumItemValue(RaumItem.PROP.RAUMFLAECHE));
            }
            try {
                JTableUtil.setFormattedFloatInTableCell(wfTabelleTable, i, 4,
                        (Float) raum.getRaumItemValue(RaumItem.PROP.RAUMHOEHE));
            } catch (Exception e) {
                JTableUtil.setTableCell(wfTabelleTable, i, 4,
                        (String) raum.getRaumItemValue(RaumItem.PROP.RAUMHOEHE));
            }
            JTableUtil.setFormattedFloatInTableCell(wfTabelleTable, i, 5,
                    (Float) raum.getRaumItemValue(RaumItem.PROP.FAKTOR_ZULUFTVERTEILUNG));
            try {
                JTableUtil.setFormattedFloatInTableCell(wfTabelleTable, i, 6,
                        (Float) raum.getRaumItemValue(RaumItem.PROP.VORGABELUFTMENGE));
            } catch (Exception e) {
                JTableUtil.setTableCell(wfTabelleTable, i, 6,
                        (String) raum.getRaumItemValue(RaumItem.PROP.VORGABELUFTMENGE));
            }
            // Zu-/Ablufttabelle aktualisieren
            JTableUtil.setTableCell(lmeTabelleTable, i, 0,
                    (String) raeume.get(i).getRaumItemValue(RaumItem.PROP.RAUMNAME));
            JTableUtil.setTableCell(lmeTabelleTable, i, 1,
                    (String) raeume.get(i).getRaumItemValue(RaumItem.PROP.LUFTART));
            try {
                JTableUtil.setFormattedFloatInTableCell(lmeTabelleTable, i, 2,
                        (Float) raum.getRaumItemValue(RaumItem.PROP.RAUMVOLUMEN));
            } catch (Exception e) {
                JTableUtil.setTableCell(lmeTabelleTable, i, 2,
                        (String) (raum.getRaumItemValue(RaumItem.PROP.RAUMVOLUMEN)));
            }
            // Überströmtabelle aktualisieren
            JTableUtil.setTableCell(lmeTabelleUeberstroemTable, i, 0,
                    (String) raum.getRaumItemValue(RaumItem.PROP.RAUMNAME));
            JTableUtil.setTableCell(lmeTabelleUeberstroemTable, i, 1,
                    (String) raum.getRaumItemValue(RaumItem.PROP.LUFTART));
            try {
                JTableUtil.setFormattedFloatInTableCell(lmeTabelleUeberstroemTable, i, 2,
                        (Float) raum.getRaumItemValue(RaumItem.PROP.RAUMVOLUMEN));
            } catch (Exception e) {
                JTableUtil.setTableCell(lmeTabelleUeberstroemTable, i, 2,
                        (String) raum.getRaumItemValue(RaumItem.PROP.RAUMVOLUMEN));
            }
        }

        berechneMittlereRaumhoehe();
        berechneGesamtflaeche();
    }

    public Rooms getRaeume() {
        return raeume;
    }

    public void setRaeume(Rooms raeume) {
        this.raeume = raeume;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        geraetestandortButtonGroup = new com.westaflex.component.classes.SeeButtonGroup();
        geraetestandortButtonGroup.setName("geraetestandortButtonGroup");
        geraetestandortButtonGroup.setButtonValues(new String[]{"KG", "EG", "OG", "DG", "SB"});
        fortluftButtonGroup = new com.westaflex.component.classes.SeeButtonGroup();
        fortluftButtonGroup.setName("fortluftButtonGroup");
        fortluftButtonGroup.setButtonValues(new String[]{"Dach", "Wand", "Wand"});
        aussenluftButtonGroup = new com.westaflex.component.classes.SeeButtonGroup();
        aussenluftButtonGroup.setName("aussenluftButtonGroup");
        aussenluftButtonGroup.setButtonValues(new String[]{"Dach", "Wand", "EWT"});
        gebaeudetypButtonGroup = new com.westaflex.component.classes.SeeButtonGroup();
        gebaeudetypButtonGroup.setName("gebaeudetypButtonGroup"); gebaeudetypButtonGroup.setButtonValues(new String[]{"MFH", "EFH", "Maison"});
        gebaeudelageButtonGroup = new com.westaflex.component.classes.SeeButtonGroup();
        gebaeudelageButtonGroup.setName("gebaeudelageButtonGroup"); gebaeudelageButtonGroup.setButtonValues(new String[]{"schwach", "stark"});
        waermeschutzButtonGroup = new com.westaflex.component.classes.SeeButtonGroup();
        waermeschutzButtonGroup.setName("waermeschutzButtonGroup"); waermeschutzButtonGroup.setButtonValues(new String[]{"hoch", "niedrig"});
        luftdichtheitButtonGroup = new com.westaflex.component.classes.SeeButtonGroup();
        luftdichtheitButtonGroup.setName("luftdichtheitButtonGroup"); luftdichtheitButtonGroup.setButtonValues(new String[]{"A", "B","C","M"});
        projektTabbedPane = new javax.swing.JTabbedPane();
        auslegungsdatenPanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeePanel adGrosshandelPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel ghFirmaLabel = new javax.swing.JLabel();
        com.westaflex.component.classes.SeeTextField ghFirma1TextField = new com.westaflex.component.classes.SeeTextField();
        com.westaflex.component.classes.SeeTextField ghFirma2TextField = new com.westaflex.component.classes.SeeTextField();
        javax.swing.JLabel ghStrasseLabel = new javax.swing.JLabel();
        com.westaflex.component.classes.SeeTextField ghStrasseTextfield = new com.westaflex.component.classes.SeeTextField();
        javax.swing.JLabel ghPlzOrtLabel = new javax.swing.JLabel();
        com.westaflex.component.classes.SeeTextField ghPlzOrtTextField = new com.westaflex.component.classes.SeeTextField();
        javax.swing.JLabel ghTelefonLabel = new javax.swing.JLabel();
        com.westaflex.component.classes.SeeTextField ghTelefonTextField = new com.westaflex.component.classes.SeeTextField();
        javax.swing.JLabel ghFaxLabel = new javax.swing.JLabel();
        com.westaflex.component.classes.SeeTextField ghFaxTextField = new com.westaflex.component.classes.SeeTextField();
        javax.swing.JLabel ghAnsprechpartnerLabel = new javax.swing.JLabel();
        com.westaflex.component.classes.SeeTextField ghAnsprechpartnerTextField = new com.westaflex.component.classes.SeeTextField();
        com.westaflex.component.classes.SeePanel adAusfuehrendeFirmaPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel afFirmaLabel = new javax.swing.JLabel();
        afFirma1TextField = new com.westaflex.component.classes.SeeTextField();
        afFirma2TextField = new com.westaflex.component.classes.SeeTextField();
        javax.swing.JLabel afStrasseLabel = new javax.swing.JLabel();
        afStrasseTextField = new com.westaflex.component.classes.SeeTextField();
        javax.swing.JLabel afPlzOrtLabel = new javax.swing.JLabel();
        afPlzOrtTextField = new com.westaflex.component.classes.SeeTextField();
        javax.swing.JLabel afTelefonLabel = new javax.swing.JLabel();
        afTelefonTextField = new com.westaflex.component.classes.SeeTextField();
        javax.swing.JLabel afFaxLabel = new javax.swing.JLabel();
        afFaxTextField = new com.westaflex.component.classes.SeeTextField();
        javax.swing.JLabel afAnsprechpartnerLabel = new javax.swing.JLabel();
        afAnsprechpartnerTextField = new com.westaflex.component.classes.SeeTextField();
        com.westaflex.component.classes.SeePanel adNotizenPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel adBauvorhabenLabel = new javax.swing.JLabel();
        adBauvorhabenTextField = new com.westaflex.component.classes.SeeTextField();
        javax.swing.JLabel adNotizenLabel = new javax.swing.JLabel();
        adNotizenScrollPane = new javax.swing.JScrollPane();
        com.westaflex.component.classes.SeeTextArea adNotizenTextArea = new com.westaflex.component.classes.SeeTextArea();
        com.westaflex.component.classes.SeePanel gebaeudePanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeePanel gebaeudetypPanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeeRadioButton gtMFHRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton gtEFHRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton gtMaisonetteRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeePanel gebaeudelagePanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeeRadioButton glWschwachRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton glWstarkRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeePanel waermeschutzPanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeeRadioButton wsHochRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton wsNiedrigRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeePanel luftdichtheitPanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeeRadioButton ldKatARadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton ldKatBRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton ldKatCRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton ldMessRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        ldDruckDiffTextField = new javax.swing.JTextField();
        javax.swing.JLabel ldDruckDiffLabel = new javax.swing.JLabel();
        ldLuftwechselTextField = new javax.swing.JTextField();
        ldDruckExpoTextField = new javax.swing.JTextField();
        javax.swing.JLabel ldLuftwechselLabel = new javax.swing.JLabel();
        javax.swing.JLabel ldDruckExpoLabel = new javax.swing.JLabel();
        com.westaflex.component.classes.SeePanel besAnforderungPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel bAnforderungLabel = new javax.swing.JLabel();
        bAnforderungTextField = new com.westaflex.component.classes.SeeTextField();
        com.westaflex.component.classes.SeePanel geometriePanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel geoWohnflaecheMLabel = new javax.swing.JLabel();
        javax.swing.JLabel geoHeoheMLabel = new javax.swing.JLabel();
        javax.swing.JLabel geoVolumenMLabel = new javax.swing.JLabel();
        javax.swing.JLabel geoHeoheTextLabel = new javax.swing.JLabel();
        javax.swing.JLabel geoWohnflaecheTextLabel = new javax.swing.JLabel();
        javax.swing.JLabel geoVolumenTextLabel = new javax.swing.JLabel();
        javax.swing.JLabel geoGeluefteteflaecheMLabel = new javax.swing.JLabel();
        javax.swing.JLabel geoGeluefteteflaecheTextLabel = new javax.swing.JLabel();
        javax.swing.JLabel geoGeVolumenMLabel = new javax.swing.JLabel();
        javax.swing.JLabel geoGeVolumenTextLabel = new javax.swing.JLabel();
        geoWohnflaecheTextField = new com.westaflex.component.classes.SeeTextField();
        geoHoeheTextField = new com.westaflex.component.classes.SeeTextField();
        geoVolumenTextField = new com.westaflex.component.classes.SeeTextField();
        geoGeluefteteflaecheTextField = new com.westaflex.component.classes.SeeTextField();
        geoGelueftetesVolumenTextField = new com.westaflex.component.classes.SeeTextField();
        com.westaflex.component.classes.SeePanel geplanteBelegungPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel personenanzahlLabel = new javax.swing.JLabel();
        personenanzahlSpinner = new com.westaflex.component.classes.SeeSpinner();
        javax.swing.JLabel mindestaussenluftrateLabel = new javax.swing.JLabel();
        mindestaussenluftrateWertLabel = new javax.swing.JLabel();
        javax.swing.JLabel jLabel38 = new javax.swing.JLabel();
        javax.swing.JLabel volumenProPersonLabel = new javax.swing.JLabel();
        volumenProPersonSpinner = new com.westaflex.component.classes.SeeSpinner();
        com.westaflex.component.classes.SeePanel informationenPanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeePanel geraetestandortPanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeeRadioButton gsKellergeschossRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton gsErdgeschossRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton gsObergeschossRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton gsDachgeschossRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton gsSpitzbodenRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeePanel luftkanalverlegungPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel lkLabel = new javax.swing.JLabel();
        com.westaflex.component.classes.SeeCheckBox lkAufputzCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeeCheckBox lkDaemmschichtCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeeCheckBox lkDeckeCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeeCheckBox lkSpitzbodenCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeePanel aussenluftPanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeeRadioButton rbAlDachdurchfuehrung = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton rbAlWand = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton rbAlErdwaermetauscher = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeePanel luftzulaessePanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeeCheckBox lzTellerventileCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeeCheckBox lzSchlitzauslassCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeeCheckBox lzFussbodenAuslassCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeeCheckBox lzSockelquellauslassCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeePanel luftauslaessePanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeeCheckBox laTellerventileCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeePanel fortluftPanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeeRadioButton flDachdurchfuehrungRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton flWandRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeeRadioButton flLichtschachtRadioButton = new com.westaflex.component.classes.SeeRadioButton();
        com.westaflex.component.classes.SeePanel hygieneKennzeichenPanel = new com.westaflex.component.classes.SeePanel();
        hkAusfuehrungCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        hkFilterungCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        hkVerschmutzungCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        hkKennzeichenWertLabel = new com.westaflex.component.classes.SeeLabel();
        hkDichtheitsklasseCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeePanel energieKennzeichenPanel = new com.westaflex.component.classes.SeePanel();
        ekBemessungCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        ekRueckgewinnungCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        ekRegelungCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        ekZuAbluftCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        ekKennzeichenWertLabel = new com.westaflex.component.classes.SeeLabel();
        seebassScrollPane = new javax.swing.JScrollPane();
        seebassTabelle = new javax.swing.JTable(){
            public String getColumnName(int i){
                return getModel().getColumnName(i);
            }
        };
        com.westaflex.component.classes.SeePanel kzRueckschlagPanel = new com.westaflex.component.classes.SeePanel();
        kzRueckschlagklappeCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeePanel kzSchallschutzPanel = new com.westaflex.component.classes.SeePanel();
        kzSchallschutzCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeePanel kzFeuerPanel = new com.westaflex.component.classes.SeePanel();
        kzFeuerstaetteCheckBox = new com.westaflex.component.classes.SeeCheckBox();
        com.westaflex.component.classes.SeePanel luftauslaessePanel4 = new com.westaflex.component.classes.SeePanel();
        kzKennzeichenLabel = new com.westaflex.component.classes.SeeLabel();
        com.westaflex.component.classes.SeePanel wohnflaechePanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JScrollPane wfTabelleScrollPane = new javax.swing.JScrollPane();
        wfTabelleTable = new com.westaflex.component.classes.SeeTable();
        wfTabelleTable.setFirstRowCol(3,2);
        wfTabelleTable.setLastCol(5);
        javax.swing.JSpinner wfTabelleSpinner = new javax.swing.JSpinner();
        com.westaflex.component.classes.SeePanel wfNeuerEintragPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel wfBezeichnungLabel = new javax.swing.JLabel();
        wfBezeichnungCombobox = new com.westaflex.component.classes.SeeComboBox();
        wfGeschossCombobox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel wfGeschossLabel = new javax.swing.JLabel();
        wfBelueftungCombobox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel wfFlaecheLabel = new javax.swing.JLabel();
        wfFlaecheTextField = new javax.swing.JTextField();
        wfHoeheTextField = new javax.swing.JTextField();
        javax.swing.JLabel wfHoeheLabel = new javax.swing.JLabel();
        javax.swing.JButton wfHinzufuegenButton = new javax.swing.JButton();
        javax.swing.JLabel wfBelueftungLabel = new javax.swing.JLabel();
        javax.swing.JLabel wfHoeheLabel1 = new javax.swing.JLabel();
        wfZuluftfaktorTextField = new javax.swing.JTextField();
        javax.swing.JLabel wfAbluftVolumenLabel = new javax.swing.JLabel();
        wfAbluftVolumenTextField = new javax.swing.JTextField();
        javax.swing.JLabel wfRaumnameLabel = new javax.swing.JLabel();
        wfRaumNameTextField = new javax.swing.JTextField();
        javax.swing.JButton wfEntfernenButton = new javax.swing.JButton();
        javax.swing.JButton wfRaumBearbeiten = new javax.swing.JButton();
        javax.swing.JButton wfRaumKopieren = new javax.swing.JButton();
        com.westaflex.component.classes.SeePanel volumenstroemePanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeePanel vsInfo1Panel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel vsGesamtvolumenLabel = new javax.swing.JLabel();
        vsGesamtAUVolumenWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsVolumentstromInfilLabel = new javax.swing.JLabel();
        vsVolumenstromInfilWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsVolumenstromInfilM3Label = new javax.swing.JLabel();
        javax.swing.JLabel vsGesAussenluftmengeM3Label = new javax.swing.JLabel();
        vsLTMerforderlichWertLabel = new com.westaflex.component.classes.SeeLabel();
        jPanel10 = new javax.swing.JPanel();
        vsInfo2Panel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel vsMindestlueftungLabel = new javax.swing.JLabel();
        javax.swing.JLabel vsGrundlueftungLabel = new javax.swing.JLabel();
        javax.swing.JLabel vsIntensivlueftungLabel = new javax.swing.JLabel();
        wirkInfiltrationCheckbox = new com.westaflex.component.classes.SeeCheckBox();
        javax.swing.JLabel vsLuftvolumenstromLabel = new javax.swing.JLabel();
        javax.swing.JLabel vsLuftwechselLabel = new javax.swing.JLabel();
        vsMindestlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungM3Label1 = new javax.swing.JLabel();
        vsMLLuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungHLabel1 = new javax.swing.JLabel();
        vsGrundlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        vsIntensivlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungM3Label2 = new javax.swing.JLabel();
        javax.swing.JLabel vsLueftungM3Label3 = new javax.swing.JLabel();
        vsGLLuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        vsILLuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungHLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel vsLueftungHLabel3 = new javax.swing.JLabel();
        javax.swing.JButton vsLuftmengeBerechnenButton = new javax.swing.JButton();
        vsInfo6Panel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel vsMindestlueftungLabel2 = new javax.swing.JLabel();
        vsRAbFELuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungHLabel8 = new javax.swing.JLabel();
        javax.swing.JLabel vsGrundlueftungLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel vsIntensivlueftungLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel vsFeuchtelueftungLabel2 = new javax.swing.JLabel();
        vsRAbFeuchteschutzWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungM3Label8 = new javax.swing.JLabel();
        javax.swing.JLabel vsLuftvolumenstromLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel vsLuftwechselLabel2 = new javax.swing.JLabel();
        vsRAbMindestlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungM3Label9 = new javax.swing.JLabel();
        vsRAbMLLuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungHLabel9 = new javax.swing.JLabel();
        vsRAbGrundlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        vsRAbIntensivlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungM3Label10 = new javax.swing.JLabel();
        javax.swing.JLabel vsLueftungM3Label11 = new javax.swing.JLabel();
        vsRAbGLLuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        vsRAbILLuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungHLabel10 = new javax.swing.JLabel();
        javax.swing.JLabel vsLueftungHLabel11 = new javax.swing.JLabel();
        vsInfo3Panel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel vsMindestlueftungLabel1 = new javax.swing.JLabel();
        vsAUFELuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungHLabel4 = new javax.swing.JLabel();
        javax.swing.JLabel vsGrundlueftungLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel vsIntensivlueftungLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel vsFeuchtelueftungLabel1 = new javax.swing.JLabel();
        vsAUFeuchteschutzWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungM3Label4 = new javax.swing.JLabel();
        javax.swing.JLabel vsLuftvolumenstromLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel vsLuftwechselLabel1 = new javax.swing.JLabel();
        vsAUMindestlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungM3Label5 = new javax.swing.JLabel();
        vsAUMLLuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungHLabel5 = new javax.swing.JLabel();
        vsAUGrundlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        vsAUIntensivlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungM3Label6 = new javax.swing.JLabel();
        javax.swing.JLabel vsLueftungM3Label7 = new javax.swing.JLabel();
        vsAUGLLuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        vsAUILLuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungHLabel6 = new javax.swing.JLabel();
        javax.swing.JLabel vsLueftungHLabel7 = new javax.swing.JLabel();
        vsInfo5Panel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel vsMindestlueftungLabel3 = new javax.swing.JLabel();
        vsPersFELuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungHLabel12 = new javax.swing.JLabel();
        javax.swing.JLabel vsGrundlueftungLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel vsIntensivlueftungLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel vsFeuchtelueftungLabel3 = new javax.swing.JLabel();
        vsPersFeuchteschutzWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungM3Label12 = new javax.swing.JLabel();
        javax.swing.JLabel vsLuftvolumenstromLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel vsLuftwechselLabel3 = new javax.swing.JLabel();
        vsPersMindestlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungM3Label13 = new javax.swing.JLabel();
        vsPersMLLuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungHLabel13 = new javax.swing.JLabel();
        vsPersGrundlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        vsPersIntensivlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungM3Label14 = new javax.swing.JLabel();
        javax.swing.JLabel vsLueftungM3Label15 = new javax.swing.JLabel();
        vsPersGLLuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        vsPersILLuftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel vsLueftungHLabel14 = new javax.swing.JLabel();
        javax.swing.JLabel vsLueftungHLabel15 = new javax.swing.JLabel();
        com.westaflex.component.classes.SeePanel luftmengenermittlungPanel = new com.westaflex.component.classes.SeePanel();
        com.westaflex.component.classes.SeePanel lmeInfo1Panel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel lmeGesamtvolumenLabel = new javax.swing.JLabel();
        lmeGesamtvolumenWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel lmeGesamtvolumenM3Label = new javax.swing.JLabel();
        javax.swing.JLabel lmeGebaeudeluftwechselLabel = new javax.swing.JLabel();
        lmeGebaeudeluftwechselWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel lmeGebaeudeluftwechselHLabel = new javax.swing.JLabel();
        lmeInfo2Panel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel lmeMindestlueftungLabel = new javax.swing.JLabel();
        lmeMindestlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel lmeMindestlueftungM3Label = new javax.swing.JLabel();
        javax.swing.JLabel lmeGrundlueftungLabel = new javax.swing.JLabel();
        lmeGrundlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel lmeGrundlueftungM3Label = new javax.swing.JLabel();
        lmeZentralgeraetCombobox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel lmeIntensivlueftungLabel = new javax.swing.JLabel();
        lmeIntensivlueftungWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel lmeIntensivlueftungM3Label = new javax.swing.JLabel();
        lmeVolumenstromCombobox = new com.westaflex.component.classes.SeeComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        javax.swing.JLabel lmeFeuchtelueftungLabel = new javax.swing.JLabel();
        lmeFeuchteschutzWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel lmeFeuchteschutzM3Label = new javax.swing.JLabel();
        javax.swing.JLabel lmeGesAussenluftmengeLabel = new javax.swing.JLabel();
        javax.swing.JLabel lmeGesAussenluftmengeM3Label = new javax.swing.JLabel();
        lmeGesAussenluftmengeWertLabel = new com.westaflex.component.classes.SeeLabel();
        pbAngebotErstellen = new javax.swing.JButton();
        javax.swing.JButton lmeSollLuftmengeAutoButton = new javax.swing.JButton();
        pbSpeichern = new javax.swing.JButton();
        javax.swing.JLabel lmeGesAussenluftmengeLabel1 = new javax.swing.JLabel();
        javax.swing.JButton pbRaumBearbeiten = new javax.swing.JButton();
        lmeSummeWertPanel = new com.westaflex.component.classes.SeePanel();
        lmeAbSummeWertLabel = new com.westaflex.component.classes.SeeLabel();
        lmeZuSummeWertLabel = new com.westaflex.component.classes.SeeLabel();
        lmeSummeZuluftmengeWertLabel = new com.westaflex.component.classes.SeeLabel();
        lmeSummeAbluftmengeWertLabel = new com.westaflex.component.classes.SeeLabel();
        lmeUebSummeWertLabel = new com.westaflex.component.classes.SeeLabel();
        lmeSumLTMAbluftmengeWertLabel = new com.westaflex.component.classes.SeeLabel();
        lmeSumLTMZuluftmengeWertLabel = new com.westaflex.component.classes.SeeLabel();
        bZentralgeraetWertLabel = new com.westaflex.component.classes.SeeLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        lmeTabelleZuluftScrollPane = new javax.swing.JScrollPane();
        lmeTabelleTable = new com.westaflex.component.classes.SeeTable();
        lmeTabelleTable.setFirstRowCol(3,2);
        lmeTabelleTable.setLastCol(12);
        int[] a = {2,4,3,6,10,11,5,7,9,12,13,8};
        lmeTabelleTable.setColIndex(a);
        lmeTabelleTable.getTableHeader().setPreferredSize(
            new Dimension(0, 35));
        lmeTabelleUeberstroemScrollPane = new javax.swing.JScrollPane();
        lmeTabelleUeberstroemTable = new com.westaflex.component.classes.SeeTable();
        lmeTabelleUeberstroemTable.setFirstRowCol(3,2);
        lmeTabelleUeberstroemTable.setLastCol(6);
        int[] aUeb = {2,4,3,6,5,7};
        lmeTabelleUeberstroemTable.setColIndex(aUeb);
        lmeTabelleUeberstroemTable.getTableHeader().setPreferredSize(
            new Dimension(0, 35));
        druckverlustberechnungPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JTabbedPane dvbTabbedPane = new javax.swing.JTabbedPane();
        com.westaflex.component.classes.SeePanel dvbTeilstreckenPanel = new com.westaflex.component.classes.SeePanel();
        dvbTeilstreckenTabelleScrollPane = new javax.swing.JScrollPane();
        dvbTeilstreckenTabelleTable = new com.westaflex.component.classes.SeeTable();
        dvbTeilstreckenTabelleTable.setFirstRowCol(3,2);
        dvbTeilstreckenTabelleTable.setLastCol(10);
        dvbTeilstreckenTabelleTable.getTableHeader().setPreferredSize(
            new Dimension(0, 45));
        dvbTeilstreckenHinzufuegenPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        dvbTeilstreckenBelueftungComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        dvbTeilstreckenNrTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        dvbTeilstreckenLuftmengeTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        dvbTeilstreckenKanalbezeichnungComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        dvbTeilstreckenLaengeTextField = new javax.swing.JTextField();
        javax.swing.JButton dvbTeilstreckenHinzufuegenButton = new javax.swing.JButton();
        javax.swing.JButton dvbTeilstreckenWiderstandsWerteBearbeitenButton = new javax.swing.JButton();
        dvbTeilstreckenEntfernenButton = new javax.swing.JButton();
        javax.swing.JSpinner dvbTeilstreckenTabelleSpinner = new javax.swing.JSpinner();
        com.westaflex.component.classes.SeePanel dvbVentileinstellungPanel = new com.westaflex.component.classes.SeePanel();
        dvbVentileinstellungTabelleScrollPane = new javax.swing.JScrollPane();
        dvbVentileinstellungTabelleTable = new com.westaflex.component.classes.SeeTable();
        dvbVentileinstellungTabelleTable.setFirstRowCol(3,2);
        dvbVentileinstellungTabelleTable.setLastCol(9);
        com.westaflex.component.classes.SeePanel dvbVentileinstellungHinzufuegenPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel jLabel35 = new javax.swing.JLabel();
        dvbVentileinstellungBelueftungComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel34 = new javax.swing.JLabel();
        dvbVentileinstellungRaumComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        dvbVentileinstellungTeilstreckenTextField = new com.westaflex.component.classes.SeeTextField();
        javax.swing.JButton dvbVentileinstellungTeilstreckenAuswaehlenButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        dvbVentileinstellungVentilbezeichnungComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JButton dvbVentileinstellungHinzufuegenButton = new javax.swing.JButton();
        javax.swing.JButton dvbVentileinstellungEntfernenButton = new javax.swing.JButton();
        javax.swing.JSpinner dvbTabelleVentilSpinner = new javax.swing.JSpinner();
        akkustikberechnungPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JTabbedPane abTabbedPane = new javax.swing.JTabbedPane();
        com.westaflex.component.classes.SeePanel abZuluftPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel abZuRaumbezeichnungLabel = new javax.swing.JLabel();
        com.westaflex.component.classes.SeeComboBox abZuRaumbezeichnungComboBox = new com.westaflex.component.classes.SeeComboBox();
        com.westaflex.component.classes.SeePanel abZuBeschriftungPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel jLabel36 = new javax.swing.JLabel();
        abZuSchallleistungspegelZuluftstutzenComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel39 = new javax.swing.JLabel();
        abZuKanalnetzComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel40 = new javax.swing.JLabel();
        abZuFilterverschmutzungComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel41 = new javax.swing.JLabel();
        abZuHauptschalldaempfer1ComboBox = new com.westaflex.component.classes.SeeComboBox();
        abZuHauptschalldaempfer1ComboBox.mySearch = "select ~Artikelnummer~ from ~artikelstamm~ where ~Klasse~ = 2 and ~Gesperrt~ = false";
        javax.swing.JLabel jLabel42 = new javax.swing.JLabel();
        abZuHauptschalldaempfer2ComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel43 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel44 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel45 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel46 = new javax.swing.JLabel();
        abZuSchalldaempferVentilComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel47 = new javax.swing.JLabel();
        abZuEinfuegungswertLuftdurchlassComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel48 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel49 = new javax.swing.JLabel();
        abZuPlaceholder1Label = new javax.swing.JLabel();
        abZuPlaceholder2Label = new javax.swing.JLabel();
        stZuGeraet = new javax.swing.JLabel();
        javax.swing.JLabel jLabel52 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        abZuPlaceholder1ComboBox = new javax.swing.JComboBox();
        abZuAnzahlUmlenkungenTextField = new com.westaflex.component.classes.SeeTextField();
        jPanel3 = new javax.swing.JPanel();
        abZuPlaceholder2ComboBox = new javax.swing.JComboBox();
        abZuLuftverteilerkastenTextField = new com.westaflex.component.classes.SeeTextField();
        jPanel4 = new javax.swing.JPanel();
        abZuLaengsdaempfungKanalComboBox = new com.westaflex.component.classes.SeeComboBox();
        abZuLaengsdaempfungKanalTextField = new com.westaflex.component.classes.SeeTextField();
        jPanel5 = new javax.swing.JPanel();
        abZuPlaceholder3ComboBox = new javax.swing.JComboBox();
        abZuRaumabsorptionTextField = new com.westaflex.component.classes.SeeTextField();
        com.westaflex.component.classes.SeePanel abZuTabellePanel = new com.westaflex.component.classes.SeePanel();
        abZuTabelleUeberschrift1Label = new com.westaflex.component.classes.SeeLabel();
        com.westaflex.component.classes.SeeLabel abZuTabelleUeberschrift2Label = new com.westaflex.component.classes.SeeLabel();
        abZuTabelleScrollPane = new javax.swing.JScrollPane();
        abZuTabelleTable = new com.westaflex.component.classes.SeeTable();
        abZuTabelleTable.setFirstRowCol(2,2);
        abZuTabelleTable.setLastCol(6);
        abZuTabelleDezibelLabel = new javax.swing.JLabel();
        abZuTabelleDezibelWertLabel = new com.westaflex.component.classes.SeeLabel();
        com.westaflex.component.classes.SeeLabel abZuTabelleMittlererSchalldruckpegelLabel = new com.westaflex.component.classes.SeeLabel();
        abZuTabelleMittlererSchalldruckpegelWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel jLabel51 = new javax.swing.JLabel();
        javax.swing.JLabel abZuHinweisLabel = new javax.swing.JLabel();
        com.westaflex.component.classes.SeePanel abAbluftPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel abAbRaumbezeichnungLabel1 = new javax.swing.JLabel();
        abAbRaumbezeichnungComboBox = new com.westaflex.component.classes.SeeComboBox();
        com.westaflex.component.classes.SeePanel abAbBeschriftungPanel = new com.westaflex.component.classes.SeePanel();
        javax.swing.JLabel jLabel21 = new javax.swing.JLabel();
        abAbSchallleistungspegelAbluftstutzenComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel22 = new javax.swing.JLabel();
        abAbKanalnetzComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel23 = new javax.swing.JLabel();
        abAbFilterverschmutzungComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel24 = new javax.swing.JLabel();
        abAbHauptschalldaempfer1ComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel25 = new javax.swing.JLabel();
        abAbHauptschalldaempfer2ComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel26 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel27 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel28 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel29 = new javax.swing.JLabel();
        abAbSchalldaempferVentilComboBox = new com.westaflex.component.classes.SeeComboBox();
        abAbSchalldaempferVentilComboBox.mySearch = "select ~Artikelnummer~ from ~Artikelstamm~ where ~Klasse~ = 2";
        javax.swing.JLabel jLabel30 = new javax.swing.JLabel();
        abAbEinfuegungswertLuftdurchlassComboBox = new com.westaflex.component.classes.SeeComboBox();
        javax.swing.JLabel jLabel31 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel32 = new javax.swing.JLabel();
        abAbPlaceholder1Label = new javax.swing.JLabel();
        javax.swing.JLabel jLabel33 = new javax.swing.JLabel();
        abAbPlaceholder2Label = new javax.swing.JLabel();
        stAbGeraet = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        abAbPlaceholder1ComboBox = new javax.swing.JComboBox();
        abAbAnzahlUmlenkungenTextField = new com.westaflex.component.classes.SeeTextField();
        jPanel8 = new javax.swing.JPanel();
        abAbLaengsdaempfungKanalTextField = new com.westaflex.component.classes.SeeTextField();
        abAbLaengsdaempfungKanalComboBox = new com.westaflex.component.classes.SeeComboBox();
        jPanel7 = new javax.swing.JPanel();
        abAbLuftverteilerkastenTextField = new com.westaflex.component.classes.SeeTextField();
        abAbPlaceholder2ComboBox = new javax.swing.JComboBox();
        jPanel9 = new javax.swing.JPanel();
        abAbPlaceholder3ComboBox = new javax.swing.JComboBox();
        abAbRaumabsorptionTextField = new com.westaflex.component.classes.SeeTextField();
        com.westaflex.component.classes.SeePanel abAbTabellePanel = new com.westaflex.component.classes.SeePanel();
        abAbTabelleUeberschrift1Label = new com.westaflex.component.classes.SeeLabel();
        com.westaflex.component.classes.SeeLabel abAbTabelleUeberschrift2Label = new com.westaflex.component.classes.SeeLabel();
        abAbTabelleScrollPane = new javax.swing.JScrollPane();
        abAbTabelleTable = new com.westaflex.component.classes.SeeTable();
        abAbTabelleTable.setFirstRowCol(2,2);
        abAbTabelleTable.setLastCol(6);
        javax.swing.JLabel abAbTabelleDezibelLabel = new javax.swing.JLabel();
        abAbTabelleDezibelWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel abAbTabelleMittlererSchalldruckpegelLabel = new javax.swing.JLabel();
        abAbTabelleMittlererSchalldruckpegelWertLabel = new com.westaflex.component.classes.SeeLabel();
        javax.swing.JLabel jLabel37 = new javax.swing.JLabel();
        javax.swing.JLabel abAbHinweisLabel = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Projekt");
        setToolTipText("WestaWAC");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/com/westaflex/resource/Wap_Projekt.png"))); // NOI18N
        setVisible(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        projektTabbedPane.setFont(new java.awt.Font("Arial", 0, 12));

        adGrosshandelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Kunde 1 (Großhandel)"));
        adGrosshandelPanel.setName("adGrosshandelPanel"); // NOI18N

        ghFirmaLabel.setDisplayedMnemonic('F');
        ghFirmaLabel.setLabelFor(ghFirma1TextField);
        ghFirmaLabel.setText("Firma");

        ghFirma1TextField.setName("ghFirma1TextField"); // NOI18N

        ghFirma2TextField.setName("ghFirma2TextField"); // NOI18N

        ghStrasseLabel.setDisplayedMnemonic('S');
        ghStrasseLabel.setLabelFor(ghStrasseTextfield);
        ghStrasseLabel.setText("Strasse");

        ghStrasseTextfield.setName("ghStrasseTextfield"); // NOI18N

        ghPlzOrtLabel.setDisplayedMnemonic('P');
        ghPlzOrtLabel.setLabelFor(ghPlzOrtTextField);
        ghPlzOrtLabel.setText("PLZ / Ort");

        ghPlzOrtTextField.setName("ghPlzOrtTextField"); // NOI18N

        ghTelefonLabel.setDisplayedMnemonic('T');
        ghTelefonLabel.setLabelFor(ghTelefonTextField);
        ghTelefonLabel.setText("Telefon");

        ghTelefonTextField.setName("ghTelefonTextField"); // NOI18N

        ghFaxLabel.setDisplayedMnemonic('F');
        ghFaxLabel.setText("Fax");

        ghFaxTextField.setName("ghFaxTextField"); // NOI18N

        ghAnsprechpartnerLabel.setDisplayedMnemonic('A');
        ghAnsprechpartnerLabel.setText("Ansprechpartner");

        ghAnsprechpartnerTextField.setName("ghAnsprechpartnerTextField"); // NOI18N

        org.jdesktop.layout.GroupLayout adGrosshandelPanelLayout = new org.jdesktop.layout.GroupLayout(adGrosshandelPanel);
        adGrosshandelPanel.setLayout(adGrosshandelPanelLayout);
        adGrosshandelPanelLayout.setHorizontalGroup(
            adGrosshandelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adGrosshandelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(adGrosshandelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(ghFirmaLabel)
                    .add(ghAnsprechpartnerLabel)
                    .add(ghFaxLabel)
                    .add(ghTelefonLabel)
                    .add(ghPlzOrtLabel)
                    .add(ghStrasseLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adGrosshandelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(adGrosshandelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, ghFirma2TextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(ghFirma1TextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, ghStrasseTextfield, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(adGrosshandelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, ghAnsprechpartnerTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, ghFaxTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, ghPlzOrtTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, ghTelefonTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        adGrosshandelPanelLayout.linkSize(new java.awt.Component[] {ghAnsprechpartnerTextField, ghFaxTextField, ghFirma1TextField, ghFirma2TextField, ghPlzOrtTextField, ghStrasseTextfield, ghTelefonTextField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        adGrosshandelPanelLayout.setVerticalGroup(
            adGrosshandelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adGrosshandelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(adGrosshandelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ghFirma1TextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ghFirmaLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ghFirma2TextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adGrosshandelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ghStrasseTextfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ghStrasseLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adGrosshandelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ghPlzOrtTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ghPlzOrtLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adGrosshandelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ghTelefonTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ghTelefonLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adGrosshandelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ghFaxTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ghFaxLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adGrosshandelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ghAnsprechpartnerTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ghAnsprechpartnerLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        adAusfuehrendeFirmaPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Kunde 2 (Ausführende Firma)"));
        adAusfuehrendeFirmaPanel.setName("aufuehrendefirma"); // NOI18N

        afFirmaLabel.setDisplayedMnemonic('r');
        afFirmaLabel.setText("Firma");

        afFirma1TextField.setName("afFirma1TextField"); // NOI18N

        afFirma2TextField.setName("afFirma2TextField"); // NOI18N

        afStrasseLabel.setDisplayedMnemonic('t');
        afStrasseLabel.setText("Strasse");

        afStrasseTextField.setName("afStrasseTextfield"); // NOI18N

        afPlzOrtLabel.setDisplayedMnemonic('O');
        afPlzOrtLabel.setText("PLZ / Ort");

        afPlzOrtTextField.setName("afPlzOrtTextField"); // NOI18N

        afTelefonLabel.setDisplayedMnemonic('l');
        afTelefonLabel.setText("Telefon");

        afTelefonTextField.setName("afTelefonTextField"); // NOI18N

        afFaxLabel.setDisplayedMnemonic('x');
        afFaxLabel.setText("Fax");

        afFaxTextField.setName("afFaxTextField"); // NOI18N

        afAnsprechpartnerLabel.setDisplayedMnemonic('p');
        afAnsprechpartnerLabel.setText("Ansprechpartner");

        afAnsprechpartnerTextField.setName("afAnsprechpartnerTextField"); // NOI18N

        org.jdesktop.layout.GroupLayout adAusfuehrendeFirmaPanelLayout = new org.jdesktop.layout.GroupLayout(adAusfuehrendeFirmaPanel);
        adAusfuehrendeFirmaPanel.setLayout(adAusfuehrendeFirmaPanelLayout);
        adAusfuehrendeFirmaPanelLayout.setHorizontalGroup(
            adAusfuehrendeFirmaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adAusfuehrendeFirmaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(adAusfuehrendeFirmaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(afFirmaLabel)
                    .add(afStrasseLabel)
                    .add(afTelefonLabel)
                    .add(afFaxLabel)
                    .add(afAnsprechpartnerLabel)
                    .add(afPlzOrtLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adAusfuehrendeFirmaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(adAusfuehrendeFirmaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(afAnsprechpartnerTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(afFaxTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(afTelefonTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(afPlzOrtTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(afFirma2TextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(afFirma1TextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(afStrasseTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        adAusfuehrendeFirmaPanelLayout.linkSize(new java.awt.Component[] {afAnsprechpartnerTextField, afFaxTextField, afFirma1TextField, afFirma2TextField, afPlzOrtTextField, afStrasseTextField, afTelefonTextField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        adAusfuehrendeFirmaPanelLayout.setVerticalGroup(
            adAusfuehrendeFirmaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adAusfuehrendeFirmaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(adAusfuehrendeFirmaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(afFirmaLabel)
                    .add(afFirma1TextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(afFirma2TextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adAusfuehrendeFirmaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(afStrasseLabel)
                    .add(afStrasseTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adAusfuehrendeFirmaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(afPlzOrtTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(afPlzOrtLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adAusfuehrendeFirmaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(afTelefonTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(afTelefonLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adAusfuehrendeFirmaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(afFaxTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(afFaxLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adAusfuehrendeFirmaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(afAnsprechpartnerTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(afAnsprechpartnerLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        adNotizenPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Notizen"));

        adBauvorhabenLabel.setDisplayedMnemonic('B');
        adBauvorhabenLabel.setText("Bauvorhaben");

        adBauvorhabenTextField.setName("adBauvorhabenTextField"); // NOI18N
        adBauvorhabenTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adBauvorhabenKeyReleasedAction(evt);
            }
        });

        adNotizenLabel.setDisplayedMnemonic('N');
        adNotizenLabel.setText("Notizen");

        adNotizenTextArea.setColumns(20);
        adNotizenTextArea.setRows(5);
        adNotizenTextArea.setName("adNotizenTextArea"); // NOI18N
        adNotizenScrollPane.setViewportView(adNotizenTextArea);

        org.jdesktop.layout.GroupLayout adNotizenPanelLayout = new org.jdesktop.layout.GroupLayout(adNotizenPanel);
        adNotizenPanel.setLayout(adNotizenPanelLayout);
        adNotizenPanelLayout.setHorizontalGroup(
            adNotizenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adNotizenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(adNotizenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(adBauvorhabenLabel)
                    .add(adNotizenLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adNotizenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(adNotizenScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 887, Short.MAX_VALUE)
                    .add(adBauvorhabenTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 887, Short.MAX_VALUE))
                .addContainerGap())
        );
        adNotizenPanelLayout.setVerticalGroup(
            adNotizenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adNotizenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(adNotizenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(adBauvorhabenLabel)
                    .add(adBauvorhabenTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adNotizenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(adNotizenPanelLayout.createSequentialGroup()
                        .add(adNotizenLabel)
                        .add(205, 205, 205))
                    .add(adNotizenPanelLayout.createSequentialGroup()
                        .add(adNotizenScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        org.jdesktop.layout.GroupLayout auslegungsdatenPanelLayout = new org.jdesktop.layout.GroupLayout(auslegungsdatenPanel);
        auslegungsdatenPanel.setLayout(auslegungsdatenPanelLayout);
        auslegungsdatenPanelLayout.setHorizontalGroup(
            auslegungsdatenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(auslegungsdatenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(auslegungsdatenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(adNotizenPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(auslegungsdatenPanelLayout.createSequentialGroup()
                        .add(adGrosshandelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 390, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(adAusfuehrendeFirmaPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 390, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        auslegungsdatenPanelLayout.linkSize(new java.awt.Component[] {adAusfuehrendeFirmaPanel, adGrosshandelPanel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        auslegungsdatenPanelLayout.setVerticalGroup(
            auslegungsdatenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(auslegungsdatenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(auslegungsdatenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(adGrosshandelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(adAusfuehrendeFirmaPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adNotizenPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                .addContainerGap())
        );

        projektTabbedPane.addTab("Kundendaten", auslegungsdatenPanel);

        gebaeudetypPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Gebäudetyp"));

        gtMFHRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        gebaeudetypButtonGroup.add(gtMFHRadioButton);
        gtMFHRadioButton.setMnemonic('f');
        gtMFHRadioButton.setSelected(true);
        gtMFHRadioButton.setText("Mehrfamilienhaus MFH");
        gtMFHRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gtMFHRadioButton.setName("gtMFHRadioButton"); // NOI18N
        gtMFHRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gtMFHRadioButtonActionPerformed(evt);
            }
        });

        gtEFHRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        gebaeudetypButtonGroup.add(gtEFHRadioButton);
        gtEFHRadioButton.setMnemonic('n');
        gtEFHRadioButton.setText("Einfamilienhaus EFH");
        gtEFHRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gtEFHRadioButton.setName("gtEFHRadioButton"); // NOI18N
        gtEFHRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gtEFHRadioButtonActionPerformed(evt);
            }
        });

        gtMaisonetteRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        gebaeudetypButtonGroup.add(gtMaisonetteRadioButton);
        gtMaisonetteRadioButton.setMnemonic('t');
        gtMaisonetteRadioButton.setText("Maisonette");
        gtMaisonetteRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gtMaisonetteRadioButton.setName("gtMaisonetteRadioButton"); // NOI18N
        gtMaisonetteRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gtMaisonetteRadioButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout gebaeudetypPanelLayout = new org.jdesktop.layout.GroupLayout(gebaeudetypPanel);
        gebaeudetypPanel.setLayout(gebaeudetypPanelLayout);
        gebaeudetypPanelLayout.setHorizontalGroup(
            gebaeudetypPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gebaeudetypPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gebaeudetypPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gtMFHRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gtEFHRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gtMaisonetteRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(102, Short.MAX_VALUE))
        );
        gebaeudetypPanelLayout.setVerticalGroup(
            gebaeudetypPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gebaeudetypPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gtMFHRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gtEFHRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gtMaisonetteRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        gebaeudelagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Gebäudelage"));

        glWschwachRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        gebaeudelageButtonGroup.add(glWschwachRadioButton);
        glWschwachRadioButton.setMnemonic('f');
        glWschwachRadioButton.setSelected(true);
        glWschwachRadioButton.setText("windschwach");
        glWschwachRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        glWschwachRadioButton.setName("glWschwachRadioButton"); // NOI18N
        glWschwachRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glWschwachRadioButtonActionPerformed(evt);
            }
        });

        glWstarkRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        gebaeudelageButtonGroup.add(glWstarkRadioButton);
        glWstarkRadioButton.setMnemonic('n');
        glWstarkRadioButton.setText("windstark");
        glWstarkRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        glWstarkRadioButton.setName("glWstarkRadioButton"); // NOI18N
        glWstarkRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glWstarkRadioButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout gebaeudelagePanelLayout = new org.jdesktop.layout.GroupLayout(gebaeudelagePanel);
        gebaeudelagePanel.setLayout(gebaeudelagePanelLayout);
        gebaeudelagePanelLayout.setHorizontalGroup(
            gebaeudelagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gebaeudelagePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gebaeudelagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(glWschwachRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(glWstarkRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(138, Short.MAX_VALUE))
        );
        gebaeudelagePanelLayout.setVerticalGroup(
            gebaeudelagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gebaeudelagePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(glWschwachRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(glWstarkRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );

        waermeschutzPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Wärmeschutz"));

        wsHochRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        waermeschutzButtonGroup.add(wsHochRadioButton);
        wsHochRadioButton.setMnemonic('f');
        wsHochRadioButton.setSelected(true);
        wsHochRadioButton.setText("hoch (Neubau / Sanierung mind. WSchV 1995)");
        wsHochRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        wsHochRadioButton.setName("wsHochRadioButton"); // NOI18N

        wsNiedrigRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        waermeschutzButtonGroup.add(wsNiedrigRadioButton);
        wsNiedrigRadioButton.setMnemonic('n');
        wsNiedrigRadioButton.setText("niedrig (Gebäudebestand vor 1995)");
        wsNiedrigRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        wsNiedrigRadioButton.setName("wsNiedrigRadioButton"); // NOI18N

        org.jdesktop.layout.GroupLayout waermeschutzPanelLayout = new org.jdesktop.layout.GroupLayout(waermeschutzPanel);
        waermeschutzPanel.setLayout(waermeschutzPanelLayout);
        waermeschutzPanelLayout.setHorizontalGroup(
            waermeschutzPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(waermeschutzPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(waermeschutzPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(wsHochRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(wsNiedrigRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        waermeschutzPanelLayout.setVerticalGroup(
            waermeschutzPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(waermeschutzPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(wsHochRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wsNiedrigRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );

        luftdichtheitPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Luftdichtheit der Gebäudehülle"));

        ldKatARadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        luftdichtheitButtonGroup.add(ldKatARadioButton);
        ldKatARadioButton.setMnemonic('f');
        ldKatARadioButton.setSelected(true);
        ldKatARadioButton.setText("Kategorie A (ventilatorgestützt)");
        ldKatARadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ldKatARadioButton.setName("ldKatARadioButton"); // NOI18N
        ldKatARadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ldKatARadioButtonActionPerformed(evt);
            }
        });

        ldKatBRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        luftdichtheitButtonGroup.add(ldKatBRadioButton);
        ldKatBRadioButton.setMnemonic('n');
        ldKatBRadioButton.setText("Kategorie B (frei, Neubau)");
        ldKatBRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ldKatBRadioButton.setName("ldKatBRadioButton"); // NOI18N
        ldKatBRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ldKatBRadioButtonActionPerformed(evt);
            }
        });

        ldKatCRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        luftdichtheitButtonGroup.add(ldKatCRadioButton);
        ldKatCRadioButton.setMnemonic('n');
        ldKatCRadioButton.setText("Kategorie C (frei, Bestand)");
        ldKatCRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ldKatCRadioButton.setName("ldKatCRadioButton"); // NOI18N
        ldKatCRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ldKatCRadioButtonActionPerformed(evt);
            }
        });

        ldMessRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        luftdichtheitButtonGroup.add(ldMessRadioButton);
        ldMessRadioButton.setMnemonic('n');
        ldMessRadioButton.setText("Messwerte");
        ldMessRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ldMessRadioButton.setName("ldMessRadioButton"); // NOI18N

        ldDruckDiffTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, ldMessRadioButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), ldDruckDiffTextField, org.jdesktop.beansbinding.BeanProperty.create("editable"));
        bindingGroup.addBinding(binding);

        ldDruckDiffLabel.setDisplayedMnemonic('z');
        ldDruckDiffLabel.setText("Druckdifferenz in Pa");

        ldLuftwechselTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, ldMessRadioButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), ldLuftwechselTextField, org.jdesktop.beansbinding.BeanProperty.create("editable"));
        bindingGroup.addBinding(binding);

        ldDruckExpoTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ldDruckExpoTextField.setText("0,666");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, ldMessRadioButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), ldDruckExpoTextField, org.jdesktop.beansbinding.BeanProperty.create("editable"));
        bindingGroup.addBinding(binding);

        ldLuftwechselLabel.setDisplayedMnemonic('z');
        ldLuftwechselLabel.setText("Luftwechsel in 1/h");

        ldDruckExpoLabel.setDisplayedMnemonic('z');
        ldDruckExpoLabel.setText("Druckexponent");

        org.jdesktop.layout.GroupLayout luftdichtheitPanelLayout = new org.jdesktop.layout.GroupLayout(luftdichtheitPanel);
        luftdichtheitPanel.setLayout(luftdichtheitPanelLayout);
        luftdichtheitPanelLayout.setHorizontalGroup(
            luftdichtheitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(luftdichtheitPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(luftdichtheitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ldKatARadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ldKatCRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ldKatBRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(luftdichtheitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(luftdichtheitPanelLayout.createSequentialGroup()
                        .add(17, 17, 17)
                        .add(luftdichtheitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(ldDruckExpoTextField)
                            .add(ldLuftwechselTextField)
                            .add(ldDruckDiffTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(luftdichtheitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(ldLuftwechselLabel)
                            .add(ldDruckDiffLabel)
                            .add(ldDruckExpoLabel)))
                    .add(ldMessRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(129, Short.MAX_VALUE))
        );
        luftdichtheitPanelLayout.setVerticalGroup(
            luftdichtheitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(luftdichtheitPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(luftdichtheitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ldKatARadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ldMessRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(luftdichtheitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ldDruckDiffLabel)
                    .add(ldKatBRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ldDruckDiffTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(luftdichtheitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ldKatCRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ldLuftwechselLabel)
                    .add(ldLuftwechselTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(luftdichtheitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ldDruckExpoLabel)
                    .add(ldDruckExpoTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        besAnforderungPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("besondere Anforderungen"));

        bAnforderungLabel.setText("Faktor für besondere bauphysikalische oder hygienische Anforderungen");

        bAnforderungTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        bAnforderungTextField.setText("1");
        bAnforderungTextField.setName("bAnforderungTextField"); // NOI18N

        org.jdesktop.layout.GroupLayout besAnforderungPanelLayout = new org.jdesktop.layout.GroupLayout(besAnforderungPanel);
        besAnforderungPanel.setLayout(besAnforderungPanelLayout);
        besAnforderungPanelLayout.setHorizontalGroup(
            besAnforderungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, besAnforderungPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(bAnforderungTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(bAnforderungLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 500, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(213, 213, 213))
        );
        besAnforderungPanelLayout.setVerticalGroup(
            besAnforderungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(besAnforderungPanelLayout.createSequentialGroup()
                .add(besAnforderungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(bAnforderungTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(bAnforderungLabel))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        geometriePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Geometrie"));

        geoWohnflaecheMLabel.setDisplayedMnemonic('z');
        geoWohnflaecheMLabel.setText("m²");

        geoHeoheMLabel.setDisplayedMnemonic('z');
        geoHeoheMLabel.setText("m");

        geoVolumenMLabel.setDisplayedMnemonic('z');
        geoVolumenMLabel.setText("m³");

        geoHeoheTextLabel.setDisplayedMnemonic('z');
        geoHeoheTextLabel.setText("mittlere Raumhöhe");

        geoWohnflaecheTextLabel.setDisplayedMnemonic('z');
        geoWohnflaecheTextLabel.setText("Wohnfläche der Nutzungseinheit");

        geoVolumenTextLabel.setDisplayedMnemonic('z');
        geoVolumenTextLabel.setText("Luftvolumen der Nutzungseinheit");

        geoGeluefteteflaecheMLabel.setDisplayedMnemonic('z');
        geoGeluefteteflaecheMLabel.setText("m²");

        geoGeluefteteflaecheTextLabel.setDisplayedMnemonic('z');
        geoGeluefteteflaecheTextLabel.setText("gelüftete Fläche");

        geoGeVolumenMLabel.setDisplayedMnemonic('z');
        geoGeVolumenMLabel.setText("m³");

        geoGeVolumenTextLabel.setDisplayedMnemonic('z');
        geoGeVolumenTextLabel.setText("gelüftetes Volumen");

        geoWohnflaecheTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        geoWohnflaecheTextField.setName("geoWohnflaecheTextField"); // NOI18N
        geoWohnflaecheTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                geoWohnflaecheTextFieldActionPerformed(evt);
            }
        });
        geoWohnflaecheTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                geoWohnflaecheTextFieldFocusLost(evt);
            }
        });

        geoHoeheTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        geoHoeheTextField.setName("geoHoeheTextField"); // NOI18N
        geoHoeheTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                geoHoeheTextFieldActionPerformed(evt);
            }
        });
        geoHoeheTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                geoHoeheTextFieldFocusLost(evt);
            }
        });

        geoVolumenTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        geoVolumenTextField.setName("geoVolumenTextField "); // NOI18N
        geoVolumenTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                geoVolumenTextFieldActionPerformed(evt);
            }
        });
        geoVolumenTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                geoVolumenTextFieldFocusLost(evt);
            }
        });

        geoGeluefteteflaecheTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        geoGeluefteteflaecheTextField.setName("geoGeluefteteflaecheTextField"); // NOI18N
        geoGeluefteteflaecheTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                geoGeluefteteflaecheTextFieldActionPerformed(evt);
            }
        });
        geoGeluefteteflaecheTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                geoGeluefteteflaecheTextFieldFocusLost(evt);
            }
        });

        geoGelueftetesVolumenTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        geoGelueftetesVolumenTextField.setName("geoGelueftetesVolumenTextField"); // NOI18N

        org.jdesktop.layout.GroupLayout geometriePanelLayout = new org.jdesktop.layout.GroupLayout(geometriePanel);
        geometriePanel.setLayout(geometriePanelLayout);
        geometriePanelLayout.setHorizontalGroup(
            geometriePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(geometriePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(geometriePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(geometriePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(geoHoeheTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                        .add(geoWohnflaecheTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                        .add(geoVolumenTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE))
                    .add(geoGeluefteteflaecheTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                    .add(geoGelueftetesVolumenTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(geometriePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(geometriePanelLayout.createSequentialGroup()
                        .add(geometriePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(geoHeoheMLabel)
                            .add(geoVolumenMLabel)
                            .add(geoWohnflaecheMLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(geometriePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(geoWohnflaecheTextLabel)
                            .add(geoHeoheTextLabel)
                            .add(geoVolumenTextLabel)))
                    .add(geometriePanelLayout.createSequentialGroup()
                        .add(geoGeluefteteflaecheMLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(geoGeluefteteflaecheTextLabel))
                    .add(geometriePanelLayout.createSequentialGroup()
                        .add(geoGeVolumenMLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(geoGeVolumenTextLabel)))
                .add(31, 31, 31))
        );
        geometriePanelLayout.setVerticalGroup(
            geometriePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(geometriePanelLayout.createSequentialGroup()
                .add(geometriePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(geoWohnflaecheMLabel)
                    .add(geoWohnflaecheTextLabel)
                    .add(geoWohnflaecheTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(geometriePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(geoHeoheMLabel)
                    .add(geoHeoheTextLabel)
                    .add(geoHoeheTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(geometriePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(geoVolumenMLabel)
                    .add(geoVolumenTextLabel)
                    .add(geoVolumenTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(geometriePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(geoGeluefteteflaecheMLabel)
                    .add(geoGeluefteteflaecheTextLabel)
                    .add(geoGeluefteteflaecheTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(geometriePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(geoGeVolumenMLabel)
                    .add(geoGeVolumenTextLabel)
                    .add(geoGelueftetesVolumenTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        geplanteBelegungPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Geplante Belegung"));

        personenanzahlLabel.setDisplayedMnemonic('z');
        personenanzahlLabel.setText("Personenanzahl");

        personenanzahlSpinner.setModel(new SpinnerNumberModel(0,0,99,1));
        personenanzahlSpinner.setName("personenanzahlSpinner"); // NOI18N
        personenanzahlSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                personenanzahlSpinnerStateChanged(evt);
            }
        });

        mindestaussenluftrateLabel.setForeground(new java.awt.Color(255, 0, 0));
        mindestaussenluftrateLabel.setText("Mindestaußenlufrate:");

        mindestaussenluftrateWertLabel.setForeground(new java.awt.Color(255, 0, 0));
        mindestaussenluftrateWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        mindestaussenluftrateWertLabel.setText("0");
        mindestaussenluftrateWertLabel.setName("mindestaussenluftrateWertLabel"); // NOI18N

        jLabel38.setForeground(new java.awt.Color(255, 0, 0));
        jLabel38.setText("m³/h");

        volumenProPersonLabel.setDisplayedMnemonic('z');
        volumenProPersonLabel.setText("Außenluftvolumenstrom pro Person");

        volumenProPersonSpinner.setModel(new javax.swing.SpinnerNumberModel(30, 20, 50, 1));
        volumenProPersonSpinner.setName("volumenProPersonSpinner"); // NOI18N
        volumenProPersonSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                volumenProPersonSpinnerStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout geplanteBelegungPanelLayout = new org.jdesktop.layout.GroupLayout(geplanteBelegungPanel);
        geplanteBelegungPanel.setLayout(geplanteBelegungPanelLayout);
        geplanteBelegungPanelLayout.setHorizontalGroup(
            geplanteBelegungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(geplanteBelegungPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(geplanteBelegungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mindestaussenluftrateLabel)
                    .add(personenanzahlLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(geplanteBelegungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(geplanteBelegungPanelLayout.createSequentialGroup()
                        .add(mindestaussenluftrateWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6)
                        .add(jLabel38))
                    .add(personenanzahlSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(volumenProPersonLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(volumenProPersonSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .add(314, 314, 314))
        );
        geplanteBelegungPanelLayout.setVerticalGroup(
            geplanteBelegungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(geplanteBelegungPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(geplanteBelegungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(personenanzahlLabel)
                    .add(personenanzahlSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(volumenProPersonLabel)
                    .add(volumenProPersonSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(geplanteBelegungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mindestaussenluftrateLabel)
                    .add(mindestaussenluftrateWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel38))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout gebaeudePanelLayout = new org.jdesktop.layout.GroupLayout(gebaeudePanel);
        gebaeudePanel.setLayout(gebaeudePanelLayout);
        gebaeudePanelLayout.setHorizontalGroup(
            gebaeudePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gebaeudePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gebaeudePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, geplanteBelegungPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, besAnforderungPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, gebaeudePanelLayout.createSequentialGroup()
                        .add(geometriePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(luftdichtheitPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, gebaeudePanelLayout.createSequentialGroup()
                        .add(gebaeudetypPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(gebaeudelagePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(waermeschutzPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(205, Short.MAX_VALUE))
        );
        gebaeudePanelLayout.setVerticalGroup(
            gebaeudePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gebaeudePanelLayout.createSequentialGroup()
                .add(19, 19, 19)
                .add(gebaeudePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gebaeudetypPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gebaeudelagePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(waermeschutzPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gebaeudePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(geometriePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(luftdichtheitPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(5, 5, 5)
                .add(besAnforderungPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(geplanteBelegungPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(111, 111, 111))
        );

        projektTabbedPane.addTab("Gebäudedaten", gebaeudePanel);

        geraetestandortPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Gerätestandort"));

        gsKellergeschossRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        geraetestandortButtonGroup.add(gsKellergeschossRadioButton);
        gsKellergeschossRadioButton.setMnemonic('K');
        gsKellergeschossRadioButton.setText("Kellergeschoss");
        gsKellergeschossRadioButton.setFocusPainted(false);
        gsKellergeschossRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gsKellergeschossRadioButton.setName("gsKellergeschossRadioButton"); // NOI18N

        gsErdgeschossRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        geraetestandortButtonGroup.add(gsErdgeschossRadioButton);
        gsErdgeschossRadioButton.setMnemonic('E');
        gsErdgeschossRadioButton.setSelected(true);
        gsErdgeschossRadioButton.setText("Erdgeschoss");
        gsErdgeschossRadioButton.setFocusPainted(false);
        gsErdgeschossRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gsErdgeschossRadioButton.setName("gsErdgeschossRadioButton"); // NOI18N

        gsObergeschossRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        geraetestandortButtonGroup.add(gsObergeschossRadioButton);
        gsObergeschossRadioButton.setMnemonic('O');
        gsObergeschossRadioButton.setText("Obergeschoss");
        gsObergeschossRadioButton.setFocusPainted(false);
        gsObergeschossRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gsObergeschossRadioButton.setName("gsObergeschossRadioButton"); // NOI18N

        gsDachgeschossRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        geraetestandortButtonGroup.add(gsDachgeschossRadioButton);
        gsDachgeschossRadioButton.setMnemonic('D');
        gsDachgeschossRadioButton.setText("Dachgeschoss");
        gsDachgeschossRadioButton.setFocusPainted(false);
        gsDachgeschossRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gsDachgeschossRadioButton.setName("gsDachgeschossRadioButton"); // NOI18N

        gsSpitzbodenRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        geraetestandortButtonGroup.add(gsSpitzbodenRadioButton);
        gsSpitzbodenRadioButton.setMnemonic('D');
        gsSpitzbodenRadioButton.setText("Spitzboden");
        gsSpitzbodenRadioButton.setFocusPainted(false);
        gsSpitzbodenRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gsSpitzbodenRadioButton.setName("gsSpitzbodenRadioButton"); // NOI18N

        org.jdesktop.layout.GroupLayout geraetestandortPanelLayout = new org.jdesktop.layout.GroupLayout(geraetestandortPanel);
        geraetestandortPanel.setLayout(geraetestandortPanelLayout);
        geraetestandortPanelLayout.setHorizontalGroup(
            geraetestandortPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(geraetestandortPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(geraetestandortPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsKellergeschossRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gsErdgeschossRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gsObergeschossRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gsDachgeschossRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gsSpitzbodenRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(74, Short.MAX_VALUE))
        );
        geraetestandortPanelLayout.setVerticalGroup(
            geraetestandortPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(geraetestandortPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gsKellergeschossRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsErdgeschossRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsObergeschossRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsDachgeschossRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsSpitzbodenRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        luftkanalverlegungPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Luftkanalverlegung"));

        lkLabel.setText("Quadroflexsystem 100 mit nur 60 mm Aufbauhöhe");

        lkAufputzCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lkAufputzCheckbox.setMnemonic('A');
        lkAufputzCheckbox.setText("Aufputz (Abkastung)");
        lkAufputzCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lkAufputzCheckbox.setName("lkAufputzCheckbox"); // NOI18N

        lkDaemmschichtCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lkDaemmschichtCheckbox.setMnemonic('D');
        lkDaemmschichtCheckbox.setText("Dämmschicht unter Estrich");
        lkDaemmschichtCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lkDaemmschichtCheckbox.setName("lkDaemmschichtCheckbox"); // NOI18N

        lkDeckeCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lkDeckeCheckbox.setMnemonic('k');
        lkDeckeCheckbox.setText("Decke (abgehängt)");
        lkDeckeCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lkDeckeCheckbox.setName("lkDeckeCheckbox"); // NOI18N

        lkSpitzbodenCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lkSpitzbodenCheckbox.setMnemonic('p');
        lkSpitzbodenCheckbox.setText("Spitzboden");
        lkSpitzbodenCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lkSpitzbodenCheckbox.setName("lkSpitzbodenCheckbox"); // NOI18N

        org.jdesktop.layout.GroupLayout luftkanalverlegungPanelLayout = new org.jdesktop.layout.GroupLayout(luftkanalverlegungPanel);
        luftkanalverlegungPanel.setLayout(luftkanalverlegungPanelLayout);
        luftkanalverlegungPanelLayout.setHorizontalGroup(
            luftkanalverlegungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(luftkanalverlegungPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(luftkanalverlegungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lkLabel)
                    .add(lkAufputzCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lkDaemmschichtCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lkDeckeCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lkSpitzbodenCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        luftkanalverlegungPanelLayout.setVerticalGroup(
            luftkanalverlegungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(luftkanalverlegungPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lkLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lkAufputzCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lkDaemmschichtCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lkDeckeCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lkSpitzbodenCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        aussenluftPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Außenluft"));

        rbAlDachdurchfuehrung.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        aussenluftButtonGroup.add(rbAlDachdurchfuehrung);
        rbAlDachdurchfuehrung.setMnemonic('n');
        rbAlDachdurchfuehrung.setText("Dachdurchführung");
        rbAlDachdurchfuehrung.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbAlDachdurchfuehrung.setName("rbAlDachdurchfuehrung"); // NOI18N

        rbAlWand.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        aussenluftButtonGroup.add(rbAlWand);
        rbAlWand.setMnemonic('n');
        rbAlWand.setSelected(true);
        rbAlWand.setText("Wand (Luftgitter)");
        rbAlWand.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbAlWand.setName("rbAlWand"); // NOI18N

        rbAlErdwaermetauscher.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        aussenluftButtonGroup.add(rbAlErdwaermetauscher);
        rbAlErdwaermetauscher.setMnemonic('n');
        rbAlErdwaermetauscher.setText("Erdwärmetauscher");
        rbAlErdwaermetauscher.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbAlErdwaermetauscher.setName("rbAlErdwaermetauscher"); // NOI18N

        org.jdesktop.layout.GroupLayout aussenluftPanelLayout = new org.jdesktop.layout.GroupLayout(aussenluftPanel);
        aussenluftPanel.setLayout(aussenluftPanelLayout);
        aussenluftPanelLayout.setHorizontalGroup(
            aussenluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aussenluftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(aussenluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rbAlDachdurchfuehrung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(rbAlWand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(rbAlErdwaermetauscher, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(68, Short.MAX_VALUE))
        );
        aussenluftPanelLayout.setVerticalGroup(
            aussenluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aussenluftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(rbAlDachdurchfuehrung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbAlWand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbAlErdwaermetauscher, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        luftzulaessePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Zuluftdurchlässe"));

        lzTellerventileCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lzTellerventileCheckbox.setMnemonic('T');
        lzTellerventileCheckbox.setText("Tellerventile");
        lzTellerventileCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lzTellerventileCheckbox.setName("lzTellerventileCheckbox"); // NOI18N

        lzSchlitzauslassCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lzSchlitzauslassCheckbox.setMnemonic('S');
        lzSchlitzauslassCheckbox.setText("Schlitzauslass (Weitwurfdüse)");
        lzSchlitzauslassCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lzSchlitzauslassCheckbox.setName("lzSchlitzauslassCheckbox"); // NOI18N

        lzFussbodenAuslassCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lzFussbodenAuslassCheckbox.setMnemonic('F');
        lzFussbodenAuslassCheckbox.setText("Fußbodenauslass");
        lzFussbodenAuslassCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lzFussbodenAuslassCheckbox.setName("lzFussbodenAuslassCheckbox"); // NOI18N

        lzSockelquellauslassCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lzSockelquellauslassCheckbox.setMnemonic('o');
        lzSockelquellauslassCheckbox.setText("Sockelquellauslass");
        lzSockelquellauslassCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lzSockelquellauslassCheckbox.setName("lzSockelquellauslassCheckbox"); // NOI18N

        org.jdesktop.layout.GroupLayout luftzulaessePanelLayout = new org.jdesktop.layout.GroupLayout(luftzulaessePanel);
        luftzulaessePanel.setLayout(luftzulaessePanelLayout);
        luftzulaessePanelLayout.setHorizontalGroup(
            luftzulaessePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(luftzulaessePanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(luftzulaessePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lzTellerventileCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lzSchlitzauslassCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lzFussbodenAuslassCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lzSockelquellauslassCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        luftzulaessePanelLayout.setVerticalGroup(
            luftzulaessePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(luftzulaessePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lzTellerventileCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lzSchlitzauslassCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lzFussbodenAuslassCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lzSockelquellauslassCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        luftauslaessePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Abluftdurchlässe"));

        laTellerventileCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        laTellerventileCheckbox.setMnemonic('v');
        laTellerventileCheckbox.setText("Tellerventile (Standard)");
        laTellerventileCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        laTellerventileCheckbox.setName("laTellerventileCheckbox"); // NOI18N

        org.jdesktop.layout.GroupLayout luftauslaessePanelLayout = new org.jdesktop.layout.GroupLayout(luftauslaessePanel);
        luftauslaessePanel.setLayout(luftauslaessePanelLayout);
        luftauslaessePanelLayout.setHorizontalGroup(
            luftauslaessePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(luftauslaessePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(laTellerventileCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(131, Short.MAX_VALUE))
        );
        luftauslaessePanelLayout.setVerticalGroup(
            luftauslaessePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(luftauslaessePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(laTellerventileCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        fortluftPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Fortluft"));

        flDachdurchfuehrungRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fortluftButtonGroup.add(flDachdurchfuehrungRadioButton);
        flDachdurchfuehrungRadioButton.setMnemonic('f');
        flDachdurchfuehrungRadioButton.setSelected(true);
        flDachdurchfuehrungRadioButton.setText("Dachdurchführung");
        flDachdurchfuehrungRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        flDachdurchfuehrungRadioButton.setName("flDachdurchfuehrungRadioButton"); // NOI18N

        flWandRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fortluftButtonGroup.add(flWandRadioButton);
        flWandRadioButton.setMnemonic('n');
        flWandRadioButton.setText("Wand (Luftgitter)");
        flWandRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        flWandRadioButton.setName("flWandRadioButton"); // NOI18N

        flLichtschachtRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fortluftButtonGroup.add(flLichtschachtRadioButton);
        flLichtschachtRadioButton.setMnemonic('t');
        flLichtschachtRadioButton.setText("Lichtschacht (Kellergeschoss)");
        flLichtschachtRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        flLichtschachtRadioButton.setName("flLichtschachtRadioButton"); // NOI18N

        org.jdesktop.layout.GroupLayout fortluftPanelLayout = new org.jdesktop.layout.GroupLayout(fortluftPanel);
        fortluftPanel.setLayout(fortluftPanelLayout);
        fortluftPanelLayout.setHorizontalGroup(
            fortluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fortluftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(fortluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(flDachdurchfuehrungRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(flWandRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(flLichtschachtRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        fortluftPanelLayout.setVerticalGroup(
            fortluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fortluftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(flDachdurchfuehrungRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(flWandRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(flLichtschachtRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        hygieneKennzeichenPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hygiene-Kennzeichen"));

        hkAusfuehrungCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        hkAusfuehrungCheckbox.setMnemonic('T');
        hkAusfuehrungCheckbox.setText("Ausführung und Lage der Außenluftansaugung");
        hkAusfuehrungCheckbox.setToolTipText("<html>Die Ausführung und Lage der Außenluftansaugung \n<br>bzw. ihre Mindesthöhe über Grund bzw. über Dach \n<br>muss sicherstellen, dass die am wenigsten belastete \n<br>Außenluft angesaugt wird. Kurzschlüsse mit der Fortluft \n<br>müssen vermieden werden. Eine Ansaugung direkt über \n<br>Erdgleiche (Staubbelastung, Schnee) in engen Gruben \n<br>und Schächten ist nicht zulässig.</html>");
        hkAusfuehrungCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        hkAusfuehrungCheckbox.setName("hkAusfuehrungCheckbox"); // NOI18N
        hkAusfuehrungCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hkAusfuehrungCheckboxActionPerformed(evt);
            }
        });

        hkFilterungCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        hkFilterungCheckbox.setMnemonic('S');
        hkFilterungCheckbox.setText("Filterung der Außenluft und der Abluft");
        hkFilterungCheckbox.setToolTipText("<html>Die Filterung der Außenluft muss sicherstellen, \n<br>dass die Zuluft bei Zu-/ Abluftanlagen möglichst \n<br>staubfrei den Räumen zugeführt wird. Dazu sind \n<br>geeignete Filter einzusetzen und in regelmäßigen \n<br>Abständen zu erneuern bzw. zu reinigen. Eine \n<br>Durchfeuchtung der Filter ist bei bestimmungs-\n<br>gemäßem Betrieb zu vermeiden.</html>");
        hkFilterungCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        hkFilterungCheckbox.setName("hkFilterungCheckbox"); // NOI18N
        hkFilterungCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hkFilterungCheckboxActionPerformed(evt);
            }
        });

        hkVerschmutzungCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        hkVerschmutzungCheckbox.setMnemonic('F');
        hkVerschmutzungCheckbox.setText("möglichst keine Verschmutzung des Luftleitungsnetzes");
        hkVerschmutzungCheckbox.setToolTipText("<html>Eine Verschmutzung des Luftleitungsnetzes \n<br>ist zu verhindern. Scharfkantige und spitze \n<br>Teile im Luftstrom begünstigen ebenso wie \n<br>innen stark oberflächenraue Luftleitungen \n<br>das Absetzen von Schmutz sowie die Geräusch-\n<br>entstehung und sollen deshalb vermieden \n<br>werden. Das gilt im besonderen Maße für Zuluftleitungen.</html>");
        hkVerschmutzungCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        hkVerschmutzungCheckbox.setName("hkVerschmutzungCheckbox"); // NOI18N
        hkVerschmutzungCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hkVerschmutzungCheckboxActionPerformed(evt);
            }
        });

        hkKennzeichenWertLabel.setForeground(new java.awt.Color(255, 0, 0));
        hkKennzeichenWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        hkKennzeichenWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        hkKennzeichenWertLabel.setName("hkKennzeichenWertLabel"); // NOI18N

        hkDichtheitsklasseCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        hkDichtheitsklasseCheckbox.setMnemonic('F');
        hkDichtheitsklasseCheckbox.setText("Dichtheitsklasse B der Luftleitungen");
        hkDichtheitsklasseCheckbox.setToolTipText("");
        hkDichtheitsklasseCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        hkDichtheitsklasseCheckbox.setName("hkDichtheitsklasseCheckbox"); // NOI18N
        hkDichtheitsklasseCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hkDichtheitsklasseCheckboxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout hygieneKennzeichenPanelLayout = new org.jdesktop.layout.GroupLayout(hygieneKennzeichenPanel);
        hygieneKennzeichenPanel.setLayout(hygieneKennzeichenPanelLayout);
        hygieneKennzeichenPanelLayout.setHorizontalGroup(
            hygieneKennzeichenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(hygieneKennzeichenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(hygieneKennzeichenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(hkAusfuehrungCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(hkFilterungCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(hkVerschmutzungCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(hkKennzeichenWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 281, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(hkDichtheitsklasseCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        hygieneKennzeichenPanelLayout.setVerticalGroup(
            hygieneKennzeichenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(hygieneKennzeichenPanelLayout.createSequentialGroup()
                .add(hkAusfuehrungCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(hkFilterungCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(hkVerschmutzungCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(hkDichtheitsklasseCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                .add(hkKennzeichenWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        energieKennzeichenPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Energie-Kennzeichen"));

        ekBemessungCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ekBemessungCheckbox.setText("Bemessung und Ausführung des Lüftungssystems ");
        ekBemessungCheckbox.setToolTipText("<html>Durch die Bemessung und Ausführung der \n<br>Lüftungsleitungen sowie der Komponenten ist \n<br>der Antriebs- und Hilfsenergieeinsatz zu minimieren.</html>");
        ekBemessungCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ekBemessungCheckbox.setName("ekBemessungCheckbox"); // NOI18N
        ekBemessungCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ekBemessungCheckboxActionPerformed(evt);
            }
        });

        ekRueckgewinnungCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ekRueckgewinnungCheckbox.setText("Rückgewinnung von Abluftwärme ");
        ekRueckgewinnungCheckbox.setToolTipText("<html>Durch Rückgewinnung von Abluftwärme mit \n<br>Wärmeübertragern/ Wärmepumpen, die Nutzung \n<br>von regenerativen Energiequellen, z.B. mit \n<br>Erdreich-Luft-Wärmeübertragern oder mit \n<br>Einkopplung von Solarwärme.</html>");
        ekRueckgewinnungCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ekRueckgewinnungCheckbox.setName("ekRueckgewinnungCheckbox"); // NOI18N
        ekRueckgewinnungCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ekRueckgewinnungCheckboxActionPerformed(evt);
            }
        });

        ekRegelungCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ekRegelungCheckbox.setText("Zweckmäßige Regelung ");
        ekRegelungCheckbox.setToolTipText("<html>Durch zweckmäßige Regelung einschließlich \n<br>eines optimierten Frostschutzbetriebes wird \n<br>eine rationelle Energienutzung gefördert.</html>");
        ekRegelungCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ekRegelungCheckbox.setName("ekRegelungCheckbox"); // NOI18N
        ekRegelungCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ekRegelungCheckboxActionPerformed(evt);
            }
        });

        ekZuAbluftCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ekZuAbluftCheckbox.setSelected(true);
        ekZuAbluftCheckbox.setText("Zu-/ Abluftgeräte mit Wärmerückgewinnung ");
        ekZuAbluftCheckbox.setToolTipText("<html>Zu-/ Abluftgeräte mit Wärmerückgewinnung \n<br>aus der Abluft mit Wärmeübertrager/ Wärmepumpe \n<br>oder mit Nutzung von regenerativen Energiequellen, \n<br>z.B. mit Erdreich-Luft-Wärmeübertragern oder \n<br>mit Einkopplung von Solarwärme.</html>");
        ekZuAbluftCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ekZuAbluftCheckbox.setName("ekZuAbluftCheckbox"); // NOI18N
        ekZuAbluftCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ekZuAbluftCheckboxActionPerformed(evt);
            }
        });

        ekKennzeichenWertLabel.setForeground(new java.awt.Color(255, 0, 0));
        ekKennzeichenWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ekKennzeichenWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        ekKennzeichenWertLabel.setName("ekKennzeichenWertLabel"); // NOI18N

        org.jdesktop.layout.GroupLayout energieKennzeichenPanelLayout = new org.jdesktop.layout.GroupLayout(energieKennzeichenPanel);
        energieKennzeichenPanel.setLayout(energieKennzeichenPanelLayout);
        energieKennzeichenPanelLayout.setHorizontalGroup(
            energieKennzeichenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(energieKennzeichenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(energieKennzeichenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ekZuAbluftCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ekKennzeichenWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 281, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ekRueckgewinnungCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ekBemessungCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ekRegelungCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        energieKennzeichenPanelLayout.setVerticalGroup(
            energieKennzeichenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(energieKennzeichenPanelLayout.createSequentialGroup()
                .add(ekZuAbluftCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ekBemessungCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ekRueckgewinnungCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ekRegelungCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ekKennzeichenWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        seebassTabelle.setEnabled(false);
        seebassTabelle.setFocusable(false);
        seebassTabelle.setName("seebassTabelle"); // NOI18N
        seebassTabelle.setOpaque(false);
        seebassScrollPane.setViewportView(seebassTabelle);

        kzRueckschlagPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Rückschlagklappe"));

        kzRueckschlagklappeCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        kzRueckschlagklappeCheckbox.setMnemonic('v');
        kzRueckschlagklappeCheckbox.setText("Lüftunganlage mit Rückschlagklappe");
        kzRueckschlagklappeCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kzRueckschlagklappeCheckbox.setName("kzRueckschlagklappeCheckbox"); // NOI18N
        kzRueckschlagklappeCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kzRueckschlagklappeCheckboxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout kzRueckschlagPanelLayout = new org.jdesktop.layout.GroupLayout(kzRueckschlagPanel);
        kzRueckschlagPanel.setLayout(kzRueckschlagPanelLayout);
        kzRueckschlagPanelLayout.setHorizontalGroup(
            kzRueckschlagPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(kzRueckschlagPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(kzRueckschlagklappeCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        kzRueckschlagPanelLayout.setVerticalGroup(
            kzRueckschlagPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(kzRueckschlagPanelLayout.createSequentialGroup()
                .add(kzRueckschlagklappeCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        kzSchallschutzPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Schallschutz-Kennzeichnung"));

        kzSchallschutzCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        kzSchallschutzCheckbox.setMnemonic('v');
        kzSchallschutzCheckbox.setText("Lüftunganlage mit Schallschutz");
        kzSchallschutzCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kzSchallschutzCheckbox.setName("kzSchallschutzCheckbox"); // NOI18N
        kzSchallschutzCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kzSchallschutzCheckboxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout kzSchallschutzPanelLayout = new org.jdesktop.layout.GroupLayout(kzSchallschutzPanel);
        kzSchallschutzPanel.setLayout(kzSchallschutzPanelLayout);
        kzSchallschutzPanelLayout.setHorizontalGroup(
            kzSchallschutzPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(kzSchallschutzPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(kzSchallschutzCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        kzSchallschutzPanelLayout.setVerticalGroup(
            kzSchallschutzPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(kzSchallschutzPanelLayout.createSequentialGroup()
                .add(kzSchallschutzCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        kzFeuerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Feuerstätten-Kennzeichnung"));

        kzFeuerstaetteCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        kzFeuerstaetteCheckBox.setMnemonic('v');
        kzFeuerstaetteCheckBox.setText("Lüftunganlage mit Sicherheitseinrichtung");
        kzFeuerstaetteCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kzFeuerstaetteCheckBox.setName("kzFeuerstaetteCheckBox"); // NOI18N
        kzFeuerstaetteCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kzFeuerstaetteCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout kzFeuerPanelLayout = new org.jdesktop.layout.GroupLayout(kzFeuerPanel);
        kzFeuerPanel.setLayout(kzFeuerPanelLayout);
        kzFeuerPanelLayout.setHorizontalGroup(
            kzFeuerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(kzFeuerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(kzFeuerstaetteCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );
        kzFeuerPanelLayout.setVerticalGroup(
            kzFeuerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(kzFeuerPanelLayout.createSequentialGroup()
                .add(kzFeuerstaetteCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        luftauslaessePanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Kennzeichnung der Lüftungsanlage"));

        kzKennzeichenLabel.setForeground(new java.awt.Color(255, 0, 0));
        kzKennzeichenLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        kzKennzeichenLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        kzKennzeichenLabel.setName("kzKennzeichenLabel"); // NOI18N

        org.jdesktop.layout.GroupLayout luftauslaessePanel4Layout = new org.jdesktop.layout.GroupLayout(luftauslaessePanel4);
        luftauslaessePanel4.setLayout(luftauslaessePanel4Layout);
        luftauslaessePanel4Layout.setHorizontalGroup(
            luftauslaessePanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(luftauslaessePanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(kzKennzeichenLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 281, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(373, Short.MAX_VALUE))
        );
        luftauslaessePanel4Layout.setVerticalGroup(
            luftauslaessePanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(luftauslaessePanel4Layout.createSequentialGroup()
                .add(kzKennzeichenLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout informationenPanelLayout = new org.jdesktop.layout.GroupLayout(informationenPanel);
        informationenPanel.setLayout(informationenPanelLayout);
        informationenPanelLayout.setHorizontalGroup(
            informationenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, informationenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(informationenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, luftauslaessePanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, informationenPanelLayout.createSequentialGroup()
                        .add(kzRueckschlagPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(kzSchallschutzPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(kzFeuerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, informationenPanelLayout.createSequentialGroup()
                        .add(informationenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(luftzulaessePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, geraetestandortPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(informationenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(luftauslaessePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(luftkanalverlegungPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(informationenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fortluftPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(aussenluftPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, informationenPanelLayout.createSequentialGroup()
                        .add(energieKennzeichenPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(hygieneKennzeichenPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(seebassScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 176, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(278, 278, 278))
        );
        informationenPanelLayout.setVerticalGroup(
            informationenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(informationenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(informationenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(informationenPanelLayout.createSequentialGroup()
                        .add(informationenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(geraetestandortPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(luftkanalverlegungPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(aussenluftPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(informationenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(luftzulaessePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(luftauslaessePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(fortluftPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(informationenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(energieKennzeichenPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(hygieneKennzeichenPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(seebassScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(informationenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(kzRueckschlagPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(kzSchallschutzPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(kzFeuerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(luftauslaessePanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        projektTabbedPane.addTab("Anlagendaten", informationenPanel);

        wfTabelleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Raum", "Geschoss", "Luftart", "Raumfläche (m²)", "Raumhöhe (m)", "Zuluftfaktor", "Abluftvolumenstrom"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        wfTabelleTable.setDragEnabled(true);
        wfTabelleTable.setName("wfTabelleTable"); // NOI18N
        wfTabelleTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                wfTabelleTableKeyTyped(evt);
            }
        });
        wfTabelleScrollPane.setViewportView(wfTabelleTable);

        wfTabelleSpinner.setBorder(null);
        wfTabelleSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                wfTabelleSpinnerStateChanged(evt);
            }
        });

        wfBezeichnungLabel.setDisplayedMnemonic('B');
        wfBezeichnungLabel.setLabelFor(wfBezeichnungCombobox);
        wfBezeichnungLabel.setText("Raumtyp");

        wfBezeichnungCombobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Wohnzimmer", "Kinderzimmer", "Schlafzimmer", "Esszimmer", "Arbeitszimmer", "Gästezimmer", "Hausarbeitsraum", "Kellerraum", "WC", "Küche, Kochnische", "Bad mit/ohne WC", "Duschraum", "Sauna", "Flur", "Diele" }));
        wfBezeichnungCombobox.setAutoCompletion(true);
        wfBezeichnungCombobox.setName("wfBezeichnungCombobox"); // NOI18N
        wfBezeichnungCombobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wfBezeichnungComboboxActionPerformed(evt);
            }
        });

        wfGeschossCombobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "KG", "EG", "OG", "DG", "SB" }));
        wfGeschossCombobox.setAutoCompletion(true);
        wfGeschossCombobox.setName("wfGeschossCombobox"); // NOI18N

        wfGeschossLabel.setDisplayedMnemonic('G');
        wfGeschossLabel.setText("Geschoss");

        wfBelueftungCombobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ZU", "AB", "ZU/AB", "ÜB" }));
        wfBelueftungCombobox.setName("wfBelueftungCombobox"); // NOI18N
        wfBelueftungCombobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wfBelueftungComboboxActionPerformed(evt);
            }
        });

        wfFlaecheLabel.setDisplayedMnemonic('F');
        wfFlaecheLabel.setText("Luftart");

        wfFlaecheTextField.setName("wfFlaecheTextField"); // NOI18N
        wfFlaecheTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wfFlaecheTextFieldActionPerformed(evt);
            }
        });

        wfHoeheTextField.setText("2,50");
        wfHoeheTextField.setName("wfHoeheTextField"); // NOI18N
        wfHoeheTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wfHoeheTextFieldActionPerformed(evt);
            }
        });

        wfHoeheLabel.setDisplayedMnemonic('H');
        wfHoeheLabel.setText("Raumhöhe (m)");

        wfHinzufuegenButton.setMnemonic('z');
        wfHinzufuegenButton.setText("Hinzufügen");
        wfHinzufuegenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wfHinzufuegenButtonActionPerformed(evt);
            }
        });
        wfHinzufuegenButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                wfHinzufuegenButtonKeyTyped(evt);
            }
        });

        wfBelueftungLabel.setDisplayedMnemonic('l');
        wfBelueftungLabel.setText("<html>Raumfläche (m<sup>2</sup>)");

        wfHoeheLabel1.setDisplayedMnemonic('H');
        wfHoeheLabel1.setText("Zuluftfaktor");

        wfZuluftfaktorTextField.setText("3");
        wfZuluftfaktorTextField.setName("wfZuluftfaktorTextField"); // NOI18N
        wfZuluftfaktorTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wfZuluftfaktorTextFieldActionPerformed(evt);
            }
        });

        wfAbluftVolumenLabel.setDisplayedMnemonic('H');
        wfAbluftVolumenLabel.setText("Abluftvolumenstrom (m³)");

        wfAbluftVolumenTextField.setEnabled(false);
        wfAbluftVolumenTextField.setName("wfAbluftVolumenTextField"); // NOI18N
        wfAbluftVolumenTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wfAbluftVolumenTextFieldActionPerformed(evt);
            }
        });

        wfRaumnameLabel.setDisplayedMnemonic('H');
        wfRaumnameLabel.setText("abw. Raumbezeichnung");

        wfRaumNameTextField.setName("wfRaumNameTextField"); // NOI18N
        wfRaumNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wfRaumNameTextFieldActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout wfNeuerEintragPanelLayout = new org.jdesktop.layout.GroupLayout(wfNeuerEintragPanel);
        wfNeuerEintragPanel.setLayout(wfNeuerEintragPanelLayout);
        wfNeuerEintragPanelLayout.setHorizontalGroup(
            wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wfNeuerEintragPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(wfBezeichnungLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                    .add(wfBezeichnungCombobox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(wfRaumNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                    .add(wfRaumnameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(wfNeuerEintragPanelLayout.createSequentialGroup()
                        .add(wfGeschossLabel)
                        .add(18, 18, 18)
                        .add(wfFlaecheLabel))
                    .add(wfNeuerEintragPanelLayout.createSequentialGroup()
                        .add(wfGeschossCombobox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(wfBelueftungCombobox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(wfBelueftungLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(wfFlaecheTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, wfNeuerEintragPanelLayout.createSequentialGroup()
                        .add(wfHoeheLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(wfHoeheLabel1))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, wfNeuerEintragPanelLayout.createSequentialGroup()
                        .add(wfHoeheTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(wfZuluftfaktorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(wfNeuerEintragPanelLayout.createSequentialGroup()
                        .add(wfAbluftVolumenTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(wfHinzufuegenButton))
                    .add(wfAbluftVolumenLabel))
                .add(81, 81, 81))
        );

        wfNeuerEintragPanelLayout.linkSize(new java.awt.Component[] {wfAbluftVolumenLabel, wfAbluftVolumenTextField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        wfNeuerEintragPanelLayout.linkSize(new java.awt.Component[] {wfHoeheLabel1, wfZuluftfaktorTextField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        wfNeuerEintragPanelLayout.setVerticalGroup(
            wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wfNeuerEintragPanelLayout.createSequentialGroup()
                .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(wfNeuerEintragPanelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(wfRaumnameLabel)
                                .add(wfBezeichnungLabel))
                            .add(wfNeuerEintragPanelLayout.createSequentialGroup()
                                .add(17, 17, 17)
                                .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(wfRaumNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(wfBezeichnungCombobox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                    .add(wfNeuerEintragPanelLayout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(wfGeschossLabel)
                            .add(wfFlaecheLabel)))
                    .add(wfNeuerEintragPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(wfGeschossCombobox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(wfBelueftungCombobox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(wfBelueftungLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(wfHoeheLabel))
                    .add(wfNeuerEintragPanelLayout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(wfHoeheLabel1)
                                .add(wfAbluftVolumenLabel))
                            .add(wfNeuerEintragPanelLayout.createSequentialGroup()
                                .add(17, 17, 17)
                                .add(wfNeuerEintragPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(wfAbluftVolumenTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(wfZuluftfaktorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(wfHoeheTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(wfHinzufuegenButton)
                                    .add(wfFlaecheTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        wfEntfernenButton.setMnemonic('E');
        wfEntfernenButton.setText("Raum entfernen");
        wfEntfernenButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        wfEntfernenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wfEntfernenButtonActionPerformed(evt);
            }
        });

        wfRaumBearbeiten.setText("Raum bearbeiten");
        wfRaumBearbeiten.setMargin(new java.awt.Insets(2, 4, 2, 4));
        wfRaumBearbeiten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wfRaumBearbeitenActionPerformed(evt);
            }
        });

        wfRaumKopieren.setText("Raum kopieren");
        wfRaumKopieren.setMargin(new java.awt.Insets(2, 4, 2, 4));
        wfRaumKopieren.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wfRaumKopierenActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout wohnflaechePanelLayout = new org.jdesktop.layout.GroupLayout(wohnflaechePanel);
        wohnflaechePanel.setLayout(wohnflaechePanelLayout);
        wohnflaechePanelLayout.setHorizontalGroup(
            wohnflaechePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wohnflaechePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(wohnflaechePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(wohnflaechePanelLayout.createSequentialGroup()
                        .add(wfTabelleScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 934, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(wfTabelleSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(wohnflaechePanelLayout.createSequentialGroup()
                        .add(wfEntfernenButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 123, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(wfRaumBearbeiten, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 127, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(wfRaumKopieren, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 127, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(wfNeuerEintragPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        wohnflaechePanelLayout.setVerticalGroup(
            wohnflaechePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wohnflaechePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(wfNeuerEintragPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(wfTabelleScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 434, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wohnflaechePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(wfRaumBearbeiten)
                    .add(wfRaumKopieren)
                    .add(wfEntfernenButton))
                .add(33, 33, 33))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, wohnflaechePanelLayout.createSequentialGroup()
                .addContainerGap(312, Short.MAX_VALUE)
                .add(wfTabelleSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(220, 220, 220))
        );

        projektTabbedPane.addTab("Raumdaten", wohnflaechePanel);

        volumenstroemePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        vsInfo1Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Notwendigkeit der lüftungstechnischen Maßnahme"));

        vsGesamtvolumenLabel.setText("Feuchteschutz: Gesamt-Außenluftvolumenstrom");

        vsGesamtAUVolumenWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsGesamtAUVolumenWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsGesamtAUVolumenWertLabel.setName("vsGesamtAUVolumenWertLabel"); // NOI18N

        vsVolumentstromInfilLabel.setText("Luftvolumenstrom durch Infiltration");

        vsVolumenstromInfilWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsVolumenstromInfilWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsVolumenstromInfilWertLabel.setName("vsVolumenstromInfilWertLabel"); // NOI18N

        vsVolumenstromInfilM3Label.setText("m³/h");

        vsGesAussenluftmengeM3Label.setText("m³/h");

        vsLTMerforderlichWertLabel.setForeground(new java.awt.Color(255, 0, 0));
        vsLTMerforderlichWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        vsLTMerforderlichWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsLTMerforderlichWertLabel.setName("vsLTMerforderlichWertLabel"); // NOI18N

        org.jdesktop.layout.GroupLayout vsInfo1PanelLayout = new org.jdesktop.layout.GroupLayout(vsInfo1Panel);
        vsInfo1Panel.setLayout(vsInfo1PanelLayout);
        vsInfo1PanelLayout.setHorizontalGroup(
            vsInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(vsInfo1PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(vsInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsGesamtvolumenLabel)
                    .add(vsVolumentstromInfilLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, vsVolumenstromInfilWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, vsGesamtAUVolumenWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(vsInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsInfo1PanelLayout.createSequentialGroup()
                        .add(vsGesAussenluftmengeM3Label)
                        .add(48, 48, 48)
                        .add(vsLTMerforderlichWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 362, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(vsVolumenstromInfilM3Label))
                .add(192, 192, 192))
        );
        vsInfo1PanelLayout.setVerticalGroup(
            vsInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(vsInfo1PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(vsInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsLTMerforderlichWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(vsInfo1PanelLayout.createSequentialGroup()
                        .add(vsInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsGesamtAUVolumenWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsGesAussenluftmengeM3Label)
                            .add(vsGesamtvolumenLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsVolumentstromInfilLabel)
                            .add(vsVolumenstromInfilM3Label)
                            .add(vsVolumenstromInfilWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .add(20, 20, 20))
        );

        volumenstroemePanel.add(vsInfo1Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 1000, -1));

        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        vsInfo2Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Gesamt-Luftvolumenstrom für lüftungstechnische Maßnahmen"));
        vsInfo2Panel.setPreferredSize(new java.awt.Dimension(100, 100));

        vsMindestlueftungLabel.setText("Reduzierte Lüftung");

        vsGrundlueftungLabel.setText("Nennlüftung");

        vsIntensivlueftungLabel.setText("Intensivlüftung");

        wirkInfiltrationCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        wirkInfiltrationCheckbox.setSelected(true);
        wirkInfiltrationCheckbox.setText("mit Infiltrationsanteil berechnen");
        wirkInfiltrationCheckbox.setToolTipText("Es werden die Raumvolumenströme inkl. des wirksamen Infiltrationsanteils berechnet.");
        wirkInfiltrationCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        wirkInfiltrationCheckbox.setName("wirkInfiltrationCheckbox"); // NOI18N

        vsLuftvolumenstromLabel.setText("Luftvolumenstrom");

        vsLuftwechselLabel.setText("Luftwechsel");

        vsMindestlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsMindestlueftungWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsMindestlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsMindestlueftungWertLabel.setName("vsMindestlueftungWertLabel"); // NOI18N

        vsLueftungM3Label1.setText("m³/h");

        vsMLLuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsMLLuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsMLLuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsMLLuftwechselWertLabel.setName("vsMLLuftwechselWertLabel"); // NOI18N

        vsLueftungHLabel1.setText("1/h");

        vsGrundlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsGrundlueftungWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsGrundlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsGrundlueftungWertLabel.setName("vsGrundlueftungWertLabel"); // NOI18N

        vsIntensivlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsIntensivlueftungWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsIntensivlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsIntensivlueftungWertLabel.setName("vsIntensivlueftungWertLabel"); // NOI18N

        vsLueftungM3Label2.setText("m³/h");

        vsLueftungM3Label3.setText("m³/h");

        vsGLLuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsGLLuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsGLLuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsGLLuftwechselWertLabel.setName("vsGLLuftwechselWertLabel"); // NOI18N

        vsILLuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsILLuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsILLuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsILLuftwechselWertLabel.setName("vsILLuftwechselWertLabel"); // NOI18N

        vsLueftungHLabel2.setText("1/h");

        vsLueftungHLabel3.setText("1/h");

        org.jdesktop.layout.GroupLayout vsInfo2PanelLayout = new org.jdesktop.layout.GroupLayout(vsInfo2Panel);
        vsInfo2Panel.setLayout(vsInfo2PanelLayout);
        vsInfo2PanelLayout.setHorizontalGroup(
            vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(vsInfo2PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsLuftvolumenstromLabel)
                    .add(vsLuftwechselLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(vsMindestlueftungLabel)
                    .add(vsMLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(vsMindestlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 169, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(vsInfo2PanelLayout.createSequentialGroup()
                        .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungHLabel1)
                            .add(vsLueftungM3Label1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(vsGLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(vsGrundlueftungWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)))
                    .add(vsGrundlueftungLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(vsInfo2PanelLayout.createSequentialGroup()
                        .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label2)
                            .add(vsLueftungHLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(vsILLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(vsIntensivlueftungWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)))
                    .add(vsIntensivlueftungLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsInfo2PanelLayout.createSequentialGroup()
                        .add(vsLueftungM3Label3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 82, Short.MAX_VALUE)
                        .add(wirkInfiltrationCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(51, 51, 51))
                    .add(vsInfo2PanelLayout.createSequentialGroup()
                        .add(vsLueftungHLabel3)
                        .addContainerGap(309, Short.MAX_VALUE))))
        );
        vsInfo2PanelLayout.setVerticalGroup(
            vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(vsInfo2PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(vsMindestlueftungLabel)
                    .add(vsGrundlueftungLabel)
                    .add(vsIntensivlueftungLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsInfo2PanelLayout.createSequentialGroup()
                        .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label1)
                            .add(vsMindestlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsMLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLueftungHLabel1)))
                    .add(vsInfo2PanelLayout.createSequentialGroup()
                        .add(vsLuftvolumenstromLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsLuftwechselLabel))
                    .add(vsInfo2PanelLayout.createSequentialGroup()
                        .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(vsLueftungM3Label3)
                                .add(wirkInfiltrationCheckbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(vsIntensivlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsILLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLueftungHLabel3)))
                    .add(vsInfo2PanelLayout.createSequentialGroup()
                        .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label2)
                            .add(vsGrundlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsGLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLueftungHLabel2))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.add(vsInfo2Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 300, 1000, 100));

        vsLuftmengeBerechnenButton.setMnemonic('a');
        vsLuftmengeBerechnenButton.setText("Berechnen");
        vsLuftmengeBerechnenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vsLuftmengeBerechnenButtonActionPerformed(evt);
            }
        });
        jPanel10.add(vsLuftmengeBerechnenButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 400, 170, -1));

        vsInfo6Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Gesamtabluftvolumenströme der Räume"));

        vsMindestlueftungLabel2.setText("Reduzierte Lüftung");

        vsRAbFELuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsRAbFELuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsRAbFELuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsRAbFELuftwechselWertLabel.setName("vsRAbFELuftwechselWertLabel"); // NOI18N

        vsLueftungHLabel8.setText("1/h");

        vsGrundlueftungLabel2.setText("Nennlüftung");

        vsIntensivlueftungLabel2.setText("Intensivlüftung");

        vsFeuchtelueftungLabel2.setText("Lüftung zum Feuchteschutz");

        vsRAbFeuchteschutzWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsRAbFeuchteschutzWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsRAbFeuchteschutzWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsRAbFeuchteschutzWertLabel.setName("vsRAbFeuchteschutzWertLabel"); // NOI18N

        vsLueftungM3Label8.setText("m³/h");

        vsLuftvolumenstromLabel2.setText("Luftvolumenstrom");

        vsLuftwechselLabel2.setText("Luftwechsel");

        vsRAbMindestlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsRAbMindestlueftungWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsRAbMindestlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsRAbMindestlueftungWertLabel.setName("vsRAbMindestlueftungWertLabel"); // NOI18N

        vsLueftungM3Label9.setText("m³/h");

        vsRAbMLLuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsRAbMLLuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsRAbMLLuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsRAbMLLuftwechselWertLabel.setName("vsRAbMLLuftwechselWertLabel"); // NOI18N

        vsLueftungHLabel9.setText("1/h");

        vsRAbGrundlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsRAbGrundlueftungWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsRAbGrundlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsRAbGrundlueftungWertLabel.setName("vsRAbGrundlueftungWertLabel"); // NOI18N

        vsRAbIntensivlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsRAbIntensivlueftungWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsRAbIntensivlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsRAbIntensivlueftungWertLabel.setName("vsRAbIntensivlueftungWertLabel"); // NOI18N

        vsLueftungM3Label10.setText("m³/h");

        vsLueftungM3Label11.setText("m³/h");

        vsRAbGLLuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsRAbGLLuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsRAbGLLuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsRAbGLLuftwechselWertLabel.setName("vsRAbGLLuftwechselWertLabel"); // NOI18N

        vsRAbILLuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsRAbILLuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsRAbILLuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsRAbILLuftwechselWertLabel.setName("vsRAbILLuftwechselWertLabel"); // NOI18N

        vsLueftungHLabel10.setText("1/h");

        vsLueftungHLabel11.setText("1/h");

        org.jdesktop.layout.GroupLayout vsInfo6PanelLayout = new org.jdesktop.layout.GroupLayout(vsInfo6Panel);
        vsInfo6Panel.setLayout(vsInfo6PanelLayout);
        vsInfo6PanelLayout.setHorizontalGroup(
            vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(vsInfo6PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(vsInfo6PanelLayout.createSequentialGroup()
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLuftvolumenstromLabel2)
                            .add(vsLuftwechselLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(vsRAbFELuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(vsRAbFeuchteschutzWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)))
                    .add(vsFeuchtelueftungLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsLueftungHLabel8)
                    .add(vsLueftungM3Label8))
                .add(11, 11, 11)
                .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(vsMindestlueftungLabel2)
                    .add(vsRAbMLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(vsRAbMindestlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 126, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(vsInfo6PanelLayout.createSequentialGroup()
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungHLabel9)
                            .add(vsLueftungM3Label9))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(vsRAbGLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(vsRAbGrundlueftungWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)))
                    .add(vsGrundlueftungLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(vsInfo6PanelLayout.createSequentialGroup()
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label10)
                            .add(vsLueftungHLabel10))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, vsRAbILLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, vsRAbIntensivlueftungWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)))
                    .add(vsIntensivlueftungLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsLueftungHLabel11)
                    .add(vsLueftungM3Label11))
                .add(169, 169, 169))
        );
        vsInfo6PanelLayout.setVerticalGroup(
            vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(vsInfo6PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(vsMindestlueftungLabel2)
                    .add(vsFeuchtelueftungLabel2)
                    .add(vsGrundlueftungLabel2)
                    .add(vsIntensivlueftungLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsInfo6PanelLayout.createSequentialGroup()
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label9)
                            .add(vsRAbMindestlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsRAbMLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLueftungHLabel9)))
                    .add(vsInfo6PanelLayout.createSequentialGroup()
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLuftvolumenstromLabel2)
                            .add(vsLueftungM3Label8)
                            .add(vsRAbFeuchteschutzWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsRAbFELuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLuftwechselLabel2)
                            .add(vsLueftungHLabel8)))
                    .add(vsInfo6PanelLayout.createSequentialGroup()
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label10)
                            .add(vsRAbGrundlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsRAbGLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLueftungHLabel10)))
                    .add(vsInfo6PanelLayout.createSequentialGroup()
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label11)
                            .add(vsRAbIntensivlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo6PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsRAbILLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLueftungHLabel11))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.add(vsInfo6Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 1000, 100));

        vsInfo3Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Gesamt-Außenluftvolumenströme für Nutzungseinheit"));

        vsMindestlueftungLabel1.setText("Reduzierte Lüftung");

        vsAUFELuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsAUFELuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsAUFELuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsAUFELuftwechselWertLabel.setName("vsFELuftwechselWertLabel"); // NOI18N

        vsLueftungHLabel4.setText("1/h");

        vsGrundlueftungLabel1.setText("Nennlüftung");

        vsIntensivlueftungLabel1.setText("Intensivlüftung");

        vsFeuchtelueftungLabel1.setText("Lüftung zum Feuchteschutz");

        vsAUFeuchteschutzWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsAUFeuchteschutzWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsAUFeuchteschutzWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsAUFeuchteschutzWertLabel.setName("vsFeuchteschutzWertLabel"); // NOI18N

        vsLueftungM3Label4.setText("m³/h");

        vsLuftvolumenstromLabel1.setText("Luftvolumenstrom");

        vsLuftwechselLabel1.setText("Luftwechsel");

        vsAUMindestlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsAUMindestlueftungWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsAUMindestlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsAUMindestlueftungWertLabel.setName("vsAUMindestlueftungWertLabel"); // NOI18N

        vsLueftungM3Label5.setText("m³/h");

        vsAUMLLuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsAUMLLuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsAUMLLuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsAUMLLuftwechselWertLabel.setName("vsAUMLLuftwechselWertLabel"); // NOI18N

        vsLueftungHLabel5.setText("1/h");

        vsAUGrundlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsAUGrundlueftungWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsAUGrundlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsAUGrundlueftungWertLabel.setName("vsAUGrundlueftungWertLabel"); // NOI18N

        vsAUIntensivlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsAUIntensivlueftungWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsAUIntensivlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsAUIntensivlueftungWertLabel.setName("vsAUIntensivlueftungWertLabel"); // NOI18N

        vsLueftungM3Label6.setText("m³/h");

        vsLueftungM3Label7.setText("m³/h");

        vsAUGLLuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsAUGLLuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsAUGLLuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsAUGLLuftwechselWertLabel.setName("vsAUGLLuftwechselWertLabel"); // NOI18N

        vsAUILLuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsAUILLuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsAUILLuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsAUILLuftwechselWertLabel.setName("vsAUILLuftwechselWertLabel"); // NOI18N

        vsLueftungHLabel6.setText("1/h");

        vsLueftungHLabel7.setText("1/h");

        org.jdesktop.layout.GroupLayout vsInfo3PanelLayout = new org.jdesktop.layout.GroupLayout(vsInfo3Panel);
        vsInfo3Panel.setLayout(vsInfo3PanelLayout);
        vsInfo3PanelLayout.setHorizontalGroup(
            vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(vsInfo3PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(vsInfo3PanelLayout.createSequentialGroup()
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLuftvolumenstromLabel1)
                            .add(vsLuftwechselLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(vsAUFELuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(vsAUFeuchteschutzWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)))
                    .add(vsFeuchtelueftungLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsLueftungHLabel4)
                    .add(vsLueftungM3Label4))
                .add(11, 11, 11)
                .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(vsMindestlueftungLabel1)
                    .add(vsAUMLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(vsAUMindestlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 126, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(vsInfo3PanelLayout.createSequentialGroup()
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungHLabel5)
                            .add(vsLueftungM3Label5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(vsAUGLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(vsAUGrundlueftungWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)))
                    .add(vsGrundlueftungLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(vsInfo3PanelLayout.createSequentialGroup()
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label6)
                            .add(vsLueftungHLabel6))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, vsAUILLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, vsAUIntensivlueftungWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)))
                    .add(vsIntensivlueftungLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsLueftungHLabel7)
                    .add(vsLueftungM3Label7))
                .add(169, 169, 169))
        );
        vsInfo3PanelLayout.setVerticalGroup(
            vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(vsInfo3PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(vsMindestlueftungLabel1)
                    .add(vsFeuchtelueftungLabel1)
                    .add(vsGrundlueftungLabel1)
                    .add(vsIntensivlueftungLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsInfo3PanelLayout.createSequentialGroup()
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label5)
                            .add(vsAUMindestlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsAUMLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLueftungHLabel5)))
                    .add(vsInfo3PanelLayout.createSequentialGroup()
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLuftvolumenstromLabel1)
                            .add(vsLueftungM3Label4)
                            .add(vsAUFeuchteschutzWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsAUFELuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLuftwechselLabel1)
                            .add(vsLueftungHLabel4)))
                    .add(vsInfo3PanelLayout.createSequentialGroup()
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label6)
                            .add(vsAUGrundlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsAUGLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLueftungHLabel6)))
                    .add(vsInfo3PanelLayout.createSequentialGroup()
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label7)
                            .add(vsAUIntensivlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo3PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsAUILLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLueftungHLabel7))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.add(vsInfo3Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 100));

        vsInfo5Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("personenbezogene Gesamt-Außenluftvolumenströme"));

        vsMindestlueftungLabel3.setText("Reduzierte Lüftung");

        vsPersFELuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsPersFELuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsPersFELuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsPersFELuftwechselWertLabel.setName("vsPersFELuftwechselWertLabel"); // NOI18N

        vsLueftungHLabel12.setText("1/h");

        vsGrundlueftungLabel3.setText("Nennlüftung");

        vsIntensivlueftungLabel3.setText("Intensivlüftung");

        vsFeuchtelueftungLabel3.setText("Lüftung zum Feuchteschutz");

        vsPersFeuchteschutzWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsPersFeuchteschutzWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsPersFeuchteschutzWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsPersFeuchteschutzWertLabel.setName("vsPersFeuchteschutzWertLabel"); // NOI18N

        vsLueftungM3Label12.setText("m³/h");

        vsLuftvolumenstromLabel3.setText("Luftvolumenstrom");

        vsLuftwechselLabel3.setText("Luftwechsel");

        vsPersMindestlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsPersMindestlueftungWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsPersMindestlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsPersMindestlueftungWertLabel.setName("vsPersMindestlueftungWertLabel"); // NOI18N

        vsLueftungM3Label13.setText("m³/h");

        vsPersMLLuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsPersMLLuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsPersMLLuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsPersMLLuftwechselWertLabel.setName("vsPersMLLuftwechselWertLabel"); // NOI18N

        vsLueftungHLabel13.setText("1/h");

        vsPersGrundlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsPersGrundlueftungWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsPersGrundlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsPersGrundlueftungWertLabel.setName("vsPersGrundlueftungWertLabel"); // NOI18N

        vsPersIntensivlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsPersIntensivlueftungWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsPersIntensivlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsPersIntensivlueftungWertLabel.setName("vsPersIntensivlueftungWertLabel"); // NOI18N

        vsLueftungM3Label14.setText("m³/h");

        vsLueftungM3Label15.setText("m³/h");

        vsPersGLLuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsPersGLLuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsPersGLLuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsPersGLLuftwechselWertLabel.setName("vsPersGLLuftwechselWertLabel"); // NOI18N

        vsPersILLuftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vsPersILLuftwechselWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        vsPersILLuftwechselWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        vsPersILLuftwechselWertLabel.setName("vsPersILLuftwechselWertLabel"); // NOI18N

        vsLueftungHLabel14.setText("1/h");

        vsLueftungHLabel15.setText("1/h");

        org.jdesktop.layout.GroupLayout vsInfo5PanelLayout = new org.jdesktop.layout.GroupLayout(vsInfo5Panel);
        vsInfo5Panel.setLayout(vsInfo5PanelLayout);
        vsInfo5PanelLayout.setHorizontalGroup(
            vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(vsInfo5PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(vsInfo5PanelLayout.createSequentialGroup()
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLuftvolumenstromLabel3)
                            .add(vsLuftwechselLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(vsPersFELuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(vsPersFeuchteschutzWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)))
                    .add(vsFeuchtelueftungLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsLueftungHLabel12)
                    .add(vsLueftungM3Label12))
                .add(11, 11, 11)
                .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(vsMindestlueftungLabel3)
                    .add(vsPersMLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(vsPersMindestlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 126, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(vsInfo5PanelLayout.createSequentialGroup()
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungHLabel13)
                            .add(vsLueftungM3Label13))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(vsPersGLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(vsPersGrundlueftungWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)))
                    .add(vsGrundlueftungLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(vsInfo5PanelLayout.createSequentialGroup()
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label14)
                            .add(vsLueftungHLabel14))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, vsPersILLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, vsPersIntensivlueftungWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)))
                    .add(vsIntensivlueftungLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsLueftungHLabel15)
                    .add(vsLueftungM3Label15))
                .add(169, 169, 169))
        );
        vsInfo5PanelLayout.setVerticalGroup(
            vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(vsInfo5PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(vsMindestlueftungLabel3)
                    .add(vsFeuchtelueftungLabel3)
                    .add(vsGrundlueftungLabel3)
                    .add(vsIntensivlueftungLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vsInfo5PanelLayout.createSequentialGroup()
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label13)
                            .add(vsPersMindestlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsPersMLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLueftungHLabel13)))
                    .add(vsInfo5PanelLayout.createSequentialGroup()
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLuftvolumenstromLabel3)
                            .add(vsLueftungM3Label12)
                            .add(vsPersFeuchteschutzWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsPersFELuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLuftwechselLabel3)
                            .add(vsLueftungHLabel12)))
                    .add(vsInfo5PanelLayout.createSequentialGroup()
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label14)
                            .add(vsPersGrundlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsPersGLLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLueftungHLabel14)))
                    .add(vsInfo5PanelLayout.createSequentialGroup()
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsLueftungM3Label15)
                            .add(vsPersIntensivlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vsInfo5PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(vsPersILLuftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vsLueftungHLabel15))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.add(vsInfo5Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 200, 1000, 100));

        volumenstroemePanel.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 1000, 470));

        projektTabbedPane.addTab("Außenluftvolumenströme", volumenstroemePanel);

        luftmengenermittlungPanel.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                luftmengenermittlungPanelAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        lmeGesamtvolumenLabel.setText("Gesamtvolumen der Nutzungseinheit");

        lmeGesamtvolumenWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lmeGesamtvolumenWertLabel.setName("lmeGesamtvolumenWertLabel"); // NOI18N

        lmeGesamtvolumenM3Label.setText("m³");

        lmeGebaeudeluftwechselLabel.setText("Luftwechsel der Nutzungseinheit");

        lmeGebaeudeluftwechselWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lmeGebaeudeluftwechselWertLabel.setName("lmeGebaeudeluftwechselWertLabel"); // NOI18N

        lmeGebaeudeluftwechselHLabel.setText("1/h");

        lmeInfo2Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Außenluftvolumenstrom der lüftungstechnischen Maßnahme"));

        lmeMindestlueftungLabel.setText("Reduzierte Lüftung");

        lmeMindestlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lmeMindestlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        lmeMindestlueftungWertLabel.setName("lmeMindestlueftungWertLabel"); // NOI18N

        lmeMindestlueftungM3Label.setText("m³/h");

        lmeGrundlueftungLabel.setText("Nennlüftung");

        lmeGrundlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lmeGrundlueftungWertLabel.setText(" ");
        lmeGrundlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        lmeGrundlueftungWertLabel.setName("lmeGrundlueftungWertLabel"); // NOI18N

        lmeGrundlueftungM3Label.setText("m³/h");

        lmeZentralgeraetCombobox.mySearch = "select ~Artikelnummer~ from ~artikelstamm~ where ~Kategorie~ = 1 and ~Gesperrt~ = false and ~MaxVolumenstrom~ <> 0";
        lmeZentralgeraetCombobox.fromDatabase();
        lmeZentralgeraetCombobox.setSelectedIndex(0);
        lmeZentralgeraetCombobox.setName("lmeZentralgeraetCombobox"); // NOI18N
        lmeZentralgeraetCombobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lmeZentralgeraetComboboxActionPerformed(evt);
            }
        });

        lmeIntensivlueftungLabel.setText("Intensivlüftung");

        lmeIntensivlueftungWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lmeIntensivlueftungWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        lmeIntensivlueftungWertLabel.setName("lmeIntensivlueftungWertLabel"); // NOI18N

        lmeIntensivlueftungM3Label.setText("m³/h");

        lmeVolumenstromCombobox.mySearch = "select distinct ~Volumenstrom~ from ~schalleistungspegel~ where ~Artikelnummer~ = '~myVolatile~'";
        lmeVolumenstromCombobox.myVolatile = lmeZentralgeraetCombobox.getString().trim();
        lmeVolumenstromCombobox.fromDatabase();
        try{
            lmeVolumenstromCombobox.setSelectedIndex(0);
        }
        catch (IllegalArgumentException  ex) {
            System.out.println("Index = 0!");
        }
        lmeVolumenstromCombobox.setName("lmeVolumenstromCombobox"); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setText("Zentralgerät");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setText("Volumenstrom");

        lmeFeuchtelueftungLabel.setText("Feuchteschutz");

        lmeFeuchteschutzWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lmeFeuchteschutzWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        lmeFeuchteschutzWertLabel.setName("lmeFeuchteschutzWertLabel"); // NOI18N

        lmeFeuchteschutzM3Label.setText("m³/h");

        org.jdesktop.layout.GroupLayout lmeInfo2PanelLayout = new org.jdesktop.layout.GroupLayout(lmeInfo2Panel);
        lmeInfo2Panel.setLayout(lmeInfo2PanelLayout);
        lmeInfo2PanelLayout.setHorizontalGroup(
            lmeInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lmeInfo2PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lmeInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lmeInfo2PanelLayout.createSequentialGroup()
                        .add(lmeFeuchtelueftungLabel)
                        .add(18, 18, 18)
                        .add(lmeFeuchteschutzWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                    .add(lmeInfo2PanelLayout.createSequentialGroup()
                        .add(lmeMindestlueftungLabel)
                        .add(18, 18, 18)
                        .add(lmeMindestlueftungWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE))
                    .add(lmeInfo2PanelLayout.createSequentialGroup()
                        .add(lmeGrundlueftungLabel)
                        .add(26, 26, 26)
                        .add(lmeGrundlueftungWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lmeInfo2PanelLayout.createSequentialGroup()
                        .add(lmeIntensivlueftungLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 37, Short.MAX_VALUE)
                        .add(lmeIntensivlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(0, 0, 0)
                .add(lmeInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lmeIntensivlueftungM3Label)
                    .add(lmeGrundlueftungM3Label)
                    .add(lmeMindestlueftungM3Label)
                    .add(lmeFeuchteschutzM3Label))
                .add(35, 35, 35)
                .add(lmeInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lmeInfo2PanelLayout.createSequentialGroup()
                        .add(jLabel9)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lmeVolumenstromCombobox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jLabel8)
                    .add(lmeZentralgeraetCombobox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        lmeInfo2PanelLayout.setVerticalGroup(
            lmeInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lmeInfo2PanelLayout.createSequentialGroup()
                .add(lmeInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lmeInfo2PanelLayout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lmeZentralgeraetCombobox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lmeInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel9)
                            .add(lmeVolumenstromCombobox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(lmeInfo2PanelLayout.createSequentialGroup()
                        .add(lmeInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lmeFeuchteschutzM3Label)
                            .add(lmeFeuchtelueftungLabel)
                            .add(lmeFeuchteschutzWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lmeInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lmeMindestlueftungLabel)
                            .add(lmeMindestlueftungM3Label)
                            .add(lmeMindestlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lmeInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lmeInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(lmeGrundlueftungM3Label)
                                .add(lmeGrundlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(lmeGrundlueftungLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lmeInfo2PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lmeIntensivlueftungWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lmeIntensivlueftungLabel)
                            .add(lmeIntensivlueftungM3Label))))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        lmeGesAussenluftmengeLabel.setText("Gesamtaußenluft-Volumenstrom");

        lmeGesAussenluftmengeM3Label.setText("m³/h");

        lmeGesAussenluftmengeWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lmeGesAussenluftmengeWertLabel.setName("lmeGesAussenluftmengeWertLabel"); // NOI18N

        pbAngebotErstellen.setText("Angebot erstellen");
        pbAngebotErstellen.setMargin(new java.awt.Insets(2, 4, 2, 4));
        pbAngebotErstellen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbAngebotErstellenActionPerformed(evt);
            }
        });

        lmeSollLuftmengeAutoButton.setMnemonic('a');
        lmeSollLuftmengeAutoButton.setText("automatische Berechnung");
        lmeSollLuftmengeAutoButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        lmeSollLuftmengeAutoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lmeSollLuftmengeAutoButtonActionPerformed(evt);
            }
        });

        pbSpeichern.setText("Speichern");
        pbSpeichern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbSpeichernActionPerformed(evt);
            }
        });

        lmeGesAussenluftmengeLabel1.setText("mit Infiltration");

        pbRaumBearbeiten.setText("Raum bearbeiten");
        pbRaumBearbeiten.setMargin(new java.awt.Insets(2, 4, 2, 4));
        pbRaumBearbeiten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbRaumBearbeitenActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lmeInfo1PanelLayout = new org.jdesktop.layout.GroupLayout(lmeInfo1Panel);
        lmeInfo1Panel.setLayout(lmeInfo1PanelLayout);
        lmeInfo1PanelLayout.setHorizontalGroup(
            lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lmeInfo1PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lmeInfo1PanelLayout.createSequentialGroup()
                        .add(lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lmeGesamtvolumenLabel)
                            .add(lmeGebaeudeluftwechselLabel)
                            .add(lmeGesAussenluftmengeLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(lmeGesAussenluftmengeWertLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lmeGesamtvolumenWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, lmeGebaeudeluftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lmeGesamtvolumenM3Label)
                            .add(lmeGebaeudeluftwechselHLabel)
                            .add(lmeGesAussenluftmengeM3Label)))
                    .add(lmeGesAussenluftmengeLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lmeInfo2Panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pbAngebotErstellen, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pbSpeichern, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lmeSollLuftmengeAutoButton)
                    .add(pbRaumBearbeiten, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
                .addContainerGap())
        );

        lmeInfo1PanelLayout.linkSize(new java.awt.Component[] {lmeGebaeudeluftwechselHLabel, lmeGesAussenluftmengeM3Label, lmeGesamtvolumenM3Label}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        lmeInfo1PanelLayout.linkSize(new java.awt.Component[] {lmeGebaeudeluftwechselLabel, lmeGesAussenluftmengeLabel, lmeGesAussenluftmengeLabel1, lmeGesamtvolumenLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        lmeInfo1PanelLayout.linkSize(new java.awt.Component[] {lmeGebaeudeluftwechselWertLabel, lmeGesAussenluftmengeWertLabel, lmeGesamtvolumenWertLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        lmeInfo1PanelLayout.setVerticalGroup(
            lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lmeInfo1PanelLayout.createSequentialGroup()
                .add(lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lmeInfo1PanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lmeInfo1PanelLayout.createSequentialGroup()
                                .add(lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(lmeGesamtvolumenM3Label)
                                    .add(lmeGesamtvolumenWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(lmeGebaeudeluftwechselHLabel)
                                    .add(lmeGebaeudeluftwechselWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lmeGesAussenluftmengeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(lmeGesAussenluftmengeM3Label)))
                            .add(lmeInfo1PanelLayout.createSequentialGroup()
                                .add(lmeGesamtvolumenLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lmeGebaeudeluftwechselLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lmeGesAussenluftmengeLabel)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lmeGesAussenluftmengeLabel1))
                    .add(lmeInfo1PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(lmeInfo1PanelLayout.createSequentialGroup()
                            .add(7, 7, 7)
                            .add(pbRaumBearbeiten)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(lmeSollLuftmengeAutoButton)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(pbSpeichern)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(pbAngebotErstellen))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, lmeInfo2Panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        lmeAbSummeWertLabel.setName("lmeAbSummeWertLabel"); // NOI18N

        lmeZuSummeWertLabel.setName("lmeZuSummeWertLabel"); // NOI18N

        lmeSummeZuluftmengeWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lmeSummeZuluftmengeWertLabel.setName("lmeSummeZuluftmengeWertLabel"); // NOI18N

        lmeSummeAbluftmengeWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lmeSummeAbluftmengeWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        lmeSummeAbluftmengeWertLabel.setName("lmeSummeAbluftmengeWertLabel"); // NOI18N

        lmeUebSummeWertLabel.setName("lmeUebSummeWertLabel"); // NOI18N

        lmeSumLTMAbluftmengeWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lmeSumLTMAbluftmengeWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        lmeSumLTMAbluftmengeWertLabel.setName("lmeSumLTMAbluftmengeWertLabel"); // NOI18N

        lmeSumLTMZuluftmengeWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lmeSumLTMZuluftmengeWertLabel.setMinimumSize(new java.awt.Dimension(30, 0));
        lmeSumLTMZuluftmengeWertLabel.setName("lmeSumLTMZuluftmengeWertLabel"); // NOI18N

        bZentralgeraetWertLabel.setName("bZentralgeraetWertLabel"); // NOI18N

        org.jdesktop.layout.GroupLayout lmeSummeWertPanelLayout = new org.jdesktop.layout.GroupLayout(lmeSummeWertPanel);
        lmeSummeWertPanel.setLayout(lmeSummeWertPanelLayout);
        lmeSummeWertPanelLayout.setHorizontalGroup(
            lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lmeSummeWertPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lmeAbSummeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lmeZuSummeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lmeUebSummeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(lmeSummeWertPanelLayout.createSequentialGroup()
                    .add(5, 5, 5)
                    .add(lmeSummeAbluftmengeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .add(lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(lmeSummeWertPanelLayout.createSequentialGroup()
                    .add(5, 5, 5)
                    .add(lmeSummeZuluftmengeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .add(lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, lmeSummeWertPanelLayout.createSequentialGroup()
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lmeSumLTMAbluftmengeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
            .add(lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(lmeSummeWertPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(lmeSumLTMZuluftmengeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .add(lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, lmeSummeWertPanelLayout.createSequentialGroup()
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(bZentralgeraetWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
        lmeSummeWertPanelLayout.setVerticalGroup(
            lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lmeSummeWertPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lmeAbSummeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lmeZuSummeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lmeUebSummeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(551, Short.MAX_VALUE))
            .add(lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(lmeSummeWertPanelLayout.createSequentialGroup()
                    .add(280, 280, 280)
                    .add(lmeSummeAbluftmengeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(280, Short.MAX_VALUE)))
            .add(lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(lmeSummeWertPanelLayout.createSequentialGroup()
                    .add(280, 280, 280)
                    .add(lmeSummeZuluftmengeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(280, Short.MAX_VALUE)))
            .add(lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, lmeSummeWertPanelLayout.createSequentialGroup()
                    .addContainerGap(290, Short.MAX_VALUE)
                    .add(lmeSumLTMAbluftmengeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(270, 270, 270)))
            .add(lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, lmeSummeWertPanelLayout.createSequentialGroup()
                    .addContainerGap(300, Short.MAX_VALUE)
                    .add(lmeSumLTMZuluftmengeWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(260, 260, 260)))
            .add(lmeSummeWertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(lmeSummeWertPanelLayout.createSequentialGroup()
                    .add(33, 33, 33)
                    .add(bZentralgeraetWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(541, Short.MAX_VALUE)))
        );

        lmeTabelleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "<html><div align=\"center\">Raum", "<html><div align=\"center\">Luftart", "<html><div align=\"center\">Raumvolumen<br>(m³)</div></html>", "<html><div align=\"center\">Luftwechsel<br>(1/h)</div></html>", "<html><div align=\"center\">Anzahl<br>Abluftventile</div></html>", "<html><div align=\"center\">Abluftmenge<br>je Ventil</div></html>", "<html><div align=\"center\">Volumenstrom<br>(m³/h)</div></html>", "<html><div align=\"center\">Anzahl<br>Zuluftventile</div></html>", "<html><div align=\"center\">Bezeichnung<br>Zuluftventile</div></html>", "<html><div align=\"center\">Bezeichnung<br>Abluftventile</div></html>", "Verteilebene", "<html><div align=\"center\">Zuluftmenge<br>je Ventil</div></html>"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        lmeTabelleTable.setName("lmeTabelleTable"); // NOI18N
        lmeTabelleZuluftScrollPane.setViewportView(lmeTabelleTable);

        jTabbedPane2.addTab("Zu-/Abluftventile", lmeTabelleZuluftScrollPane);

        lmeTabelleUeberstroemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "<html><div align=\"center\">Raum</div></html>", "<html><div align=\"center\">Luftart</div></html>", "<html><div align=\"center\">Volumen<br>(m³)</div></html>", "<html><div align=\"center\">Anzahl<br>Ventile</div></html>", "<html><div align=\"center\">Volumenstrom<br>(m³/h)</div></html>", "<html><div align=\"center\">Überström-<br>Elemente</div></html>"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        lmeTabelleUeberstroemTable.setName("lmeTabelleUeberstroemTable"); // NOI18N
        lmeTabelleUeberstroemScrollPane.setViewportView(lmeTabelleUeberstroemTable);

        jTabbedPane2.addTab("Überströmventile", lmeTabelleUeberstroemScrollPane);

        org.jdesktop.layout.GroupLayout luftmengenermittlungPanelLayout = new org.jdesktop.layout.GroupLayout(luftmengenermittlungPanel);
        luftmengenermittlungPanel.setLayout(luftmengenermittlungPanelLayout);
        luftmengenermittlungPanelLayout.setHorizontalGroup(
            luftmengenermittlungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(luftmengenermittlungPanelLayout.createSequentialGroup()
                .add(lmeSummeWertPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(luftmengenermittlungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lmeInfo1Panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE))
                .addContainerGap())
        );
        luftmengenermittlungPanelLayout.setVerticalGroup(
            luftmengenermittlungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(luftmengenermittlungPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 405, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lmeInfo1Panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
            .add(lmeSummeWertPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        projektTabbedPane.addTab("Raumvolumenströme", luftmengenermittlungPanel);

        dvbTeilstreckenTabelleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "<html><div align=\"center\">Luftart</div></html>", "<html><div align=\"center\">Teilstrecke</div></html>", "<html><div align=\"center\">Luftvolumen-<br/>strom<br>[m³/h]</div></html>", "<html><div align=\"center\">Kanalbezeichnung</div></html>", "<html><div align=\"center\">Kanallänge<br><br>[m]</div></html>", "<html><div align=\"center\">Geschwindigkeit<br><br>[m/s]</div></html>", "<html><div align=\"center\">Reibungswiderstand<br/>gerader Kanal<br>[Pa]</div></html>", "<html><div align=\"center\">Gesamtwider-<br>standszahl</div></html>", "<html><div align=\"center\">Einzelwider-<br>stand<br>[Pa]</div></html>", "<html><div align=\"center\">Widerstand<br>Teilstrecke<br>[Pa]</div></html>", "Intern"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dvbTeilstreckenTabelleTable.setName("dvbTeilstreckenTabelleTable"); // NOI18N
        dvbTeilstreckenTabelleScrollPane.setViewportView(dvbTeilstreckenTabelleTable);

        jLabel1.setText("Luftart");

        dvbTeilstreckenBelueftungComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ZU", "AB" }));
        dvbTeilstreckenBelueftungComboBox.setName("dvbTeilstreckenBelueftungComboBox"); // NOI18N

        jLabel5.setText("Nr. Teilstrecke");

        dvbTeilstreckenNrTextField.setName("dvbTeilstreckenNrTextField"); // NOI18N
        dvbTeilstreckenNrTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dvbTeilstreckenNrTextFieldActionPerformed(evt);
            }
        });

        jLabel4.setText("Luftmenge (m³/h)");

        dvbTeilstreckenLuftmengeTextField.setName("dvbTeilstreckenLuftmengeTextField"); // NOI18N
        dvbTeilstreckenLuftmengeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dvbTeilstreckenLuftmengeTextFieldActionPerformed(evt);
            }
        });

        jLabel3.setText("Kanalbezeichnung");

        dvbTeilstreckenKanalbezeichnungComboBox.mySearch = "select ~Artikelnummer~ from ~artikelstamm~ where ~Klasse~ between 4 and 8";
        dvbTeilstreckenKanalbezeichnungComboBox .fromDatabase();
        dvbTeilstreckenKanalbezeichnungComboBox.setName("dvbTeilstreckenKanalbezeichnungComboBox"); // NOI18N

        jLabel2.setText("Länge (m)");

        dvbTeilstreckenLaengeTextField.setName("dvbTeilstreckenLaengeTextField"); // NOI18N
        dvbTeilstreckenLaengeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dvbTeilstreckenLaengeTextFieldActionPerformed(evt);
            }
        });

        dvbTeilstreckenHinzufuegenButton.setText("Hinzufügen");
        dvbTeilstreckenHinzufuegenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dvbTeilstreckenHinzufuegenButtonActionPerformed(evt);
            }
        });

        dvbTeilstreckenWiderstandsWerteBearbeitenButton.setText("Widerstandsbeiwerte...");
        dvbTeilstreckenWiderstandsWerteBearbeitenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                widerstandsbeiwerteBearbeitenAction(evt);
            }
        });

        org.jdesktop.layout.GroupLayout dvbTeilstreckenHinzufuegenPanelLayout = new org.jdesktop.layout.GroupLayout(dvbTeilstreckenHinzufuegenPanel);
        dvbTeilstreckenHinzufuegenPanel.setLayout(dvbTeilstreckenHinzufuegenPanelLayout);
        dvbTeilstreckenHinzufuegenPanelLayout.setHorizontalGroup(
            dvbTeilstreckenHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, dvbTeilstreckenHinzufuegenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(dvbTeilstreckenHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(dvbTeilstreckenBelueftungComboBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dvbTeilstreckenHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dvbTeilstreckenNrTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .add(6, 6, 6)
                .add(dvbTeilstreckenHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dvbTeilstreckenLuftmengeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dvbTeilstreckenHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dvbTeilstreckenKanalbezeichnungComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dvbTeilstreckenHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(dvbTeilstreckenHinzufuegenPanelLayout.createSequentialGroup()
                        .add(dvbTeilstreckenLaengeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dvbTeilstreckenHinzufuegenButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dvbTeilstreckenWiderstandsWerteBearbeitenButton)))
                .addContainerGap(202, Short.MAX_VALUE))
        );
        dvbTeilstreckenHinzufuegenPanelLayout.setVerticalGroup(
            dvbTeilstreckenHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, dvbTeilstreckenHinzufuegenPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(dvbTeilstreckenHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jLabel4)
                    .add(jLabel1)
                    .add(jLabel5)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dvbTeilstreckenHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dvbTeilstreckenNrTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dvbTeilstreckenLuftmengeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dvbTeilstreckenBelueftungComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dvbTeilstreckenKanalbezeichnungComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dvbTeilstreckenLaengeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dvbTeilstreckenHinzufuegenButton)
                    .add(dvbTeilstreckenWiderstandsWerteBearbeitenButton))
                .add(43, 43, 43))
        );

        dvbTeilstreckenHinzufuegenPanelLayout.linkSize(new java.awt.Component[] {dvbTeilstreckenBelueftungComboBox, dvbTeilstreckenKanalbezeichnungComboBox, dvbTeilstreckenLaengeTextField, dvbTeilstreckenLuftmengeTextField, dvbTeilstreckenNrTextField}, org.jdesktop.layout.GroupLayout.VERTICAL);

        dvbTeilstreckenEntfernenButton.setText("Entfernen");
        dvbTeilstreckenEntfernenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dvbTeilstreckenEntfernenButtonActionPerformed(evt);
            }
        });

        dvbTeilstreckenTabelleSpinner.setBorder(null);
        dvbTeilstreckenTabelleSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                dvbTeilstreckenTabelleSpinnerStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout dvbTeilstreckenPanelLayout = new org.jdesktop.layout.GroupLayout(dvbTeilstreckenPanel);
        dvbTeilstreckenPanel.setLayout(dvbTeilstreckenPanelLayout);
        dvbTeilstreckenPanelLayout.setHorizontalGroup(
            dvbTeilstreckenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dvbTeilstreckenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(dvbTeilstreckenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dvbTeilstreckenPanelLayout.createSequentialGroup()
                        .add(dvbTeilstreckenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(dvbTeilstreckenHinzufuegenPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, dvbTeilstreckenTabelleScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 909, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dvbTeilstreckenTabelleSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(dvbTeilstreckenEntfernenButton))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        dvbTeilstreckenPanelLayout.setVerticalGroup(
            dvbTeilstreckenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dvbTeilstreckenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(dvbTeilstreckenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dvbTeilstreckenPanelLayout.createSequentialGroup()
                        .add(dvbTeilstreckenHinzufuegenPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dvbTeilstreckenTabelleScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dvbTeilstreckenEntfernenButton)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, dvbTeilstreckenPanelLayout.createSequentialGroup()
                        .add(dvbTeilstreckenTabelleSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(208, 208, 208))))
        );

        dvbTabbedPane.addTab("Kanalnetz", dvbTeilstreckenPanel);

        dvbVentileinstellungTabelleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Bel.", "Raum", "Teilstrecken", "Ventiltyp", "dP Pa (offen)", "Gesamt Pa", "Differenz", "Abgleich Pa", "Einstellung"
            }
        ));
        dvbVentileinstellungTabelleTable.setName("dvbVentileinstellungTabelleTable"); // NOI18N
        dvbVentileinstellungTabelleScrollPane.setViewportView(dvbVentileinstellungTabelleTable);

        jLabel35.setText("Luftart");

        dvbVentileinstellungBelueftungComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ZU", "AB", "AU", "FO" }));

        jLabel34.setText("Raum");

        dvbVentileinstellungRaumComboBox.setEditable(true);
        dvbVentileinstellungRaumComboBox.setModel(wfBezeichnungCombobox.makeModelFromComboBox());
        dvbVentileinstellungRaumComboBox.setName("dvbVentileinstellungRaumComboBox"); // NOI18N

        jLabel7.setText("Teilstrecken");

        dvbVentileinstellungTeilstreckenTextField.setName("dvbVentileinstellungTeilstreckenTextField"); // NOI18N
        dvbVentileinstellungTeilstreckenTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dvbVentileinstellungTeilstreckenTextFieldActionPerformed(evt);
            }
        });

        dvbVentileinstellungTeilstreckenAuswaehlenButton.setText("auswählen");
        dvbVentileinstellungTeilstreckenAuswaehlenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dvbVentileinstellungTeilstreckenAuswaehlenButtonActionPerformed(evt);
            }
        });

        jLabel6.setText("Ventilbezeichnung");

        dvbVentileinstellungVentilbezeichnungComboBox.mySearch = "select distinct(~Artikelnummer~) from ~druckverlust~ where ~ausblaswinkel~ <> 180;";
        dvbVentileinstellungVentilbezeichnungComboBox.fromDatabase();
        dvbVentileinstellungVentilbezeichnungComboBox.addItem("");
        dvbVentileinstellungVentilbezeichnungComboBox.setName("dvbVentileinstellungVentilbezeichnungComboBox"); // NOI18N

        dvbVentileinstellungHinzufuegenButton.setText("Hinzufügen");
        dvbVentileinstellungHinzufuegenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dvbVentileinstellungHinzufuegenButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout dvbVentileinstellungHinzufuegenPanelLayout = new org.jdesktop.layout.GroupLayout(dvbVentileinstellungHinzufuegenPanel);
        dvbVentileinstellungHinzufuegenPanel.setLayout(dvbVentileinstellungHinzufuegenPanelLayout);
        dvbVentileinstellungHinzufuegenPanelLayout.setHorizontalGroup(
            dvbVentileinstellungHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dvbVentileinstellungHinzufuegenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(dvbVentileinstellungHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dvbVentileinstellungBelueftungComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel35))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dvbVentileinstellungHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dvbVentileinstellungRaumComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel34))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dvbVentileinstellungHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dvbVentileinstellungTeilstreckenTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dvbVentileinstellungTeilstreckenAuswaehlenButton)
                .add(14, 14, 14)
                .add(dvbVentileinstellungHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6)
                    .add(dvbVentileinstellungHinzufuegenPanelLayout.createSequentialGroup()
                        .add(dvbVentileinstellungVentilbezeichnungComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dvbVentileinstellungHinzufuegenButton)))
                .addContainerGap(200, Short.MAX_VALUE))
        );
        dvbVentileinstellungHinzufuegenPanelLayout.setVerticalGroup(
            dvbVentileinstellungHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dvbVentileinstellungHinzufuegenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(dvbVentileinstellungHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(jLabel34)
                    .add(jLabel6)
                    .add(jLabel35))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dvbVentileinstellungHinzufuegenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dvbVentileinstellungTeilstreckenTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dvbVentileinstellungRaumComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dvbVentileinstellungBelueftungComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dvbVentileinstellungVentilbezeichnungComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dvbVentileinstellungTeilstreckenAuswaehlenButton)
                    .add(dvbVentileinstellungHinzufuegenButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dvbVentileinstellungHinzufuegenPanelLayout.linkSize(new java.awt.Component[] {dvbVentileinstellungBelueftungComboBox, dvbVentileinstellungRaumComboBox, dvbVentileinstellungTeilstreckenTextField, dvbVentileinstellungVentilbezeichnungComboBox}, org.jdesktop.layout.GroupLayout.VERTICAL);

        dvbVentileinstellungEntfernenButton.setText("Entfernen");
        dvbVentileinstellungEntfernenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dvbVentileinstellungEntfernenButtonActionPerformed(evt);
            }
        });

        dvbTabelleVentilSpinner.setBorder(null);
        dvbTabelleVentilSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                dvbTabelleVentilSpinnerStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout dvbVentileinstellungPanelLayout = new org.jdesktop.layout.GroupLayout(dvbVentileinstellungPanel);
        dvbVentileinstellungPanel.setLayout(dvbVentileinstellungPanelLayout);
        dvbVentileinstellungPanelLayout.setHorizontalGroup(
            dvbVentileinstellungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dvbVentileinstellungPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(dvbVentileinstellungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dvbVentileinstellungHinzufuegenPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dvbVentileinstellungPanelLayout.createSequentialGroup()
                        .add(dvbVentileinstellungTabelleScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 911, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dvbTabelleVentilSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(dvbVentileinstellungEntfernenButton))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        dvbVentileinstellungPanelLayout.setVerticalGroup(
            dvbVentileinstellungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dvbVentileinstellungPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(dvbVentileinstellungHinzufuegenPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dvbVentileinstellungTabelleScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 358, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dvbVentileinstellungEntfernenButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, dvbVentileinstellungPanelLayout.createSequentialGroup()
                .addContainerGap(234, Short.MAX_VALUE)
                .add(dvbTabelleVentilSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(204, 204, 204))
        );

        dvbTabbedPane.addTab("Ventileinstellung", dvbVentileinstellungPanel);

        org.jdesktop.layout.GroupLayout druckverlustberechnungPanelLayout = new org.jdesktop.layout.GroupLayout(druckverlustberechnungPanel);
        druckverlustberechnungPanel.setLayout(druckverlustberechnungPanelLayout);
        druckverlustberechnungPanelLayout.setHorizontalGroup(
            druckverlustberechnungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, druckverlustberechnungPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(dvbTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 987, Short.MAX_VALUE)
                .addContainerGap())
        );
        druckverlustberechnungPanelLayout.setVerticalGroup(
            druckverlustberechnungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(druckverlustberechnungPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(dvbTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                .add(96, 96, 96))
        );

        projektTabbedPane.addTab("Druckverlustberechnung", druckverlustberechnungPanel);

        abZuRaumbezeichnungLabel.setText("Raumbezeichnung");

        abZuRaumbezeichnungComboBox.setEditable(true);
        abZuRaumbezeichnungComboBox.setModel(wfBezeichnungCombobox.makeModelFromComboBox());
        abZuRaumbezeichnungComboBox.setAutoCompletion(true);
        abZuRaumbezeichnungComboBox.setName("abZuRaumbezeichnungComboBox"); // NOI18N

        abZuBeschriftungPanel.setLayout(new java.awt.GridBagLayout());

        jLabel36.setForeground(new java.awt.Color(255, 51, 51));
        jLabel36.setText("Schallleistungspegel Zuluftstutzen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel36, gridBagConstraints);

        abZuSchallleistungspegelZuluftstutzenComboBox.setModel(lmeVolumenstromCombobox.getModel());
        abZuSchallleistungspegelZuluftstutzenComboBox.setMinimumSize(new java.awt.Dimension(80, 18));
        abZuSchallleistungspegelZuluftstutzenComboBox.setName("abZuSchallleistungspegelZuluftstutzenComboBox"); // NOI18N
        abZuSchallleistungspegelZuluftstutzenComboBox.setPreferredSize(new java.awt.Dimension(80, 20));
        abZuSchallleistungspegelZuluftstutzenComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechnungZuluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        abZuBeschriftungPanel.add(abZuSchallleistungspegelZuluftstutzenComboBox, gridBagConstraints);

        jLabel39.setForeground(new java.awt.Color(255, 51, 51));
        jLabel39.setText("Schallleistungspegelerhöhung Kanalnetz");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel39, gridBagConstraints);

        abZuKanalnetzComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10", "20", "30", "40", "50", "60", "70", "80", "90", "100", "110", "120", "130", "140", "150", "160", "170", "180", "190", "200" }));
        abZuKanalnetzComboBox.setName("abZuKanalnetzComboBox"); // NOI18N
        abZuKanalnetzComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechnungZuluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abZuBeschriftungPanel.add(abZuKanalnetzComboBox, gridBagConstraints);

        jLabel40.setForeground(new java.awt.Color(255, 51, 51));
        jLabel40.setText("Schallleistungspegelerhöhung Filter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel40, gridBagConstraints);

        abZuFilterverschmutzungComboBox.setModel(abZuKanalnetzComboBox.makeModelFromComboBox());
        abZuFilterverschmutzungComboBox.setName("abZuFilterverschmutzungComboBox"); // NOI18N
        abZuFilterverschmutzungComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechnungZuluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abZuBeschriftungPanel.add(abZuFilterverschmutzungComboBox, gridBagConstraints);

        jLabel41.setForeground(new java.awt.Color(51, 204, 0));
        jLabel41.setText("1. Hauptschalldämpfer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel41, gridBagConstraints);

        abZuHauptschalldaempfer1ComboBox.setModel(abZuHauptschalldaempfer1ComboBox.makeModelFromMySearch(true));
        abZuHauptschalldaempfer1ComboBox.setName("abZuHauptschalldaempfer1ComboBox"); // NOI18N
        abZuHauptschalldaempfer1ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechnungZuluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abZuBeschriftungPanel.add(abZuHauptschalldaempfer1ComboBox, gridBagConstraints);

        jLabel42.setForeground(new java.awt.Color(51, 204, 0));
        jLabel42.setText("2. Hauptschalldämpfer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel42, gridBagConstraints);

        abZuHauptschalldaempfer2ComboBox.setModel(abZuHauptschalldaempfer1ComboBox.makeModelFromComboBox());
        abZuHauptschalldaempfer2ComboBox.setName("abZuHauptschalldaempfer2ComboBox"); // NOI18N
        abZuHauptschalldaempfer2ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechnungZuluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abZuBeschriftungPanel.add(abZuHauptschalldaempfer2ComboBox, gridBagConstraints);

        jLabel43.setForeground(new java.awt.Color(51, 204, 0));
        jLabel43.setText("Anzahl der Umlenkungen 90° Stck.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel43, gridBagConstraints);

        jLabel44.setForeground(new java.awt.Color(51, 204, 0));
        jLabel44.setText("Luftverteilerkasten Stck.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel44, gridBagConstraints);

        jLabel45.setForeground(new java.awt.Color(51, 204, 0));
        jLabel45.setText("Längsdämpfung Kanal lfdm.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel45, gridBagConstraints);

        jLabel46.setForeground(new java.awt.Color(51, 204, 0));
        jLabel46.setText("Schalldämpfer Ventil");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel46, gridBagConstraints);

        abZuSchalldaempferVentilComboBox.setModel(abZuHauptschalldaempfer1ComboBox.makeModelFromComboBox());
        abZuSchalldaempferVentilComboBox.setName("abZuSchalldaempferVentilComboBox"); // NOI18N
        abZuSchalldaempferVentilComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechnungZuluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abZuBeschriftungPanel.add(abZuSchalldaempferVentilComboBox, gridBagConstraints);

        jLabel47.setForeground(new java.awt.Color(51, 204, 0));
        jLabel47.setText("Einfügungsdämmwert Luftdurchlass");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel47, gridBagConstraints);

        abZuEinfuegungswertLuftdurchlassComboBox.setModel(dvbVentileinstellungVentilbezeichnungComboBox.makeModelFromComboBox());
        abZuEinfuegungswertLuftdurchlassComboBox.setName("abZuEinfuegungswertLuftdurchlassComboBox"); // NOI18N
        abZuEinfuegungswertLuftdurchlassComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechnungZuluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abZuBeschriftungPanel.add(abZuEinfuegungswertLuftdurchlassComboBox, gridBagConstraints);

        jLabel48.setForeground(new java.awt.Color(51, 204, 0));
        jLabel48.setText("Raumabsorption (Annahme) BAD=0 WOHNEN=1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel48, gridBagConstraints);

        jLabel49.setText("Korrektur der A-Bewertung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel49, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        abZuBeschriftungPanel.add(abZuPlaceholder1Label, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        abZuBeschriftungPanel.add(abZuPlaceholder2Label, gridBagConstraints);

        stZuGeraet.setText(lmeZentralgeraetCombobox.getString());
        stZuGeraet.setText(lmeZentralgeraetCombobox.getString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        abZuBeschriftungPanel.add(stZuGeraet, gridBagConstraints);

        jLabel52.setText("Bewerteter Schallpegel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abZuBeschriftungPanel.add(jLabel52, gridBagConstraints);

        jPanel2.setLayout(new java.awt.BorderLayout(5, 0));

        abZuPlaceholder1ComboBox.setMinimumSize(new java.awt.Dimension(130, 18));
        abZuPlaceholder1ComboBox.setPreferredSize(new java.awt.Dimension(130, 20));
        abZuPlaceholder1ComboBox.setVisible(false);
        jPanel2.add(abZuPlaceholder1ComboBox, java.awt.BorderLayout.WEST);

        abZuAnzahlUmlenkungenTextField.setColumns(3);
        abZuAnzahlUmlenkungenTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        abZuAnzahlUmlenkungenTextField.setText("5");
        abZuAnzahlUmlenkungenTextField.setName("abZuAnzahlUmlenkungenTextField"); // NOI18N
        abZuAnzahlUmlenkungenTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                abZuAnzahlUmlenkungenTextFieldKeyReleased(evt);
            }
        });
        jPanel2.add(abZuAnzahlUmlenkungenTextField, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abZuBeschriftungPanel.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.BorderLayout(5, 0));

        abZuPlaceholder2ComboBox.setMinimumSize(new java.awt.Dimension(130, 18));
        abZuPlaceholder2ComboBox.setPreferredSize(new java.awt.Dimension(130, 20));
        abZuPlaceholder2ComboBox.setVisible(false);
        jPanel3.add(abZuPlaceholder2ComboBox, java.awt.BorderLayout.WEST);

        abZuLuftverteilerkastenTextField.setColumns(3);
        abZuLuftverteilerkastenTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        abZuLuftverteilerkastenTextField.setText("1");
        abZuLuftverteilerkastenTextField.setName("abZuLuftverteilerkastenTextField"); // NOI18N
        abZuLuftverteilerkastenTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                abZuLuftverteilerkastenTextFieldKeyReleased(evt);
            }
        });
        jPanel3.add(abZuLuftverteilerkastenTextField, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abZuBeschriftungPanel.add(jPanel3, gridBagConstraints);

        jPanel4.setLayout(new java.awt.BorderLayout(5, 0));

        abZuLaengsdaempfungKanalComboBox.mySearch = "select ~Artikelnummer~ from ~artikelstamm~ where ~Klasse~ between 4 and 8";
        abZuLaengsdaempfungKanalComboBox.fromDatabase();
        abZuLaengsdaempfungKanalComboBox.setMinimumSize(new java.awt.Dimension(130, 18));
        abZuLaengsdaempfungKanalComboBox.setName("abZuLaengsdaempfungKanalComboBox"); // NOI18N
        abZuLaengsdaempfungKanalComboBox.setPreferredSize(new java.awt.Dimension(130, 20));
        abZuLaengsdaempfungKanalComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechnungZuluftDataAction(evt);
            }
        });
        jPanel4.add(abZuLaengsdaempfungKanalComboBox, java.awt.BorderLayout.WEST);

        abZuLaengsdaempfungKanalTextField.setColumns(3);
        abZuLaengsdaempfungKanalTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        abZuLaengsdaempfungKanalTextField.setText("12");
        abZuLaengsdaempfungKanalTextField.setName("abZuLaengsdaempfungKanalTextField"); // NOI18N
        abZuLaengsdaempfungKanalTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                abZuLaengsdaempfungKanalTextFieldKeyReleased(evt);
            }
        });
        jPanel4.add(abZuLaengsdaempfungKanalTextField, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abZuBeschriftungPanel.add(jPanel4, gridBagConstraints);

        jPanel5.setLayout(new java.awt.BorderLayout(5, 0));

        abZuPlaceholder3ComboBox.setMinimumSize(new java.awt.Dimension(130, 18));
        abZuPlaceholder3ComboBox.setPreferredSize(new java.awt.Dimension(130, 20));
        abZuPlaceholder3ComboBox.setVisible(false);
        jPanel5.add(abZuPlaceholder3ComboBox, java.awt.BorderLayout.WEST);

        abZuRaumabsorptionTextField.setColumns(3);
        abZuRaumabsorptionTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        abZuRaumabsorptionTextField.setText("1");
        abZuRaumabsorptionTextField.setName("abZuRaumabsorptionTextField"); // NOI18N
        abZuRaumabsorptionTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                abZuRaumabsorptionTextFieldKeyReleased(evt);
            }
        });
        jPanel5.add(abZuRaumabsorptionTextField, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abZuBeschriftungPanel.add(jPanel5, gridBagConstraints);

        abZuTabelleUeberschrift1Label.setForeground(new java.awt.Color(255, 0, 0));
        abZuTabelleUeberschrift1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        abZuTabelleUeberschrift1Label.setText("Zentrales Lüftungsgerät WAC 250 CF");
        abZuTabelleUeberschrift1Label.setFont(new java.awt.Font("Tahoma", 1, 12));
        abZuTabelleUeberschrift1Label.setText(Strings.MAIN_DEVICE + lmeZentralgeraetCombobox.getString());

        abZuTabelleUeberschrift2Label.setForeground(new java.awt.Color(255, 0, 0));
        abZuTabelleUeberschrift2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        abZuTabelleUeberschrift2Label.setText("Zuluft");
        abZuTabelleUeberschrift2Label.setFont(new java.awt.Font("Tahoma", 1, 12));

        abZuTabelleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {"-16,1", "-8,6", "-3,2", "0,0", "1,2", "1,00"},
                {null, null, null, null, null, null}
            },
            new String [] {
                "125", "250", "500", "1000", "2000", "4000"
            }
        ));
        abZuTabelleTable.setName("abZuTabelleTable"); // NOI18N
        abZuTabelleTable.setRowHeight(28);
        abZuTabelleScrollPane.setViewportView(abZuTabelleTable);

        abZuTabelleDezibelLabel.setText("dB(A)");

        abZuTabelleDezibelWertLabel.setName("abZuTabelleDezibelWertLabel"); // NOI18N
        abZuTabelleDezibelWertLabel.setPreferredSize(new java.awt.Dimension(34, 14));

        abZuTabelleMittlererSchalldruckpegelLabel.setText("Mittlerer Schalldruckpegel* dB(A) =");

        abZuTabelleMittlererSchalldruckpegelWertLabel.setText("28,2");
        abZuTabelleMittlererSchalldruckpegelWertLabel.setName("abZuTabelleMittlererSchalldruckpegelWertLabel"); // NOI18N

        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel51.setText("Oktavmittenfrequenz in Hz");

        org.jdesktop.layout.GroupLayout abZuTabellePanelLayout = new org.jdesktop.layout.GroupLayout(abZuTabellePanel);
        abZuTabellePanel.setLayout(abZuTabellePanelLayout);
        abZuTabellePanelLayout.setHorizontalGroup(
            abZuTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(abZuTabellePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(abZuTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(abZuTabellePanelLayout.createSequentialGroup()
                        .add(abZuTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(abZuTabelleMittlererSchalldruckpegelLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(abZuTabelleScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(abZuTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(abZuTabelleMittlererSchalldruckpegelWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(abZuTabelleDezibelLabel)
                            .add(abZuTabelleDezibelWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(abZuTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, abZuTabelleUeberschrift2Label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, abZuTabelleUeberschrift1Label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel51, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)))
                .add(251, 251, 251))
        );
        abZuTabellePanelLayout.setVerticalGroup(
            abZuTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(abZuTabellePanelLayout.createSequentialGroup()
                .add(abZuTabelleUeberschrift1Label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(abZuTabelleUeberschrift2Label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 15, Short.MAX_VALUE)
                .add(jLabel51)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(abZuTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(abZuTabelleScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 388, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(abZuTabellePanelLayout.createSequentialGroup()
                        .add(abZuTabelleDezibelLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(abZuTabelleDezibelWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(abZuTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(abZuTabelleMittlererSchalldruckpegelLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(abZuTabelleMittlererSchalldruckpegelWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        abZuHinweisLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        abZuHinweisLabel.setText("* Bei dieser Berechnung handelt es sich um eine theoretische Auslegung, deren Werte in der Praxis abweichen können");

        org.jdesktop.layout.GroupLayout abZuluftPanelLayout = new org.jdesktop.layout.GroupLayout(abZuluftPanel);
        abZuluftPanel.setLayout(abZuluftPanelLayout);
        abZuluftPanelLayout.setHorizontalGroup(
            abZuluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(abZuluftPanelLayout.createSequentialGroup()
                .add(abZuluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(abZuluftPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(abZuluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(abZuluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, abZuRaumbezeichnungComboBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, abZuRaumbezeichnungLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(abZuBeschriftungPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(abZuTabellePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE))
                    .add(abZuHinweisLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 972, Short.MAX_VALUE))
                .addContainerGap())
        );
        abZuluftPanelLayout.setVerticalGroup(
            abZuluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(abZuluftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(abZuluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(abZuluftPanelLayout.createSequentialGroup()
                        .add(abZuRaumbezeichnungLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(abZuRaumbezeichnungComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(46, 46, 46)
                        .add(abZuBeschriftungPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(abZuTabellePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(abZuHinweisLabel)
                .add(44, 44, 44))
        );

        abTabbedPane.addTab("Zuluft", abZuluftPanel);

        abAbRaumbezeichnungLabel1.setText("Raumbezeichnung");

        abAbRaumbezeichnungComboBox.setEditable(true);
        abAbRaumbezeichnungComboBox.setModel(wfBezeichnungCombobox.makeModelFromComboBox());
        abAbRaumbezeichnungComboBox.setAutoCompletion(true);
        abAbRaumbezeichnungComboBox.setName("abAbRaumbezeichnungComboBox"); // NOI18N

        abAbBeschriftungPanel.setLayout(new java.awt.GridBagLayout());

        jLabel21.setForeground(new java.awt.Color(255, 51, 51));
        jLabel21.setText("Schallleistungspegel Abluftstutzen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel21, gridBagConstraints);

        abAbSchallleistungspegelAbluftstutzenComboBox.setModel(lmeVolumenstromCombobox.getModel());
        abAbSchallleistungspegelAbluftstutzenComboBox.setMinimumSize(new java.awt.Dimension(80, 18));
        abAbSchallleistungspegelAbluftstutzenComboBox.setName("abAbSchallleistungspegelAbluftstutzenComboBox"); // NOI18N
        abAbSchallleistungspegelAbluftstutzenComboBox.setPreferredSize(new java.awt.Dimension(80, 20));
        abAbSchallleistungspegelAbluftstutzenComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                akkustikBerechnungAbluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        abAbBeschriftungPanel.add(abAbSchallleistungspegelAbluftstutzenComboBox, gridBagConstraints);

        jLabel22.setForeground(new java.awt.Color(255, 51, 51));
        jLabel22.setText("Schallleistungspegelerhöhung Kanalnetz");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel22, gridBagConstraints);

        abAbKanalnetzComboBox.setModel(abZuKanalnetzComboBox.makeModelFromComboBox());
        abAbKanalnetzComboBox.setName("abAbKanalnetzComboBox"); // NOI18N
        abAbKanalnetzComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                akkustikBerechnungAbluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abAbBeschriftungPanel.add(abAbKanalnetzComboBox, gridBagConstraints);

        jLabel23.setForeground(new java.awt.Color(255, 51, 51));
        jLabel23.setText("Schallleistungspegelerhöhung Filter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel23, gridBagConstraints);

        abAbFilterverschmutzungComboBox.setModel(abZuKanalnetzComboBox.makeModelFromComboBox());
        abAbFilterverschmutzungComboBox.setName("abAbFilterverschmutzungComboBox"); // NOI18N
        abAbFilterverschmutzungComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                akkustikBerechnungAbluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abAbBeschriftungPanel.add(abAbFilterverschmutzungComboBox, gridBagConstraints);

        jLabel24.setForeground(new java.awt.Color(51, 204, 0));
        jLabel24.setText("1. Hauptschalldämpfer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel24, gridBagConstraints);

        abAbHauptschalldaempfer1ComboBox.setModel(abZuHauptschalldaempfer1ComboBox.makeModelFromComboBox());
        abAbHauptschalldaempfer1ComboBox.setName("abAbHauptschalldaempfer1ComboBox"); // NOI18N
        abAbHauptschalldaempfer1ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                akkustikBerechnungAbluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abAbBeschriftungPanel.add(abAbHauptschalldaempfer1ComboBox, gridBagConstraints);

        jLabel25.setForeground(new java.awt.Color(51, 204, 0));
        jLabel25.setText("2. Hauptschalldämpfer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel25, gridBagConstraints);

        abAbHauptschalldaempfer2ComboBox.setModel(abZuHauptschalldaempfer1ComboBox.makeModelFromComboBox());
        abAbHauptschalldaempfer2ComboBox.setName("abAbHauptschalldaempfer2ComboBox"); // NOI18N
        abAbHauptschalldaempfer2ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                akkustikBerechnungAbluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abAbBeschriftungPanel.add(abAbHauptschalldaempfer2ComboBox, gridBagConstraints);

        jLabel26.setForeground(new java.awt.Color(51, 204, 0));
        jLabel26.setText("Anzahl der Umlenkungen 90° Stck.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel26, gridBagConstraints);

        jLabel27.setForeground(new java.awt.Color(51, 204, 0));
        jLabel27.setText("Luftverteilerkasten Stck.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel27, gridBagConstraints);

        jLabel28.setForeground(new java.awt.Color(51, 204, 0));
        jLabel28.setText("Längsdämpfung Kanal lfdm.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel28, gridBagConstraints);

        jLabel29.setForeground(new java.awt.Color(51, 204, 0));
        jLabel29.setText("Schalldämpfer Ventil");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel29, gridBagConstraints);

        abAbSchalldaempferVentilComboBox.setModel(abZuHauptschalldaempfer1ComboBox.makeModelFromComboBox());
        abAbSchalldaempferVentilComboBox.setName("abAbSchalldaempferVentilComboBox"); // NOI18N
        abAbSchalldaempferVentilComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                akkustikBerechnungAbluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abAbBeschriftungPanel.add(abAbSchalldaempferVentilComboBox, gridBagConstraints);

        jLabel30.setForeground(new java.awt.Color(51, 204, 0));
        jLabel30.setText("Einfügungsdämmwert Luftdurchlass");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel30, gridBagConstraints);

        abAbEinfuegungswertLuftdurchlassComboBox.setModel(abZuEinfuegungswertLuftdurchlassComboBox.makeModelFromComboBox());
        abAbEinfuegungswertLuftdurchlassComboBox.setName("abAbEinfuegungswertLuftdurchlassComboBox"); // NOI18N
        abAbEinfuegungswertLuftdurchlassComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                akkustikBerechnungAbluftDataAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abAbBeschriftungPanel.add(abAbEinfuegungswertLuftdurchlassComboBox, gridBagConstraints);

        jLabel31.setForeground(new java.awt.Color(51, 204, 0));
        jLabel31.setText("Raumabsorption (Annahme) BAD=0 WOHNEN=1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel31, gridBagConstraints);

        jLabel32.setText("Korrektur der A-Bewertung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel32, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        abAbBeschriftungPanel.add(abAbPlaceholder1Label, gridBagConstraints);

        jLabel33.setText("Bewerteter Schallpegel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        abAbBeschriftungPanel.add(jLabel33, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        abAbBeschriftungPanel.add(abAbPlaceholder2Label, gridBagConstraints);

        stAbGeraet.setText(stZuGeraet.getText());
        stAbGeraet.setText(lmeZentralgeraetCombobox.getString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        abAbBeschriftungPanel.add(stAbGeraet, gridBagConstraints);

        jPanel6.setLayout(new java.awt.BorderLayout(5, 0));

        abAbPlaceholder1ComboBox.setPreferredSize(new java.awt.Dimension(130, 20));
        abAbPlaceholder1ComboBox.setVisible(false);
        jPanel6.add(abAbPlaceholder1ComboBox, java.awt.BorderLayout.WEST);

        abAbAnzahlUmlenkungenTextField.setColumns(3);
        abAbAnzahlUmlenkungenTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        abAbAnzahlUmlenkungenTextField.setText("4");
        abAbAnzahlUmlenkungenTextField.setName("abAbAnzahlUmlenkungenTextField"); // NOI18N
        abAbAnzahlUmlenkungenTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                abAbAnzahlUmlenkungenTextFieldKeyReleased(evt);
            }
        });
        jPanel6.add(abAbAnzahlUmlenkungenTextField, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abAbBeschriftungPanel.add(jPanel6, gridBagConstraints);

        jPanel8.setLayout(new java.awt.BorderLayout(5, 0));

        abAbLaengsdaempfungKanalTextField.setColumns(3);
        abAbLaengsdaempfungKanalTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        abAbLaengsdaempfungKanalTextField.setText("7");
        abAbLaengsdaempfungKanalTextField.setName("abAbLaengsdaempfungKanalTextField"); // NOI18N
        abAbLaengsdaempfungKanalTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                abAbLaengsdaempfungKanalTextFieldKeyReleased(evt);
            }
        });
        jPanel8.add(abAbLaengsdaempfungKanalTextField, java.awt.BorderLayout.EAST);

        abAbLaengsdaempfungKanalComboBox.setModel(abZuLaengsdaempfungKanalComboBox.makeModelFromComboBox());
        abAbLaengsdaempfungKanalComboBox.setName("abAbLaengsdaempfungKanalComboBox"); // NOI18N
        abAbLaengsdaempfungKanalComboBox.setPreferredSize(new java.awt.Dimension(130, 20));
        abAbLaengsdaempfungKanalComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                akkustikBerechnungAbluftDataAction(evt);
            }
        });
        jPanel8.add(abAbLaengsdaempfungKanalComboBox, java.awt.BorderLayout.WEST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abAbBeschriftungPanel.add(jPanel8, gridBagConstraints);

        jPanel7.setLayout(new java.awt.BorderLayout(5, 0));

        abAbLuftverteilerkastenTextField.setColumns(3);
        abAbLuftverteilerkastenTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        abAbLuftverteilerkastenTextField.setText("1");
        abAbLuftverteilerkastenTextField.setName("abAbLuftverteilerkastenTextField"); // NOI18N
        abAbLuftverteilerkastenTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                abAbLuftverteilerkastenTextFieldKeyReleased(evt);
            }
        });
        jPanel7.add(abAbLuftverteilerkastenTextField, java.awt.BorderLayout.EAST);

        abAbPlaceholder2ComboBox.setPreferredSize(new java.awt.Dimension(130, 20));
        abAbPlaceholder2ComboBox.setVisible(false);
        jPanel7.add(abAbPlaceholder2ComboBox, java.awt.BorderLayout.WEST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abAbBeschriftungPanel.add(jPanel7, gridBagConstraints);

        jPanel9.setLayout(new java.awt.BorderLayout(5, 0));

        abAbPlaceholder3ComboBox.setPreferredSize(new java.awt.Dimension(130, 20));
        abAbPlaceholder3ComboBox.setVisible(false);
        jPanel9.add(abAbPlaceholder3ComboBox, java.awt.BorderLayout.WEST);

        abAbRaumabsorptionTextField.setColumns(3);
        abAbRaumabsorptionTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        abAbRaumabsorptionTextField.setText("0");
        abAbRaumabsorptionTextField.setName("abAbRaumabsorptionTextField"); // NOI18N
        abAbRaumabsorptionTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                abAbRaumabsorptionTextFieldKeyReleased(evt);
            }
        });
        jPanel9.add(abAbRaumabsorptionTextField, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        abAbBeschriftungPanel.add(jPanel9, gridBagConstraints);

        abAbTabelleUeberschrift1Label.setForeground(new java.awt.Color(0, 0, 255));
        abAbTabelleUeberschrift1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        abAbTabelleUeberschrift1Label.setText("Zentrales Lüftungsgerät WAC 250 CF");
        abAbTabelleUeberschrift1Label.setFont(new java.awt.Font("Tahoma", 1, 12));
        abAbTabelleUeberschrift1Label.setText(Strings.MAIN_DEVICE + lmeZentralgeraetCombobox.getString());

        abAbTabelleUeberschrift2Label.setForeground(new java.awt.Color(0, 0, 255));
        abAbTabelleUeberschrift2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        abAbTabelleUeberschrift2Label.setText("Abluft");
        abAbTabelleUeberschrift2Label.setFont(new java.awt.Font("Tahoma", 1, 12));

        abAbTabelleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {"-16,1", "-8,6", "-3,2", "0,0", "1,2", null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "125", "250", "500", "1000", "2000", "4000"
            }
        ));
        abAbTabelleTable.setName("abAbTabelleTable"); // NOI18N
        abAbTabelleTable.setRowHeight(28);
        abAbTabelleScrollPane.setViewportView(abAbTabelleTable);

        abAbTabelleDezibelLabel.setText("dB(A)");

        abAbTabelleDezibelWertLabel.setName("abAbTabelleDezibelWertLabel"); // NOI18N
        abAbTabelleDezibelWertLabel.setPreferredSize(new java.awt.Dimension(34, 14));

        abAbTabelleMittlererSchalldruckpegelLabel.setText("Mittlerer Schalldruckpegel* dB(A) =");

        abAbTabelleMittlererSchalldruckpegelWertLabel.setText("28,2");
        abAbTabelleMittlererSchalldruckpegelWertLabel.setName("abAbTabelleMittlererSchalldruckpegelWertLabel"); // NOI18N

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("Oktavmittenfrequenz in Hz");

        org.jdesktop.layout.GroupLayout abAbTabellePanelLayout = new org.jdesktop.layout.GroupLayout(abAbTabellePanel);
        abAbTabellePanel.setLayout(abAbTabellePanelLayout);
        abAbTabellePanelLayout.setHorizontalGroup(
            abAbTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(abAbTabellePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(abAbTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, abAbTabellePanelLayout.createSequentialGroup()
                        .add(abAbTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(abAbTabelleMittlererSchalldruckpegelLabel)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, abAbTabelleScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 361, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(abAbTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(abAbTabelleMittlererSchalldruckpegelWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(abAbTabelleDezibelWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(abAbTabelleDezibelLabel)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, abAbTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, abAbTabelleUeberschrift2Label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, abAbTabelleUeberschrift1Label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)))
                .addContainerGap(117, Short.MAX_VALUE))
        );
        abAbTabellePanelLayout.setVerticalGroup(
            abAbTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(abAbTabellePanelLayout.createSequentialGroup()
                .add(abAbTabelleUeberschrift1Label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(abAbTabelleUeberschrift2Label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 15, Short.MAX_VALUE)
                .add(jLabel37)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(abAbTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(abAbTabelleScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 388, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(abAbTabellePanelLayout.createSequentialGroup()
                        .add(abAbTabelleDezibelLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(abAbTabelleDezibelWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(abAbTabellePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(abAbTabelleMittlererSchalldruckpegelLabel)
                    .add(abAbTabelleMittlererSchalldruckpegelWertLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        abAbHinweisLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        abAbHinweisLabel.setText("* Bei dieser Berechnung handelt es sich um eine theoretische Auslegung, deren Werte in der Praxis abweichen können");

        org.jdesktop.layout.GroupLayout abAbluftPanelLayout = new org.jdesktop.layout.GroupLayout(abAbluftPanel);
        abAbluftPanel.setLayout(abAbluftPanelLayout);
        abAbluftPanelLayout.setHorizontalGroup(
            abAbluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(abAbluftPanelLayout.createSequentialGroup()
                .add(abAbluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(abAbHinweisLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 962, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, abAbluftPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(abAbluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(abAbluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, abAbRaumbezeichnungComboBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, abAbRaumbezeichnungLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(abAbBeschriftungPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(abAbTabellePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        abAbluftPanelLayout.setVerticalGroup(
            abAbluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(abAbluftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(abAbluftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(abAbluftPanelLayout.createSequentialGroup()
                        .add(abAbRaumbezeichnungLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(abAbRaumbezeichnungComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(46, 46, 46)
                        .add(abAbBeschriftungPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(abAbTabellePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(abAbHinweisLabel))
        );

        abTabbedPane.addTab("Abluft", abAbluftPanel);

        org.jdesktop.layout.GroupLayout akkustikberechnungPanelLayout = new org.jdesktop.layout.GroupLayout(akkustikberechnungPanel);
        akkustikberechnungPanel.setLayout(akkustikberechnungPanelLayout);
        akkustikberechnungPanelLayout.setHorizontalGroup(
            akkustikberechnungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(akkustikberechnungPanelLayout.createSequentialGroup()
                .add(abTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 987, Short.MAX_VALUE)
                .add(155, 155, 155))
        );
        akkustikberechnungPanelLayout.setVerticalGroup(
            akkustikberechnungPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(akkustikberechnungPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(abTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                .addContainerGap())
        );

        projektTabbedPane.addTab("Akustikberechnung", akkustikberechnungPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(11, 11, 11)
                .add(projektTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1012, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(projektTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 600, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1039)/2, (screenSize.height-648)/2, 1039, 648);
    }// </editor-fold>//GEN-END:initComponents

    private void pbAngebotErstellenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pbAngebotErstellenActionPerformed

        boolean bCheck = true, bTest = false;
        float fTuerspalt = 0f;

        for (int i = 0; i < lmeTabelleTable.getRowCount(); i++) {
            try {
                bTest = ((String) lmeTabelleUeberstroemTable.getValueAt(i, 5)).isEmpty();
            } catch (Exception e) {
                if (!bTest) {
                    String sRaumname = (String) lmeTabelleTable.getValueAt(i, 0);
                    String sGeschoss = (String) wfTabelleTable.getValueAt(i, 1);
                    int iIndex = -1;
                    Raum r = null;
                    for (int j = 0; j < raeume.getRowCount(); j++) {
                        r = raeume.get(j);
                        if (r.getRaumItemValue(RaumItem.PROP.RAUMNAME).equals(sRaumname) && r.getRaumItemValue(RaumItem.PROP.GESCHOSS).equals(sGeschoss)) {
                            iIndex = j;
                        }
                    }
                    r = raeume.get(iIndex);
                    Vector<Tuer> t = r.getTueren();
                    for (Tuer tuer : t) {
                        fTuerspalt = tuer.getSpalthoehe();
                        if (fTuerspalt > Float.parseFloat((String) r.getRaumItemValue(RaumItem.PROP.MAX_TUERSPALTHOEHE))) {
                            bCheck = false;
                        }
                    }
                }
            }
        }
        if (bCheck) {
            Tools.msgbox("Bitte überprüfen Sie die Anzahl\nder Überströmelemente mit\nden vorhandenen Plänen!");
            wacMainFrame.performAngebot(null);
        }
}//GEN-LAST:event_pbAngebotErstellenActionPerformed

    private void pbSpeichernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pbSpeichernActionPerformed
        saveProject();
}//GEN-LAST:event_pbSpeichernActionPerformed

    private void toggleUebernehmenEnabled() {
//        if (lmeSollLuftmengeTextField.getText().isEmpty() ||
//                lmeIstLuftmengeTextField.getText().isEmpty() ||
//                lmeTypenbezeichnungCombobox.getString().isEmpty() ||
//                lmeVerteilebeneCombobox.getString().isEmpty()) {
//            lmeUebernehmenButton.setEnabled(false);
//        } else {
//            lmeUebernehmenButton.setEnabled(true);
//        }
    }

    private void wfTabelleTableKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_wfTabelleTableKeyTyped
        System.out.println("KeyCode = " + evt.getKeyCode() + "  " + evt.getKeyChar());
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            wfEntfernenButtonActionPerformed(new ActionEvent(evt.getSource(), evt.getKeyCode(), "Delete"));
        }
    }//GEN-LAST:event_wfTabelleTableKeyTyped

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        if (queryClosing() == true) {
            wacMainFrame.removeProject(this);
            wacMainFrame.unregisterShutdownObserver(this);
            dispose();
        }
    }//GEN-LAST:event_formInternalFrameClosing

    private void wfHinzufuegenButtonKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_wfHinzufuegenButtonKeyTyped
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            wfHinzufuegenButtonActionPerformed(new ActionEvent(evt.getSource(), evt.getKeyCode(), "Enter"));
        }
    }//GEN-LAST:event_wfHinzufuegenButtonKeyTyped

    private void luftmengenermittlungPanelAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_luftmengenermittlungPanelAncestorAdded
        if (lmeTabelleTable.getRowCount() > 0) {
            updateLuftmengeTab(false);
        }
    }//GEN-LAST:event_luftmengenermittlungPanelAncestorAdded

    private void dvbTeilstreckenLaengeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dvbTeilstreckenLaengeTextFieldActionPerformed
        dvbTeilstreckenHinzufuegenButtonActionPerformed(null);
    }//GEN-LAST:event_dvbTeilstreckenLaengeTextFieldActionPerformed

    private void dvbTeilstreckenLuftmengeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dvbTeilstreckenLuftmengeTextFieldActionPerformed
        dvbTeilstreckenHinzufuegenButtonActionPerformed(null);
    }//GEN-LAST:event_dvbTeilstreckenLuftmengeTextFieldActionPerformed

    private void dvbTeilstreckenNrTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dvbTeilstreckenNrTextFieldActionPerformed
        dvbTeilstreckenHinzufuegenButtonActionPerformed(null);
    }//GEN-LAST:event_dvbTeilstreckenNrTextFieldActionPerformed

    private void wfHoeheTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wfHoeheTextFieldActionPerformed
        wfHinzufuegenButtonActionPerformed(null);
    }//GEN-LAST:event_wfHoeheTextFieldActionPerformed

    private void wfFlaecheTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wfFlaecheTextFieldActionPerformed
        wfHinzufuegenButtonActionPerformed(null);
    }//GEN-LAST:event_wfFlaecheTextFieldActionPerformed

    private void dvbVentileinstellungTeilstreckenTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dvbVentileinstellungTeilstreckenTextFieldActionPerformed
        dvbVentileinstellungHinzufuegenButtonActionPerformed(null);
    }//GEN-LAST:event_dvbVentileinstellungTeilstreckenTextFieldActionPerformed

    private void wfBezeichnungComboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wfBezeichnungComboboxActionPerformed
        int idx;
        String val;
        wfZuluftfaktorTextField.setEnabled(true);
        wfAbluftVolumenTextField.setEnabled(true);

        wfRaumNameTextField.setText(((JComboBox) evt.getSource()).getSelectedItem().toString());

        idx = ((JComboBox) (evt.getSource())).getSelectedIndex();
        if (idx < 6) {
            val = "ZU";
            if (idx < 1) {
                wfZuluftfaktorTextField.setText("3");
            } else if (idx < 3) {
                wfZuluftfaktorTextField.setText("2");
            } else {
                wfZuluftfaktorTextField.setText("1,5");
            }
            wfAbluftVolumenTextField.setText("");
        } else if (idx < 13) {
            val = "AB";
            if (idx < 9) {
                wfAbluftVolumenTextField.setText("25");
            } else if (idx < 12) {
                wfAbluftVolumenTextField.setText("45");
            } else {
                wfAbluftVolumenTextField.setText("100");
            }
            wfZuluftfaktorTextField.setText("");
        } else {
            val = "ÜB";
            wfZuluftfaktorTextField.setText("");
            wfZuluftfaktorTextField.setEnabled(false);
            wfAbluftVolumenTextField.setText("");
            wfAbluftVolumenTextField.setEnabled(false);
        }
        wfBelueftungCombobox.setSelectedItem(val);
    }//GEN-LAST:event_wfBezeichnungComboboxActionPerformed

    private void dvbVentileinstellungEntfernenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dvbVentileinstellungEntfernenButtonActionPerformed

        int selectedRow = -1;

        selectedRow = dvbVentileinstellungTabelleTable.getSelectedRow();

        if (selectedRow > -1) {

            if (Tools.askbox(Strings.DELETE_LINE, Strings.CONFIRM) == Tools.OPTION_OK) {

                ((DefaultTableModel) dvbVentileinstellungTabelleTable.getModel()).removeRow(selectedRow);
            }

        } else {

            Tools.msgbox(Strings.CHOOSE_LINE, Strings.ERROR);
        }

    }//GEN-LAST:event_dvbVentileinstellungEntfernenButtonActionPerformed

    private void dvbTeilstreckenEntfernenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dvbTeilstreckenEntfernenButtonActionPerformed

        int j = 0;
        int answer = -1;
        int teilstreckeSelectedRow = -1;
        String teilstreckeEntfernenNr = null;
        int teilstreckeVerwendetCount = 0;
        Set<Integer> ventileinstellungTeilstreckeVerwendet = new TreeSet<Integer>();
        String tmp = null;
        StringBuffer question = new StringBuffer(Strings.DELETE_LINE);
        DefaultTableModel model = null;

        teilstreckeSelectedRow = dvbTeilstreckenTabelleTable.getSelectedRow();
        if (teilstreckeSelectedRow > -1) {

            teilstreckeEntfernenNr = (String) dvbTeilstreckenTabelleTable.getValueAt(teilstreckeSelectedRow, 1);

            // Prüfe, wie oft Teilstrecke in Ventileinstellungen verwendet wird
            // Hole Zelle 2 (Teilstrecken) von dvbVentileinstellungTabelleTable
            for (int row = 0; row < dvbVentileinstellungTabelleTable.getRowCount(); row++) {

                tmp = (String) dvbVentileinstellungTabelleTable.getValueAt(row, 2);
                if (tmp.indexOf(teilstreckeEntfernenNr) >= 0) {
                    teilstreckeVerwendetCount++;
                    ventileinstellungTeilstreckeVerwendet.add(row);
                }

            }

            if (teilstreckeVerwendetCount > 0) {
                question.append("\nDadurch "
                        + (teilstreckeVerwendetCount > 1 ? "werden" : "wird") + " auch ").
                        append(teilstreckeVerwendetCount).
                        append(" Einstellung"
                        + (teilstreckeVerwendetCount > 1 ? "en" : "")
                        + " gelöscht!");
            }

            answer = Tools.askbox(question);

            if (answer == Tools.OPTION_OK) {

                // Entferne Zeile aus dvbTeilstreckenTabelleTable...
                model = (DefaultTableModel) dvbTeilstreckenTabelleTable.getModel();
                model.removeRow(teilstreckeSelectedRow);

                // ...und entsprechende Zeilen aus dvVentileinstellungTabelleTable
                model = (DefaultTableModel) dvbVentileinstellungTabelleTable.getModel();
                for (Integer i : ventileinstellungTeilstreckeVerwendet) {
                    model.removeRow(i - j++);
                }
            }

        } else {
            Tools.msgbox(Strings.CHOOSE_LINE);
        }

    }//GEN-LAST:event_dvbTeilstreckenEntfernenButtonActionPerformed

    private void dvbVentileinstellungHinzufuegenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dvbVentileinstellungHinzufuegenButtonActionPerformed

        DefaultTableModel model = null;
        Object[] o = new Object[9];

        o[0] = (String) dvbVentileinstellungBelueftungComboBox.getSelectedItem();
        o[1] = (String) dvbVentileinstellungRaumComboBox.getSelectedItem();
        o[2] = dvbVentileinstellungTeilstreckenTextField.getText();
        o[3] = (String) dvbVentileinstellungVentilbezeichnungComboBox.getSelectedItem();

        model = (DefaultTableModel) dvbVentileinstellungTabelleTable.getModel();
        model.addRow(o);
        if (berechnungen.calculateVentileinstellung() == false) {
            model.removeRow(model.getRowCount() - 1);
        }

    }//GEN-LAST:event_dvbVentileinstellungHinzufuegenButtonActionPerformed

    private void dvbVentileinstellungTeilstreckenAuswaehlenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dvbVentileinstellungTeilstreckenAuswaehlenButtonActionPerformed

        String belueftung = null;
        List<String> teilstrecken = new LinkedList<String>();
        String tmp = null;

//        VentileinstellungTeilstreckenInternalFrame vets =
        TeilstreckenAuswahl vets =
                new TeilstreckenAuswahl(wacMainFrame, true);//VentileinstellungTeilstreckenInternalFrame(this);

        belueftung = (String) dvbVentileinstellungBelueftungComboBox.getSelectedItem();

        // Sezte Teilstrecken für AB und ZU
        for (int row = 0; row < dvbTeilstreckenTabelleTable.getRowCount(); row++) {
            tmp = (String) dvbTeilstreckenTabelleTable.getValueAt(row, 0);
            if (belueftung.equals(tmp)) {
                teilstrecken.add((String) dvbTeilstreckenTabelleTable.getValueAt(row, 1));
            }
        }

        vets.setTeilstrecken(teilstrecken.toArray(new String[teilstrecken.size()]), null);
        belueftung = vets.execute();
        if (belueftung != null) {
            dvbVentileinstellungTeilstreckenTextField.setText(belueftung);
        }
    }//GEN-LAST:event_dvbVentileinstellungTeilstreckenAuswaehlenButtonActionPerformed

    private void dvbTeilstreckenHinzufuegenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dvbTeilstreckenHinzufuegenButtonActionPerformed

        boolean nrExists = false;
        NumberFormat nfGerman = NumberFormat.getInstance(Locale.GERMAN);
        Object[] o = new Object[9];
        DefaultTableModel d = (DefaultTableModel) dvbTeilstreckenTabelleTable.getModel();

        // Nr
        o[1] = dvbTeilstreckenNrTextField.getText();

        // Prüfen ob Nr. schon vergeben ist
        for (int i = 0; i < d.getRowCount(); i++) {

            if (("" + d.getValueAt(i, 1)).equals(o[1])) {
                nrExists = true;
            }
        }

        if (!nrExists) {
            nfGerman.setMinimumFractionDigits(2);
            nfGerman.setMaximumFractionDigits(2);

            // Belüftung
            o[0] = (String) dvbTeilstreckenBelueftungComboBox.getSelectedItem();
            // Nr
//            o[1] = dvbTeilstreckenNrTextField.getText();
            // Luftmenge
            try {
                o[2] = nfGerman.format(Float.valueOf(dvbTeilstreckenLuftmengeTextField.getText()));
            } catch (NumberFormatException e) {
                o[2] = "";
            }
            // Kanalbezeichnung
            o[3] = (String) dvbTeilstreckenKanalbezeichnungComboBox.getSelectedItem();
            // Länge
            try {
                o[4] = nfGerman.format(ConversionUtil.parseFloatFromComponent(dvbTeilstreckenLaengeTextField));
            } catch (NumberFormatException e) {
                o[4] = "";
            }
            // Einzelwiderstände Zeta
            o[7] = nfGerman.format(0.0f);
            // Einzelwiderstände Pa
            o[8] = nfGerman.format(0.0f);

            if (!o[1].equals("") && !o[2].equals("") && !o[4].equals("")) {

                d.addRow(o);
                // Werte zurücksetzen
                dvbTeilstreckenNrTextField.setText("");
                dvbTeilstreckenLuftmengeTextField.setText("");
                dvbTeilstreckenLaengeTextField.setText("");
                berechnungen.berechneTeilstrecke("" + o[3]);
                updateDvbTeilstreckeTab();
                // Erstellte Zeile auswählen
                dvbTeilstreckenTabelleTable.changeSelection(dvbTeilstreckenTabelleTable.getRowCount() - 1, 0, false, false);
                dvbTeilstreckenNrTextField.requestFocus();
            } else {
                Tools.errbox(Strings.CORRECT_VALUES, Strings.ERROR);
            }
        } else {

            Tools.msgbox(String.format(Strings.SECTION, o[1]), Strings.ERROR);
        }
    }//GEN-LAST:event_dvbTeilstreckenHinzufuegenButtonActionPerformed

    private void abAbLaengsdaempfungKanalTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_abAbLaengsdaempfungKanalTextFieldKeyReleased
        updateAkkustikberechnungAbluft();
    }//GEN-LAST:event_abAbLaengsdaempfungKanalTextFieldKeyReleased

    private void abAbAnzahlUmlenkungenTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_abAbAnzahlUmlenkungenTextFieldKeyReleased
        updateAkkustikberechnungAbluft();
    }//GEN-LAST:event_abAbAnzahlUmlenkungenTextFieldKeyReleased

    private void abAbLuftverteilerkastenTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_abAbLuftverteilerkastenTextFieldKeyReleased
        updateAkkustikberechnungAbluft();
    }//GEN-LAST:event_abAbLuftverteilerkastenTextFieldKeyReleased

    private void abAbRaumabsorptionTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_abAbRaumabsorptionTextFieldKeyReleased
        updateAkkustikberechnungAbluft();
    }//GEN-LAST:event_abAbRaumabsorptionTextFieldKeyReleased

    private void akkustikBerechnungAbluftDataAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_akkustikBerechnungAbluftDataAction
        updateAkkustikberechnungAbluft();
    }//GEN-LAST:event_akkustikBerechnungAbluftDataAction

    private void abZuRaumabsorptionTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_abZuRaumabsorptionTextFieldKeyReleased
        updateAkkustikberechnungZuluft();
    }//GEN-LAST:event_abZuRaumabsorptionTextFieldKeyReleased

    private void abZuLaengsdaempfungKanalTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_abZuLaengsdaempfungKanalTextFieldKeyReleased
        updateAkkustikberechnungZuluft();
    }//GEN-LAST:event_abZuLaengsdaempfungKanalTextFieldKeyReleased

    private void abZuLuftverteilerkastenTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_abZuLuftverteilerkastenTextFieldKeyReleased
        updateAkkustikberechnungZuluft();
    }//GEN-LAST:event_abZuLuftverteilerkastenTextFieldKeyReleased

    private void abZuAnzahlUmlenkungenTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_abZuAnzahlUmlenkungenTextFieldKeyReleased
        updateAkkustikberechnungZuluft();
    }//GEN-LAST:event_abZuAnzahlUmlenkungenTextFieldKeyReleased

    private void rechnungZuluftDataAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechnungZuluftDataAction
        updateAkkustikberechnungZuluft();
    }//GEN-LAST:event_rechnungZuluftDataAction

    private void lmeSollLuftmengeAutoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lmeSollLuftmengeAutoButtonActionPerformed

        bZentralgeraet = false;
        setBZentralgeraet();
        // Auto Soll berechnen
        berechnungen.luftmengeAutoBerechnen(true);
        updateLuftmengeTab(true);
        float fUeberstroemMenge = 0;
        for (int i = 0; i < lmeTabelleTable.getRowCount(); i++) {
            if (lmeTabelleTable.getValueAt(i, 1).toString().contains("ZU")) {
//                raeume.get(i).setRaumItemValue(RaumItem.PROP.ZULUFTVOLUMENSTROM, JTableUtil.parseFloatFromTableCell(lmeTabelleTable, i, 6));
                JTableUtil.setFormattedFloatInTableCell(lmeTabelleUeberstroemTable, i, 4,
                        (Float) raeume.get(i).getRaumItemValue(RaumItem.PROP.ZULUFTVOLUMENSTROM));
            }
            if (lmeTabelleTable.getValueAt(i, 1).toString().contains("AB")) {
//                raeume.get(i).setRaumItemValue(RaumItem.PROP.ABLUFTVOLUMENSTROM, JTableUtil.parseFloatFromTableCell(lmeTabelleTable, i, 6));
                JTableUtil.setFormattedFloatInTableCell(lmeTabelleUeberstroemTable, i, 4,
                        (Float) raeume.get(i).getRaumItemValue(RaumItem.PROP.ABLUFTVOLUMENSTROM));
            }
            if (lmeTabelleTable.getValueAt(i, 1).toString().equals("ZU/AB")) {
                fUeberstroemMenge = Math.abs(((Float) raeume.get(i).getRaumItemValue(RaumItem.PROP.ABLUFTVOLUMENSTROM)
                        - (Float) raeume.get(i).getRaumItemValue(RaumItem.PROP.ZULUFTVOLUMENSTROM)));
                JTableUtil.setFormattedFloatInTableCell(lmeTabelleUeberstroemTable, i, 4, fUeberstroemMenge);
            }
        }
        berechneTueren();

    }//GEN-LAST:event_lmeSollLuftmengeAutoButtonActionPerformed

    private void lmeZentralgeraetComboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lmeZentralgeraetComboboxActionPerformed

        int k;
        Object idx = null;

        bZentralgeraet = true;
        setBZentralgeraet();

        if (lmeVolumenstromCombobox.getSelectedItem() != null) {
            idx = lmeVolumenstromCombobox.getSelectedItem();
        }

        String geraet = ((DocumentAware) evt.getSource()).getString().trim();
        lmeVolumenstromCombobox.myVolatile = geraet;
        lmeVolumenstromCombobox.fromDatabase();
        for (int i = 0; i < lmeVolumenstromCombobox.getItemCount() - 1; i += 2) {
            k = Integer.parseInt((String) lmeVolumenstromCombobox.getItemAt(i)) + 5;
            lmeVolumenstromCombobox.insertItemAt(Integer.toString(k), i + 1);
        }
        lmeVolumenstromCombobox.setSelectedItem(idx);
        abZuSchallleistungspegelZuluftstutzenComboBox.setModel(lmeVolumenstromCombobox.getModel());
        abAbSchallleistungspegelAbluftstutzenComboBox.setModel(lmeVolumenstromCombobox.getModel());
        updateAbZuTexte();
    }//GEN-LAST:event_lmeZentralgeraetComboboxActionPerformed

    private void updateAbZuTexte() {

        String geraet = lmeZentralgeraetCombobox.getString();

        // Ändern des Textes in der Akkustikberechnung
        abZuTabelleUeberschrift1Label.setText(Strings.MAIN_DEVICE + geraet);
        abAbTabelleUeberschrift1Label.setText(Strings.MAIN_DEVICE + geraet);
        stZuGeraet.setText(geraet);
        stAbGeraet.setText(geraet);

    }

    private void wfHinzufuegenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wfHinzufuegenButtonActionPerformed

        float flaeche = 0f;
        float hoehe = 0f;
        float volumen = 0f;
        float zuluftfaktor = 0f;
        float abluftvolumen = 0f;
        float fDummy = 0f;
        String sDummy = "10.0";
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
        DefaultTableModel d = null;
        DefaultTableModel dUeb = null;

        nf.setGroupingUsed(true);
        nf.setMinimumFractionDigits(2);

        if (!wfFlaecheTextField.getText().isEmpty()) {

            flaeche = ConversionUtil.parseFloatFromComponent(wfFlaecheTextField);
            hoehe = ConversionUtil.parseFloatFromComponent(wfHoeheTextField);
            if (!wfZuluftfaktorTextField.getText().isEmpty()) {
                zuluftfaktor = ConversionUtil.parseFloatFromComponent(wfZuluftfaktorTextField);
            }
            if (!wfBelueftungCombobox.getString().contains("ÜB")) {
                zuluftfaktor = checkZuluftfaktor(zuluftfaktor);
            }
            if (!wfAbluftVolumenTextField.getText().isEmpty()) {
                abluftvolumen = ConversionUtil.parseFloatFromComponent(wfAbluftVolumenTextField);
            }
//            if (!wfBelueftungCombobox.getString().contains("ZU/AB")) {
            // neuen Raum in Klasse 'Raum' und 'Rooms' hinzufügen
            Raum r = raeume.add(wfGeschossCombobox.getString());
            if (r != null) {
                String rb = wfBezeichnungCombobox.getString();
                if (!wfRaumNameTextField.getText().isEmpty()) {
                    r.setRaumItemValue(RaumItem.PROP.RAUMNAME, wfRaumNameTextField.getText());
                } else {
                    r.setRaumItemValue(RaumItem.PROP.RAUMNAME, rb);
                }
                r.setRaumItemValue(RaumItem.PROP.RAUMTYP, rb);
                r.setRaumItemValue(RaumItem.PROP.GESCHOSS, wfGeschossCombobox.getString());
                r.setRaumItemValue(RaumItem.PROP.LUFTART, wfBelueftungCombobox.getString());
                if (!wfZuluftfaktorTextField.getText().isEmpty()) {
                    r.setRaumItemValue(RaumItem.PROP.FAKTOR_ZULUFTVERTEILUNG, ConversionUtil.parseFloatFromComponent(wfZuluftfaktorTextField));
                } else {
                    r.setRaumItemValue(RaumItem.PROP.FAKTOR_ZULUFTVERTEILUNG, fDummy);
                }
                if (!wfAbluftVolumenTextField.getText().isEmpty()) {
                    r.setRaumItemValue(RaumItem.PROP.VORGABELUFTMENGE, ConversionUtil.parseFloatFromComponent(wfAbluftVolumenTextField));
                } else {
                    r.setRaumItemValue(RaumItem.PROP.VORGABELUFTMENGE, fDummy);
                }
                r.setRaumItemValue(RaumItem.PROP.MAX_TUERSPALTHOEHE, sDummy);
                r.setRaumItemValue(RaumItem.PROP.RAUMFLAECHE, ConversionUtil.parseFloatFromComponent(wfFlaecheTextField));
                r.setRaumItemValue(RaumItem.PROP.RAUMHOEHE, ConversionUtil.parseFloatFromComponent(wfHoeheTextField));
                volumen = (Float) r.getRaumItemValue(RaumItem.PROP.RAUMFLAECHE) * (Float) r.getRaumItemValue(RaumItem.PROP.RAUMHOEHE);
                r.setRaumItemValue(RaumItem.PROP.RAUMVOLUMEN, volumen);
                // neuen Raum in Klasse hinzufügen ENDE!
                d = (DefaultTableModel) wfTabelleTable.getModel();
                dUeb = (DefaultTableModel) lmeTabelleUeberstroemTable.getModel();
                d.addRow(new Object[]{
                            r.getRaumItemValue(RaumItem.PROP.RAUMNAME),
                            wfGeschossCombobox.getSelectedItem(),
                            wfBelueftungCombobox.getSelectedItem(),
                            nf.format(flaeche),
                            nf.format(hoehe),
                            nf.format(zuluftfaktor),
                            nf.format(abluftvolumen)
                        });
                berechnungen.luftmengenUpdate(d.getRowCount() - 1);
                // für alle Räume Tabelle anlegen (ÜB-Elemente)
                int iZeile = d.getRowCount() - 1;
                int iUebZeile = dUeb.getRowCount();
                dUeb.addRow(new Object[]{});
                dUeb.setValueAt(wfTabelleTable.getValueAt(iZeile, 0), iUebZeile, 0);
                dUeb.setValueAt(wfTabelleTable.getValueAt(iZeile, 2), iUebZeile, 1);
                JTableUtil.setFormattedFloatInTableCell(lmeTabelleUeberstroemTable, iUebZeile,
                        2, (Float) r.getRaumItemValue(RaumItem.PROP.RAUMVOLUMEN));
                // Erstellte Zeile auswählen
                wfTabelleTable.changeSelection(wfTabelleTable.getRowCount() - 1, 0, false, false);
            }
            berechneMittlereRaumhoehe();
            berechneGesamtflaeche();
        }
//wfTabelleTable.setModel(raeume);
//lmeTabelleTable.setModel(raeume);
//Raum rm = new Raum();
//rm.setValue(Item.RAUMNAME, "Warzenraum");
//raeume.add(rm);
    }//GEN-LAST:event_wfHinzufuegenButtonActionPerformed

    private void wfEntfernenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wfEntfernenButtonActionPerformed

        int row = wfTabelleTable.getSelectedRow();

        if (row >= 0) {
            // Remove room from Rooms instance
            raeume.removeRow(row);

            ((DefaultTableModel) lmeTabelleUeberstroemTable.getModel()).removeRow(row);
            ((DefaultTableModel) lmeTabelleTable.getModel()).removeRow(row);
            ((DefaultTableModel) wfTabelleTable.getModel()).removeRow(row);
            //
            row--;
        }

        wfTabelleTable.changeSelection(row, 0, false, false);
        lmeTabelleTable.changeSelection(row, 0, false, false);
        lmeTabelleUeberstroemTable.changeSelection(row, 0, false, false);

    }//GEN-LAST:event_wfEntfernenButtonActionPerformed

    private boolean queryClosing() {

        boolean b = false;
        if (changed != 0) {

            int option = Tools.askbox(String.format(Strings.SAVE_CHANGES, getTitle()));

            if (option == Tools.OPTION_OK) {
                // Projekt speichern
                b = saveProject();
            } else if (option == Tools.OPTION_NO) {
                // Projekt verwerfen; nicht speichern
                b = true;

            }
        } else {
            b = true;
        }
        return b;
    }

    private void wfTabelleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_wfTabelleSpinnerStateChanged

        int selectedRow = wfTabelleTable.getSelectedRow();
        JSpinner spinner = (JSpinner) evt.getSource();
        int spinnerOffset = (Integer) spinner.getModel().getValue(); // 1 = index-up, -1 = index-down

        if (spinnerOffset == 0) {
            return;
        }

        // Nur Zeilen bewegen wenn auch eine ausgewählt wurde
        if (selectedRow > -1) {
            if (spinnerOffset == 1 && selectedRow > 0
                    || spinnerOffset == -1 && selectedRow < raeume.getRowCount() - 1) {
                ((DefaultTableModel) wfTabelleTable.getModel()).moveRow(selectedRow, selectedRow, selectedRow - spinnerOffset);
                ((DefaultTableModel) lmeTabelleTable.getModel()).moveRow(selectedRow, selectedRow, selectedRow - spinnerOffset);
                ((DefaultTableModel) lmeTabelleUeberstroemTable.getModel()).moveRow(selectedRow, selectedRow, selectedRow - spinnerOffset);
                wfTabelleTable.changeSelection(selectedRow - spinnerOffset, 0, false, false);
                lmeTabelleTable.changeSelection(selectedRow - spinnerOffset, 0, false, false);
                lmeTabelleUeberstroemTable.changeSelection(selectedRow - spinnerOffset, 0, false, false);
            }
            if (spinnerOffset == -1) {
                raeume.moveUp(selectedRow);
            } else {
                raeume.moveDown(selectedRow);
            }
        }
        spinner.getModel().setValue(new Integer(0));

    }//GEN-LAST:event_wfTabelleSpinnerStateChanged

    private void personenanzahlSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_personenanzahlSpinnerStateChanged
        mindestaussenluftrateWertLabel.setText("" + Integer.valueOf(""
                + ((JSpinner) evt.getSource()).getValue()) * Integer.parseInt(((DocumentAware) volumenProPersonSpinner).getString()));
    }//GEN-LAST:event_personenanzahlSpinnerStateChanged

    private void adBauvorhabenKeyReleasedAction(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_adBauvorhabenKeyReleasedAction

        setProjektTitel();
    }//GEN-LAST:event_adBauvorhabenKeyReleasedAction

    private void widerstandsbeiwerteBearbeitenAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_widerstandsbeiwerteBearbeitenAction

        com.westaflex.dialogs.WbwDialog wbwDialog = null;
        Object collect = null;
        int row = dvbTeilstreckenTabelleTable.getSelectedRow();
        int intern = -1;

        if (row > -1) {
            // Dialog darf nur aufgehen, wenn eine Tabellenzeile gewählt wurde
//            fixInternColumn();
            if (dvbTeilstreckenTabelleTable.getColumn("Intern") != null) {
                intern = dvbTeilstreckenTabelleTable.getColumn("Intern").getModelIndex();
                collect = dvbTeilstreckenTabelleTable.getValueAt(row, intern);
            } else {
                collect = null;
            }

            wbwDialog = new com.westaflex.dialogs.WbwDialog(wacMainFrame, collect);
            collect =
                    wbwDialog.execute();
            if (collect != null) {
                dvbTeilstreckenTabelleTable.setValueAt(collect, row, intern);
                dvbTeilstreckenTabelleTable.setValueAt(wbwDialog.getSumme(), row, 7);
            }

            updateAllComponents();
        }
    }//GEN-LAST:event_widerstandsbeiwerteBearbeitenAction

private void ldKatARadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ldKatARadioButtonActionPerformed
    setLuftwechsel();
}//GEN-LAST:event_ldKatARadioButtonActionPerformed

private void ldKatBRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ldKatBRadioButtonActionPerformed
    setLuftwechsel();
}//GEN-LAST:event_ldKatBRadioButtonActionPerformed

private void ldKatCRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ldKatCRadioButtonActionPerformed
    setLuftwechsel();
}//GEN-LAST:event_ldKatCRadioButtonActionPerformed

private void hkAusfuehrungCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hkAusfuehrungCheckboxActionPerformed
    setHKZWertLabel();
    setKennzeichen();
}//GEN-LAST:event_hkAusfuehrungCheckboxActionPerformed

private void hkFilterungCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hkFilterungCheckboxActionPerformed
    setHKZWertLabel();
    setKennzeichen();
}//GEN-LAST:event_hkFilterungCheckboxActionPerformed

private void hkVerschmutzungCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hkVerschmutzungCheckboxActionPerformed
    setHKZWertLabel();
    setKennzeichen();
}//GEN-LAST:event_hkVerschmutzungCheckboxActionPerformed

private void ekBemessungCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ekBemessungCheckboxActionPerformed
    setEKZWertLabel();
    setKennzeichen();
}//GEN-LAST:event_ekBemessungCheckboxActionPerformed

private void ekRueckgewinnungCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ekRueckgewinnungCheckboxActionPerformed
    setEKZWertLabel();
    setKennzeichen();
}//GEN-LAST:event_ekRueckgewinnungCheckboxActionPerformed

private void ekRegelungCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ekRegelungCheckboxActionPerformed
    setEKZWertLabel();
    setKennzeichen();
}//GEN-LAST:event_ekRegelungCheckboxActionPerformed

private void ekZuAbluftCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ekZuAbluftCheckboxActionPerformed
    setEKZWertLabel();
    setEnableEnergie();
    setKennzeichen();
}//GEN-LAST:event_ekZuAbluftCheckboxActionPerformed

private void vsLuftmengeBerechnenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vsLuftmengeBerechnenButtonActionPerformed
    //Berechnungen der AU-Volumenströme
    berechneAussenluftvolumenstroeme();
}//GEN-LAST:event_vsLuftmengeBerechnenButtonActionPerformed

private void geoWohnflaecheTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_geoWohnflaecheTextFieldActionPerformed
    berechneGelueftetesVolumen();
}//GEN-LAST:event_geoWohnflaecheTextFieldActionPerformed

private void geoHoeheTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_geoHoeheTextFieldActionPerformed
    berechneGelueftetesVolumen();
}//GEN-LAST:event_geoHoeheTextFieldActionPerformed

private void geoHoeheTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_geoHoeheTextFieldFocusLost
    berechneGelueftetesVolumen();
}//GEN-LAST:event_geoHoeheTextFieldFocusLost

private void geoVolumenTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_geoVolumenTextFieldActionPerformed
    berechneGelueftetesVolumen();
}//GEN-LAST:event_geoVolumenTextFieldActionPerformed

private void geoVolumenTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_geoVolumenTextFieldFocusLost
    berechneGelueftetesVolumen();
}//GEN-LAST:event_geoVolumenTextFieldFocusLost

private void geoGeluefteteflaecheTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_geoGeluefteteflaecheTextFieldActionPerformed
    berechneGelueftetesVolumen();
}//GEN-LAST:event_geoGeluefteteflaecheTextFieldActionPerformed

private void geoGeluefteteflaecheTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_geoGeluefteteflaecheTextFieldFocusLost
    berechneGelueftetesVolumen();
}//GEN-LAST:event_geoGeluefteteflaecheTextFieldFocusLost

private void wfZuluftfaktorTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wfZuluftfaktorTextFieldActionPerformed
    wfHinzufuegenButtonActionPerformed(null);
}//GEN-LAST:event_wfZuluftfaktorTextFieldActionPerformed

private void wfAbluftVolumenTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wfAbluftVolumenTextFieldActionPerformed
    wfHinzufuegenButtonActionPerformed(null);
}//GEN-LAST:event_wfAbluftVolumenTextFieldActionPerformed

private void wfBelueftungComboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wfBelueftungComboboxActionPerformed
    if (((DocumentAware) evt.getSource()).getString().contains("ÜB")) {
        wfAbluftVolumenTextField.setText("");
        wfZuluftfaktorTextField.setText("");
        wfZuluftfaktorTextField.setEnabled(false);
        wfAbluftVolumenTextField.setEnabled(false);
    } else if (!((DocumentAware) evt.getSource()).getString().contains("ZU/AB")) {
        if (((DocumentAware) evt.getSource()).getString().contains("ZU")) {
            wfZuluftfaktorTextField.setEnabled(true);
            wfAbluftVolumenTextField.setText("");
            wfAbluftVolumenTextField.setEnabled(false);
        } else if (((DocumentAware) evt.getSource()).getString().contains("AB")) {
            wfAbluftVolumenTextField.setEnabled(true);
            wfZuluftfaktorTextField.setText("");
            wfZuluftfaktorTextField.setEnabled(false);
        }
    } else {
        wfZuluftfaktorTextField.setEnabled(true);
        wfAbluftVolumenTextField.setEnabled(true);
    }

    if (((DocumentAware) evt.getSource()).getString().contains("ZU")) {
        if (!wfZuluftfaktorTextField.getText().isEmpty()) {
            setZuluftfaktor(ConversionUtil.parseFloatFromComponent(wfZuluftfaktorTextField));
        } else {
            setZuluftfaktor(0f);
        }
    }

}//GEN-LAST:event_wfBelueftungComboboxActionPerformed

private void volumenProPersonSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_volumenProPersonSpinnerStateChanged
    mindestaussenluftrateWertLabel.setText("" + Integer.valueOf(""
            + ((JSpinner) evt.getSource()).getValue()) * Integer.parseInt(((DocumentAware) personenanzahlSpinner).getString()));
}//GEN-LAST:event_volumenProPersonSpinnerStateChanged

private void glWschwachRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glWschwachRadioButtonActionPerformed
    setDiffDruck();
}//GEN-LAST:event_glWschwachRadioButtonActionPerformed

private void glWstarkRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glWstarkRadioButtonActionPerformed
    setDiffDruck();
}//GEN-LAST:event_glWstarkRadioButtonActionPerformed

private void geoWohnflaecheTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_geoWohnflaecheTextFieldFocusLost
    berechneGelueftetesVolumen();
}//GEN-LAST:event_geoWohnflaecheTextFieldFocusLost

private void pbRaumBearbeitenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pbRaumBearbeitenActionPerformed
    int row = lmeTabelleTable.getSelectedRow();

    if (row > raeume.getRowCount() - 1) {
        row = raeume.getRowCount() - 1;
    }
    if (row > -1) {
        RaumDialog rd = new RaumDialog(wacMainFrame, true, raeume, row);
        rd.setVisible(true);
        if (rd.getResult() == true) {
            raeume = rd.getRaeume();
            aktualisiereTabelle();
        }
    }
}//GEN-LAST:event_pbRaumBearbeitenActionPerformed

private void dvbTeilstreckenTabelleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_dvbTeilstreckenTabelleSpinnerStateChanged

    int selectedRow = dvbTeilstreckenTabelleTable.getSelectedRow();

    // Nur Zeilen bewegen wenn auch eine ausgewählt wurde
    if (selectedRow > -1) {

        // Werte in Tabelle wfTabelleTable nach oben oder unten verschieben
        JTableUtil.spinnerMovesTableRow(
                dvbTeilstreckenTabelleTable, (JSpinner) evt.getSource());

    }

}//GEN-LAST:event_dvbTeilstreckenTabelleSpinnerStateChanged

private void dvbTabelleVentilSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_dvbTabelleVentilSpinnerStateChanged

    int selectedRow = dvbVentileinstellungTabelleTable.getSelectedRow();

    // Nur Zeilen bewegen, wenn auch eine ausgewählt wurde
    if (selectedRow > -1) {

        // Werte in Tabelle dvbVentileinstellungTabelleTable nach oben oder unten verschieben
        JTableUtil.spinnerMovesTableRow(
                dvbVentileinstellungTabelleTable, (JSpinner) evt.getSource());

    }

}//GEN-LAST:event_dvbTabelleVentilSpinnerStateChanged

private void wfRaumNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wfRaumNameTextFieldActionPerformed
    wfHinzufuegenButtonActionPerformed(null);
}//GEN-LAST:event_wfRaumNameTextFieldActionPerformed

private void wfRaumBearbeitenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wfRaumBearbeitenActionPerformed
    int row = wfTabelleTable.getSelectedRow();

    if (row > raeume.getRowCount() - 1) {
        row = raeume.getRowCount() - 1;
    }

    if (row > -1) {
        RaumDialog rd = new RaumDialog(wacMainFrame, true, raeume, row);
        rd.setVisible(true);
        if (rd.getResult() == true) {
            raeume = rd.getRaeume();
            aktualisiereTabelle();
        }
    }

}//GEN-LAST:event_wfRaumBearbeitenActionPerformed

private void wfRaumKopierenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wfRaumKopierenActionPerformed

    int row = wfTabelleTable.getSelectedRow();
    NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
    DefaultTableModel d = null;
    DefaultTableModel dUeb = null;
    nf.setGroupingUsed(true);
    nf.setMinimumFractionDigits(2);


    if (row > -1) {
//        Raum raum = raeume.add(raeume.get(row).copy());
        Raum r = raeume.add(wfGeschossCombobox.getString());

        for (RaumItem.PROP prop : RaumItem.PROP.values()) {
            if (!prop.equals(RaumItem.PROP.RAUMNUMMER)) {
                r.setRaumItemValue(prop, raeume.get(row).getRaumItemValue(prop));
            }
        }

//        if (!((String) raeume.get(row).getRaumItemValue(RaumItem.PROP.LUFTART)).contains("ZU/AB")) {
        d = (DefaultTableModel) wfTabelleTable.getModel();
        d.addRow(new Object[]{
                    r.getRaumItemValue(RaumItem.PROP.RAUMNAME),
                    r.getRaumItemValue(RaumItem.PROP.GESCHOSS),
                    r.getRaumItemValue(RaumItem.PROP.LUFTART),
                    nf.format(JTableUtil.parseFloatFromTableCell(wfTabelleTable, row, 3)),
                    nf.format(JTableUtil.parseFloatFromTableCell(wfTabelleTable, row, 4)),
                    nf.format(JTableUtil.parseFloatFromTableCell(wfTabelleTable, row, 5)),
                    nf.format(JTableUtil.parseFloatFromTableCell(wfTabelleTable, row, 6))
                });
        berechnungen.luftmengenUpdate(d.getRowCount() - 1);
        // für alle Räume Tabelle anlegen (ÜB-Elemente)
        dUeb = (DefaultTableModel) lmeTabelleUeberstroemTable.getModel();
        int iUebZeile = dUeb.getRowCount();
        int iZeile = d.getRowCount() - 1;
        dUeb.addRow(new Object[]{});
        dUeb.setValueAt(wfTabelleTable.getValueAt(iZeile, 0), iUebZeile, 0);
        dUeb.setValueAt(wfTabelleTable.getValueAt(iZeile, 2), iUebZeile, 1);
        JTableUtil.setFormattedFloatInTableCell(lmeTabelleUeberstroemTable, iUebZeile,
                2, (Float) r.getRaumItemValue(RaumItem.PROP.RAUMVOLUMEN));
        // erstellte Zeile auswählen
        wfTabelleTable.changeSelection(wfTabelleTable.getRowCount() - 1, 0, false, false);

        // neuen Raum im Dialog anzeigen
        RaumDialog rd = new RaumDialog(wacMainFrame, true, raeume, wfTabelleTable.getRowCount() - 1);
        rd.setVisible(true);
        if (rd.getResult() == true) {
            raeume = rd.getRaeume();
            aktualisiereTabelle();
        }
    }

}//GEN-LAST:event_wfRaumKopierenActionPerformed

private void kzRueckschlagklappeCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kzRueckschlagklappeCheckboxActionPerformed
    setKennzeichen();
}//GEN-LAST:event_kzRueckschlagklappeCheckboxActionPerformed

private void kzSchallschutzCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kzSchallschutzCheckboxActionPerformed
    setKennzeichen();
}//GEN-LAST:event_kzSchallschutzCheckboxActionPerformed

private void kzFeuerstaetteCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kzFeuerstaetteCheckBoxActionPerformed
    setKennzeichen();
}//GEN-LAST:event_kzFeuerstaetteCheckBoxActionPerformed

private void gtMFHRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gtMFHRadioButtonActionPerformed
    setKennzeichen();
}//GEN-LAST:event_gtMFHRadioButtonActionPerformed

private void gtEFHRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gtEFHRadioButtonActionPerformed
    setKennzeichen();
}//GEN-LAST:event_gtEFHRadioButtonActionPerformed

private void gtMaisonetteRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gtMaisonetteRadioButtonActionPerformed
    setKennzeichen();
}//GEN-LAST:event_gtMaisonetteRadioButtonActionPerformed

private void hkDichtheitsklasseCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hkDichtheitsklasseCheckboxActionPerformed
    setHKZWertLabel();
    setKennzeichen();
}//GEN-LAST:event_hkDichtheitsklasseCheckboxActionPerformed
    // GZ: never used
    /*private void fixInternColumn() {

    if (!dvbTeilstreckenTabelleTable.getColumnModel().getColumn(dvbTeilstreckenTabelleTable.getColumnCount() - 1).getIdentifier().equals("Intern")) {

    TableColumn tc = new TableColumn(dvbTeilstreckenTabelleTable.getColumnCount());
    tc.setIdentifier("Intern");
    tc.setMinWidth(0);
    tc.setMaxWidth(0);
    dvbTeilstreckenTabelleTable.addColumn(tc);
    }

    }*/

    public void setProjektTitel() {
        setTitle(Strings.PROJECT + counter + " - " + adBauvorhabenTextField.getText());
        wacMainFrame.updateFensterMenu();
    }

    public File getWpxFile() {
        return wpxFile;
    }

    public void setWpxFile(File wpxFile) {

        this.wpxFile = wpxFile;

        // Prüfe auf richtige Erweiterung
        if (wpxFile != null) {
            // RBE-20100129
            String path = wpxFile.getPath();
//            if (null != path) {
//                path = path.replace(File.pathSeparatorChar, '_');
//                path = path.replace(File.separatorChar, '_');
//                path = path.replaceAll("\\\\", "_");
            if (!wpxFile.getPath().endsWith(".wpx")) {
                this.wpxFile = new File(path + ".wpx");
            }
//            } else {
//                logger.log(Level.SEVERE, "Could not save WPX, path=" + path);
//            }
            // RBE-20100129
        }

    }

    /**
     *
     * @param oDoc
     */
    public void toDocument(OfficeDocument oDoc) {

        toDocument(this, oDoc);

        XTextFieldsSupplier xTFS = (XTextFieldsSupplier) UnoRuntime.queryInterface(XTextFieldsSupplier.class, oDoc);

        XRefreshable xRefreshable =
                (XRefreshable) UnoRuntime.queryInterface(
                XRefreshable.class, xTFS.getTextFields());
        xRefreshable.refresh();

        XModifiable xMod = (XModifiable) UnoRuntime.queryInterface(XModifiable.class, oDoc);




        try {
            xMod.setModified(false);
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param me
     * @param tfm
     */
    public void toDocument(Container me, OfficeDocument oDoc) {

        Component[] myComp = null;

        if (me instanceof DocumentAware) {
            ((DocumentAware) me).toDoc(oDoc);
        }

        myComp = me.getComponents();
        for (Component comp : myComp) {
            toDocument((Container) comp, oDoc);
        }

    }

    public Object[] collectTransferData() {

        Object[] ret = new Object[4];
        Object[][] controls = new Object[][]{
            {"Bauvorhaben", adBauvorhabenTextField},
            {"Firma1", afFirma1TextField},
            {"Firma2", afFirma2TextField},
            {"Strasse", afStrasseTextField},
            {"PlzOrt", afPlzOrtTextField},
            {"Telefon", afTelefonTextField},
            {"Telefax", afFaxTextField},
            {"Ansprechpartner", afAnsprechpartnerTextField},
            {"Volumenstrom", lmeVolumenstromCombobox},
            {"Zentralgeraet", lmeZentralgeraetCombobox},
            {"Aussenluft", aussenluftButtonGroup},
            {"Fortluft", fortluftButtonGroup},
            {"Geraetestandort", geraetestandortButtonGroup}
        };//{"Ueberstroemelement", lmeTabelleTable.getValueAt(row, column)},
        final int UEBERSTROEM = 5;
        final int VENTILBEZEICHNUNG = 9;
        final int VERTEILEBENE = 10;

        NamedValue[] first = new NamedValue[controls.length + 1];
        for (int i = 0; i < controls.length; i++) {
            first[i] = new NamedValue((String) controls[i][0], ((DocumentAware) controls[i][1]).getString());
        }

        // Hier kommt die Grafik hin!
        Description desc = new Description();
        int lmeRowCount = lmeTabelleTable.getRowCount();
        for (int i = 0; i < lmeRowCount; i++) {
            String luftart = (String) lmeTabelleTable.getValueAt(i, 1); // Luftart
            String verteilebene = (String) lmeTabelleTable.getValueAt(i, 10); // Verteilebene
            String ventil = null;
            String raumname = null;
            if (luftart.equals("AB")) {
                ventil = (String) lmeTabelleTable.getValueAt(i, 9);
                raumname = (String) lmeTabelleTable.getValueAt(i, 0) + " / " + ventil;
                desc.addConnector(luftart, verteilebene, raumname);
            } else if (luftart.equals("ZU")){
                ventil = (String) lmeTabelleTable.getValueAt(i, 8);
                raumname = (String) lmeTabelleTable.getValueAt(i, 0) + " / " + ventil;
                desc.addConnector(luftart, verteilebene, raumname);
            } else if (luftart.equals("ZU/AB")) {
                // AB
                ventil = (String) lmeTabelleTable.getValueAt(i, 9);
                raumname = (String) lmeTabelleTable.getValueAt(i, 0) + " / " + ventil;
                desc.addConnector("AB", verteilebene, raumname);
                // ZU
                ventil = (String) lmeTabelleTable.getValueAt(i, 8);
                raumname = (String) lmeTabelleTable.getValueAt(i, 0) + " / " + ventil;
                desc.addConnector("ZU", verteilebene, raumname);
            }
        }
        //
        WestaDB westaDb = WestaDB.getInstance();
        ResultSet rs;
//      const AUSSENLUFT = 70, FORTLUFT = 71, GERAETEPAKET = 72, GRUNDPAKET = 73, ERWEITERUNGSPAKET = 74, VERTEILPAKET = 75, LUFTDURCHLASS = 76
//	const PAKET = 1, GERAET = 2, VOLUMENSTROM = 3, BEDINGUNG = 4, UEBERSTROEMELEMENT = 77
//	sZentralgeraet = oCore.getPropertyValue("Zentralgeraet")
//
//      ' 4. Außenluft
//	' ¯¯¯¯¯¯¯¯¯¯¯¯
//	oQueryAuFo = prepareDBStatement("select ~stueckliste~.~Artikel~, ~stueckliste~.~Anzahl~ from ~stueckliste~, ~pakete~ where ~stueckliste~.~Paket~ = ~pakete~.~ID~ and ~pakete~.~Kategorie~= ? and ~pakete~.~Geraet~= ? and ~pakete~.~Bedingung~ = ?" & order)
//	oQueryAuFo.setInt(PAKET, AUSSENLUFT)
//	oQueryAuFo.setString(GERAET, sZentralgeraet)
//	oQueryAuFo.setString(3, oCore.getPropertyValue("Aussenluft"))
        rs = westaDb.queryDB("select ~stueckliste~.~Artikel~, ~stueckliste~.~Anzahl~ from ~stueckliste~, ~pakete~ where ~stueckliste~.~Paket~ = ~pakete~.~ID~ and ~pakete~.~Kategorie~= 70 and ~pakete~.~Geraet~= '" + lmeZentralgeraetCombobox.getString() + "' and ~pakete~.~Bedingung~ = '" + aussenluftButtonGroup.getValue().toString() + "' and ~stueckliste~.~Reihenfolge~ = 10");
        try {
            rs.next();
            desc.putName("aussenluft", rs.getString(1));
        } catch (SQLException ex) {
            Logger.getLogger(ProjectInternalFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
//	' 5. Fortluft
//	' ¯¯¯¯¯¯¯¯¯¯¯¯
//	oQueryAuFo.setInt(PAKET, FORTLUFT)
//	oQueryAuFo.setString(3, oCore.getPropertyValue("Fortluft"))
        rs = westaDb.queryDB("select ~stueckliste~.~Artikel~, ~stueckliste~.~Anzahl~ from ~stueckliste~, ~pakete~ where ~stueckliste~.~Paket~ = ~pakete~.~ID~ and ~pakete~.~Kategorie~= 71 and ~pakete~.~Geraet~= '" + lmeZentralgeraetCombobox.getString() + "' and ~pakete~.~Bedingung~ = '" + fortluftButtonGroup.getValue().toString() + "' and ~stueckliste~.~Reihenfolge~ = 10");
        try {
            rs.next();
            desc.putName("fortluft", rs.getString(1));
        } catch (SQLException ex) {
            Logger.getLogger(ProjectInternalFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        desc.putName("zentralgeraet", lmeZentralgeraetCombobox.getString());

        try {
            first[first.length - 1] = new NamedValue("grafik", desc.drawText().getAbsolutePath());
        } catch (Exception e) {
        }
        // Beim Volumenstrom die Kommata entfernen
        first[8].Value = ((String) first[8].Value).replaceAll(",", ".");

        //
        ret[0] = first;
        ret[1] = makeNamedValuesFromTable(lmeTabelleTable, VERTEILEBENE);
        ret[2] = makeNamedValuesFromTable(lmeTabelleTable, VENTILBEZEICHNUNG);
        ret[3] = makeNamedValuesFromTable(lmeTabelleUeberstroemTable, UEBERSTROEM);
        return ret;
    }

    NamedValue[] makeNamedValuesFromTable(SeeTable table, int index) {
        final int LUFTART = 1, ANZAHLABLUFT = 4, ANZAHLZULUFT = 7, LUFTMENGE = 6;

        HashMap hm = new HashMap();
        String itemName = "";
        String itemName2 = "";
        String keyString[] = null;
        float itemCount = 0;
        float itemCountZU = 0;
        float luftMenge = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (((String) lmeTabelleTable.getValueAt(i, LUFTART)).contains("ZU") && index == 9) {
                itemName2 = (String) table.getValueAt(i, index - 1);
            } else {
                if (index == 5) {
                    itemName2 = "";
                } else {
                    itemName2 = (String) table.getValueAt(i, index);
                }
            }
//            System.out.println(String.format("i = %d;index =%d", i, index));
            // Nur für Abluft
            itemName = (String) table.getValueAt(i, index);
            if ((itemName != null) && (!itemName.isEmpty()) && index != 5
                    && ((String) lmeTabelleTable.getValueAt(i, LUFTART)).contains("AB")) {
                itemCount = JTableUtil.parseFloatFromTableCell(table, i, ANZAHLABLUFT);
                luftMenge = JTableUtil.parseFloatFromTableCell(table, i, LUFTMENGE);
                itemName += "AB";
                // itemName ist die Zusammensetzung aus Luftart und Verteilebene
                if (hm.containsKey(itemName)) {
                    keyString = ((String) hm.get(itemName)).split(";");
                    itemCount = itemCount + Float.parseFloat(keyString[0]);
                    luftMenge = luftMenge + Float.parseFloat(keyString[1]);
                }
                hm.put(itemName, itemCount + ";" + luftMenge);
            }
            // Nur für Zuluft
            if ((itemName2 != null) && (!itemName2.isEmpty())
                    && ((String) lmeTabelleTable.getValueAt(i, LUFTART)).contains("ZU")) {
                itemCountZU = JTableUtil.parseFloatFromTableCell(table, i, ANZAHLZULUFT);
                luftMenge = JTableUtil.parseFloatFromTableCell(table, i, LUFTMENGE);
                itemName2 += "ZU";
                if (hm.containsKey(itemName2)) {
                    keyString = ((String) hm.get(itemName2)).split(";");
                    itemCountZU = itemCountZU + Float.parseFloat(keyString[0]);
                    luftMenge = luftMenge + Float.parseFloat(keyString[1]);
                }
                hm.put(itemName2, itemCountZU + ";" + luftMenge);
            }
            // Nur für Überströmelemente
            if ((itemName != null) && (!itemName.isEmpty()) && index == 5) {
                itemCount = JTableUtil.parseFloatFromTableCell(table, i, 3);
                if (hm.containsKey(itemName)) {
                    keyString = ((String) hm.get(itemName)).split(";");
                    itemCount = itemCount + Float.parseFloat(keyString[0]);
                    luftMenge = luftMenge + Float.parseFloat(keyString[1]);
                }
                hm.put(itemName, itemCount + ";" + luftMenge);
            }
        }
        NamedValue[] nv = new NamedValue[hm.size()];
        Object[] keys = hm.keySet().toArray();
        for (int i = 0; i < hm.size(); i++) {
            nv[i] = new NamedValue((String) keys[i], hm.get(keys[i]));
        }

        return nv;
    }

    public SeeComboBox getAbAbEinfuegungswertLuftdurchlassComboBox() {
        return abAbEinfuegungswertLuftdurchlassComboBox;
    }

    public SeeComboBox getAbAbFilterverschmutzungComboBox() {
        return abAbFilterverschmutzungComboBox;
    }

    public SeeComboBox getAbAbHauptschalldaempfer1ComboBox() {
        return abAbHauptschalldaempfer1ComboBox;
    }

    public SeeComboBox getAbAbHauptschalldaempfer2ComboBox() {
        return abAbHauptschalldaempfer2ComboBox;
    }

    public SeeComboBox getAbAbKanalnetzComboBox() {
        return abAbKanalnetzComboBox;
    }

    public SeeComboBox getAbAbLaengsdaempfungKanalComboBox() {
        return abAbLaengsdaempfungKanalComboBox;
    }

    public SeeComboBox getAbAbRaumbezeichnungComboBox() {
        return abAbRaumbezeichnungComboBox;
    }

    public SeeComboBox getAbAbSchalldaempferVentilComboBox() {
        return abAbSchalldaempferVentilComboBox;
    }

    public SeeComboBox getAbAbSchallleistungspegelAbluftstutzenComboBox() {
        return abAbSchallleistungspegelAbluftstutzenComboBox;
    }

    public JTable getAbAbTabelleTable() {
        return abAbTabelleTable;
    }

    public SeeComboBox getAbZuSchallleistungspegelZuluftstutzenComboBox() {
        return abZuSchallleistungspegelZuluftstutzenComboBox;
    }

    public SeeComboBox getAbZuHauptschalldaempfer2ComboBox() {
        return abZuHauptschalldaempfer2ComboBox;
    }

    public SeeComboBox getAbZuHauptschalldaempfer1ComboBox() {
        return abZuHauptschalldaempfer1ComboBox;
    }

    public SeeComboBox getAbZuSchalldaempferVentilComboBox() {
        return abZuSchalldaempferVentilComboBox;
    }

    public SeeComboBox getAbZuLaengsdaempfungKanalComboBox() {
        return abZuLaengsdaempfungKanalComboBox;
    }

    public SeeComboBox getAbZuKanalnetzComboBox() {
        return abZuKanalnetzComboBox;
    }

    public SeeComboBox getAbZuEinfuegungswertLuftdurchlassComboBox() {
        return abZuEinfuegungswertLuftdurchlassComboBox;
    }

    public SeeComboBox getAbZuFilterverschmutzungComboBox() {
        return abZuFilterverschmutzungComboBox;
    }

    public SeeTable getAbZuTabelleTable() {
        return abZuTabelleTable;
    }

    public SeeComboBox getDvbTeilstreckenBelueftungComboBox() {
        return dvbTeilstreckenBelueftungComboBox;
    }

    public SeeComboBox getDvbTeilstreckenKanalbezeichnungComboBox() {
        return dvbTeilstreckenKanalbezeichnungComboBox;
    }

    public JTable getDvbTeilstreckenTabelleTable() {
        return dvbTeilstreckenTabelleTable;
    }

    public SeeComboBox getDvbVentileinstellungBelueftungComboBox() {
        return dvbVentileinstellungBelueftungComboBox;
    }

    public SeeComboBox getDvbVentileinstellungRaumComboBox() {
        return dvbVentileinstellungRaumComboBox;
    }

    public SeeComboBox getDvbVentileinstellungVentilbezeichnungComboBox() {
        return dvbVentileinstellungVentilbezeichnungComboBox;
    }

    public SeeTable getDvbVentileinstellungTabelleTable() {
        return dvbVentileinstellungTabelleTable;
    }

    public SeeComboBox getWfBelueftungCombobox() {
        return wfBelueftungCombobox;
    }

    public SeeComboBox getWfBezeichnungCombobox() {
        return wfBezeichnungCombobox;
    }

    public SeeComboBox getWfGeschossCombobox() {
        return wfGeschossCombobox;
    }

    public JTable getWfTabelleTable() {
        return wfTabelleTable;
    }

    public SeeComboBox getLmeZentralgeraetCombobox() {
        return lmeZentralgeraetCombobox;
    }

    public JLabel getLmeGesamtvolumenWertLabel() {
        return lmeGesamtvolumenWertLabel;
    }

    public JTable getLmeTabelleTable() {
        return lmeTabelleTable;
    }

    public JTable getLmeTabelleUeberstroemTable() {
        return lmeTabelleUeberstroemTable;
    }

    public JTable getSeebassTabelle() {
        return seebassTabelle;
    }

    public void setBZentralgeraet() {
        if (bZentralgeraet) {
            bZentralgeraetWertLabel.setText("1");
        } else {
            bZentralgeraetWertLabel.setText("0");
        }
    }

    public boolean getBZentralgeraet() {
        if (bZentralgeraetWertLabel.getText().equals("1")) {
            bZentralgeraet = true;
        } else if (bZentralgeraetWertLabel.getText().equals("0")) {
            bZentralgeraet = false;
        }
        return bZentralgeraet;
    }

    public void setLmeSummeAbluftmengeWertLabel(float abluftmenge) {
        lmeSummeAbluftmengeWertLabel.setText(nfDE.format(abluftmenge));
    }

    public void setLmeSummeZuluftmengeWertLabel(float zuluftmenge) {
        lmeSummeZuluftmengeWertLabel.setText(nfDE.format(zuluftmenge));
    }

    public void setLmeGebaeudeluftwechselWertLabel(float luftwechsel) {
        lmeGebaeudeluftwechselWertLabel.setText(nfDE.format(luftwechsel));
    }

    public void setLmeGesAussenluftVolumenWertLabel(float fGesAussen) {
        ConversionUtil.setFormattedFloatInComponent(lmeGesAussenluftmengeWertLabel, round5(fGesAussen), Locale.GERMAN);
    }

    public void setLmeGesamtvolumenWertLabel(float gesamtvolumen) {
        lmeGesamtvolumenWertLabel.setText(nfDE.format(gesamtvolumen));
    }

    public void setVsGesamtAUVolumenWertLabel(float fWert) {
        vsGesamtAUVolumenWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsVolumenstromInfilWertLabel(float fWert) {
        vsVolumenstromInfilWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsAUFeuchteschutzWertLabel(float fWert) {
        vsAUFeuchteschutzWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsAUFELuftwechselWertLabel(float fWert) {
        vsAUFELuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsAUMindestlueftungWertLabel(float fWert) {
        vsAUMindestlueftungWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsAUMLLuftwechselWertLabel(float fWert) {
        vsAUMLLuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsAUGrundlueftungWertLabel(float fWert) {
        vsAUGrundlueftungWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsAUGLLuftwechselWertLabel(float fWert) {
        vsAUGLLuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsAUIntensivlueftungWertLabel(float fWert) {
        vsAUIntensivlueftungWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsAUILLuftwechselWertLabel(float fWert) {
        vsAUILLuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsMindestlueftungWertLabel(float fWert) {
        vsMindestlueftungWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsMLLuftwechselWertLabel(float fWert) {
        vsMLLuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public float getVsGrundlueftungWertLabel() {
        float fLuft = 0f;
        if (!vsGrundlueftungWertLabel.getText().isEmpty()) {
            fLuft = ConversionUtil.parseFloatFromComponent(vsGrundlueftungWertLabel);
        }

        return fLuft;
    }

    public void setVsGrundlueftungWertLabel(float fWert) {
        vsGrundlueftungWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsGLLuftwechselWertLabel(float fWert) {
        vsGLLuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsIntensivlueftungWertLabel(float fWert) {
        vsIntensivlueftungWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsILLuftwechselWertLabel(float fWert) {
        vsILLuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsRAbFeuchteschutzWertLabel(float fWert) {
        vsRAbFeuchteschutzWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsRAbFELuftwechselWertLabel(float fWert) {
        vsRAbFELuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsRAbMindestlueftungWertLabel(float fWert) {
        vsRAbMindestlueftungWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsRAbMLLuftwechselWertLabel(float fWert) {
        vsRAbMLLuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsRAbGrundlueftungWertLabel(float fWert) {
        vsRAbGrundlueftungWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsRAbGLLuftwechselWertLabel(float fWert) {
        vsRAbGLLuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsRAbIntensivlueftungWertLabel(float fWert) {
        vsRAbIntensivlueftungWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsRAbILLuftwechselWertLabel(float fWert) {
        vsRAbILLuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsPersFeuchteschutzWertLabel(float fWert) {
        vsPersFeuchteschutzWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsPersFELuftwechselWertLabel(float fWert) {
        vsPersFELuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsPersMindestlueftungWertLabel(float fWert) {
        vsPersMindestlueftungWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsPersMLLuftwechselWertLabel(float fWert) {
        vsPersMLLuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsPersGrundlueftungWertLabel(float fWert) {
        vsPersGrundlueftungWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsPersGLLuftwechselWertLabel(float fWert) {
        vsPersGLLuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsPersIntensivlueftungWertLabel(float fWert) {
        vsPersIntensivlueftungWertLabel.setText(nfDE.format(fWert));
    }

    public void setVsPersILLuftwechselWertLabel(float fWert) {
        vsPersILLuftwechselWertLabel.setText(nfDE.format(fWert));
    }

    public void setSumLTMAbluftmengeWertLabel() {
        float fWert = berechnungen.sumLuftMenge("AB");
        lmeSumLTMAbluftmengeWertLabel.setText(nfDE.format(fWert));
    }

    public void setSumLTMZuluftmengeWertLabel() {
        float fWert = berechnungen.sumLuftMenge("ZU");
        lmeSumLTMZuluftmengeWertLabel.setText(nfDE.format(fWert));
    }

    public OfficeDocument getOfficeDocument() {
        return officeDocument;
    }

    public void setOfficeDocument(OfficeDocument officeDocument) {
        if (this.officeDocument == null) {
            this.officeDocument = officeDocument;
        }

    }

    public int getMindestluftwert() {
        return Integer.valueOf(mindestaussenluftrateWertLabel.getText());
    }

    public String getGebaeudetyp() {
        return gebaeudetypButtonGroup.getString();
    }

    public String getGebaeudelage() {
        return gebaeudelageButtonGroup.getString();
    }

    public float getWaermeschutz() {
        String sWaerme = waermeschutzButtonGroup.getString();
        float fWaerme = 0f;
        if (sWaerme.contains("hoch")) {
            fWaerme = 0.3f;
        } else {
            fWaerme = 0.4f;
        }

        return fWaerme;
    }

    public float getDiffDruck() {
        float fDruck = 0f;
        if (!ldDruckDiffTextField.getText().isEmpty()) {
            fDruck = ConversionUtil.parseFloatFromComponent(ldDruckDiffTextField);
        }

        return fDruck;
    }

    public void setDiffDruck() {
        float fDruck = 0f;
        if (getGebaeudelage().contains("schwach")) {
            fDruck = 2.0f;
        } else if (getGebaeudelage().contains("stark")) {
            fDruck = 4.0f;
        }

        ConversionUtil.setFormattedFloatInComponent(ldDruckDiffTextField, fDruck, Locale.GERMAN);
    }

    public float getLuftwechsel() {
        float fWechsel = 0f;
        if (!ldLuftwechselTextField.getText().isEmpty()) {
            fWechsel = ConversionUtil.parseFloatFromComponent(ldLuftwechselTextField);
        }

        return fWechsel;
    }

    private void setLuftwechsel() {
        float fLuft = 0f, fExpo = 0.666f;
        if (luftdichtheitButtonGroup.getString().contains("A")) {
            fLuft = 1.0f;
            ConversionUtil.setFormattedFloatInComponent(ldDruckExpoTextField, fExpo, Locale.GERMAN);
        }
        if (luftdichtheitButtonGroup.getString().contains("B")) {
            fLuft = 1.5f;
            ConversionUtil.setFormattedFloatInComponent(ldDruckExpoTextField, fExpo, Locale.GERMAN);
        }
        if (luftdichtheitButtonGroup.getString().contains("C")) {
            fLuft = 2.0f;
            ConversionUtil.setFormattedFloatInComponent(ldDruckExpoTextField, fExpo, Locale.GERMAN);
        }

        ConversionUtil.setFormattedFloatInComponent(ldLuftwechselTextField, fLuft, Locale.GERMAN);
    }

    public float getDruckExp() {
        float fDExpo = 0f;
        if (!ldDruckExpoTextField.getText().isEmpty()) {
            fDExpo = ConversionUtil.parseFloatFromComponent(ldDruckExpoTextField);
        }

        return fDExpo;
    }

    public String getLuftdicht() {
        return luftdichtheitButtonGroup.getString();
    }

    public float getGelueftetVolumen() {
        if (!geoGelueftetesVolumenTextField.getText().isEmpty()) {
            return ConversionUtil.parseFloatFromComponent(geoGelueftetesVolumenTextField);
        }

        return 0f;
    }
//Wertübernahme von Gesamtvolumen nach gelüftetes Volumen

    public void setGelueftetVolumen(float fZahl) {
        ConversionUtil.setFormattedFloatInComponent(geoGelueftetesVolumenTextField, fZahl, Locale.GERMAN);
    }
//Wertübernahme von Gesamtvolumen nach gelüftetes Volumen

    public void setGesamtVolumen(float fZahl) {
        ConversionUtil.setFormattedFloatInComponent(geoVolumenTextField, fZahl, Locale.GERMAN);
    }

    public boolean getWirkInfiltration() {
        return wirkInfiltrationCheckbox.isSelected();
    }

//    public void setWirkInfilLabel() {
//        if (getWirkInfiltration()) {
//            lmeWirkInfilWertLabel.setText("mit");
//        } else {
//            lmeWirkInfilWertLabel.setText("ohne");
//        }
//    }
    public void setEKZWertLabel() {
        if (ekZuAbluftCheckbox.isSelected() && ekBemessungCheckbox.isSelected()
                && ekRueckgewinnungCheckbox.isSelected() && ekRegelungCheckbox.isSelected()) {
            ekKennzeichenWertLabel.setText("Energiekennzeichen gesetzt!");
        } else {
            ekKennzeichenWertLabel.setText("");
        }
    }

    public void setKennzeichen() {
        String sKennzeichen = "ZuAbLS-Z-", sGeTyp = "", sEKZ = "", sHKZ = "", sRSK = "", sSS = "", sFS = "";
        if (getGebaeudetyp().contains("EFH")) {
            sGeTyp = "EFH-";
        } else {
            sGeTyp = "WE-";
        }
        if (!ekKennzeichenWertLabel.getText().isEmpty()) {
            sEKZ = "E-";
        } else {
            sEKZ = "0-";
        }
        if (!hkKennzeichenWertLabel.getText().isEmpty()) {
            sHKZ = "H-";
        } else {
            sHKZ = "0-";
        }
        if (kzRueckschlagklappeCheckbox.isSelected()) {
            sRSK = "RK-";
        } else {
            sRSK = "0-";
        }
        if (kzSchallschutzCheckbox.isSelected()) {
            sSS = "S-";
        } else {
            sSS = "0-";
        }
        if (kzFeuerstaetteCheckBox.isSelected()) {
            sFS = "F";
        } else {
            sFS = "0";
        }
        sKennzeichen = sKennzeichen + sGeTyp + "WÜT-" + sEKZ + sHKZ + sRSK + sSS + sFS;
        kzKennzeichenLabel.setText(sKennzeichen);
    }

    public void setHKZWertLabel() {
        if (hkAusfuehrungCheckbox.isSelected() && hkFilterungCheckbox.isSelected()
                && hkVerschmutzungCheckbox.isSelected() && hkDichtheitsklasseCheckbox.isSelected()) {
            hkKennzeichenWertLabel.setText("Hygienekennzeichen gesetzt!");
        } else {
            hkKennzeichenWertLabel.setText("");
        }
    }

    public float getBesAnforderFaktor() {
        return ConversionUtil.parseFloatFromComponent(bAnforderungTextField);
    }

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

    public float getHoehe() {
        return ConversionUtil.parseFloatFromComponent(wfHoeheTextField);
    }

    public void setHoehe(float fHoehe) {
        wfHoeheTextField.setText(nfDE.format(fHoehe));
    }

    public float getHoehe2() {
        return ConversionUtil.parseFloatFromComponent(geoHoeheTextField);
    }

    public void setHoehe2(float fHoehe) {
        geoHoeheTextField.setText(nfDE.format(fHoehe));
    }

    public boolean saveProject() {

        File f = null;
        @SuppressWarnings("static-access")
        String projectSaveDirectory =
                applHelper.getPreference(applHelper.PROJECT_SAVE_DIRECTORY);

        // FileChooser anzeigen
        f = getWpxFile();
        if (f == null) {
            f = Tools.saveSingleFile(applHelper.getWacMainFrame(), projectSaveDirectory);
        }
        if (f != null) {
            if (f.exists()) {
                if (Tools.askbox(Strings.OVERWRITE_PROJECT) == Tools.OPTION_OK) {
                    setWpxFile(f);

//                    jt = new JTable(raeume);
//                    jt.setName("seebassTabelle");
//                    add(jt);
                    seebassTabelle.setModel(raeume);

                    WacMainFrame.ProjektSpeichernSwingWorker saveThread = applHelper.getWacMainFrame().
                            getProjektSpeichernSwingWorker(this);
                    saveThread.start();
                    changed = 0;
                    for (PropertyChangeListener elem : getPropertyChangeListeners("Changed")) {
                        removePropertyChangeListener(elem);
                    }

                    String s = getTitle();
                    if (s.endsWith("*")) {
                        setTitle(s.substring(0, s.length() - 2));
                    }
                }
            } else {
                setWpxFile(f);

//                jt = new JTable(raeume);
//                jt.setName("seebassTabelle");
//                add(jt);
                seebassTabelle.setModel(raeume);
                seebassTabelle.setTableHeader(new JTableHeader(null));


                WacMainFrame.ProjektSpeichernSwingWorker saveThread = applHelper.getWacMainFrame().
                        getProjektSpeichernSwingWorker(this);
                saveThread.start();
                changed = 0;
                for (PropertyChangeListener elem : getPropertyChangeListeners("Changed")) {
                    removePropertyChangeListener(elem);
                }
                String s = getTitle();
                if (s.endsWith("*")) {
                    setTitle(s.substring(0, s.length() - 2));
                }
            }
            // Hilfstabelle wieder entfernen
            //remove(jt);
        }
        return f != null;
    }

    public SeeTable getWbwTabelle() {
        return dvbTeilstreckenTabelleTable;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.westaflex.component.classes.SeeTextField abAbAnzahlUmlenkungenTextField;
    protected com.westaflex.component.classes.SeeComboBox abAbEinfuegungswertLuftdurchlassComboBox;
    protected com.westaflex.component.classes.SeeComboBox abAbFilterverschmutzungComboBox;
    protected com.westaflex.component.classes.SeeComboBox abAbHauptschalldaempfer1ComboBox;
    protected com.westaflex.component.classes.SeeComboBox abAbHauptschalldaempfer2ComboBox;
    protected com.westaflex.component.classes.SeeComboBox abAbKanalnetzComboBox;
    protected com.westaflex.component.classes.SeeComboBox abAbLaengsdaempfungKanalComboBox;
    private com.westaflex.component.classes.SeeTextField abAbLaengsdaempfungKanalTextField;
    private com.westaflex.component.classes.SeeTextField abAbLuftverteilerkastenTextField;
    private javax.swing.JComboBox abAbPlaceholder1ComboBox;
    private javax.swing.JLabel abAbPlaceholder1Label;
    private javax.swing.JComboBox abAbPlaceholder2ComboBox;
    private javax.swing.JLabel abAbPlaceholder2Label;
    private javax.swing.JComboBox abAbPlaceholder3ComboBox;
    private com.westaflex.component.classes.SeeTextField abAbRaumabsorptionTextField;
    private com.westaflex.component.classes.SeeComboBox abAbRaumbezeichnungComboBox;
    protected com.westaflex.component.classes.SeeComboBox abAbSchalldaempferVentilComboBox;
    protected com.westaflex.component.classes.SeeComboBox abAbSchallleistungspegelAbluftstutzenComboBox;
    private com.westaflex.component.classes.SeeLabel abAbTabelleDezibelWertLabel;
    private com.westaflex.component.classes.SeeLabel abAbTabelleMittlererSchalldruckpegelWertLabel;
    private javax.swing.JScrollPane abAbTabelleScrollPane;
    private com.westaflex.component.classes.SeeTable abAbTabelleTable;
    private com.westaflex.component.classes.SeeLabel abAbTabelleUeberschrift1Label;
    private com.westaflex.component.classes.SeeTextField abZuAnzahlUmlenkungenTextField;
    protected com.westaflex.component.classes.SeeComboBox abZuEinfuegungswertLuftdurchlassComboBox;
    protected com.westaflex.component.classes.SeeComboBox abZuFilterverschmutzungComboBox;
    protected com.westaflex.component.classes.SeeComboBox abZuHauptschalldaempfer1ComboBox;
    protected com.westaflex.component.classes.SeeComboBox abZuHauptschalldaempfer2ComboBox;
    protected com.westaflex.component.classes.SeeComboBox abZuKanalnetzComboBox;
    protected com.westaflex.component.classes.SeeComboBox abZuLaengsdaempfungKanalComboBox;
    private com.westaflex.component.classes.SeeTextField abZuLaengsdaempfungKanalTextField;
    private com.westaflex.component.classes.SeeTextField abZuLuftverteilerkastenTextField;
    private javax.swing.JComboBox abZuPlaceholder1ComboBox;
    private javax.swing.JLabel abZuPlaceholder1Label;
    private javax.swing.JComboBox abZuPlaceholder2ComboBox;
    private javax.swing.JLabel abZuPlaceholder2Label;
    private javax.swing.JComboBox abZuPlaceholder3ComboBox;
    private com.westaflex.component.classes.SeeTextField abZuRaumabsorptionTextField;
    protected com.westaflex.component.classes.SeeComboBox abZuSchalldaempferVentilComboBox;
    protected com.westaflex.component.classes.SeeComboBox abZuSchallleistungspegelZuluftstutzenComboBox;
    private javax.swing.JLabel abZuTabelleDezibelLabel;
    private com.westaflex.component.classes.SeeLabel abZuTabelleDezibelWertLabel;
    private com.westaflex.component.classes.SeeLabel abZuTabelleMittlererSchalldruckpegelWertLabel;
    private javax.swing.JScrollPane abZuTabelleScrollPane;
    private com.westaflex.component.classes.SeeTable abZuTabelleTable;
    private com.westaflex.component.classes.SeeLabel abZuTabelleUeberschrift1Label;
    private com.westaflex.component.classes.SeeTextField adBauvorhabenTextField;
    public javax.swing.JScrollPane adNotizenScrollPane;
    private com.westaflex.component.classes.SeeTextField afAnsprechpartnerTextField;
    private com.westaflex.component.classes.SeeTextField afFaxTextField;
    private com.westaflex.component.classes.SeeTextField afFirma1TextField;
    private com.westaflex.component.classes.SeeTextField afFirma2TextField;
    private com.westaflex.component.classes.SeeTextField afPlzOrtTextField;
    private com.westaflex.component.classes.SeeTextField afStrasseTextField;
    private com.westaflex.component.classes.SeeTextField afTelefonTextField;
    private com.westaflex.component.classes.SeePanel akkustikberechnungPanel;
    private com.westaflex.component.classes.SeePanel auslegungsdatenPanel;
    private com.westaflex.component.classes.SeeButtonGroup aussenluftButtonGroup;
    private com.westaflex.component.classes.SeeTextField bAnforderungTextField;
    private com.westaflex.component.classes.SeeLabel bZentralgeraetWertLabel;
    private com.westaflex.component.classes.SeePanel druckverlustberechnungPanel;
    protected com.westaflex.component.classes.SeeComboBox dvbTeilstreckenBelueftungComboBox;
    private javax.swing.JButton dvbTeilstreckenEntfernenButton;
    private com.westaflex.component.classes.SeePanel dvbTeilstreckenHinzufuegenPanel;
    protected com.westaflex.component.classes.SeeComboBox dvbTeilstreckenKanalbezeichnungComboBox;
    private javax.swing.JTextField dvbTeilstreckenLaengeTextField;
    private javax.swing.JTextField dvbTeilstreckenLuftmengeTextField;
    private javax.swing.JTextField dvbTeilstreckenNrTextField;
    private javax.swing.JScrollPane dvbTeilstreckenTabelleScrollPane;
    private com.westaflex.component.classes.SeeTable dvbTeilstreckenTabelleTable;
    protected com.westaflex.component.classes.SeeComboBox dvbVentileinstellungBelueftungComboBox;
    protected com.westaflex.component.classes.SeeComboBox dvbVentileinstellungRaumComboBox;
    private javax.swing.JScrollPane dvbVentileinstellungTabelleScrollPane;
    private com.westaflex.component.classes.SeeTable dvbVentileinstellungTabelleTable;
    public com.westaflex.component.classes.SeeTextField dvbVentileinstellungTeilstreckenTextField;
    protected com.westaflex.component.classes.SeeComboBox dvbVentileinstellungVentilbezeichnungComboBox;
    private com.westaflex.component.classes.SeeCheckBox ekBemessungCheckbox;
    private com.westaflex.component.classes.SeeLabel ekKennzeichenWertLabel;
    private com.westaflex.component.classes.SeeCheckBox ekRegelungCheckbox;
    private com.westaflex.component.classes.SeeCheckBox ekRueckgewinnungCheckbox;
    private com.westaflex.component.classes.SeeCheckBox ekZuAbluftCheckbox;
    private com.westaflex.component.classes.SeeButtonGroup fortluftButtonGroup;
    private com.westaflex.component.classes.SeeButtonGroup gebaeudelageButtonGroup;
    private com.westaflex.component.classes.SeeButtonGroup gebaeudetypButtonGroup;
    private com.westaflex.component.classes.SeeTextField geoGeluefteteflaecheTextField;
    private com.westaflex.component.classes.SeeTextField geoGelueftetesVolumenTextField;
    private com.westaflex.component.classes.SeeTextField geoHoeheTextField;
    private com.westaflex.component.classes.SeeTextField geoVolumenTextField;
    private com.westaflex.component.classes.SeeTextField geoWohnflaecheTextField;
    private com.westaflex.component.classes.SeeButtonGroup geraetestandortButtonGroup;
    private com.westaflex.component.classes.SeeCheckBox hkAusfuehrungCheckbox;
    private com.westaflex.component.classes.SeeCheckBox hkDichtheitsklasseCheckbox;
    private com.westaflex.component.classes.SeeCheckBox hkFilterungCheckbox;
    private com.westaflex.component.classes.SeeLabel hkKennzeichenWertLabel;
    private com.westaflex.component.classes.SeeCheckBox hkVerschmutzungCheckbox;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTabbedPane jTabbedPane2;
    private com.westaflex.component.classes.SeeCheckBox kzFeuerstaetteCheckBox;
    private com.westaflex.component.classes.SeeLabel kzKennzeichenLabel;
    private com.westaflex.component.classes.SeeCheckBox kzRueckschlagklappeCheckbox;
    private com.westaflex.component.classes.SeeCheckBox kzSchallschutzCheckbox;
    private javax.swing.JTextField ldDruckDiffTextField;
    private javax.swing.JTextField ldDruckExpoTextField;
    private javax.swing.JTextField ldLuftwechselTextField;
    private com.westaflex.component.classes.SeeLabel lmeAbSummeWertLabel;
    private com.westaflex.component.classes.SeeLabel lmeFeuchteschutzWertLabel;
    protected com.westaflex.component.classes.SeeLabel lmeGebaeudeluftwechselWertLabel;
    protected com.westaflex.component.classes.SeeLabel lmeGesAussenluftmengeWertLabel;
    protected com.westaflex.component.classes.SeeLabel lmeGesamtvolumenWertLabel;
    private com.westaflex.component.classes.SeeLabel lmeGrundlueftungWertLabel;
    private com.westaflex.component.classes.SeePanel lmeInfo2Panel;
    private com.westaflex.component.classes.SeeLabel lmeIntensivlueftungWertLabel;
    private com.westaflex.component.classes.SeeLabel lmeMindestlueftungWertLabel;
    protected com.westaflex.component.classes.SeeLabel lmeSumLTMAbluftmengeWertLabel;
    private com.westaflex.component.classes.SeeLabel lmeSumLTMZuluftmengeWertLabel;
    protected com.westaflex.component.classes.SeeLabel lmeSummeAbluftmengeWertLabel;
    private com.westaflex.component.classes.SeePanel lmeSummeWertPanel;
    protected com.westaflex.component.classes.SeeLabel lmeSummeZuluftmengeWertLabel;
    private com.westaflex.component.classes.SeeTable lmeTabelleTable;
    private javax.swing.JScrollPane lmeTabelleUeberstroemScrollPane;
    private com.westaflex.component.classes.SeeTable lmeTabelleUeberstroemTable;
    private javax.swing.JScrollPane lmeTabelleZuluftScrollPane;
    private com.westaflex.component.classes.SeeLabel lmeUebSummeWertLabel;
    private com.westaflex.component.classes.SeeComboBox lmeVolumenstromCombobox;
    protected com.westaflex.component.classes.SeeComboBox lmeZentralgeraetCombobox;
    private com.westaflex.component.classes.SeeLabel lmeZuSummeWertLabel;
    private com.westaflex.component.classes.SeeButtonGroup luftdichtheitButtonGroup;
    private javax.swing.JLabel mindestaussenluftrateWertLabel;
    private javax.swing.JButton pbAngebotErstellen;
    private javax.swing.JButton pbSpeichern;
    private com.westaflex.component.classes.SeeSpinner personenanzahlSpinner;
    protected javax.swing.JTabbedPane projektTabbedPane;
    private javax.swing.JScrollPane seebassScrollPane;
    private javax.swing.JTable seebassTabelle;
    private javax.swing.JLabel stAbGeraet;
    private javax.swing.JLabel stZuGeraet;
    private com.westaflex.component.classes.SeeSpinner volumenProPersonSpinner;
    private com.westaflex.component.classes.SeeLabel vsAUFELuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsAUFeuchteschutzWertLabel;
    private com.westaflex.component.classes.SeeLabel vsAUGLLuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsAUGrundlueftungWertLabel;
    private com.westaflex.component.classes.SeeLabel vsAUILLuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsAUIntensivlueftungWertLabel;
    private com.westaflex.component.classes.SeeLabel vsAUMLLuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsAUMindestlueftungWertLabel;
    private com.westaflex.component.classes.SeeLabel vsGLLuftwechselWertLabel;
    protected com.westaflex.component.classes.SeeLabel vsGesamtAUVolumenWertLabel;
    private com.westaflex.component.classes.SeeLabel vsGrundlueftungWertLabel;
    private com.westaflex.component.classes.SeeLabel vsILLuftwechselWertLabel;
    private com.westaflex.component.classes.SeePanel vsInfo2Panel;
    private com.westaflex.component.classes.SeePanel vsInfo3Panel;
    private com.westaflex.component.classes.SeePanel vsInfo5Panel;
    private com.westaflex.component.classes.SeePanel vsInfo6Panel;
    private com.westaflex.component.classes.SeeLabel vsIntensivlueftungWertLabel;
    private com.westaflex.component.classes.SeeLabel vsLTMerforderlichWertLabel;
    private com.westaflex.component.classes.SeeLabel vsMLLuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsMindestlueftungWertLabel;
    private com.westaflex.component.classes.SeeLabel vsPersFELuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsPersFeuchteschutzWertLabel;
    private com.westaflex.component.classes.SeeLabel vsPersGLLuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsPersGrundlueftungWertLabel;
    private com.westaflex.component.classes.SeeLabel vsPersILLuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsPersIntensivlueftungWertLabel;
    private com.westaflex.component.classes.SeeLabel vsPersMLLuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsPersMindestlueftungWertLabel;
    private com.westaflex.component.classes.SeeLabel vsRAbFELuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsRAbFeuchteschutzWertLabel;
    private com.westaflex.component.classes.SeeLabel vsRAbGLLuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsRAbGrundlueftungWertLabel;
    private com.westaflex.component.classes.SeeLabel vsRAbILLuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsRAbIntensivlueftungWertLabel;
    private com.westaflex.component.classes.SeeLabel vsRAbMLLuftwechselWertLabel;
    private com.westaflex.component.classes.SeeLabel vsRAbMindestlueftungWertLabel;
    protected com.westaflex.component.classes.SeeLabel vsVolumenstromInfilWertLabel;
    private com.westaflex.component.classes.SeeButtonGroup waermeschutzButtonGroup;
    private javax.swing.JTextField wfAbluftVolumenTextField;
    private com.westaflex.component.classes.SeeComboBox wfBelueftungCombobox;
    private com.westaflex.component.classes.SeeComboBox wfBezeichnungCombobox;
    private javax.swing.JTextField wfFlaecheTextField;
    private com.westaflex.component.classes.SeeComboBox wfGeschossCombobox;
    private javax.swing.JTextField wfHoeheTextField;
    private javax.swing.JTextField wfRaumNameTextField;
    private com.westaflex.component.classes.SeeTable wfTabelleTable;
    private javax.swing.JTextField wfZuluftfaktorTextField;
    private com.westaflex.component.classes.SeeCheckBox wirkInfiltrationCheckbox;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
