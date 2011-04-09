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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.prefs.BackingStoreException

/**
 * WAC-161: Zuletzt geöffnete Projekte
 * Save and load preferences for a Most Recently Used (MRU) list.
 */
class MRUFileManager {
    
    
    private static final String LAST_TEN_OPEN_PROJECTS = "LastTenWacProjects"
    private static final String PREFS_USER_NODE = "/wac/projects";
    private static final int DEFAULT_MAX_SIZE = 10;

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

    //--------------------------------------------------------------------------
    //   Constructors:
    //--------------------------------------------------------------------------
    public MRUFileManager() {
        load();
        setMaxSize(DEFAULT_MAX_SIZE);
    }

    public MRUFileManager(int maxSize) {
        load();
        setMaxSize(maxSize);
    }
    //--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------

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
                    getPrefs().put("" + i, mruFileList.get(i));
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
        mruFileList.add(0, mruFileList.remove(index));
    }

    /**
     * Adds an object to the mru.
     */
    protected void setMRU(Object o) {
        int index = mruFileList.indexOf(o);

        if (index == -1) {
            mruFileList.add(0, o);
            setMaxSize(currentMaxSize);
        } else {
            moveToTop(index);
        }
    }
    
    public String getPrefValue(int i) {
        String value = null;
        try{
            value = getPrefs().get("" + i);
        }
        catch (Exception e) {
            
        }
        return value;
    }

    /**
     * Loads the MRU file list in from a file and stores it in a LinkedList.
     * If no file exists, a new LinkedList is created.
     */
    protected void load() {
        
        mruFileList = new LinkedList();
        for (int i = 0; i < DEFAULT_MAX_SIZE; i++)
        {
            try{
                String value = getPrefValue(i);
                if (null != value)
                {
                    setMRU(value);
                }
            }
            catch (Exception e) {
                // key is not in prefs...
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
