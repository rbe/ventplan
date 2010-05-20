/*
 * Holiday.java
 *
 * Created on 17. März 2006, 16:23
 *
 */

package com.bensmann.superframe.time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

/**
 *
 */
class HolidayInfo {
    
    /**
     *
     */
    public String name;
    
    /**
     *
     */
    public Calendar calendar;
    
    /**
     *
     */
    public boolean officialHoliday;
    
    /**
     *
     */
    public boolean halfHoliday;
    
    /**
     * 
     * @param name 
     * @param calendar 
     * @param officialHoliday 
     * @param halfHoliday 
     */
    public HolidayInfo(String name, Calendar calendar,
            boolean officialHoliday, boolean halfHoliday) {
        
        setCalendar(calendar);
        setName(name);
        
    }
    
    /**
     * 
     * @param calendar 
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
    
    /**
     * 
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 
     * @param officialHoliday 
     */
    public void setOfficialHoliday(boolean officialHoliday) {
        this.officialHoliday = officialHoliday;
    }
    
    /**
     * 
     * @param halfHoliday 
     */
    public void setHalfHoliday(boolean halfHoliday) {
        this.halfHoliday = halfHoliday;
    }
    
    /**
     * 
     * @return 
     */
    public Calendar getCalendar() {
        return calendar;
    }
    
    /**
     * 
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isOfficialHoliday() {
        return officialHoliday;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isHalfHoliday() {
        return halfHoliday;
    }
    
}

/**
 *
 * $Header$
 * @author rb
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public class Holiday {
    
    /**
     *
     */
    private static Calendar easter;
    
    /**
     *
     */
    private static List<HolidayInfo> holidays;
    
    // Static initializer
    static {
        initialize();
    }
    
    /**
     * Do not create a new instance of Holiday
     */
    private Holiday() {
    }
    
    /**
     *
     */
    public static void initialize() {
        
        // Create list with holidays
        holidays = new Vector<HolidayInfo>();
        
        // Calculate easter
        easter = getEastern();
        
        // Neujahr
        holidays.add(
                new HolidayInfo("Neujahr", getCalendarOfDate(1, 1),
                true, false));
        
        // Hl. 3 Könige
        holidays.add(
                new HolidayInfo("Heilige 3 Könige",
                getCalendarOfDate(6, 1),
                false, false));
        
        // Rosenmontag
        Calendar rosenmontag = (Calendar) easter.clone();
        rosenmontag.add(Calendar.DAY_OF_MONTH, -48);
        holidays.add(
                new HolidayInfo("Rosenmontag", rosenmontag,
                false, false));
        
        // Fastnacht
        Calendar fastnacht = (Calendar) easter.clone();
        fastnacht.add(Calendar.DAY_OF_MONTH, -47);
        holidays.add(
                new HolidayInfo("Fastnacht", fastnacht,
                false, false));
        
        // Aschermittwoch
        Calendar aschermittwoch = (Calendar) easter.clone();
        aschermittwoch.add(Calendar.DAY_OF_MONTH, -46);
        holidays.add(
                new HolidayInfo("Aschermittwoch", aschermittwoch,
                false, false));
        
        // Valentinstag
        holidays.add(
                new HolidayInfo("Valentinstag",
                getCalendarOfDate(14, 2),
                false, false));
        
        // Gruendonnerstag
        Calendar gruendonnerstag = (Calendar) easter.clone();
        gruendonnerstag.add(Calendar.DAY_OF_MONTH, -3);
        holidays.add(
                new HolidayInfo("Gruendonnerstag", gruendonnerstag,
                false, false));
        
        // Karfreitag
        Calendar karfreitag = (Calendar) easter.clone();
        karfreitag.add(Calendar.DAY_OF_MONTH, -2);
        holidays.add(
                new HolidayInfo("Karfreitag", karfreitag,
                true, false));
        
        // Ostersonntag
        Calendar ostersonntag = (Calendar) easter.clone();
        holidays.add(
                new HolidayInfo("Ostersonntag", ostersonntag,
                true, false));
        
        // Ostermontag
        Calendar ostermontag = (Calendar) easter.clone();
        ostermontag.add(Calendar.DAY_OF_MONTH, 1);
        holidays.add(
                new HolidayInfo("Ostermontag", ostermontag,
                true, false));
        
        // Maifeiertag
        Calendar maifeiertag = Calendar.getInstance();
        maifeiertag.set(Calendar.DAY_OF_MONTH, 01);
        maifeiertag.set(Calendar.MONTH, 05);
        holidays.add(
                new HolidayInfo("Maifeiertag", maifeiertag,
                true, false));
        
        // Muttertag ?????
        Calendar muttertag = (Calendar) maifeiertag.clone();
        muttertag.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        holidays.add(
                new HolidayInfo("Muttertag", muttertag,
                false, false));
        
        // Pfingstsonntag
        Calendar pfingstsonntag = (Calendar) easter.clone();
        pfingstsonntag.add(Calendar.DAY_OF_MONTH, 49);
        holidays.add(
                new HolidayInfo("Pfingstsonntag", pfingstsonntag,
                true, false));
        
        // Pfingstmontag
        Calendar pfingstmontag = (Calendar) easter.clone();
        pfingstmontag.add(Calendar.DAY_OF_MONTH, 50);
        holidays.add(
                new HolidayInfo("Pfingstmontag", pfingstmontag,
                true, false));
        
        // Frohnleichnam
        Calendar frohnleichnam = (Calendar) easter.clone();
        frohnleichnam.add(Calendar.DAY_OF_MONTH, 60);
        holidays.add(
                new HolidayInfo("Frohnleichnam", frohnleichnam,
                true, false));
        
        // Mariä Himmelfahrt
        holidays.add(
                new HolidayInfo("Mariä Himmelfahrt",
                getCalendarOfDate(15, 9),
                true, false));
        
    }
    
