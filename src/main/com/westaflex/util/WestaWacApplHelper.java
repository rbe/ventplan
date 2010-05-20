/*
 * ApplHelper.java
 *
 * Created on 23. Juli 2006, 13:32
 *
 */
package com.westaflex.util;

import com.bensmann.superswing.ApplHelper;
import com.westaflex.component.ProjectInternalFrame;
import com.westaflex.component.WacMainFrame;
import com.westaflex.database.WestaDB;
import java.awt.Component;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * $Header$
 * @author Oliver Seebass
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public class WestaWacApplHelper extends ApplHelper {

    public static final String PROJECT_LOAD_DIRECTORY = "load_dir";

    public static final String PROJECT_SAVE_DIRECTORY = "save_dir";

    /**
     */
    private static WestaWacApplHelper instance;

    /**
     *
     */
    private String configResource;

    /**
     *
     */
    private WacMainFrame wacMainFrame;

    /**
     * Extension f�r _W_estaWAC _P_rojekt _X_ML-Dateien
     */
    private String projectFileExtension = "wpx";

    /**
     */
    private NodeList alleRaeume = null;

    /**
     */
    private NodeList abluftRaeume = null;

    /**
     */
    private NodeList zuluftRaeume = null;

    /**
     */
    private NodeList alleGeschosse = null;

    /**
     */
    private NodeList alleBelueftungen = null;

    /**
     */
    private NodeList zuUndAbBelueftungen = null;

    /**
     */
    private NodeList alleLuftmengentypen = null;

    /**
     */
    private NodeList alleZentralgeraete = null;

    /**
     */
    private NodeList alleZuluftstutzen = null;

    /**
     */
    private NodeList alleAbluftstutzen = null;

    /**
     */
    private NodeList zuluftKanalnetze = null;

    /**
     */
    private NodeList abluftKanalnetze = null;

    /**
     */
    private NodeList alleFilterverschmutzung = null;

    /**
     */
    private NodeList alleSchalldaempfer = null;

    /**
     */
    private NodeList alleLaengsdaempfungen = null;

    /**
     */
    private NodeList zuluftLuftdurchlaesse = null;

    /**
     */
    private NodeList abluftLuftdurchlaesse = null;

    /**
     */
    private NodeList alleTeilkanaele = null;

    /**
     */
    private NodeList alleVentile = null;

    /**
     */
    private NodeList alleWiderstandsbeiwerte = null;

    /**
     *
     */
    private WestaWacApplHelper() {
        configResource = "/com/westaflex/resource/config/westawac.xml";
    }

    /**
     *
     * @return
     */
    public static WestaWacApplHelper getInstance() {

        if (instance == null) {
            instance = new WestaWacApplHelper();
        }

        return instance;
    }

    /**
     *
     * @throws org.jdom.JDOMException
     * @throws java.io.IOException
     */
    @Override
    public void loadXmlConfiguration() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        loadXmlConfiguration(configResource);

        // Räume
        alleRaeume = (NodeList) xpath.compile("//westawac/raum/name").
                evaluate(document, XPathConstants.NODESET);
        zuluftRaeume = (NodeList) xpath.compile("//westawac/raum/name[@zuluft='ja']").
                evaluate(document, XPathConstants.NODESET);
        abluftRaeume = (NodeList) xpath.compile("//westawac/raum/name[@abluft='ja']").
                evaluate(document, XPathConstants.NODESET);

        // Geschosse
        alleGeschosse = (NodeList) xpath.compile("//westawac/geschoss/name").
                evaluate(document, XPathConstants.NODESET);

        // Belüftung
        alleBelueftungen = (NodeList) xpath.compile("//westawac/belueftung/name").
                evaluate(document, XPathConstants.NODESET);

        zuUndAbBelueftungen = (NodeList) xpath.compile("//westawac/belueftung/name[@zuluft='ja' or @abluft='ja']").
                evaluate(document, XPathConstants.NODESET);

        // Luftmengentyp
        alleLuftmengentypen = (NodeList) xpath.compile("//westawac/luftmengentyp/modell").
                evaluate(document, XPathConstants.NODESET);

        // Zentralgerät
        alleZentralgeraete = (NodeList) xpath.compile("//westawac/zentralgeraet/modell").
                evaluate(document, XPathConstants.NODESET);

        // Zuluftstutzen
        alleZuluftstutzen = (NodeList) xpath.compile("//westawac/zuluftstutzen/modell").
                evaluate(document, XPathConstants.NODESET);

        // Abluftstutzen
        alleAbluftstutzen = (NodeList) xpath.compile("//westawac/abluftstutzen/modell").
                evaluate(document, XPathConstants.NODESET);

        // Zuluft Kanalnetz
        zuluftKanalnetze = (NodeList) xpath.compile("//westawac/kanalnetz/zuluft/druck").
                evaluate(document, XPathConstants.NODESET);

        // Abluft Kanalnetz
        abluftKanalnetze = (NodeList) xpath.compile("//westawac/kanalnetz/abluft/druck").
                evaluate(document, XPathConstants.NODESET);

        // Filterverschmutzung
        alleFilterverschmutzung = (NodeList) xpath.compile("//westawac/filterverschmutzung/druck").
                evaluate(document, XPathConstants.NODESET);

        // Schalldämpfer
        alleSchalldaempfer = (NodeList) xpath.compile("//westawac/schalldaempfer/modell").
                evaluate(document, XPathConstants.NODESET);

        // Längsdämpfung
        alleLaengsdaempfungen = (NodeList) xpath.compile("//westawac/laengsdaempfung/modell").
                evaluate(document, XPathConstants.NODESET);

        // Luftdurchlässe Zuluft
        zuluftLuftdurchlaesse = (NodeList) xpath.compile("//westawac/luftdurchlass/zuluft/modell").
                evaluate(document, XPathConstants.NODESET);

        // Luftdurchlässe Abluft
        abluftLuftdurchlaesse = (NodeList) xpath.compile("//westawac/luftdurchlass/abluft/modell").
                evaluate(document, XPathConstants.NODESET);

        // Druckverlustberechnung / Teilstrecken / Kanalbezeichnung
        alleTeilkanaele = (NodeList) xpath.compile("//westawac/teilkanal/modell").
                evaluate(document, XPathConstants.NODESET);

        // Druckverlustberechnung / Teilstrecken / Widerstandsbeiwerte
        alleWiderstandsbeiwerte = (NodeList) xpath.compile("//westawac/widerstandsbeiwerte/widerstandsbeiwert").
                evaluate(document, XPathConstants.NODESET);

        // Druckverlustberechnung / Ventileinstellung / Ventilbezeichnung
        alleVentile = (NodeList) xpath.compile("//westawac/ventil/modell").
                evaluate(document, XPathConstants.NODESET);
    }

    /**
     * Initialisiert alle ComboBoxen mit Daten aus
     * /com/westaflex/resource/westawac.xml
     *
     * @throws org.jdom.JDOMException
     */
    public void initializeComboBoxes(ProjectInternalFrame project) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
