/*
 * com/bensmann/superframe/java/lang/CompilerUtil.java
 *
 * CompilerUtil.java created on 18. Januar 2007, 15:38 by rb
 *
 * Copyright (C) 2006 Ralf Bensmann, java@bensmann.com
 *
 */

package com.bensmann.superframe.java.lang;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 *
 * @author rb
 * @version 1.0
 */
public class CompilerUtil {
    
    /**
     * Creates a new instance of CompilerUtil
     */
    private CompilerUtil() {
    }
    
    /**
     *
     *
     * @param javaFile
     * @param source
     * @param compilerOptions
     * @return
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public static Class<?> createAndCompileJavaSource(
            File javaFile, String source, String[] compilerOptions)
            throws IOException, ClassNotFoundException {
        
        // Save Java code in temporary directory for source code
        PrintWriter out = new PrintWriter(javaFile);
        out.print(source);
        out.close();
        
        // Return compiled class
        return compileJavaSource(javaFile, compilerOptions);
        
    }
    
    /**
     *
     *
     * @param javaFile
     * @param compilerOptions
     * @return
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public static Class<?> compileJavaSource(
            File javaFile, String[] compilerOptions)
            throws IOException, ClassNotFoundException {
        
        File parentFile = null;
        JavaCompiler javaCompiler = null;
        StandardJavaFileManager fileManager = null;
        Iterable<? extends JavaFileObject> fileObjects = null;
        JavaNameScanner nameScanner = null;
        URL[] urls = null;
        URLClassLoader urlClassLoader = null;
        Class<?> clazz = null;
        
        // Parent directory for Java source file
        parentFile = javaFile.getParentFile();
        
        // Java compiler object
        javaCompiler = ToolProvider.getSystemJavaCompiler();
        
        // Standard Java file manager object
        fileManager =
                javaCompiler.getStandardFileManager(null, null, null);
        
        //
        fileObjects = fileManager.getJavaFileObjects(javaFile);
        
        // Start reading Java source file for finding package name as
        // thread
        nameScanner = new JavaNameScanner(javaFile);
        new Thread(nameScanner).start();
        
        // Compiler options
        List<String> opt = null;
        opt = new LinkedList<String>();
        for (String s : compilerOptions) {
            opt.add(s);
        }
        opt.add("-d");
        opt.add(javaFile.getParent());
        
        // Start compilation (thread)
        javaCompiler.getTask(
                null,
                fileManager,
                null,
                opt,
                null,
                fileObjects).call();
        
        // Close file manager
        fileManager.close();
        
        // Load generated class via URLClassLoader
        urls = new URL[] { parentFile.toURI().toURL() };
        urlClassLoader = new URLClassLoader(urls);
        
        // Wait for thread to complete
        synchronized (nameScanner) {
            
            try {
                
                while (nameScanner.getPackageName() == null
                        && nameScanner.getClassName() == null) {
                    
                    nameScanner.wait();
                    
                }
                
                clazz = urlClassLoader.loadClass(
                        nameScanner.getPackageName()
                        + "."
                        + nameScanner.getClassName());
                
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            
        }
        
        javaFile.delete();
        
        return clazz;
        
    }
    
    public static void main(String[] args) throws Exception {
        
        File javaSourceFile = new File("c:/TEMP/Test2.java");
        
        Class<?> c =
                CompilerUtil.createAndCompileJavaSource(
                javaSourceFile,
                "package com.bensmann.superframe.bla;\r\n"
                + "public class Test2 {\r\n"
                + "public void dudu() { System.out.println(\"dudu\"); }\r\n"
                + "}\r\n",
                new String[] { "-g:none" });
        
        Object object = c.newInstance();
        c.getMethod("dudu", new Class<?>[] {}).invoke(object);
        
    }
    
}
