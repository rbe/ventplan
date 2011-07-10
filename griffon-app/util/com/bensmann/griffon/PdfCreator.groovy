/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/util/com/bensmann/griffon/GriffonHelper.groovy
 * Last modified at 09.03.2011 17:46:39 by rbe
 */
package com.bensmann.griffon

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Create Pdf with iText library.
 * First call createDocument to instantiate a new document.
 * 
 */
class PdfCreator {
	
	public static boolean DEBUG = false
    
    def static TITLE_FONT = new Font(FontFamily.HELVETICA, 24)
    
    def document
    def table
    
	
	/**
     * Creates a PDF with information about the items to use for each room.
     * Also adds metadata like author, creator and creation date.
     */
    def createDocument = { filename ->
    	// Instantiate a new document object
        document = new Document();
        // create a new writer
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        // Set box to write in
        writer.setBoxSize("art", new Rectangle(36, 54, 559, 748))
        // open the document for writing
        document.open();
        
        // Add some information
        document.addAuthor("westaflex GmbH")
        document.addCreator("westaflex GmbH")
        document.addCreationDate()
    }
    
    /**
     * Adds an image to the top of the document with a space of 10.
     */
    def addLogo = { imgsrc -> 
        def yCoordinate = (document.top() - 10f) as float
        Image img = Image.getInstance(imgsrc);
        img.setAbsolutePosition(36f, yCoordinate);
        document.add(img);
    }
    
    /**
     * Close the document when you are finished with adding content to the document.
     */
    def closeDocument = {
        try {
            if (document) {
                document?.close()
            }
        } catch (e) {
            println "Error closing document ${e}"
        }
    }    
    
    /**
     * Creates a table with the specified columns size.
     * Just call it once or add table when finished with a table and then create
     * a new one.
     */
    def createTable = { columns ->
        try {
            // Instantiate the table with specified columns
            table = new PdfPTable(columns)
            // set width to full page width
            table.setWidthPercentage(100f);
            // set padding so the content looks nicer :-)
            table.getDefaultCell().setPadding(5);
            // Set background color to highlight the table header
            table.getDefaultCell().setBackgroundColor(BaseColor.LIGHT_GRAY);
            // center content
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell("Raum");
            table.addCell("Luftart");
            table.addCell("Ventile");
            table.addCell("Anzahl");
            // Now remove background color. Default will be used.
            table.getDefaultCell().setBackgroundColor(null);
        } catch (e) {
            println "Error closing document ${e}"
        }
    }
    
    /**
     * Adds a heading to the PDF document with a 24px font size.
     * Also adds the current date and time.
     */
    def addTitle = { title ->
        try {
            // Add empty paragraph for spacing...
            document.add(new Paragraph("    ", TITLE_FONT))
            // Now add title
            document.add(new Paragraph(title, TITLE_FONT))
            // Add date of creation
            document.add(new Phrase("Stückliste vom " + new Date().format('dd.MM.yyyy HH:mm')))
        } catch (Exception e) {
            
        }
    }
    
    /**
     * Adds the table to the document.
     * Call this when the table has all its content.
     */
    def addTable = {
        try {
            document?.add(table)
        } catch (e) {
            println "Error closing document ${e}"
        }
    }

    /**
     * Needs a table to be instantiated first.
     * Specified method to fit on a table with 4 columns.
     * Otherwise the items will display broken.
     */
    def addArtikel = { raumBezeichnung, luftart, ventil, anzahl ->
        try {
            PdfPCell cell = new PdfPCell()
            cell.setPaddingBottom(5f)
            table?.addCell("" + raumBezeichnung)
            table?.addCell("" + luftart)
            table?.addCell("" + ventil)
            table?.addCell("" + anzahl)
        } catch (e) {
            println "Error adding content to document ${e}"
        }
    }
    
    def addArtikelToDocument = { artikel ->
        try {
            // Add paragraph for artikel
            document.add(new Paragraph(artikel))
        } catch (Exception e) {
            
        }
    }

}