    /**
     *
     * @param day
     * @param month
     * @param year
     * @return
     */
    public static Calendar getCalendarOfDate(int day, int month, int year) {
        
        Calendar c = Calendar.getInstance();
        
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.YEAR, year);
        
        return c;
        
    }
    
    /**
     *
     * @param day
     * @param month
     * @return
     */
    public static Calendar getCalendarOfDate(int day, int month) {
        
        Calendar c = Calendar.getInstance();
        
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.MONTH, month - 1);
        
        return c;
        
    }
    
    /**
     * Calculation of easter in a certain year with formula of
     * Carl Friedrich Gauß
     *
     * @param year
     * @return Reference to GregorianCalendar object
     */
    public static Calendar getEastern(int year) {
        
        int c = year / 100;
        int n = year - 19 * (year / 19);
        int k = (c - 17) / 25;
        int l1 = c - c / 4 - (c - k) / 3 + 19 * n + 15;
        int l2 = l1 - 30 * (l1 / 30);
        int l3 = l2 - (l2 / 28) * (1 - (l2 / 28) * (29 / (l2 + 1)) *
                ((l2 - n) / 11));
        int a1 = year + year / 4 + l3 + 2 - c + c / 4;
        int a2 = a1 - 7 * (a1 / 7);
        int l = l3 - a2;
        int month = 3 + (l + 40) / 44;
        int day = l + 28 - 31 * (month / 4);
        
        return new GregorianCalendar(year, month - 1, day);
        
    }
    
    /**
     * Return date of easter of current year
     *
     * @return
     */
    public static Calendar getEastern() {
        return getEastern(Calendar.getInstance().get(Calendar.YEAR));
    }
    
    /**
     *
     * @param calendar
     * @return
     */
    public static boolean isOfficialHoliday(Calendar calendar) {
        
        boolean b = false;
        
        for (HolidayInfo h : holidays) {
            
            if (calendar.equals(h.getCalendar())) {
                b = true;
                break;
            }
            
        }
        
        return b;
        
    }
    
//    /**
//     *
//     * @param args
//     */
//    public static void main(String[] args) {
//        
//        Calendar c = getEastern();
//        System.out.println("Ostern:" +
//                c.get(Calendar.DAY_OF_MONTH) + "." +
//                (c.get(Calendar.MONTH) + 1) + "." +
//                c.get(Calendar.YEAR));
//        c.add(Calendar.DAY_OF_MONTH, 49);
//        System.out.println("Ostern + 49 Tage:" +
//                c.get(Calendar.DAY_OF_MONTH) + "." +
//                (c.get(Calendar.MONTH) + 1) + "." +
//                c.get(Calendar.YEAR));
//        //new java.sql.Date(c.getTimeInMillis());
//        
//    }
    
}
