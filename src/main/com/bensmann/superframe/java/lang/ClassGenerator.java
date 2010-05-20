/*
 * com/bensmann/superframe/java/lang/ClassGenerator.java
 *
 * ClassGenerator.java created on 18. Januar 2007, 15:41 by rb
 *
 * Copyright (C) 2006 Ralf Bensmann, java@bensmann.com
 *
 */

package com.bensmann.superframe.java.lang;

import com.bensmann.superframe.exception.ClassGeneratorException;
import com.bensmann.superframe.java.util.StringUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rb
 * @version 1.0
 */
public class ClassGenerator {
    
    /**
     * Package name for generated class
     */
    private String packageName;
    
    /**
     * Class name
     */
    private String className;
    
    /**
     * Interfaces that should be implemented by the generated class
     */
    private List<Class> interfaces;
    
    /**
     * Map for field types (name of field -> type)
     */
    private Map<String, Class> fieldTypeMap;
    
    /**
     * List with field names that are access via bean pattern
     * (getter/setter)
     */
    private List<String> bean;
    
    /**
     * Return type of methods
     */
    private Map<String, Class> methodReturnTypeMap;
    
    /**
     * Types of method parameters
     */
    private Map<String, Class[]> methodParameterTypeMap;
    
    /**
     * Names of method parameters
     */
    private Map<String, String[]> methodParameterName;
    
    /**
     * Source code for body of methods
     */
    private Map<String, String> methodBody;
    
    /**
     * Array of type Class representing "an empty Class array"
     */
    private Class<?>[] emptyClassArray;
    
    /**
     * Line separator (remember \n on Unix, \r\n on Windows, \r on Mac)
     */
    private String lineSeparator;
    
    /**
     * Creates a new instance of ClassGenerator
     *
     * @param packageName
     * @param className
     */
    public ClassGenerator(String packageName, String className) {
        
        this.packageName = packageName.toLowerCase();
        this.className = StringUtil.toUpperCaseFirst(className);
        
        emptyClassArray = new Class[] {};
        
        // Initialize lists
        interfaces = new LinkedList<Class>();
        bean = new LinkedList<String>();
        
        // Initialize maps
        fieldTypeMap = new Hashtable<String, Class>();
        methodReturnTypeMap = new Hashtable<String, Class>();
        methodParameterTypeMap = new Hashtable<String, Class[]>();
        methodParameterName = new Hashtable<String, String[]>();
        methodBody = new Hashtable<String, String>();
        
        // Fetch operating system specific line separator
        lineSeparator = System.getProperty("line.separator");
        
    }
    
    /**
     *
     * @param clazz
     * @return
     */
    private String getClassTypeName(Class clazz) {
//        java.lang.String t = clazz.getName();
//        if (t.indexOf("[") >= 0) {
//            StringBuffer sb = new StringBuffer(clazz.getCanonicalName());
//            for (int j = 0; j < t.length(); j++) {
//                if (t.charAt(j) == '[') {
//                    sb.append("[]");
//                }
//            }
//            t = sb.toString();
//        }
//        return t;
        return clazz.getCanonicalName();
    }
    
    /**
     * Add an interface that the generated class should implement
     *
     * @param intf
     */
    public void addInterface(Class intf) {
        interfaces.add(intf);
    }
    
    /**
     * Add an interface that the generated class should implement
     *
     * @param intf
     */
    public void addInterface(String intf) throws ClassGeneratorException {
        
        Class ic = null;
        
        try {
            ic = Class.forName(intf);
            interfaces.add(ic);
        } catch (ClassNotFoundException e) {
            throw new ClassGeneratorException(
                    "Cannot find interface " + intf, e);
        }
        
    }
    
    /**
     * Add a field to the class
     *
     * @param name Name of field
     * @param type Type of field
     * @param bean Wether this field is accessed via a bean pattern or not
     */
    public void addField(String name, Class type, boolean bean) {
        
        fieldTypeMap.put(name, type);
        
        if (bean) {
            this.bean.add(name);
        }
        
    }
    
    /**
     *
     * @param name
     * @param type
     */
    public void addField(String name, Class type) {
        addField(name, type, false);
    }
    
    /**
     *
     * @param name
     * @param type
     */
    public void addField(String name, String type, boolean bean)
    throws ClassGeneratorException {
        
        Class t = null;
        
        try {
            t = Class.forName(type);
            addField(name, t, bean);
        } catch (ClassNotFoundException e) {
            throw new ClassGeneratorException(
                    "Cannot add field " + name + " of type " + type
                    + ": class not found", e);
        }
        
    }
    