//        loadXmlConfiguration();
        // Wohnfläche / Geschoss
//        project.getWfGeschossCombobox().setModel(
//                XmlToComboBox.generateComboBox(alleGeschosse, null, "default").
//                getModel());
        // Wohnfläche / Belüftung
//        project.getWfBelueftungCombobox().setModel(
//                XmlToComboBox.generateComboBox(alleBelueftungen, null, "default").
//                getModel());
        // Luftmengenermittlung / Typenbezeichnung
//        project.getLmeTypenbezeichnungCombobox().setModel(
//                XmlToComboBox.generateComboBox(alleLuftmengentypen, "name", "default").
//                getModel());
        // Luftmengenermittlung / Zentralgerät
//        project.getLmeZentralgeraetCombobox().setModel(
//                XmlToComboBox.generateComboBox(alleZentralgeraete, "name", "default").
//                getModel());
        // Akkustikberechnung / Zuluft / Raumbezeichnung
//        project.getAbZuRaumbezeichnungComboBox().setModel(
//                XmlToComboBox.generateComboBox(zuluftRaeume).
//                getModel());
        // Akkustikberechnung / Zuluft / Zuluftstutzen
//        project.getAbZuSchallleistungspegelZuluftstutzenComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleZuluftstutzen, "name", "default").
//                getModel());
        // Akkustikberechnung / Zuluft / Kanalnetz
//        project.getAbZuKanalnetzComboBox().setModel(
//                XmlToComboBox.generateComboBox(zuluftKanalnetze, null, "default").
//                getModel());
        // Akkustikberechnung / Zuluft / FilterverschmutZung
//        project.getAbZuFilterverschmutzungComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleFilterverschmutzung, null, "default").
//                getModel());
        // Akkustikberechnung / Zuluft / 1. Hauptschalldämpfer
//        project.getAbZuHauptschalldaempfer1ComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleSchalldaempfer, "name", "zuluft-hauptschalldaempfer1-default").
//                getModel());
        // Akkustikberechnung / Zuluft / 2. Hauptschalldämpfer
//        project.getAbZuHauptschalldaempfer2ComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleSchalldaempfer, "name", "zuluft-hauptschalldaempfer2-default").
//                getModel());
        // Akkustikberechnung / Zuluft / Längsdämpfung
//        project.getAbZuLaengsdaempfungKanalComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleLaengsdaempfungen, "name", "default").
//                getModel());
        // Akkustikberechnung / Zuluft / Schalldämpfer Ventil
//        project.getAbZuSchalldaempferVentilComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleSchalldaempfer, "name", "zuluft-schalldaempfer-ventil").
//                getModel());
        // Akkustikberechnung / Zuluft / Einfügungsdämmwert Luftdurchlass
//        project.getAbZuEinfuegungswertLuftdurchlassComboBox().setModel(
//                XmlToComboBox.generateComboBox(zuluftLuftdurchlaesse, "name", "default").
//                getModel());
        // Akkustikberechnung / Abluft / Raumbezeichnung
//        project.getAbAbRaumbezeichnungComboBox().setModel(
//                XmlToComboBox.generateComboBox(abluftRaeume).
//                getModel());
        // Akkustikberechnung / Abluft / Abluftstutzen
//        project.getAbAbSchallleistungspegelAbluftstutzenComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleAbluftstutzen, "name", "default").
//                getModel());
        // Akkustikberechnung / Abluft / Kanalnetz
//        project.getAbAbKanalnetzComboBox().setModel(
//                XmlToComboBox.generateComboBox(abluftKanalnetze, null, "default").
//                getModel());
        // Akkustikberechnung / Abluft / Filterverschmutzung
//        project.getAbAbFilterverschmutzungComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleFilterverschmutzung, null, "default").
//                getModel());
        // Akkustikberechnung / Abluft / 1. Hauptschalldämpfer
//        project.getAbAbHauptschalldaempfer1ComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleSchalldaempfer, "name", "abluft-hauptschalldaempfer1-default").
//                getModel());
        // Akkustikberechnung / Abluft / 2. Hauptschalldämpfer
//        project.getAbAbHauptschalldaempfer2ComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleSchalldaempfer, "name", "abluft-hauptschalldaempfer2-default").
//                getModel());
        // Akkustikberechnung / Abluft / Längsdämpfung
//        project.getAbAbLaengsdaempfungKanalComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleLaengsdaempfungen, "name", "default").
//                getModel());
        // Akkustikberechnung / Abluft / Schalldämpfer Ventil
//        project.getAbAbSchalldaempferVentilComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleSchalldaempfer, "name", "abluft-schalldaempfer-ventil").
//                getModel());
        // Akkustikberechnung / Abluft / Einfügungsdämmwert Luftdurchlass
//        project.getAbAbEinfuegungswertLuftdurchlassComboBox().setModel(
//                XmlToComboBox.generateComboBox(abluftLuftdurchlaesse, "name", "default").
//                getModel());
        // Druckverlustberechnung / Teilstrecken / Belüftung
//        project.getDvbTeilstreckenBelueftungComboBox().setModel(
//                XmlToComboBox.generateComboBox(zuUndAbBelueftungen, null, "default").
//                getModel());
//
        // Druckverlustberechnung / Teilstrecken / Kanalbezeichnung
//        project.getDvbTeilstreckenKanalbezeichnungComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleTeilkanaele, "name", "default").
//                getModel());
        // Druckverlustberechnung / Ventileinstellung / Belüftung
//        project.getDvbVentileinstellungBelueftungComboBox().setModel(
//                XmlToComboBox.generateComboBox(zuUndAbBelueftungen, null, "default").
//                getModel());
        // Druckverlustberechnung / Ventileinstellung / Raum
//        project.getDvbVentileinstellungRaumComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleRaeume, null, "default").
//                getModel());
        // Druckverlustberechnung / Ventileinstellung / Ventilbezeichnung
