/*
 * ClassUtil.java
 *
 * Created on 15. Juli 2006, 17:07
 *
 */

package com.bensmann.superframe.java.lang;

import com.bensmann.superframe.exception.ReflectionException;
import com.bensmann.superframe.java.util.StringUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * $Header$
 * @author rb
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public class ClassUtil {
    
    /**
     *
     */
    private static Class<?>[] emptyClassArray;
    
    /**
     *
     */
    static {
        emptyClassArray = new Class<?>[] {};
    }
    
    /**
     * Do not create a new instance of ClassUtil
     */
    protected ClassUtil() {
    }
    
    /**
     * Convert array of objects to array of classes of every object
     *
     * @param objects
     * @return
     */
    public static Class<?>[] getClassTypeForObject(Object... objects) {
        
        Class[] clazzez = new Class[objects.length];
        int i = 0;
        
        for (Object o : objects) {
            clazzez[i++] = o.getClass();
        }
        
        return clazzez;
        
    }
    
    /**
     *
     * @param clazz
     * @param argumentTypes
     * @return
     */
    public static <E> Constructor<E> findConstructor(
            Class<E> clazz, Class<?>... argumentTypes)
            throws ReflectionException {
        
        Constructor<E> constructor = null;
        
        try {
            constructor = (Constructor<E>) clazz.getConstructor(argumentTypes);
        } catch (SecurityException e) {
            throw new ReflectionException("Cannot get constructor", e);
        } catch (NoSuchMethodException e) {
            throw new ReflectionException(
                    "Cannot find requested constructor", e);
        }
        
        return constructor;
        
    }
    
    /**
     * Try to find a certain method in a class
     *
     * @param clazz
     * @param methodName
     * @param ignoreCase
     * @return
     */
    public static Method findMethodByName(
            Class<?> clazz, String methodName, boolean ignoreCase) {
        
        Method foundMethod = null;
        String tmpMethodName = null;
        Method[] methods = clazz.getDeclaredMethods();
        
        if (ignoreCase) {
            methodName = methodName.toLowerCase();
        }
        
        // Look at every method in class
        for (Method method : methods) {
            
            foundMethod = method;
            tmpMethodName = foundMethod.getName();
            
            if (ignoreCase) {
                tmpMethodName = tmpMethodName.toLowerCase();
            }
            
            if (tmpMethodName.equals(methodName)) {
                break;
            }
            
            // We didn't find a method (break-statement inside if above
            // would have exited this loop before the following statement)
            foundMethod = null;
            
        }
        
        return foundMethod;
        
    }
    
    /**
     * Look for a getter-method inside a class
     *
     * @param clazz
     * @param fieldName
     * @param ignoreCase
     * @return
     */
    public static Method getGetterMethodByName(
            Class clazz, String fieldName, boolean ignoreCase) {
    
        return findMethodByName(clazz,
                "get" + StringUtil.toUpperCaseFirst(fieldName),
                ignoreCase);
        
    }
    
    /**
     * Look for a setter-method inside a class
     *
     * @param clazz
     * @param fieldName
     * @param ignoreCase
     * @return
     */
    public static Method getSetterMethodByName(
            Class clazz, String fieldName, boolean ignoreCase) {
        
        return findMethodByName(clazz,
                "set" + StringUtil.toUpperCaseFirst(fieldName),
                ignoreCase);
        
    }
    
    /**
     * 
     * 
     * @param clazz 
     * @param haveModifier Wether the field should have the given modifiers
     * or not
     * @param modifiers 
     * @return 
     */
    public static Field[] getFields(
            Class clazz, boolean haveModifier, int... modifiers) {
        
        List<Field> fields = null;
        Field[] clazzFields = null;
        int modifier = 0;
        boolean test = false;
        
        clazzFields = clazz.getDeclaredFields();
        fields = new LinkedList<Field>();
        
        for (Field f : clazzFields) {
            
            modifier = f.getModifiers();
            System.out.println("modifier of " + f.getName() + "="+modifier);
            
            // Look for modifier
            for (int mod : modifiers) {
                
                if (haveModifier) {
                    test = (mod & modifier) == mod;
                } else {
                    test = (mod & modifier) != mod;
                }
                
                if (test) {
                    fields.add(f);
                }
                
            }
            
        }
        
        return fields.toArray(new Field[fields.size()]);
        
    }
    
    /**
     * 
     * 
     * @param clazz 
     * @param haveModifier Wether the field should have the given modifiers
     * or not
     * @param modifiers 
     * @return 
     */
    public static Method[] getMethods(
            Class clazz, boolean haveModifier, int... modifiers) {
        
        List<Method> methods = null;
        Method[] clazzMethods = null;
        int modifier = 0;
        boolean test = false;
        
        clazzMethods = clazz.getDeclaredMethods();
        methods = new LinkedList<Method>();
        
        for (Method m : methods) {
            
            modifier = m.getModifiers();
            
            // Look for modifier
            for (int mod : modifiers) {
                
                if (haveModifier) {
                    test = (mod & modifier) == mod;
                } else {
                    test = (mod & modifier) != mod;
                }
                
                if (test) {
                    methods.add(m);
                }
                
            }
            
        }
        
        return methods.toArray(new Method[methods.size()]);
        
    }
    
    /**
     * Try to get the value from a certain field of a certain object by
     * accessing the field directly (a public field) or by using a getter
     * method (private field/bean pattern).
     *
     * @param field
     * @param object
     * @return
     * @throws com.bensmann.superframe.exception.ReflectionException
     */
    public static Object getFieldValue(Field field, Object object)
    throws ReflectionException {
        
        Object value = null;
        int modifier = 0;
        Method method = null;
        boolean gotViaPublic = false;
        boolean gotViaAccessor = false;
        
        String generic = field.toGenericString();
        modifier = field.getModifiers();
        
        if (Modifier.isTransient(modifier)) {
            
            // public
            try {
                value = field.get(object);
                gotViaPublic = true;
            } catch (IllegalArgumentException e) {
                value = null;
            } catch (IllegalAccessException e) {
                value = null;
            }
            
        } else {
            
            // private, so try to use a getter
            method = getGetterMethodByName(
                    object.getClass(), field.getName(), false);
            
            try {
                value = method.invoke(object);
                gotViaAccessor = true;
            } catch (InvocationTargetException e) {
                value = null;
            } catch (IllegalAccessException e) {
                value = null;
            }
            
        }
        
        if (!gotViaPublic && !gotViaAccessor) {
            throw new ReflectionException(
                    "Could not get value from field " + field.getName());
        }
        
        return value;
        
    }
    
    /**
     *
     * @throws com.bensmann.superframe.exception.ReflectionException
     * @return
     */
    public static String[] getFieldNames(Class clazz) throws ReflectionException {
        
        Field[] fields = clazz.getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fieldNames.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        
        return fieldNames;
        
    }
    
    /**
     * 
     * @param clazz 
     * @throws com.bensmann.superframe.exception.ReflectionException 
     * @return 
     */
    public static Field[] getFields(Class clazz) throws ReflectionException {
        return clazz.getDeclaredFields();
    }
    
    /**
     * 
     * @param clazz 
     * @param fieldName 
     * @throws com.bensmann.superframe.exception.ReflectionException 
     * @return 
     */
    public static int getFieldModifier(Class clazz, String fieldName)
    throws ReflectionException {
        
        try {
            return clazz.getDeclaredField(fieldName).getModifiers();
        } catch (SecurityException e) {
            throw new ReflectionException("", e);
        } catch (NoSuchFieldException e) {
            throw new ReflectionException("", e);
        }
        
    }
    
    /**
     * 
     * @param clazz 
     * @param fieldName 
     * @throws com.bensmann.superframe.exception.ReflectionException 
     * @return 
     */
    public static Class<?> getFieldType(Class clazz, String fieldName)
    throws ReflectionException {
        
        Field field = null;
        Class<?> type = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            type = field.getType();
        } catch (SecurityException e) {
            throw new ReflectionException(
                    "Security: cannot access field " + fieldName, e);
        } catch (NoSuchFieldException e) {
            throw new ReflectionException("Cannot find field " + fieldName, e);
        }
        
        return type;
        
    }
    