    /**
     *
     * @param name
     * @param type
     */
    public void addField(String name, String type)
    throws ClassGeneratorException {
        addField(name, type, false);
    }
    
    /**
     *
     *
     * @param name
     * @param returnType
     * @param argumentNames Names of arguments; can be null
     * @param parameterTypes Types of parameters corresponding to
     * argumentNames; can be null
     */
    public void addMethod(
            String name, Class returnType,
            String[] argumentNames, Class... argumentTypes) {
        
        int i = 0;
        Class<?>[] classArray = null;
        
        // Save return type of method
        if (returnType == null) {
            returnType = void.class;
        }
        methodReturnTypeMap.put(name, returnType);
        
        if (argumentTypes.length > 0) {
            
            // Generate Class[] to hold all argument types
            classArray = new Class[argumentTypes.length];
            for (Class c : argumentTypes) {
                classArray[i++] = c;
            }
            // Save argument types array
            methodParameterTypeMap.put(name, classArray);
            
            // Argument names are generated names when no argument names
            // but types were given
            if (argumentNames == null && argumentTypes.length > 0) {
                
                argumentNames = new String[argumentTypes.length];
                
                for (i = 0; i < argumentTypes.length; i++) {
                    argumentNames[i] = "arg" + i;
                }
                
            }
            
            methodParameterName.put(name, argumentNames);
            
        }
        
    }
    
    /**
     *
     * @param method
     */
    public void addMethod(Method method) {
        
        addMethod(
                method.getName(),
                method.getReturnType(),
                null,
                emptyClassArray);
        
    }
    
    /**
     *
     * @param name
     * @param body
     */
    public void addMethodBody(String name, String body) {
        methodBody.put(name, body);
    }
    
    /**
     *
     */
    public void implementInterfaces() {
        
        String methodName = null;
        Method[] methods = null;
        
        for (Class intf : interfaces) {
            
            methods = intf.getDeclaredMethods();
            for (Method m : methods) {
                addMethod(m);
            }
            
        }
        
    }
    
    /**
     *
     * @return
     */
    public void generateBeanMethods() {
        
        String methodName = null;
        
        for (String fieldName : bean) {
            
            // Getter
            methodName = "get" + StringUtil.toUpperCaseFirst(fieldName);
            addMethod(
                    methodName,
                    fieldTypeMap.get(fieldName),
                    null,
                    emptyClassArray);
            addMethodBody(
                    methodName,
                    "return " + fieldName + ";");
            
            // Setter
            methodName = "set" + StringUtil.toUpperCaseFirst(fieldName);
            addMethod(
                    methodName,
                    void.class,
                    new String[] { fieldName },
                    new Class[] { fieldTypeMap.get(fieldName) });
            addMethodBody(
                    methodName,
                    "this." + fieldName + " = " + fieldName + ";");
            
        }
        
    }
    
    /**
     *
     * @return
     */
    private String generateFileHeader() {
        
        String fullName = packageName + "." + className;
        String fileName = fullName.replaceAll("\\.", "/") + ".java";
        
        return
                "// Class " + fullName + " (" + fileName + ")" + lineSeparator
                + "// Generated on-the-fly at " + new Date() + lineSeparator
                + "package " + packageName + ";" + lineSeparator;
        
    }
    
    /**
     *
     * @return
     */
    private String generateClassHeader() {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("public final class ").append(className);
        
        // Interfaces
        if (interfaces.size() > 0) {
            
            sb.append(" implements ");
            
            for (int i = 0; i < interfaces.size(); i++) {
                
                sb.append(interfaces.get(i).getName());
                
                if (i < interfaces.size() - 1) {
                    sb.append(", ");
                }
                
            }
            
        }
        
        sb.append(" {").append(lineSeparator);
        
        // Empty contructor
        sb.append("\tpublic ").
                append(className).
                append("() {").
                append(lineSeparator).
                append("\t}").
                append(lineSeparator);
        
        return sb.toString();
        
    }
    
    /**
     *
     * @return
     */
    private String generateFields() {
        
        StringBuffer body = new StringBuffer();
        Iterator<String> keys = null;
        String key = null;
        
        // Fields
        keys = fieldTypeMap.keySet().iterator();
        while (keys.hasNext()) {
            
            key = keys.next();
            
            body.append("\tprivate ").
                    append(getClassTypeName(fieldTypeMap.get(key))).
                    append(" ").
                    append(key).
                    append(";").
                    append(lineSeparator);
            
        }
        
        return body.toString();
        
    }
    
