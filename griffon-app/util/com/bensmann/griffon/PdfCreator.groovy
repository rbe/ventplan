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

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 
 */
class PdfCreator {
	
	public static boolean DEBUG = false
    
    def document
    def table
    
	
	/**
     * Creates a PDF with information about the movies
     * @param    filename the name of the PDF file that will be created.
     * @throws    DocumentException 
     * @throws    IOException
     */
    def createDocument = { filename ->
    	// step 1
        document = new Document();
        // step 2
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        // step 3
        document.open();
        
        // Add some information
        document.addAuthor("westaflex GmbH")
        document.addCreator("westaflex GmbH")
        document.addCreationDate()
        
        // step 4
        //document.add(createFirstTable(3));
        // step 5
        //document.close();
    }
    
    def addLogo = { imgsrc -> 
        def yCoordinate = (document.top() - 10f) as float
        Image img = Image.getInstance(imgsrc);
        img.setAbsolutePosition(20f, yCoordinate);
        document.add(img);
    }
    
    def closeDocument = {
        try {
            if (document) {
                document?.close()
            }
        } catch (e) {
            println "Error closing document ${e}"
        }
    }    
    
    def createTable = { columns ->
        try {
            table = new PdfPTable(columns)
            table.setWidthPercentage(100f);
            table.getDefaultCell().setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell("Raum");
            table.addCell("Luftart");
            table.addCell("Ventile");
            table.addCell("Anzahl");
            table.getDefaultCell().setBackgroundColor(null);
        } catch (e) {
            println "Error closing document ${e}"
        }
    }
    
    def addTable = {
        try {
            document?.add(table)
        } catch (e) {
            println "Error closing document ${e}"
        }
    }

    def addRaum = { raumBezeichnung ->
        try {
            PdfPCell cell = new PdfPCell(new Phrase(raumBezeichnung))
            cell.setColspan(3);
            cell.setMinimumHeight(20f)
            table?.addCell(cell)
            
        } catch (e) {
            println "Error adding raum to document ${e}"
        }
    } 

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

}
