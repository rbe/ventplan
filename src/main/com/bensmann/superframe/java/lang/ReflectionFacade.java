/*
 * com/bensmann/superframe/java/lang/ReflectionFacade.java
 *
 * ReflectionFacade.java created on 20. Januar 2007, 15:20 by rb
 *
 * Copyright (C) 2006-2007 Ralf Bensmann, java@bensmann.com
 *
 */

package com.bensmann.superframe.java.lang;

import com.bensmann.superframe.exception.ClassGeneratorException;
import com.bensmann.superframe.exception.ReflectionException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Provides a facade to ClassUtil to easily work with generated or dynamically
 * loaded classes
 *
 * @param E 
 * @author rb
 * @version 1.0
 */
public class ReflectionFacade<E> {

    /**
     *
     */
    private static Logger logger;

    /**
     * Class to work on using reflection
     */
//    private Class<E> clazz;
    private Class clazz;

    /**
     * Instance of class
     */
    private E object;

    /**
     *
     */
    private Object[] emptyObjectArray;

    /**
     *
     */
    private List<Method> getterMethods;

    /**
     *
     */
    private List<Method> setterMethods;
    static {
        logger =Logger.getLogger(ReflectionFacade.class.getName());
    }

    /**
     *
     */
    private ReflectionFacade() {
        emptyObjectArray = new Object[] {};
        getterMethods = new ArrayList<Method>();
        setterMethods = new ArrayList<Method>();
    }

    /**
     * Creates a new instance of ReflectionFacade
     * @param clazz
     */
    public ReflectionFacade(Class clazz) {
        this();
        this.clazz = clazz;
    }
//    /**
//     * Creates a new instance of ReflectionFacade
//     * @param clazz
//     */
//    public ReflectionFacade(Class<E> clazz) {
//        this.clazz = clazz;
//        emptyObjectArray = new Object[] {};
//    }

    /**
     * Creates a new instance of ReflectionFacade
     * @param object
     */
    public ReflectionFacade(E object) {
        this();
        this.object = object;
        this.clazz = object.getClass();
    }

    /**
     *
     * @param object
     */
    public void setObject(E object) {
        this.object = object;
    }

    /**
     *
     *
     * @return 
     * @throws com.bensmann.superframe.exception.ReflectionException
     */
    @SuppressWarnings("unchecked")
    public E instantiate() throws ReflectionException {

        try {
            object = (E) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new ReflectionException("Cannot create an instance of class " +
                    clazz.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Cannot create an instance of class " +
                    clazz.getName(), e);
        }

        return object;
    }