//        project.getDvbVentileinstellungVentilbezeichnungComboBox().setModel(
//                XmlToComboBox.generateComboBox(alleVentile, "name", "default").
//                getModel());
    }

    /**
     *
     * @param project
     * @throws org.jdom.JDOMException
     * @throws java.io.IOException
     */
    public void initializeTables(ProjectInternalFrame project) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        loadXmlConfiguration();

//        // Druckverlustberechnung / Teilstrecken / Widerstandsbeiwerte bearbeiten
//        project.getDvbWiderstandsbeiwerteTabelleTable().setModel(
//                XmlToTable.generateTable(
//                alleWiderstandsbeiwerte,
//                new String[] {"Anzahl", "Bezeichnung", "Wert"}).
//                getModel());
    }

    public String getProjectFileExtension() {
        return projectFileExtension;
    }

    public void setWacMainFrame(WacMainFrame wacMainFrame) {
        this.wacMainFrame = wacMainFrame;
    }

    public WacMainFrame getWacMainFrame() {
        return wacMainFrame;
    }

    public NodeList getAlleWiderstandsbeiwerte() {
        return alleWiderstandsbeiwerte;
    }

    public int getAnzahlWiderstandsbeiwerte() {
        return alleWiderstandsbeiwerte.getLength();
    }

    public Element getWiderstandsbeiwertElement(int row) {
        return (Element) alleWiderstandsbeiwerte.item(row);
    }

    public Element getZentralgeraeteElement(int row) {
        return (Element) alleZentralgeraete.item(row);
    }

    public Element getLuftmengentypElement(String modell) {

        XPathExpression expr = null;
        Element element = null;
        NodeList nodeList = null;

        if (modell != null && modell.length() > 0) {

            try {

                expr = xpath.compile("//westawac/luftmengentyp/modell[@name='" + modell + "']");
                nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
                element = (Element) nodeList.item(0);

                System.out.println("name=" + element.getAttribute("name"));
            } catch (XPathExpressionException ex) {
                ex.printStackTrace();
            }
        }

        return element;
    }

    /**
     * Suche Element mit Attribut 'name' = name und lese alle Werte
     * der <oktavmf>-Elemente aus und schreibe sie in einen Array
     *
     * @param name
     * @return
     */
    public float[] getOktavmittenfrequenz(String prefix, String name) {

        float[] array = null;
        XPathExpression expr = null;
        Element element = null;
        NodeList nodeList = null;

        try {

            expr = xpath.compile(prefix + "[@name='" + name + "']/oktavmf");
            nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            array = new float[nodeList.getLength()];

            for (int i = 0; i < nodeList.getLength(); i++) {

                element = (Element) nodeList.item(i);

                try {
                    array[i] = Float.valueOf(element.getTextContent());
                } catch (NumberFormatException e) {
                    array[i] = 0.0f;
                }
            }
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
        }

        return array;
    }

    /**
     *
     * @param name
     * @return
     */
    public float getDezibel(String name) {

        XPathExpression expr = null;
        Float f = null;

        try {

            expr = xpath.compile("//*[@name='" + name + "']/dezibel/text()");
            f = ((Double) expr.evaluate(document, XPathConstants.NUMBER)).floatValue();
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
        }

        return f;
    }

    /**
     *
     * @param teilstreckenName
     * @return m=float[0], dH=float[1], A=float[2]
     */
    public float[] getTeilkanalWerte(String teilstreckenName) {
        float[] f = new float[3];

        /*  WORKAROUND  */
        f[0] = f[1] = f[2] = 1;
        /* TODO Seebass
        try {
        expr = xpath.compile("//westawac/teilkanal/modell[@name='" + teilstreckenName + "']/werte");
        nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
        element = (Element) nodeList.item(i);
        name = element.getAttribute("name");
        if (name.equals("m")) {
        f[0] = Float.valueOf(element.getAttribute("value"));
        } else if (name.equals("dH")) {
        f[1] = Float.valueOf(element.getAttribute("value"));
        } else if (name.equals("A")) {
        f[2] = Float.valueOf(element.getAttribute("value"));
        }
        }
        } catch (XPathExpressionException ex) {
        ex.printStackTrace();
        }
         */
        return f;
    }

    /**
     *
     * @return
     */
    public int getAnzahlVentile() {
        return alleVentile.getLength();
    }

    public Element getVentil(String name, boolean b) {

        XPathExpression expr = null;
        Element element = null;

        // Suffix abschneiden (z.B. " ZU")
//        name = name.substring(0, name.lastIndexOf(" "));
        try {

            expr = xpath.compile("//westawac/ventil/modell[@name='" + name + "']");
            element = (Element) expr.evaluate(document, XPathConstants.NODE);
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
        }

        return element;
    }

    /**
     *
     * @param ventil
     * @return
     */
    public Integer[] getVentilLuftmengen(Element ventil) {

        List<Integer> luftmengen = new LinkedList<Integer>();
        NodeList children = ventil.getChildNodes();
        Element element = null;

        for (int i = 0; i < children.getLength(); i++) {

            element = (Element) children.item(i);
            luftmengen.add(Integer.valueOf(element.getAttribute("wert")));
        }

        return luftmengen.toArray(new Integer[luftmengen.size()]);
    }

    public Vector<String> askConfig(String nodepath, String defaultAttribute) {

        XPathExpression expr = null;
        NodeList nodes = null;
        Vector<String> ret = new Vector<String>();

        System.out.format("Suchstring = %s%n", nodepath);
        try {
            expr = xpath.compile(nodepath);
            nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                ret.add(nodes.item(i).getTextContent());
            }
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
        } finally {
            return ret;
        }
    }

    public float[] getOktavmittenfrequenz(String artnr, String volumenstrom, String zuabex) {
        final String sql = "select s.~125~, s.~250~, s.~500~, s.~1000~, s.~2000~, s.~4000~, s.~dbA~" + " from ~schalleistungspegel~ s" + " where ~Artikelnummer~ = '$s1' and ~Volumenstrom~ >= $s2 and ~ZuAbEx~ = '$s3'";
        int i;
        StringBuffer sOmv = new StringBuffer(sql);

        // 1. Artikelnummer einfügen
        i = sOmv.lastIndexOf("$s1");
        sOmv.delete(i, i + 3);
        sOmv.insert(i, artnr);

        // 2. Volumenstrom einfügen
        i = sOmv.lastIndexOf("$s2");
        sOmv.delete(i, i + 3);
        sOmv.insert(i, volumenstrom);

        // 3. ZuAbEx versorgen
        i = sOmv.lastIndexOf("$s3");
        sOmv.delete(i, i + 3);
        if (zuabex.equalsIgnoreCase("zu")) {
            sOmv.insert(i, "0");
        } else if (zuabex.equalsIgnoreCase("ab")) {
            sOmv.insert(i, "1");
        } else if (zuabex.equalsIgnoreCase("ex")) {
            sOmv.insert(i, "2");
        }
//System.out.println(sOmv);
        return WestaDB.getInstance().queryDBResultRowAsFloat(sOmv.toString());
    }

    public float[] getOktavmittenfrequenz(String artnr, int factor) {

        float[] f = getOktavmittenfrequenz(artnr);
        for (int i = 0; i < f.length; i++) {
            f[i] *= factor;
        }
        return f;
    }

    public float[] getOktavmittenfrequenz(String artnr) {
        final String sql = "select s.~125~, s.~250~, s.~500~, s.~1000~, s.~2000~, s.~4000~" + " from ~schalleistungspegel~ s where ~Artikelnummer~ = '$s1'";
        int i;
        StringBuffer sOmv = new StringBuffer(sql);
        i = sOmv.lastIndexOf("$s1");
        sOmv.delete(i, i + 3);
        sOmv.insert(i, artnr);

        return WestaDB.getInstance().queryDBResultRowAsFloat(sOmv.toString());
    }

    public float[] getPegelerhoehungExternerDruck(String sArtikelnummer) {

        final String sql = "select s.~125~, s.~250~, s.~500~, s.~1000~, s.~2000~, s.~4000~ " + "from ~schalleistungspegel~ s where ~ZuAbEx~ = 2 and ~Artikelnummer~ = '";

        return WestaDB.getInstance().queryDBResultRowAsFloat(sql + sArtikelnummer + "'");
    }

    public String getWiderstandsBeiwertBild(String sPicPath) {

        return null;
    }

    public ProjectInternalFrame getMyInternalFrame(Object comp) {

        Component c = ((Component) comp).getParent();

        while ((c != null) && !(c instanceof ProjectInternalFrame)) {
            c = c.getParent();
        }
        return (ProjectInternalFrame) c;
    }

    public void setStatusText(Object... action) {
        getWacMainFrame().setStatusText(action);
    }

    void setProgressBar(boolean b) {
        getWacMainFrame().setProgressBar(b);
    }

}
