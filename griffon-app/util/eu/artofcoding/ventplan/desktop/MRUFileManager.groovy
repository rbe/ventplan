/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 17:23
 */
package eu.artofcoding.ventplan.desktop

/**
 * WAC-161: Zuletzt geöffnete Projekte
 * Save and load preferences for a Most Recently Used (MRU) list.
 */
class MRUFileManager {

    private static final String LAST_TEN_OPEN_PROJECTS = "LastTenProjects"
    private static final String PREFS_USER_NODE = "/ventplanprojects";
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
            for (int i = 0; i < mruFileList.size(); i++) {
                if (i < DEFAULT_MAX_SIZE) {
                    def f = mruFileList.get(i)
                    if (f instanceof java.io.File) {
                        getPrefs().put('' + i, f.getAbsolutePath());
                    }
                    else {
                        getPrefs().put('' + i, f);
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
        //def o = mruFileList.get(index)
        def o = mruFileList.remove(index);
        mruFileList.addFirst(o);
    }

    protected void setMRU(File f) {
        if (null != f) { // java.lang.NullPointerException: Cannot invoke method getAbsolutePath() on null object
            setMRU(f.getAbsolutePath())
        }
    }

    /**
     * Adds an object to the mru.
     */
    protected void setMRU(String s) {
        def file = new java.io.File(s.toString())
        if (file.exists()) {
            def contains = false
            mruFileList.each {
                if (it.equals(s)) {
                    contains = true
                }
            }
            if (!contains) {
                mruFileList.addFirst(s);
                setMaxSize(DEFAULT_MAX_SIZE);
            } else {
                int index = mruFileList.indexOf(s);
                moveToTop(index);
            }
        } else {
            //println "setMRU: file does not exists. Could not add. ${file?.dump()}"
        }
    }

    public String getPrefValue(int i) {
        String value = null;
        try {
            value = getPrefs().get('' + i, '');
        } catch (Exception e) {
            //println "getPrefValue -> ${e}"
            e.printStackTrace()
        }
        return value;
    }

    /**
     * Loads the MRU file list in from a file and stores it in a LinkedList.
     * If no file exists, a new LinkedList is created.
     */
    protected void load() {
        if (null == mruFileList) {
            mruFileList = new LinkedList();
        }
        for (int i = 0; i < DEFAULT_MAX_SIZE; i++) {
            try {
                String value = getPrefValue(i);
                if (value) {
                    setMRU(value);
                }
            } catch (Exception e) {
                e.printStackTrace()
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
        if (maxSize < mruFileList.size()) {
            for (int i = 0; i < mruFileList.size() - maxSize; i++) {
                mruFileList.removeLast();
            }
        }
        currentMaxSize = maxSize;
    }

}
