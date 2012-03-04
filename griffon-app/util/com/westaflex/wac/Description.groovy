/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac;


import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 *
 * @author rbe
 */
public class Description {

    /**
     * Map for positions of AB LVK descriptions: this is the
     * Y offset of the lowest connection of the LVK
     */
    private static Map<String, Integer> lvkAbOffsetY;

    /**
     * Spacer in pixels between AB LVK descriptions
     */
    private static int lvkAbSpacer;

    /**
     * Map for positions of ZU LVK descriptions: this is the
     * Y offset of the highest connection of the LVK
     */
    private static Map<String, Integer> lvkZuOffsetY;

    /**
     * Spacer in pixels between ZU LVK descriptions
     */
    private static int lvkZuSpacer;

    /**
     * Font for Abluft/Zuluft
     */
    private static Font abZuFont;

    /**
     * x Offset for Abluft/Zuluft
     */
    private static int abZuXOffset;

    /**
     * Font for Aussenluft/Fortluft/Zentralgeraet
     */
    private static Font geraetFont;

    /**
     *
     */
    private Map<String, String> names = new HashMap<String, String>();

    /**
     *
     */
    private HashMap<String, HashMap<String, List<String>>> connector = new HashMap<String, HashMap<String, List<String>>>();

    static {
        abZuFont = new Font("LTSyntax Medium", Font.PLAIN, 10);
        geraetFont = new Font("LTSyntax Medium", Font.PLAIN, 10);
        int lvkHeight = 92; // Height of LVK
        abZuXOffset = 615; // X offset
        // Abluft
        int lvkAbLast = 257; // Y offset Abluft unten->oben
        lvkAbOffsetY = new HashMap<String, Integer>();
        lvkAbOffsetY.put("AB_LVK_1", lvkAbLast);
        lvkAbOffsetY.put("AB_LVK_2", lvkAbLast - 1 * lvkHeight);
        lvkAbOffsetY.put("AB_LVK_3", lvkAbLast - 2 * lvkHeight);
        lvkAbSpacer = 11; // Pixels between connectors
        // Zuluft
        int lvkZuFirst = 288; // Y offset Zuluft oben->unten
        lvkZuOffsetY = new HashMap<String, Integer>();
        lvkZuOffsetY.put("ZU_LVK_1", lvkZuFirst);
        lvkZuOffsetY.put("ZU_LVK_2", lvkZuFirst + 1 * lvkHeight);
        lvkZuOffsetY.put("ZU_LVK_3", lvkZuFirst + 2 * lvkHeight);
        lvkZuSpacer = 11; // Pixels between connectors
    }

    /**
     *
     * @param luftart
     * @param verteilebene
     * @param raumname
     */
    public void addConnector(String luftart, String verteilebene, String raumname) {
        HashMap<String, List<String>> verteilebeneMap = connector.get(luftart);
        if (null == verteilebeneMap) {
            verteilebeneMap = new HashMap<String, List<String>>();
            connector.put(luftart.toUpperCase(), verteilebeneMap);
        }
        List<String> raumList = verteilebeneMap.get(verteilebene);
        if (null == raumList) {
            raumList = new ArrayList<String>();
            verteilebeneMap.put(verteilebene.toUpperCase(), raumList);
        }
        raumList.add(raumname);
    }

    /**
     *
     * @param name
     * @param value
     */
    public void putName(String name, String value) {
        names.put(name, value);
    }

