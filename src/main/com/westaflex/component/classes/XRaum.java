/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.westaflex.component.classes;

/**
 *
 * @author seebass
 */
public interface XRaum {

    enum Geschoss {

        KG(0, "Kellergeschoss"), EG(1, "Erdgeschoss"), OG(2, "Obergschoss"), DG(3, "Dachgeschoss"), SB(4, "Spitzboden");
        private int value = -1;
        private String longName = "";

        private Geschoss(int value) {
            this.value = value;
        }

        private Geschoss(int value, String longName) {
            this(value);
            this.longName = longName;
        }

        public int value() {
            return value;
        }

        static String longName(String geschoss) {
            return valueOf(geschoss).longName;
        }
    }

    enum Luftart {

        Zuluft, Abluft, AbUndZuLuft, Ueberstroem
    }

    enum Durchlass {

        Decke, WandOben, WandUnten, Boden
    }

    enum Kanalanschluss {

        DeckeGleicheEbene, BodenGleicheEbene, BodenNaechsteEbene, DeckeVorebene
    }
}