    /**
     *
     * @param methodName
     * @return
     */
    private String generateMethod(String methodName) {
        
        StringBuffer body = new StringBuffer();
        Iterator<String> methodParameterIterator = null;
        String parameterName = null;
        Class c = null;
        Class[] cc = null;
        String returnType = null;
        String bd = null;
        
        c = methodReturnTypeMap.get(methodName);
        if (c == null || c == void.class) {
            returnType = "void";
        } else {
            returnType = getClassTypeName(c);
        }
        
        body.append("\tpublic final ").
                append(returnType).
                append(" ").
                append(methodName).
                append("(");
        
        // Parameter; Class[]
        cc = methodParameterTypeMap.get(methodName);
        if (cc != null) {
            
            int ccLength = cc.length;
            
            for (int i = 0; i < ccLength; i++) {
                
                body.append(getClassTypeName(cc[i]));
                body.append(" ");
                body.append(methodParameterName.get(methodName)[i]);
                
                if (i < ccLength - 1) {
                    body.append(", ");
                }
                
            }
            
        }
        
        body.append(") {").append(lineSeparator);
        
        bd = methodBody.get(methodName);
        if (bd == null) {
            bd = "";
        }
        body.append(bd);
        
        body.append(lineSeparator).append("\t}").append(lineSeparator);
        
        return body.toString();
        
    }
    
    /**
     *
     * @return
     */
    private String generateMethods() {
        
        StringBuffer body = new StringBuffer();
        Iterator<String> methodNameIterator = null;
        
        methodNameIterator = methodReturnTypeMap.keySet().iterator();
        
        while (methodNameIterator.hasNext()) {
            body.append(generateMethod(methodNameIterator.next()));
        }
        
        return body.toString();
        
    }
    
    /**
     *
     * @return
     */
    private String generateClassBody() {
        return generateFields() + generateMethods();
    }
    
    /**
     *
     * @return
     */
    private String generateFileFooter() {
        return "}" + lineSeparator;
    }
    
    /**
     * Return contents of generated Java source code file
     *
     * @return
     */
    public String toString() {
        
        return generateFileHeader()
        + generateClassHeader()
        + generateClassBody()
        + generateFileFooter();
        
    }
    
    /**
     *
     *
     * @return
     * @throws com.bensmann.superframe.exception.ClassGeneratorException
     */
    public Class<?> compileClass() throws ClassGeneratorException {
        
        Class<?> clazz = null;
        File tempFile = null;
        
        try {
            tempFile = File.createTempFile("JCG", ".java");
        } catch (IOException e) {
            throw new ClassGeneratorException(
                    "Could not create Java source file", e);
        }
        tempFile = new File(tempFile.getParentFile(), className + ".java");
        
        try {
            
            clazz = CompilerUtil.createAndCompileJavaSource(
                    tempFile,
                    toString(),
                    new String[] { "-g:none" });
            
        } catch (IOException e) {
            throw new ClassGeneratorException("", e);
        } catch (ClassNotFoundException e) {
            throw new ClassGeneratorException("", e);
        }
        
        return clazz;
        
    }
    
    public static void main(String[] args) throws Exception {
        
        ClassGenerator cg = new ClassGenerator("com.bensmann.test", "Blabla");
        cg.addInterface("java.io.Serializable");
        cg.addInterface(Runnable.class);
        cg.implementInterfaces();
        cg.addField("vorname", String.class, true);
        cg.addField("nachname", String.class, false);
        cg.addField("strasse", "java.lang.String", true);
        cg.addField("telefon", String[].class, true);
        cg.generateBeanMethods();
        cg.addMethod(
                "dududu",
                null,
                null,
                String.class, URL.class);
        cg.addMethodBody("dududu", "System.out.println(arg0);");
        
        System.out.println(cg);
        
        Class<?> c = cg.compileClass();
        Object o = c.newInstance();
        Method m = c.getDeclaredMethod(
                "setVorname",
                new Class<?>[] { String.class });
        m.invoke(o,
                new Object[] { "bהההההההh"} );
        System.out.println("" +
                c.getDeclaredMethod("getVorname", new Class[] { }).invoke(o));
        
    }
    
}
