/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wacconverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.zip.GZIPOutputStream;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author manuel
 */
public class WacConverter {

    private File griffonXmlFile;
    private File outputFile;

    /*
    public static void main(String[] args)
    {
        File griffonFile = new File("/home/manuel/Desktop/testprojekt_6.wpx");
        File outputFile = new File("/home/manuel/Desktop");
        WacConverter wacConverter = new WacConverter(griffonFile, outputFile);
        wacConverter.parse();
    }
    */

    /**
     * If the outputFile value is a folder, the file will be saved under the
     * griffonXmlFile name with the current time in millis as prefix.
     *
     * @param griffonXmlFile The wpx file from new griffon application.
     * @param outputFile The folder or file where to save the output.
     */
    public WacConverter(File griffonXmlFile, File outputFile)
    {
        this.griffonXmlFile = griffonXmlFile;
        this.outputFile = outputFile;
    }

    /**
     * Parse the documents.
     */
    public void parse()
    {
        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        Document griffonDoc = null;
        Document oldWacDoc = null;
        InputStream is = null;
        try
        {
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();

            // load old wac document as template...
            // TODO: create template and save it in this lib...
            is = getClass().getResourceAsStream("template.xml");
            oldWacDoc = builder.parse(is);

            if (null != oldWacDoc)
            {
                // load griffon document to get the values from
                griffonDoc = builder.parse(griffonXmlFile);

                replaceAllValues(griffonDoc, oldWacDoc);


                if (outputFile.isDirectory())
                {
                    String fullFolderPath = outputFile.getAbsolutePath();
                    String filename = System.currentTimeMillis() + "_" + griffonXmlFile.getName();
                    outputFile = new File(fullFolderPath, filename);
                }
                boolean gzip = true;
                writeDocument(oldWacDoc, outputFile, gzip);
            }
            else
            {
                System.out.println("oldWacDoc is null...");
            }
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(WacConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Replace the value from oldWacExpression with the value from griffonExpression.
     * @param griffonExpression
     * @param griffonDoc
     * @param oldWacExpression
     * @param oldWacDoc
     */
    private void replaceValue(String griffonExpression, Document griffonDoc, String oldWacExpression, Document oldWacDoc)
    {
        Object griffonResult = createXPathExpression(griffonExpression, griffonDoc, XPathConstants.NODE);
        Node griffonNode = (Node) griffonResult;

        Object oldWacResult = createXPathExpression(oldWacExpression, oldWacDoc, XPathConstants.NODE);
        Node oldWacNode = (Node) oldWacResult;
        String replaceValue = getTextContentFromNode(griffonNode);
        if (null == replaceValue)
        {
            replaceValue = "";
        }
        oldWacNode.getAttributes().getNamedItem("value").setTextContent(replaceValue);
    }

    /**
     * Replace the value from oldWacExpression with the value from griffonExpression.
     * @param replaceValue
     * @param oldWacExpression
     * @param oldWacDoc
     */
    private void replaceValueFromString(String replaceValue, String oldWacExpression, Document oldWacDoc)
    {
        Object oldWacResult = createXPathExpression(oldWacExpression, oldWacDoc, XPathConstants.NODE);
        Node oldWacNode = (Node) oldWacResult;

        oldWacNode.getAttributes().getNamedItem("value").setTextContent(replaceValue);
    }

    /**
     * Create a xpath expression, evaluate it against the given document and return
     * the result object depending on the given qname value.
     * @param expression
     * @param doc
     * @param qname
     * @return
     */
    private Object createXPathExpression(String expression, Document doc, QName qname)
    {
        Object result = null;
        try
        {
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile(expression);
            result = expr.evaluate(doc, qname);
        }
        catch (XPathExpressionException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Save the document (oldWacDocument) to the given file.
     * @param document
     * @param saveToFile
     */
    private void writeDocument(Document document, File saveToFile, boolean gzip)
    {
        try
        {
            // Use a Transformer for output
            TransformerFactory tFactory =
                    TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            FileOutputStream fos = new FileOutputStream(saveToFile);

            OutputStream os = null;
            if (gzip)
            {
                GZIPOutputStream gos = new GZIPOutputStream(fos);
                os = gos;
                transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-15");

                transformer.transform(
                        new DOMSource(document), new StreamResult(os));

                os.close();
                fos.close();
            }
            else
            {
                transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-15");

                transformer.transform(
                        new DOMSource(document), new StreamResult(fos));

                fos.close();
            }

            


        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            System.out.println("* IOException: " + ex.getMessage());
            ex.printStackTrace();
        }
        catch (TransformerConfigurationException tce)
        {
            // Error generated by the parser
            System.out.println("* Transformer Factory error");
            System.out.println("  " + tce.getMessage());

            // Use the contained exception, if any
            Throwable x = tce;
            if (tce.getException() != null)
                x = tce.getException();
            x.printStackTrace();
        }
        catch (TransformerException te)
        {
            // Error generated by the parser
            System.out.println("* Transformation error");
            System.out.println("  " + te.getMessage());

            // Use the contained exception, if any
            Throwable x = te;
            if (te.getException() != null)
                x = te.getException();
            x.printStackTrace();

        }

    }

    /**
     * Get text from node.
     * @param expression
     * @param oldWacDoc
     * @return
     */
    private String getTextContentFromNode(String expression, Document oldWacDoc, boolean convert)
    {
        System.out.println("oldWacDoc -> " + oldWacDoc + "::: expression -> " + expression);
        Object resultObject = createXPathExpression(expression, oldWacDoc, XPathConstants.NODE);
        if (null != resultObject)
        {
            System.out.println("resultObject is not null");
            Node node = (Node) resultObject;
            return getTextContentFromNode(node, convert);
        }
        return "";
    }

    /**
     * Get text from node.
     * @param expression
     * @param oldWacDoc
     * @return
     */
    private String getTextContentFromNode(String expression, Document oldWacDoc)
    {
        System.out.println("oldWacDoc -> " + oldWacDoc + "::: expression -> " + expression);
        Object resultObject = createXPathExpression(expression, oldWacDoc, XPathConstants.NODE);
        if (null != resultObject)
        {
            System.out.println("resultObject is not null");
            Node node = (Node) resultObject;
            return getTextContentFromNode(node, true);
        }
        return "";
    }

    private String getTextContentFromNode(Node node)
    {
        return getTextContentFromNode(node, true);

    }

    private String getTextContentFromNode(Node node, boolean convert)
    {
        if (null != node)
        {
            try
            {
                String text = node.getTextContent();
                System.out.println("nodeValue -> " + node.getNodeValue());
                System.out.println("textContent -> " + node.getTextContent());
                // check for numeric value...
                if (convert && null != text && !text.isEmpty())
                {
                    try
                    {
                        Matcher m = Constants.pattern.matcher(text);
                        if (m.matches())
                        {
                            text = text.replace(".", ",");
                        }
                    }
                    catch (Exception e)
                    {

                    }
                }

                return text;
            }
            catch (Exception e)
            {
                return "";
            }
        }
        return "";
    }

    private String replaceRaumTypValue(String value)
    {
        if (value.equals(Constants.WOH))
        {
            value = Constants.WOHNZIMMER;
        }
        else if (value.equals(Constants.KIN))
        {
            value = Constants.KINDERZIMMER;
        }
        else if (value.equals(Constants.SLF))
        {
            value = Constants.SCHLAFZIMMER;
        }
        else if (value.equals(Constants.ESS))
        {
            value = Constants.ESSZIMMER;
        }
        else if (value.equals(Constants.ARB))
        {
            value = Constants.ARBEITSZIMMER;
        }
        else if (value.equals(Constants.GAS))
        {
            value = Constants.GAESTEZIMMER;
        }
        else if (value.equals(Constants.HAU))
        {
            value = Constants.HAUSARBEITSRAUM;
        }
        else if (value.equals(Constants.KEL))
        {
            value = Constants.KELLERRAUM;
        }
        else if (value.equals(Constants.KUC))
        {
            value = Constants.KUECHE;
        }
        else if (value.equals(Constants.BAD2))
        {
            value = Constants.BAD;
        }
        else if (value.equals(Constants.DUS))
        {
            value = Constants.DUSCHRAUM;
        }
        else if (value.equals(Constants.SAU))
        {
            value = Constants.SAUNA;
        }
        else if (value.equals(Constants.FLU))
        {
            value = Constants.FLUR;
        }
        else if (value.equals(Constants.DIE))
        {
            value = Constants.DIELE;
        }
        return value;
    }
    

    /**
     * TODO: replace all values...
     * @param griffonDoc
     * @param oldWacDoc
     */
    private void replaceAllValues(Document griffonDoc, Document oldWacDoc)
    {
        replaceGebaudeValues(griffonDoc, oldWacDoc);

        replaceGeometrieValues(griffonDoc, oldWacDoc);

        replaceRaumValues(griffonDoc, oldWacDoc);

        replaceZentralgeratValues(griffonDoc, oldWacDoc);

        replaceFirmaValues(griffonDoc, oldWacDoc);

        replaceDvbVentilEinstellungenValues(griffonDoc, oldWacDoc);
    }

    private void replaceDvbVentilEinstellungenValues(Document griffonDoc, Document oldWacDoc)
    {
        /*
        <component name="dvbVentileinstellungTabelleTable" type="com.westaflex.component.classes.SeeTable">
            <tablemodel columns="9" rows="0" selected-row="-1" type="javax.swing.table.DefaultTableModel">
                <column-header>
                    <column cell-editor="javax.swing.JTable$GenericEditor" index="0" name="Bel."/>
                    <column cell-editor="javax.swing.JTable$GenericEditor" index="1" name="Raum"/>
                    <column cell-editor="javax.swing.JTable$GenericEditor" index="2" name="Teilstrecken"/>
                    <column cell-editor="javax.swing.JTable$GenericEditor" index="3" name="Ventiltyp"/>
                    <column cell-editor="javax.swing.JTable$GenericEditor" index="4" name="dP Pa (offen)"/>
                    <column cell-editor="javax.swing.JTable$GenericEditor" index="5" name="Gesamt Pa"/>
                    <column cell-editor="javax.swing.JTable$GenericEditor" index="6" name="Differenz"/>
                    <column cell-editor="javax.swing.JTable$GenericEditor" index="7" name="Abgleich Pa"/>
                    <column cell-editor="javax.swing.JTable$GenericEditor" index="8" name="Einstellung"/>
                </column-header>
            </tablemodel>
        </component>
         */
    }

    private void replaceGebaudeValues(Document griffonDoc, Document oldWacDoc)
    {
        String ldKatAValue = "false";
        String ldKatBValue = "false";
        String ldKatCValue = "false";

        Object luftDichtheitGriffonResult = createXPathExpression("//gebaude/luftdichtheit/text()", griffonDoc, XPathConstants.NODE);
        Node luftDichtheitGriffonNode = (Node) luftDichtheitGriffonResult;
        String luftDichtheitValue = getTextContentFromNode(luftDichtheitGriffonNode);
        if (luftDichtheitValue.equalsIgnoreCase("A"))
        {
            ldKatAValue = "true";
        }
        else if (luftDichtheitValue.equalsIgnoreCase("B"))
        {
            ldKatBValue = "true";
        }
        else if (luftDichtheitValue.equalsIgnoreCase("C"))
        {
            ldKatCValue = "true";
        }
        replaceValueFromString(ldKatAValue,
                               "//component[@name='ldKatARadioButton']",
                               oldWacDoc);
        replaceValueFromString(ldKatBValue,
                               "//component[@name='ldKatBRadioButton']",
                               oldWacDoc);
        replaceValueFromString(ldKatCValue,
                               "//component[@name='ldKatCRadioButton']",
                               oldWacDoc);

        if (ldKatAValue.equals("false") && ldKatBValue.equals("false") && ldKatBValue.equals("false"))
        {

            String ldMessValue = "false";
            Object ldMessGriffonResult = createXPathExpression("//gebaude/luftdichtheitDruckdifferenz/text()", griffonDoc, XPathConstants.NODE);
            Node ldMessGriffonNode = (Node) ldMessGriffonResult;
            String ldMessStringValue = getTextContentFromNode(ldMessGriffonNode);
            if (null != ldMessStringValue && ldMessStringValue.trim().length() > 0 && !ldMessStringValue.trim().equals(""))
            {
                ldMessValue = "true";
            }
            replaceValueFromString(ldMessValue,
                                   "//component[@name='ldMessRadioButton']",
                                   oldWacDoc);
        }
        else if (ldKatAValue.equals("true") || ldKatBValue.equals("true") || ldKatBValue.equals("true"))
        {
            replaceValueFromString("false",
                                   "//component[@name='ldMessRadioButton']",
                                   oldWacDoc);
        }

        /*
        <luftdichtheitDruckdifferenz>2.0</luftdichtheitDruckdifferenz>
        <luftdichtheitLuftwechsel>1.0</luftdichtheitLuftwechsel>
        <luftdichtheitDruckexponent>0.666</luftdichtheitDruckexponent>
         */

        String mfhValue = "false";
        String efhValue = "false";
        String maisonetteValue = "false";

        Object gebaudeTypGriffonResult = createXPathExpression("//gebaude/gebaudeTyp/text()", griffonDoc, XPathConstants.NODE);
        Node gebaudeTypGriffonNode = (Node) gebaudeTypGriffonResult;
        String gebaudeTypValue = getTextContentFromNode(gebaudeTypGriffonNode);
        if (gebaudeTypValue.equalsIgnoreCase("MFH"))
        {
            mfhValue = "true";
        }
        else if (gebaudeTypValue.equalsIgnoreCase("EFH"))
        {
            efhValue = "true";
        }
        else if (gebaudeTypValue.equalsIgnoreCase("MAI"))
        {
            maisonetteValue = "true";
        }
        replaceValueFromString(mfhValue,
                               "//component[@name='gtMFHRadioButton']",
                               oldWacDoc);
        replaceValueFromString(efhValue,
                               "//component[@name='gtEFHRadioButton']",
                               oldWacDoc);
        replaceValueFromString(maisonetteValue,
                               "//component[@name='gtMaisonetteRadioButton']",
                               oldWacDoc);


        String schwachValue = "false";
        String starkValue = "false";

        Object gebaudeLageGriffonResult = createXPathExpression("//gebaude/gebaudeLage/text()", griffonDoc, XPathConstants.NODE);
        Node gebaudeLageGriffonNode = (Node) gebaudeLageGriffonResult;
        String gebaudeLageValue = getTextContentFromNode(gebaudeLageGriffonNode);
        if (gebaudeLageValue.equalsIgnoreCase("SCH"))
        {
            schwachValue = "true";
        }
        else if (gebaudeLageValue.equalsIgnoreCase("STA"))
        {
            starkValue = "true";
        }
        replaceValueFromString(schwachValue,
                               "//component[@name='glWschwachRadioButton']",
                               oldWacDoc);
        replaceValueFromString(starkValue,
                               "//component[@name='glWstarkRadioButton']",
                               oldWacDoc);


        String hochValue = "false";
        String niedrigValue = "false";

        Object warmeschutzGriffonResult = createXPathExpression("//gebaude/warmeschutz/text()", griffonDoc, XPathConstants.NODE);
        Node warmeschutzGriffonNode = (Node) warmeschutzGriffonResult;
        String warmeschutzValue = getTextContentFromNode(warmeschutzGriffonNode);
        if (warmeschutzValue.equalsIgnoreCase("HOC"))
        {
            hochValue = "true";
        }
        else if (warmeschutzValue.equalsIgnoreCase("NIE"))
        {
            niedrigValue = "true";
        }
        replaceValueFromString(hochValue,
                               "//component[@name='wsHochRadioButton']",
                               oldWacDoc);
        replaceValueFromString(niedrigValue,
                               "//component[@name='wsNiedrigRadioButton']",
                               oldWacDoc);


        replaceValue("//gebaude/besAnfFaktor/text()", griffonDoc,
                     "//component[@name='bAnforderungTextField']", oldWacDoc);

        replaceValue("//gebaude/personenAnzahl/text()", griffonDoc,
                     "//component[@name='personenanzahlSpinner']", oldWacDoc);
        replaceValue("//gebaude/personenAnzahl/text()", griffonDoc,
                     "//component[@name='personenanzahlSpinner']/spinnermodel/item", oldWacDoc);

        String personenAnzahl = getTextContentFromNode("//gebaude/personenAnzahl/text()", griffonDoc);
        String personenVolumen = getTextContentFromNode("//gebaude/personenVolumen/text()", griffonDoc);
        try
        {
            int persAnzahlAsInt = 0;
            if (null != personenAnzahl && !personenAnzahl.trim().equals(""))
            {
                persAnzahlAsInt = Integer.parseInt(personenAnzahl);
            }
            
            int persVolumenAsInt = 0;
            if (null != personenVolumen && !personenVolumen.trim().equals(""))
            {
                persVolumenAsInt = Integer.parseInt(personenVolumen);
            }

            personenVolumen = "" + (persVolumenAsInt / persAnzahlAsInt);
        }
        catch (NumberFormatException e)
        {
            personenVolumen = "";
        }
        replaceValueFromString(personenVolumen,
                     "//component[@name='volumenProPersonSpinner']", oldWacDoc);
        replaceValueFromString(personenVolumen,
                     "//component[@name='volumenProPersonSpinner']/spinnermodel/item", oldWacDoc);


        //replaceValue("//gebaude/personenVolumen/text()", griffonDoc,
        //             "//component[@name='volumenProPersonSpinner']", oldWacDoc);
        //replaceValue("//gebaude/personenVolumen/text()", griffonDoc,
        //             "//component[@name='volumenProPersonSpinner']/spinnermodel/item", oldWacDoc);

        String gsKellergeschossRadioButton = "false";
        String gsErdgeschossRadioButton = "false";
        String gsObergeschossRadioButton = "false";
        String gsDachgeschossRadioButton = "false";
        String gsSpitzbodenRadioButton = "false";
        Object geratestandortGriffonResult = createXPathExpression("//zentralgerat/geratestandort/text()", griffonDoc, XPathConstants.NODE);
        Node geratestandortGriffonNode = (Node) geratestandortGriffonResult;
        String geratestandortValue = getTextContentFromNode(geratestandortGriffonNode);

        if (geratestandortValue.equalsIgnoreCase("KG"))
        {
            gsKellergeschossRadioButton = "true";
        }
        else if (geratestandortValue.equalsIgnoreCase("EG"))
        {
            gsErdgeschossRadioButton = "true";
        }
        else if (geratestandortValue.equalsIgnoreCase("OG"))
        {
            gsObergeschossRadioButton = "true";
        }
        else if (geratestandortValue.equalsIgnoreCase("DG"))
        {
            gsDachgeschossRadioButton = "true";
        }
        else if (geratestandortValue.equalsIgnoreCase("SB"))
        {
            gsSpitzbodenRadioButton = "true";
        }
        replaceValueFromString(gsKellergeschossRadioButton,
                               "//component[@name='gsKellergeschossRadioButton']",
                               oldWacDoc);
        replaceValueFromString(gsErdgeschossRadioButton,
                               "//component[@name='gsErdgeschossRadioButton']",
                               oldWacDoc);
        replaceValueFromString(gsObergeschossRadioButton,
                               "//component[@name='gsObergeschossRadioButton']",
                               oldWacDoc);
        replaceValueFromString(gsDachgeschossRadioButton,
                               "//component[@name='gsDachgeschossRadioButton']",
                               oldWacDoc);
        replaceValueFromString(gsSpitzbodenRadioButton,
                               "//component[@name='gsSpitzbodenRadioButton']",
                               oldWacDoc);



        String lzTellerventileCheckbox = "false";
        String lzSchlitzauslassCheckbox = "false";
        String lzFussbodenAuslassCheckbox = "false";
        String lzSockelquellauslassCheckbox = "false";

        Object zuluftdurchlasseGriffonResult = createXPathExpression("//gebaude/zuluftdurchlasse", griffonDoc, XPathConstants.NODESET);
        if (null != zuluftdurchlasseGriffonResult)
        {
            NodeList zdlNodeList = (NodeList) zuluftdurchlasseGriffonResult;
            int count = zdlNodeList.getLength();
            for (int i = 0; i < count; i++)
            {
                Node node = zdlNodeList.item(i);
                String text = getTextContentFromNode(node);
                if (null != text && text.equalsIgnoreCase("TEL"))
                {
                    lzTellerventileCheckbox = "true";
                }
                else if (null != text && text.equalsIgnoreCase("SCH"))
                {
                    lzSchlitzauslassCheckbox = "true";
                }
                else if (null != text && text.equalsIgnoreCase("FUS"))
                {
                    lzFussbodenAuslassCheckbox = "true";
                }
                else if (null != text && text.equalsIgnoreCase("SOC"))
                {
                    lzSockelquellauslassCheckbox = "true";
                }
            }

            replaceValueFromString(lzTellerventileCheckbox,
                                   "//component[@name='lzTellerventileCheckbox']",
                                   oldWacDoc);
            replaceValueFromString(lzSchlitzauslassCheckbox,
                                   "//component[@name='lzSchlitzauslassCheckbox']",
                                   oldWacDoc);
            replaceValueFromString(lzFussbodenAuslassCheckbox,
                                   "//component[@name='lzFussbodenAuslassCheckbox']",
                                   oldWacDoc);
            replaceValueFromString(lzSockelquellauslassCheckbox,
                                   "//component[@name='lzSockelquellauslassCheckbox']",
                                   oldWacDoc);
        }

        String lkAufputzCheckbox = "false";
        String lkDaemmschichtCheckbox = "false";
        String lkDeckeCheckbox = "false";
        String lkSpitzbodenCheckbox = "false";

        Object lkDurchlassGriffonResult = createXPathExpression("//gebaude/luftkanalverlegung", griffonDoc, XPathConstants.NODESET);
        if (null != lkDurchlassGriffonResult)
        {
            NodeList lkdNodeList = (NodeList) lkDurchlassGriffonResult;
            int count = lkdNodeList.getLength();
            for (int i = 0; i < count; i++)
            {
                Node node = lkdNodeList.item(i);
                String text = getTextContentFromNode(node);
                if (null != text && text.equalsIgnoreCase("AUF"))
                {
                    lkAufputzCheckbox = "true";
                }
                else if (null != text && text.equalsIgnoreCase("DAM"))
                {
                    lkDaemmschichtCheckbox = "true";
                }
                else if (null != text && text.equalsIgnoreCase("DEC"))
                {
                    lkDeckeCheckbox = "true";
                }
                else if (null != text && text.equalsIgnoreCase("SPI"))
                {
                    lkSpitzbodenCheckbox = "true";
                }
            }

            replaceValueFromString(lkAufputzCheckbox,
                                   "//component[@name='lkAufputzCheckbox']",
                                   oldWacDoc);
            replaceValueFromString(lkDaemmschichtCheckbox,
                                   "//component[@name='lkDaemmschichtCheckbox']",
                                   oldWacDoc);
            replaceValueFromString(lkDeckeCheckbox,
                                   "//component[@name='lkDeckeCheckbox']",
                                   oldWacDoc);
            replaceValueFromString(lkSpitzbodenCheckbox,
                                   "//component[@name='lkSpitzbodenCheckbox']",
                                   oldWacDoc);
        }


        String laTellerventileCheckbox = "false";
        String abluftDurchlassGriffonResult = getTextContentFromNode("//gebaude/abluftdurchlasse", griffonDoc);
        if (null != abluftDurchlassGriffonResult && abluftDurchlassGriffonResult.equalsIgnoreCase("TEL"))
        {
            laTellerventileCheckbox = "true";
        }
        replaceValueFromString(laTellerventileCheckbox,
                               "//component[@name='laTellerventileCheckbox']",
                               oldWacDoc);


        String flDachdurchfuehrungRadioButton = "false";
        String flWandRadioButton = "false";
        String flLichtschachtRadioButton = "false";
        String fortluftValue = "false";
        String fortluftGriffonResult = getTextContentFromNode("//gebaude/fortluft", griffonDoc);
        if (null != fortluftGriffonResult && fortluftGriffonResult.equalsIgnoreCase("DAC"))
        {
            flDachdurchfuehrungRadioButton = "true";
        }
        else if (null != fortluftGriffonResult && fortluftGriffonResult.equalsIgnoreCase("WAN"))
        {
            flWandRadioButton = "true";
        }
        else if (null != fortluftGriffonResult && fortluftGriffonResult.equalsIgnoreCase("LIC"))
        {
            flLichtschachtRadioButton = "true";
        }
        replaceValueFromString(flDachdurchfuehrungRadioButton,
                               "//component[@name='flDachdurchfuehrungRadioButton']",
                               oldWacDoc);
        replaceValueFromString(flWandRadioButton,
                               "//component[@name='flWandRadioButton']",
                               oldWacDoc);
        replaceValueFromString(flLichtschachtRadioButton,
                               "//component[@name='flLichtschachtRadioButton']",
                               oldWacDoc);
    }

    private void replaceGeometrieValues(Document griffonDoc, Document oldWacDoc)
    {
        replaceValue("//gebaude/geometrie/gelufteteFlache/text()", griffonDoc,
                     "//component[@name='geoGeluefteteflaecheTextField']", oldWacDoc);
        replaceValue("//gebaude/geometrie/wohnflache/text()", griffonDoc,
                     "//component[@name='geoWohnflaecheTextField']", oldWacDoc);
        replaceValue("//gebaude/geometrie/mittlereRaumhohe/text()", griffonDoc,
                     "//component[@name='geoHoeheTextField']", oldWacDoc);
        replaceValue("//gebaude/geometrie/luftvolumen/text()", griffonDoc,
                     "//component[@name='geoVolumenTextField ']", oldWacDoc);
        replaceValue("//gebaude/geometrie/geluftetesVolumen/text()", griffonDoc,
                     "//component[@name='geoGelueftetesVolumenTextField']", oldWacDoc);
    }

    private void replaceRaumValues(Document griffonDoc, Document oldWacDoc)
    {

        /*
        <position>4</position>
        <raumnummer>105</raumnummer>
        <bezeichnung>Bad mit/ohne WC</bezeichnung>
        <raumtyp>BAD</raumtyp>
        <geschoss>EG</geschoss>
        <luftart>AB</luftart>
        <raumflache>13.0</raumflache>
        <raumhohe>2.5</raumhohe>
        <raumlange>0.0</raumlange>
        <raumvolumen>32.5</raumvolumen>
        <zuluftfaktor>0.0</zuluftfaktor>
        <abluftvolumenstrom>45.0</abluftvolumenstrom>
        <luftwechsel>0.0</luftwechsel>
        <volumenstrom>0.0</volumenstrom>
        <bezeichnungAbluftventile/>
        <anzahlAbluftventile>0</anzahlAbluftventile>
        <abluftmengeJeVentil>0.0</abluftmengeJeVentil>
        <bezeichnungZuluftventile/>
        <anzahlZuluftventile>0</anzahlZuluftventile>
        <zuluftmengeJeVentil>0.0</zuluftmengeJeVentil>
        <ventilebene/>
        <anzahlUberstromventile>0</anzahlUberstromventile>
        <uberstromelement/>


        <column cell-editor="javax.swing.JTable$GenericEditor" index="6" name="Vorgabeluftmenge"/>
        <column cell-editor="javax.swing.JTable$GenericEditor" index="7" name="Zuluftvolumenstrom"/>
        <column cell-editor="javax.swing.JTable$GenericEditor" index="9" name="Durchlassposition Zuluft"/>
        <column cell-editor="javax.swing.JTable$GenericEditor" index="10" name="Durchlassposition Abluft"/>
        <column cell-editor="javax.swing.JTable$GenericEditor" index="11" name="Kanalanschluss Zuluft"/>
        <column cell-editor="javax.swing.JTable$GenericEditor" index="12" name="Kanalanschluss Abluft"/>
        <column cell-editor="javax.swing.JTable$GenericEditor" index="13" name="Tueren"/>
        <column cell-editor="javax.swing.JTable$GenericEditor" index="14" name="Max. Türspalthöhe"/>
         */

        int count = 0;

        NodeList raumNodeList = null;
        Node seebassTableNode = null;
        Node wfTableModelNode = null;
        Node lmeTableModelNode = null;
        Node lmeUberstromTableModelNode = null;

        Object raumResult = createXPathExpression("//gebaude/raum", griffonDoc, XPathConstants.NODESET);
        if (null != raumResult)
        {
            raumNodeList = (NodeList) raumResult;
            count = raumNodeList.getLength();
        }


        Object seebassTableModelResult = createXPathExpression("//component[@name='seebassTabelle']/tablemodel", oldWacDoc, XPathConstants.NODE);
        if (null != seebassTableModelResult)
        {
            seebassTableNode = (Node) seebassTableModelResult;
            seebassTableNode.getAttributes().getNamedItem("rows").setTextContent("" + count);
        }

        Object lmeTableModelResult = createXPathExpression("//component[@name='lmeTabelleTable']/tablemodel", oldWacDoc, XPathConstants.NODE);
        if (null != lmeTableModelResult)
        {
            lmeTableModelNode = (Node) lmeTableModelResult;
            lmeTableModelNode.getAttributes().getNamedItem("rows").setTextContent("" + count);
        }
        
        Object lmeUberstromTableModelResult = createXPathExpression("//component[@name='lmeTabelleUeberstroemTable']/tablemodel", oldWacDoc, XPathConstants.NODE);
        if (null != lmeUberstromTableModelResult)
        {
            lmeUberstromTableModelNode = (Node) lmeUberstromTableModelResult;
            lmeUberstromTableModelNode.getAttributes().getNamedItem("rows").setTextContent("" + count);
        }

        Object wfTableModelResult = createXPathExpression("//component[@name='wfTabelleTable']/tablemodel", oldWacDoc, XPathConstants.NODE);
        if (null != wfTableModelResult)
        {
            wfTableModelNode = (Node) wfTableModelResult;
            wfTableModelNode.getAttributes().getNamedItem("rows").setTextContent("" + count);
        }


        replaceValue("//gebaude/geometrie/mittlereRaumhohe/text()", griffonDoc,
                     "//component[@name='geoHoeheTextField']", oldWacDoc);

        String hoehe = getTextContentFromNode("//gebaude/geometrie/mittlereRaumhohe/text()", griffonDoc);
        if (null == hoehe || (null != hoehe &&  hoehe.trim().length() < 2))
        {
            hoehe = "2,50";
        }
        replaceValueFromString(hoehe,
                       "//component[@name='wfHoeheTextField']",
                       oldWacDoc);

        
        // seebassTabelleTable
        for (int i = 0; i < count; i++)
        {

            // 1 - raumnummer
            String text = getTextContentFromNode("//raum[position='"+i+"']/raumnummer/text()", griffonDoc);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "0", "" + i, text));
            // 2
            text = getTextContentFromNode("//raum[position='"+i+"']/bezeichnung/text()", griffonDoc);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "1", "" + i, text));
            // 3
            text = getTextContentFromNode("//raum[position='"+i+"']/raumtyp/text()", griffonDoc);
            text = replaceRaumTypValue(text);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "2", "" + i, text));
            // 4
            text = getTextContentFromNode("//raum[position='"+i+"']/geschoss/text()", griffonDoc);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "3", "" + i, text));
            // 5
            text = getTextContentFromNode("//raum[position='"+i+"']/luftart/text()", griffonDoc);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "4", "" + i, text));
            // 6
            text = getTextContentFromNode("//raum[position='"+i+"']/zuluftfaktor/text()", griffonDoc, false);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "5", "" + i, text));
            // 7
            //tableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "6", "" + i, getTextContentFromNode("//gebaude/raum["+tempCount+"]/", oldWacDoc, false)));
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "6", "" + i, ""));
            // 8
            //tableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "7", "" + i, getTextContentFromNode("//gebaude/raum["+tempCount+"]/", oldWacDoc, false)));
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "7", "" + i, ""));
            // 9
            text = getTextContentFromNode("//raum[position='"+i+"']/abluftvolumenstrom/text()", griffonDoc);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "8", "" + i, text));
            // 10
            //tableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "9", "" + i, getTextContentFromNode("//gebaude/raum["+tempCount+"]/", oldWacDoc)));
            // 11
            //tableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "10", "" + i, getTextContentFromNode("//gebaude/raum["+tempCount+"]/", oldWacDoc)));
            // 12
            //tableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "11", "" + i, getTextContentFromNode("//gebaude/raum["+tempCount+"]/", oldWacDoc)));
            // 13
            //tableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "12", "" + i, getTextContentFromNode("//gebaude/raum["+tempCount+"]/", oldWacDoc)));
            // 14
            //tableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "13", "" + i, getTextContentFromNode("//gebaude/raum["+tempCount+"]/", oldWacDoc)));
            // 15
            //tableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "14", "" + i, getTextContentFromNode("//gebaude/raum["+tempCount+"]/", oldWacDoc)));
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "9", "" + i, ""));
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "10", "" + i, ""));
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "11", "" + i, ""));
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "12", "" + i, ""));

            text = createTurValues(i, griffonDoc);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "13", "" + i, text));
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "14", "" + i, ""));
            // 16
            text = getTextContentFromNode("//raum[position='"+i+"']/raumlange/text()", griffonDoc, false);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "15", "" + i, text));
            // 17
            text = getTextContentFromNode("//raum[position='"+i+"']/raumbreite/text()", griffonDoc, false);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "16", "" + i, text));
            // 18
            text = getTextContentFromNode("//raum[position='"+i+"']/raumflache/text()", griffonDoc, false);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "17", "" + i, text));
            // 19
            text = getTextContentFromNode("//raum[position='"+i+"']/raumvolumen/text()", griffonDoc, false);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "18", "" + i, text));
            // 20
            text = getTextContentFromNode("//raum[position='"+i+"']/raumhohe/text()", griffonDoc, false);
            seebassTableNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "19", "" + i, text));


            // wfTabelleTable
            text = getTextContentFromNode("//raum[position='"+i+"']/bezeichnung/text()", griffonDoc);
            wfTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "0", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/geschoss/text()", griffonDoc);
            wfTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "1", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/luftart/text()", griffonDoc);
            wfTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "2", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/raumflache/text()", griffonDoc);
            wfTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "3", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/raumhohe/text()", griffonDoc);
            wfTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "4", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/zuluftfaktor/text()", griffonDoc);
            wfTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "5", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/abluftvolumenstrom/text()", griffonDoc);
            wfTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "6", "" + i, text));


            // lmeTabelleTable
            text = getTextContentFromNode("//raum[position='"+i+"']/bezeichnung/text()", griffonDoc);
            lmeTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "0", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/luftart/text()", griffonDoc);
            lmeTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "1", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/raumvolumen/text()", griffonDoc);
            lmeTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "2", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/luftwechsel/text()", griffonDoc);
            lmeTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "3", "" + i, text));
            // Anzahl Ventile
            text = getTextContentFromNode("//raum[position='"+i+"']/anzahlAbluftventile/text()", griffonDoc, false);
            lmeTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "4", "" + i, text));
            // AbLuftmenge je Ventil
            text = getTextContentFromNode("//raum[position='"+i+"']/abluftmengeJeVentil/text()", griffonDoc, false);
            lmeTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "5", "" + i, text));

            text = getTextContentFromNode("//raum[position='"+i+"']/abluftvolumenstrom/text()", griffonDoc, false);
            lmeTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "6", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/anzahlZuluftventile/text()", griffonDoc, false);
            lmeTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "7", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/bezeichnungZuluftventile/text()", griffonDoc);
            lmeTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "8", "" + i, text));
            // Ventilbezeichnung
            text = getTextContentFromNode("//raum[position='"+i+"']/bezeichnungAbluftventile/text()", griffonDoc);
            lmeTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "9", "" + i, text));
            // verteilebene
            text = getTextContentFromNode("//raum[position='"+i+"']/ventilebene/text()", griffonDoc);
            lmeTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "10", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/zuluftmengeJeVentil/text()", griffonDoc, false);
            lmeTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "11", "" + i, text));



            text = getTextContentFromNode("//raum[position='"+i+"']/bezeichnung/text()", griffonDoc);
            lmeUberstromTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "0", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/luftart/text()", griffonDoc);
            lmeUberstromTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "1", "" + i, text));
            text = getTextContentFromNode("//raum[position='"+i+"']/raumvolumen/text()", griffonDoc);
            lmeUberstromTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "2", "" + i, ""));
            text = getTextContentFromNode("//raum[position='"+i+"']/anzahlUberstromventile/text()", griffonDoc);
            lmeUberstromTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "3", "" + i, text));
            //text = ;
            lmeUberstromTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "4", "" + i, ""));
            text = getTextContentFromNode("//raum[position='"+i+"']/uberstromelement/text()", griffonDoc);
            lmeUberstromTableModelNode.appendChild(createRaumTableRowDataElement(oldWacDoc, "5", "" + i, text));

        }

    }

    private String createTurValues(int raumposition, Document griffonDoc)
    {
        String value = "";
        int count = 6;
        for (int i = 1; i <= count; i++)
        {
            String name = getTextContentFromNode("//raum[position='"+raumposition+"']/tur["+i+"]/name/text()", griffonDoc, false);
            String breite = getTextContentFromNode("//raum[position='"+raumposition+"']/tur["+i+"]/breite/text()", griffonDoc, false);
            String dichtung = getTextContentFromNode("//raum[position='"+raumposition+"']/tur["+i+"]/dichtung/text()", griffonDoc, false);
            if (null != name && name.trim().equals(""))
            {
                if (value.trim().equals(""))
                {
                    value += Constants.TUR_VALUE;
                }
                else
                {
                    value += "|" + Constants.TUR_VALUE;
                }
            }
            else
            {
                if (value.trim().equals(""))
                {
                    value += name + ";"+breite+";0.0;0.0;"+dichtung;
                }
                else
                {
                    value += "|" + name + ";"+breite+";0.0;0.0;"+dichtung;
                }
            }
        }
        return value;
    }

    private Element createRaumTableRowDataElement(Document oldWacDoc, String column, String row, String text)
    {
        Element rowDataElement = oldWacDoc.createElement("rowdata");
        rowDataElement.setAttribute("column", column);
        rowDataElement.setAttribute("row", "" + row);
        rowDataElement.setTextContent(text);
        System.out.println("text: " + text);
        return rowDataElement;
    }


    private void replaceZentralgeratValues(Document griffonDoc, Document oldWacDoc)
    {
        /*
      <zentralgerat>
      <name>140WACCF</name>
      <manuell>false</manuell>
      <volumenstrom>85</volumenstrom>
      <geratestandort>SB</geratestandort>
    </zentralgerat>
         */

        String hkAusfuehrungCheckbox = "false";

        Object hkAusfuehrungGriffonResult = createXPathExpression("//zentralgerat/hygiene/ausfuhrung/text()", griffonDoc, XPathConstants.NODE);
        Node hkAusfuehrungGriffonNode = (Node) hkAusfuehrungGriffonResult;
        String hkAusfuehrungValue = getTextContentFromNode(hkAusfuehrungGriffonNode);
        if (hkAusfuehrungValue.equalsIgnoreCase("true"))
        {
            hkAusfuehrungCheckbox = "true";
        }
        replaceValueFromString(hkAusfuehrungCheckbox,
                               "//component[@name='hkAusfuehrungCheckbox']",
                               oldWacDoc);

        String hkFilterungCheckbox = "false";

        Object hkFilterungGriffonResult = createXPathExpression("//zentralgerat/hygiene/filterung/text()", griffonDoc, XPathConstants.NODE);
        Node hkFilterungGriffonNode = (Node) hkFilterungGriffonResult;
        String hkFilterungValue = getTextContentFromNode(hkFilterungGriffonNode);
        if (hkFilterungValue.equalsIgnoreCase("true"))
        {
            hkFilterungCheckbox = "true";
        }
        replaceValueFromString(hkFilterungCheckbox,
                               "//component[@name='hkFilterungCheckbox']",
                               oldWacDoc);

        String hkVerschmutzungCheckbox = "false";

        Object hkVerschmutzungGriffonResult = createXPathExpression("//zentralgerat/hygiene/keineVerschmutzung/text()", griffonDoc, XPathConstants.NODE);
        Node hkVerschmutzungGriffonNode = (Node) hkVerschmutzungGriffonResult;
        String hkVerschmutzungValue = getTextContentFromNode(hkVerschmutzungGriffonNode);
        if (hkVerschmutzungValue.equalsIgnoreCase("true"))
        {
            hkVerschmutzungCheckbox = "true";
        }
        replaceValueFromString(hkVerschmutzungCheckbox,
                               "//component[@name='hkVerschmutzungCheckbox']",
                               oldWacDoc);

        String hkDichtheitsklasseCheckbox = "false";

        Object hkDichtheitsklasseGriffonResult = createXPathExpression("//zentralgerat/hygiene/dichtheitsklasseB/text()", griffonDoc, XPathConstants.NODE);
        Node hkDichtheitsklasseGriffonNode = (Node) hkDichtheitsklasseGriffonResult;
        String hkDichtheitsklasseValue = getTextContentFromNode(hkDichtheitsklasseGriffonNode);
        if (hkDichtheitsklasseValue.equalsIgnoreCase("true"))
        {
            hkDichtheitsklasseCheckbox = "true";
        }
        replaceValueFromString(hkDichtheitsklasseCheckbox,
                               "//component[@name='hkDichtheitsklasseCheckbox']",
                               oldWacDoc);


        
        
        String rbAlDachdurchfuehrung = "false";
        String rbAlWand = "false";
        String rbAlErdwaermetauscher = "false";
        String aussenluftGriffonResult = getTextContentFromNode("//gebaude/aussenluft", griffonDoc);
        if (null != aussenluftGriffonResult && aussenluftGriffonResult.equalsIgnoreCase("DAC"))
        {
            rbAlDachdurchfuehrung = "true";
        }
        else if (null != aussenluftGriffonResult && aussenluftGriffonResult.equalsIgnoreCase("WAN"))
        {
            rbAlWand = "true";
        }
        else if (null != aussenluftGriffonResult && aussenluftGriffonResult.equalsIgnoreCase("ERD"))
        {
            rbAlErdwaermetauscher = "true";
        }
        replaceValueFromString(rbAlDachdurchfuehrung,
                               "//component[@name='rbAlDachdurchfuehrung']",
                               oldWacDoc);
        replaceValueFromString(rbAlWand,
                               "//component[@name='rbAlWand']",
                               oldWacDoc);
        replaceValueFromString(rbAlErdwaermetauscher,
                               "//component[@name='rbAlErdwaermetauscher']",
                               oldWacDoc);



        String ekZuAbluftCheckbox = "false";

        Object ekZuAbluftGriffonResult = createXPathExpression("//zentralgerat/energie/zuAbluftWarme/text()", griffonDoc, XPathConstants.NODE);
        Node ekZuAbluftGriffonNode = (Node) ekZuAbluftGriffonResult;
        String ekZuAbluftValue = getTextContentFromNode(ekZuAbluftGriffonNode);
        if (ekZuAbluftValue.equalsIgnoreCase("true"))
        {
            ekZuAbluftCheckbox = "true";
        }
        replaceValueFromString(ekZuAbluftCheckbox,
                               "//component[@name='ekZuAbluftCheckbox']",
                               oldWacDoc);

        String ekRueckgewinnungCheckbox = "false";

        Object ekRueckgewinnungGriffonResult = createXPathExpression("//zentralgerat/energie/ruckgewinnung/text()", griffonDoc, XPathConstants.NODE);
        Node ekRueckgewinnungGriffonNode = (Node) ekRueckgewinnungGriffonResult;
        String ekRueckgewinnungValue = getTextContentFromNode(ekRueckgewinnungGriffonNode);
        if (ekRueckgewinnungValue.equalsIgnoreCase("true"))
        {
            ekRueckgewinnungCheckbox = "true";
        }
        replaceValueFromString(ekRueckgewinnungCheckbox,
                               "//component[@name='ekRueckgewinnungCheckbox']",
                               oldWacDoc);

        String ekBemessungCheckbox = "false";

        Object ekBemessungGriffonResult = createXPathExpression("//zentralgerat/energie/bemessung/text()", griffonDoc, XPathConstants.NODE);
        Node ekBemessungGriffonNode = (Node) ekBemessungGriffonResult;
        String ekBemessungValue = getTextContentFromNode(ekBemessungGriffonNode);
        if (ekBemessungValue.equalsIgnoreCase("true"))
        {
            ekBemessungCheckbox = "true";
        }
        replaceValueFromString(ekBemessungCheckbox,
                               "//component[@name='ekBemessungCheckbox']",
                               oldWacDoc);

        String ekRegelungCheckbox = "false";

        Object ekRegelungGriffonResult = createXPathExpression("//zentralgerat/energie/regelung/text()", griffonDoc, XPathConstants.NODE);
        Node ekRegelungGriffonNode = (Node) ekRegelungGriffonResult;
        String ekRegelungValue = getTextContentFromNode(ekRegelungGriffonNode);
        if (ekRegelungValue.equalsIgnoreCase("true"))
        {
            ekRegelungCheckbox = "true";
        }
        replaceValueFromString(ekRegelungCheckbox,
                               "//component[@name='ekRegelungCheckbox']",
                               oldWacDoc);





        String kzRueckschlagklappeCheckbox = "false";

        Object kzRueckschlagklappeGriffonResult = createXPathExpression("//zentralgerat/ruckschlagkappe/text()", griffonDoc, XPathConstants.NODE);
        Node kzRueckschlagklappeGriffonNode = (Node) kzRueckschlagklappeGriffonResult;
        String kzRueckschlagklappeValue = getTextContentFromNode(kzRueckschlagklappeGriffonNode);
        if (kzRueckschlagklappeValue.equalsIgnoreCase("true"))
        {
            kzRueckschlagklappeCheckbox = "true";
        }
        replaceValueFromString(kzRueckschlagklappeCheckbox,
                               "//component[@name='kzRueckschlagklappeCheckbox']",
                               oldWacDoc);

        String kzSchallschutzCheckbox = "false";

        Object kzSchallschutzGriffonResult = createXPathExpression("//zentralgerat/schallschutz/text()", griffonDoc, XPathConstants.NODE);
        Node kzSchallschutzGriffonNode = (Node) kzSchallschutzGriffonResult;
        String kzSchallschutzValue = getTextContentFromNode(kzSchallschutzGriffonNode);
        if (kzSchallschutzValue.equalsIgnoreCase("true"))
        {
            kzSchallschutzCheckbox = "true";
        }
        replaceValueFromString(kzSchallschutzCheckbox,
                               "//component[@name='kzSchallschutzCheckbox']",
                               oldWacDoc);

        String kzFeuerstaetteCheckBox = "false";

        Object kzFeuerstaetteGriffonResult = createXPathExpression("//zentralgerat/feuerstatte/text()", griffonDoc, XPathConstants.NODE);
        Node kzFeuerstaetteGriffonNode = (Node) kzFeuerstaetteGriffonResult;
        String kzFeuerstaetteValue = getTextContentFromNode(kzFeuerstaetteGriffonNode);
        if (kzFeuerstaetteValue.equalsIgnoreCase("true"))
        {
            kzFeuerstaetteCheckBox = "true";
        }
        replaceValueFromString(kzFeuerstaetteCheckBox,
                               "//component[@name='kzFeuerstaetteCheckBox']",
                               oldWacDoc);

    }

    private void replaceFirmaValues(Document griffonDoc, Document oldWacDoc)
    {

        replaceValue("//projekt/notizen/text()", griffonDoc,
                     "//component[@name='adNotizenTextArea']", oldWacDoc);

        replaceValue("//projekt/bauvorhaben/text()", griffonDoc,
                     "//component[@name='adBauvorhabenTextField']", oldWacDoc);





        // Grosshandel
        replaceValue("//firma[rolle='Grosshandel']/firma2/text()", griffonDoc,
                     "//component[@name='ghFirma2TextField']", oldWacDoc);

        replaceValue("//firma[rolle='Grosshandel']/firma1/text()", griffonDoc,
                     "//component[@name='ghFirma1TextField']", oldWacDoc);

        replaceValue("//firma[rolle='Grosshandel']/adresse/strasse/text()", griffonDoc,
                     "//component[@name='ghStrasseTextfield']", oldWacDoc);

        replaceValue("//firma[rolle='Grosshandel']/kontakt/person/name/text()", griffonDoc,
                     "//component[@name='ghAnsprechpartnerTextField']", oldWacDoc);
        replaceValue("//firma[rolle='Grosshandel']/fax/text()", griffonDoc,
                     "//component[@name='ghFaxTextField']", oldWacDoc);
        mergeTwoValuesInOneValue("//firma[rolle='Grosshandel']/adresse/postleitzahl/text()",
                           "//firma[rolle='Grosshandel']/adresse/ort/text()",
                           griffonDoc,
                           "//component[@name='ghPlzOrtTextField']",
                           oldWacDoc);

        replaceValue("//firma[rolle='Grosshandel']/tel/text()", griffonDoc,
                     "//component[@name='ghTelefonTextField']", oldWacDoc);


        // Ausfuhrende
        replaceValue("//firma[rolle='Ausfuhrende']/kontakt/person/name/text()", griffonDoc,
                     "//component[@name='afAnsprechpartnerTextField']", oldWacDoc);
        replaceValue("//firma[rolle='Ausfuhrende']/fax/text()", griffonDoc,
                     "//component[@name='afFaxTextField']", oldWacDoc);
        replaceValue("//firma[rolle='Ausfuhrende']/tel/text()", griffonDoc,
                     "//component[@name='afTelefonTextField']", oldWacDoc);

        mergeTwoValuesInOneValue("//firma[rolle='Ausfuhrende']/adresse/postleitzahl/text()",
                           "//firma[rolle='Ausfuhrende']/adresse/ort/text()",
                           griffonDoc,
                           "//component[@name='afPlzOrtTextField']",
                           oldWacDoc);

        replaceValue("//firma[rolle='Ausfuhrende']/firma2/text()", griffonDoc,
                     "//component[@name='afFirma2TextField']", oldWacDoc);
        replaceValue("//firma[rolle='Ausfuhrende']/firma1/text()", griffonDoc,
                     "//component[@name='afFirma1TextField']", oldWacDoc);
        replaceValue("//firma[rolle='Ausfuhrende']/adresse/strasse/text()", griffonDoc,
                     "//component[@name='afStrasseTextfield']", oldWacDoc);
    }

    private void mergeTwoValuesInOneValue(String firstGriffonExpression, String secondGriffonExpression, Document griffonDoc, String plzOrtOldWacExpression, Document oldWacDoc)
    {
        Object firstGriffonResult = createXPathExpression(firstGriffonExpression, griffonDoc, XPathConstants.NODE);
        Node firstGriffonNode = (Node) firstGriffonResult;
        String firstValue = getTextContentFromNode(firstGriffonNode);
        if (null == firstValue)
        {
            firstValue = "";
        }

        Object secondGriffonResult = createXPathExpression(secondGriffonExpression, griffonDoc, XPathConstants.NODE);
        Node secondGriffonNode = (Node) secondGriffonResult;
        String secondValue = getTextContentFromNode(secondGriffonNode);
        if (null == secondValue)
        {
            secondValue = "";
        }

        String mergedValue = firstValue + " " + secondValue;

        Object oneValueOldWacResult = createXPathExpression(plzOrtOldWacExpression, oldWacDoc, XPathConstants.NODE);
        Node oneValueOldWacNode = (Node) oneValueOldWacResult;
        oneValueOldWacNode.getAttributes().getNamedItem("value").setTextContent(mergedValue);
    }

}