//    /**
//     *
//     * @deprecated Use getFieldValue and check for transient fields by yourself
//     * @param field
//     * @param object
//     * @return
//     */
//    public static Object getValueFromField(Field field, Object object)
//    throws ReflectionException {
//        
//        int modifier = 0;
//        Object value = null;
//        
//        modifier = field.getModifiers();
//        
//        // Ignore transient fields
//        if (Modifier.isTransient(modifier)) {
//            value = null;
//        } else {
//            value = getFieldValue(field, object);
//        }
//        
//        return value;
//        
//    }
    
    public static void main(String[] args) {
        
//        1024 512 256 128 64 32 16 8 4 2 1
//           0   0   0   1  0  0  0 0 0 0 0
//        1024 512 256 128 64 32 16 8 4 2 1
//           0   0   0   1  0  0  0 0 0 1 0
//        int[] i = new int[11];
//        i[0] = 1;
//        for (int j = 1; j < i.length; j++) {
//            i[j] = i[j - 1] * 2;
//        }
//        for (int j = 0; j < i.length; j++) {
//            System.out.println(i[j]);
//        }
        System.out.println("129 | 128=" + (129 | 128));
        System.out.println("129 |   1=" + (129 | 1));
        System.out.println("129 |   2=" + (129 | 2));
        System.out.println("129 & 128=" + (129 & 128));
        System.out.println("129 &   1=" + (129 & 1));
        System.out.println("130 & 128=" + (130 & 128));
        System.out.println("130 &   1=" + (130 & 1));
        System.out.println("130 &   2=" + (130 & 2));
        
        for (Field f : ClassUtil.getFields(MyBean.class, false, Modifier.TRANSIENT)) {
            System.out.println("f="+f.getName());
        }
        
    }
    
}
