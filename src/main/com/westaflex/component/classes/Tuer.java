/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.westaflex.component.classes;

/**
 *
 * @author seebass
 */
public class Tuer {

    private String bezeichnung = "Mustertür";
    private int breite = 0;
    private float querschnittsflaeche = 0f;
    private float spalthoehe = 0f; // Spalthöhe in mm
    private boolean dichtung = false;

    public Tuer() {
        super();
    }

    public Tuer(String string) {
        String[] s = string.split(";");

        setBezeichnung(s[0].equals("null")?"":s[0]);
        if (s.length > 0) {
            setBreite(Integer.parseInt(s[1]));
            setQuerschnittsflaeche(Float.parseFloat(s[2]));
            setSpalthoehe(Float.parseFloat(s[3]));
            setDichtung(s[4].equals("true") ? true : false);
        } else {
            setBreite(0);
            setQuerschnittsflaeche(0f);
            setSpalthoehe(0f);
            setDichtung(true);
        }
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public int getBreite() {
        return breite;
    }

    public void setBreite(int breite) {
        this.breite = breite;
    }

    public float getQuerschnittsflaeche() {
        return querschnittsflaeche;
    }

    public void setQuerschnittsflaeche(float querschnittsflaeche) {
        this.querschnittsflaeche = querschnittsflaeche;
    }

    public float getSpalthoehe() {
        return spalthoehe;
    }

    public void setSpalthoehe(float spalthoehe) {
        this.spalthoehe = spalthoehe;
    }

    public boolean getDichtung() {
        return dichtung;
    }

    public void setDichtung(boolean dichtung) {
        this.dichtung = dichtung;
    }

    @Override
    public String toString() {

        return String.format("%s;%s;%s;%s;%s", getBezeichnung(), getBreite(), getQuerschnittsflaeche(), getSpalthoehe(), getDichtung());
    }
}