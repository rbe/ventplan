/**
 * /Users/rbe/project/wac2/griffon-app/util/com/westaflex/wac/CheckUpdate.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import java.util.LinkedList;
import java.util.prefs.BackingStoreException

/**
 * WAC-161: Zuletzt geöffnete Projekte
 * Save and load preferences for a Most Recently Used (MRU) list.
 */
class MRUFileManager {

    public static boolean DEBUG = false    
    
    private static final String LAST_TEN_OPEN_PROJECTS = "LastTenWacProjects"
    private static final String PREFS_USER_NODE = "/wacprojects";
    public static final int DEFAULT_MAX_SIZE = 10;

    private int currentMaxSize = 0;
    private LinkedList mruFileList;
    
    private static MRUFileManager INSTANCE = null;
    
    public static MRUFileManager getInstance() {
        if (null == INSTANCE) {
            return new MRUFileManager();
        }
        else {
            return INSTANCE;
        }
    }

    private MRUFileManager() {
        load();
        setMaxSize(DEFAULT_MAX_SIZE);
    }

    /**
     * Saves a list of MRU files out to a file.
     */
    public void save() {
        try {
            // Remove node and save the new list...
            getPrefs().removeNode();
            for (int i = 0; i < mruFileList.size(); i++)
            {
                if (i < DEFAULT_MAX_SIZE) {
                    def f = mruFileList.get(i)
                    if (f instanceof java.io.File) {
                        getPrefs().put("" + i, f.getAbsolutePath());
                    }
                    else {
                        getPrefs().put("" + i, f);
                    }
                }
            }
            getPrefs().flush();
        } catch (Exception e) {
            // do nothing
            e.printStackTrace();
        }
    }

    /**
     * Gets the size of the MRU file list.
     */
    public int size() {
        return mruFileList.size();
    }

    /**
     * Adds a file name to the MRU file list.
     */
    public void set(File file) {
        setMRU(file);
    }

    /**
     * Adds a string to the MRU file list.
     */
    public void set(String string) {
        setMRU(string);
    }

    /**
     * Gets the list of files stored in the MRU file list.
     */
    public String[] getMRUFileList() {
        if (size() == 0) {
            return null;
        }

        String[] ss = new String[size()];

        for (int i = 0; i < size(); i++) {
            String s = getPrefValue(i);
            if (null != s) {
                ss[i] = s;
            }
        }

        return ss;
    }

    /**
     * Moves the the index to the top of the MRU List
     *
     * @param index The index to be first in the mru list
     */
    public void moveToTop(int index) {
        def o = mruFileList.get(index)
        mruFileList.remove(o);
        mruFileList.addFirst(o);
    }

    /**
     * Adds an object to the mru.
     */
    protected void setMRU(Object o) {
        int index = mruFileList.indexOf(o);

        def file = new java.io.File(o.toString())
        if (file.exists()) {
            if (index == -1) {
                if (DEBUG) println "setMRU: add file to list -> ${mruFileList.dump()}"
                mruFileList.add(0, o);
                setMaxSize(DEFAULT_MAX_SIZE);
            } else {
                if (DEBUG) println "setMRU: move file to top -> ${mruFileList.dump()}"
                moveToTop(index);
            }
        }
        else {
            if (DEBUG) println "setMRU: file does not exists. Could not add. ${file?.dump()}"
        }
    }
    
    public String getPrefValue(int i) {
        String value = null;
        try{
            value = getPrefs().get("" + i, "");
            if (DEBUG) println "getPrefValue value -> ${value}, index=${index}"
        }
        catch (Exception e) {
            if (DEBUG) println "getPrefValue -> ${e}"
        }
        return value;
    }

    /**
     * Loads the MRU file list in from a file and stores it in a LinkedList.
     * If no file exists, a new LinkedList is created.
     */
    protected void load() {
        
        if (DEBUG) println "preferences absolute path -> ${getPrefs().absolutePath()}"
        
        if (null == mruFileList) {
            mruFileList = new LinkedList();
        }
        for (int i = 0; i < DEFAULT_MAX_SIZE; i++)
        {
            try{
                String value = getPrefValue(i);
                if (null != value && value.length() > 0)
                {
                    setMRU(value);
                }
            }
            catch (Exception e) {
                // key is not in prefs...
                break
            }
        }

    }

    protected java.util.prefs.Preferences getPrefs() {
        return java.util.prefs.Preferences.userRoot().node(PREFS_USER_NODE);
    }

    /**
     * Ensures that the MRU list will have a MaxSize.
     */
    protected void setMaxSize(int maxSize) {
        if (DEBUG) println "setMaxSize ... ${maxSize < mruFileList.size()}"
        if (DEBUG) println "setMaxSize ...maxSize = ${maxSize}"
        if (DEBUG) println "setMaxSize ...mruFileList.size() = ${mruFileList.size()}"
        if (maxSize < mruFileList.size()) {
            for (int i = 0; i < mruFileList.size() - maxSize; i++) {
                if (DEBUG) println "removeLast ... ${mruFileList.dump()}"
                mruFileList.removeLast();
            }
        }

        currentMaxSize = maxSize;
    }

    
}