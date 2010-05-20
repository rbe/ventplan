/*
 * ReloadableClassLoader.java
 *
 * Created on 25.09.2007, 14:26:23
 *
 */

package com.bensmann.superframe.java.lang;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 *
 * @author Ralf_Bensmann
 */
public class ReloadableClassLoader extends URLClassLoader {

    /**
     *
     */
    private static Logger logger;

    /**
     * List of JAR files where classes should be reloadable
     */
    private List<URL> jarUrls;

    /**
     *
     */
    private Map classes = new ConcurrentHashMap();
    static {
        logger =Logger.getLogger(ReloadableClassLoader.class.getName());
    }

    /**
     *
     * @param urls
     */
    public ReloadableClassLoader(URL[] urls) {
        super(urls);
    }

    @Override
    public Class loadClass(String className) throws ClassNotFoundException {
        return findClass(className);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public Class findClass(String className) {
        Class result = null;
        // TODO Lookup class in cache
//            result = (Class) classes.get(className);
//            if (result != null) {
//                // Log
//                logger.finest("Returning cached class " + className);
//                return result;
//            }
        // Load system class (e.g. Object)
        try {
            return findSystemClass(className);
        } catch (Exception e) {
        }
        // Load (re)loadable class by myself
        try {
            // Convert package notation into path
            String name = className.replace('.', '/') + ".class";
            // Log
            logger.finest("Loading class " + className + " from " + name);
            //
            URL classURL = getResource(name);
            //
            String classPath = classURL.getFile();
            byte[] classByte = loadClassData(classPath);
            //
//                CodeSource cs =
//                        new CodeSource(new URL(""),
//                        new CodeSigner[] {});
            ProtectionDomain protectionDomain =
                    new ProtectionDomain(null, null);
            //
            result =defineClass(className, classByte, 0, classByte.length,
                    protectionDomain);
            // Cache class
            classes.put(className, result);
            // Log
            logger.finest("Loaded class " + className + " " + classByte.length +
                    " bytes");
        } catch (ClassFormatError e) {
            result = null;
            // Log
            logger.severe("Could not load class " + className + ": " + e);
        } catch (NoClassDefFoundError e) {
            result = null;
            // Log
            logger.severe("Could not load class " + className + ": " + e);
        } catch (SecurityException e) {
            result = null;
            // Log
            logger.severe("Could not load class " + className + ": " + e);
        } catch (IOException e) {
            result = null;
            // Log
            logger.severe("Could not load class " + className + ": " + e);
        }
        // Keep cache clean if we could not (re)load the class
        if (result == null) {
            classes.remove(className);
        }
        return result;
    }

    /**
     *
     */
    private byte[] loadClassData(String className) throws IOException {
        File f = new File(className);
        byte[] buff = new byte[(int) f.length()];
        DataInputStream dis =
                new DataInputStream(new FileInputStream(f));
        dis.readFully(buff);
        dis.close();
        return buff;
    }
//    public static void main(String[] args) throws Exception {
//        String s =
//                new File("c:/workspace/Java/IRB/Test/build/classes/").toURI().
//                toASCIIString();
//        while (true) {
//            Class c =
//                    new ReloadableClassLoader(new URL[] {new URL(s)}).getClass("test.Main");
//            Object o = c.newInstance();
//            @SuppressWarnings(value = "unchecked")
//            Method m = c.getDeclaredMethod("test", new Class[] {});
//            m.invoke(o, new Object[] {});
//            Thread.sleep(5 * 1000);
//        }
}