    /**
     * Add descriptions for 'Abluft'.
     * @param g2
     * @param ab Two-dimensional String[] 1=LVK, 2=Verbinder am LVK
     */
    private void drawAbluft(BufferedImage img, HashMap<String, List<String>> ab) {
        // Create graphics
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.black);
        g2.setFont(abZuFont);
        int idx = 1;
        for (String verteilebene: ab.keySet()) {
            int offset = lvkAbOffsetY.get("AB_LVK_" + idx);
            String[] cn = ab.get(verteilebene).toArray(new String[0]);
            for (int abConnector = 0; abConnector < cn.length; abConnector++) {
                // Draw text
                g2.drawString(cn[abConnector], abZuXOffset, offset - abConnector * lvkAbSpacer);
            }
            idx++;
        }
        /*
          for (int abLvk = 0; abLvk < ab.length; abLvk++) {
          int offset = lvkAbOffsetY.get("AB_LVK_" + (abLvk + 1));
          for (int abConnector = 0; abConnector < ab[abLvk].length; abConnector++) {
          // Draw text
          g2.drawString(ab[abLvk][abConnector], abZuXOffset, offset - abConnector * lvkAbSpacer);
          }
          }
           */
    }

    /**
     * Add descriptions for 'Zuluft'.
     * @param g2
     * @param zu Two-dimensional String[] 1=LVK, 2=Verbinder am LVK
     */
    private void drawZuluft(BufferedImage img, HashMap<String, List<String>> zu) {
        // Create graphics
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.black);
        g2.setFont(abZuFont);
        int idx = 1;
        for (String verteilebene: zu.keySet()) {
            int offset = lvkZuOffsetY.get("ZU_LVK_" + idx);
            String[] cn = zu.get(verteilebene).toArray(new String[0]);
            for (int zuConnector = 0; zuConnector < cn.length; zuConnector++) {
                // Draw text
                g2.drawString(cn[zuConnector], abZuXOffset, offset + zuConnector * lvkZuSpacer);
            }
            idx++;
        }
        /*
          for (int zuLvk = 0; zuLvk < zu.length; zuLvk++) {
          int offset = lvkZuOffsetY.get("ZU_LVK_" + (zuLvk + 1));
          for (int zuConnector = 0; zuConnector < zu[zuLvk].length; zuConnector++) {
          // Draw text
          g2.drawString(zu[zuLvk][zuConnector], abZuXOffset, offset + zuConnector * lvkZuSpacer);
          }
          }
           */
    }

    /**
     *
     * @param g2
     * @param name
     */
    private void drawAussenluft(BufferedImage img, String name) {
        // Create graphics
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.black);
        g2.setFont(geraetFont);
        // Draw text
        g2.drawString(name, 10, 260);
    }

    /**
     *
     * @param g2
     * @param name
     */
    private void drawFortluft(BufferedImage img, String name) {
        // Create graphics
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.black);
        g2.setFont(geraetFont);
        // Draw text
        g2.drawString(name, 10, 345);
    }

    /**
     *
     * @param g2
     * @param name
     */
    private void drawZentralgeraet(BufferedImage img, String name) {
        // Create graphics
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.black);
        g2.setFont(geraetFont);
        // Draw text
        g2.drawString("Zentrallüftungsgerät Typ " + name, 170, 350);
    }

    /**
     * @return
     * @throws Exception
     */
    public File drawText() throws Exception {
        // Which graphic to use as base?
        String filename = "AB" + connector.get("AB").size() + "_" + "ZU" + connector.get("ZU").size();
        // Load graphic
        BufferedImage img = ImageIO.read(getClass().getResource("/com/westaflex/resource/image/" + filename + ".png"));
        // Aussenluft
        if (names.containsKey("aussenluft")) {
            drawAussenluft(img, names.get("aussenluft"));
        }
        // Fortluft
        if (names.containsKey("fortluft")) {
            drawFortluft(img, names.get("fortluft"));
        }
        // Zentralgeraet
        if (names.containsKey("zentralgeraet")) {
            drawZentralgeraet(img, names.get("zentralgeraet"));
        }
        // Abluft
        drawAbluft(img, connector.get("AB"));
        // Zuluft
        drawZuluft(img, connector.get("ZU"));
        // Save file
        File outputfile = File.createTempFile("westaflex_", ".png");
        // Delete file when application/JVM is shut down
        outputfile.deleteOnExit();
        ImageIO.write(img, "png", outputfile);
        // Return full path of generated file
        return outputfile;
    }

    /*
     public static void main(String[] args) throws Exception {
         Description d = new Description();
         d.putName("aussenluft", "200LE01");
         d.putName("fortluft", "200LG01");
         d.putName("zentralgeraet", "300WAC");
         d.addConnector("AB", "EG", "Kinderzimmer 1 / 100ULC");
         d.addConnector("AB", "EG", "Wohnzimmer / 100ULC");
         d.addConnector("AB", "EG", "Kinderzimmer 1 / 100ULC");
         d.addConnector("AB", "EG", "Kinderzimmer 1 / 100ULC");
         d.addConnector("AB", "EG", "Wohnzimmer / 100ULC");
         d.addConnector("AB", "EG", "Wohnzimmer / 100ULC");
         d.addConnector("AB", "DG", "Wohnzimmer / 100ULC");
         d.addConnector("AB", "DG", "Wohnzimmer / 100ULC");
         d.addConnector("AB", "OG", "Kinderzimmer 1 / 100ULC");
         d.addConnector("AB", "OG", "Wohnzimmer / 100ULC");
         d.addConnector("AB", "OG", "Wohnzimmer / 100ULC");
         d.addConnector("ZU", "DG", "Büro / 125 URH");
         d.addConnector("ZU", "DG", "Schlafzimmer / 125 URH");
         d.addConnector("ZU", "EG", "Büro / 125 URH");
         d.addConnector("ZU", "EG", "Büro / 125 URH");
         d.addConnector("ZU", "OG", "Büro / 125 URH");
         d.addConnector("ZU", "OG", "Schlafzimmer / 125 URH");
         System.out.println(d.drawText().getAbsolutePath());
     }
     */

}