    /**
     *
     *
     * @param arguments
     * @return 
     * @throws com.bensmann.superframe.exception.ReflectionException
     */
    @SuppressWarnings("unchecked")
    public E instantiate(Object... arguments) throws ReflectionException {

        Class[] argumentClasses = null;
        Constructor<E> constructor = null;

        argumentClasses =
                ClassUtil.getClassTypeForObject(arguments);
        constructor =
                (Constructor<E>) ClassUtil.findConstructor(clazz, argumentClasses);

        try {
            object = constructor.newInstance(arguments);
        } catch (InstantiationException e) {
            throw new ReflectionException("Cannot create an instance of class " +
                    clazz.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Cannot create an instance of class " +
                    clazz.getName(), e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException("Cannot create an instance of class " +
                    clazz.getName(), e);
        }

        return object;
    }

    /**
     *
     * @throws com.bensmann.superframe.exception.ReflectionException
     * @return
     */
    public String[] getFieldNames() throws ReflectionException {
        return ClassUtil.getFieldNames(clazz);
    }

    /**
     *
     * @param fieldName
     * @throws com.bensmann.superframe.exception.ReflectionException
     * @return
     */
    public boolean isFieldPublic(String fieldName) throws ReflectionException {
        return Modifier.isPublic(ClassUtil.getFieldModifier(clazz, fieldName));
    }

    /**
     *
     * @param fieldName
     * @throws com.bensmann.superframe.exception.ReflectionException
     * @return
     */
    public boolean isFieldTransient(String fieldName) throws ReflectionException {
        return Modifier.isTransient(ClassUtil.getFieldModifier(clazz, fieldName));
    }

    /**
     *
     * @param fieldName 
     * @throws com.bensmann.superframe.exception.ReflectionException
     * @return
     */
    public Class<?> getFieldType(String fieldName) throws ReflectionException {
        return ClassUtil.getFieldType(clazz, fieldName);
    }

    /**
     *
     * @param c 
     * @throws com.bensmann.superframe.exception.ReflectionException
     * @return
     */
    public Field[] getFieldByType(Class c) throws ReflectionException {

        List<Field> list = new LinkedList<Field>();

        for (Field field : ClassUtil.getFields(clazz)) {

            if (ClassUtil.getFieldType(clazz, field.getName()) == c) {
                try {
                    list.add(clazz.getDeclaredField(field.getName()));
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                } catch (NoSuchFieldException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return list.toArray(new Field[list.size()]);
    }

    /**
     *
     * @return
     * @param fieldName
     * @throws com.bensmann.superframe.exception.ReflectionException
     */
    public Object getFieldValue(String fieldName) throws ReflectionException {

        Field field = null;
        Object ret = null;

        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (SecurityException e) {
//            throw new ReflectionException(
//                    "Cannot access field " + fieldName, e);
        } catch (NoSuchFieldException e) {
//            throw new ReflectionException(
//                    "Cannot find field " + fieldName, e);
        }

        if (object == null) {
            instantiate();
        }

        ret =   ClassUtil.getFieldValue(field, object);

        return ret;
    }

    /**
     *
     * @return
     */
    public Method[] getPublicGetterMethods() {

        if (getterMethods.size() == 0) {
            for (Method method : clazz.getMethods()) {
                if (method.getName().startsWith("get")) {
                    getterMethods.add(method);
                }
            }
        }

        return getterMethods.toArray(new Method[getterMethods.size()]);
    }

    /**
     *
     * @param pattern 
     * @return
     */
    public Method[] getPublicGetterMethods(String... pattern) {

        for (Method method : clazz.getMethods()) {

            if (method.getName().startsWith("get")) {

                for (String tmp : pattern) {

                    if (method.getName().indexOf(tmp) >= 0) {
                        getterMethods.add(method);
                    }
                }
            }
        }

        return getterMethods.toArray(new Method[getterMethods.size()]);
    }

    /**
     *
     * @return
     */
    public Method[] getPublicSetterMethods() {

        if (setterMethods.size() == 0) {
            for (Method method : clazz.getMethods()) {
                if (method.getName().startsWith("set")) {
                    setterMethods.add(method);
                }
            }
        }

        return setterMethods.toArray(new Method[setterMethods.size()]);
    }

    /**
     *
     * @param methodName 
     * @return
     * @throws com.bensmann.superframe.exception.ReflectionException 
     */
    public Object invokeMethod(String methodName) throws ReflectionException {

        Object ret = null;
        Method method = null;

        if (object == null) {
            instantiate();
        }

        method =ClassUtil.findMethodByName(clazz, methodName, false);

        try {
            ret = method.invoke(object, emptyObjectArray);
        } catch (IllegalArgumentException e) {
            throw new ReflectionException("", e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("", e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException("", e);
        }

        return ret;
    }

    /**
     *
     * @param methodName 
     * @param arguments
     * @return
     * @throws com.bensmann.superframe.exception.ReflectionException 
     */
    public Object invokeMethod(String methodName, Object... arguments) throws ReflectionException {

        Object ret = null;
        Method method = null;

        if (object == null) {
            instantiate();
        }

        method =ClassUtil.findMethodByName(clazz, methodName, false);

        try {
            ret = method.invoke(object, arguments);
        } catch (IllegalArgumentException e) {
            throw new ReflectionException("", e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("", e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException("", e);
        }

        return ret;
    }

    private static void delta(String text, long start, long stop) {
        System.out.println(text + ": " + ((stop - start) / 1000000d) + " ms");
    }

    public static void main(String[] args) throws ReflectionException,
                                                  ClassGeneratorException {

        long start = 0L;
        long stop = 0L;

//        // Example 1
//        start = System.nanoTime();
//        ReflectionFacade<MyBean> rf = new ReflectionFacade<MyBean>(MyBean.class);
//        stop = System.nanoTime();
//        delta(start, stop);
//
//        start = System.nanoTime();
//        MyBean b = rf.instantiate();
//        stop = System.nanoTime();
//        delta(start, stop);
//
//        start = System.nanoTime();
//        MyBean b2 = rf.instantiate("hallo");
//        stop = System.nanoTime();
//        delta(start, stop);
//        System.out.println("" + rf.invokeMethod("getA"));
        // Example 2
        start = System.nanoTime();
        ClassGenerator cg =
                new ClassGenerator("com.bensmann.test", "Blabla");
        cg.addField("vorname", String.class, true);
        cg.addField("nachname", String.class, false);
        cg.addField("strasse", "java.lang.String", true);
        cg.addField("telefon", String[].class, true);
        cg.addField("intftest", java.util.LinkedList.class, true);
        cg.generateBeanMethods();
        stop = System.nanoTime();
        delta("ClassGenerator", start, stop);

        System.out.println(cg);

        start = System.nanoTime();
        Class<?> c = cg.compileClass();
        stop = System.nanoTime();
        delta("compileClass", start, stop);

        start = System.nanoTime();
        ReflectionFacade rf2 =
                new ReflectionFacade(c);
        rf2.instantiate();
        stop = System.nanoTime();
        delta("instantiate", start, stop);

        start = System.nanoTime();
        rf2.invokeMethod("setVorname", "raaaalf");
        stop = System.nanoTime();
        delta("invokeMethod", start, stop);

        start = System.nanoTime();
        System.out.println(rf2.invokeMethod("getVorname"));
        stop = System.nanoTime();
        delta("invokeMethod", start, stop);

        start = System.nanoTime();
        rf2.getFieldValue("vorname");
        stop = System.nanoTime();
        delta("getFieldValue", start, stop);

        System.out.println("" + rf2.getFieldType("telefon"));

        for (Field field : rf2.getFieldByType(List.class)) {
            System.out.println("" + field.getName());
        }
    }
}