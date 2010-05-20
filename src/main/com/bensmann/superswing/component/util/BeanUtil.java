/*
 * BeanUtil.java
 *
 * Created on 20. Mai 2007, 18:24
 *
 */

package com.bensmann.superswing.component.util;

import com.bensmann.superframe.java.lang.ReflectionFacade;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author rb
 */
public class BeanUtil {
    
    /**
     *
     */
    private static Logger logger;
    
    static {
        logger = Logger.getLogger(BeanUtil.class.getName());
    }
    
    /** Creates a new instance of BeanUtil */
    public BeanUtil() {
    }
    
    /**
     * 
     * @param bean 
     * @param component 
     */
    public static void applyBeanToTextComponent(Object bean, Component component) {
        
        ReflectionFacade beanReflectionFacade = new ReflectionFacade(bean);
        ReflectionFacade componentReflectionFacade = new ReflectionFacade(component);
        
        Class beanClazz = bean.getClass();
        Class componentClass = component.getClass();
        String beanGetterMethodName = null;
        String componentGetterMethodName = null;
        String tmp = null;
        JTextField textField = null;
        
        for (Method beanGetterMethod : beanReflectionFacade.getPublicGetterMethods()) {
            
            beanGetterMethodName = beanGetterMethod.getName();
            
            for (Method panelMethod : componentReflectionFacade.getPublicGetterMethods()) {
                
                componentGetterMethodName = panelMethod.getName();
                tmp = beanGetterMethodName.replaceAll("get", "") + "TextField";
                
                if (componentGetterMethodName.indexOf(tmp) > 0) {
                    
                    logger.finest("Applying value from " + 
                            beanGetterMethodName +
                            " -> " +
                            componentGetterMethodName);
                    
                    try {
                        textField = (JTextField) panelMethod.invoke(component);
                        textField.setText("" + beanGetterMethod.invoke(bean));
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                    
                }
                
            }
            
        }
        
        beanClazz = null;
        componentClass = null;
        beanGetterMethodName = null;
        tmp = null;
        textField = null;
        
    }
    
    public static void applyTextComponentToBean(Component component, Object bean) {
        
        ReflectionFacade beanReflectionFacade = new ReflectionFacade(bean);
        ReflectionFacade componentReflectionFacade = new ReflectionFacade(component);
        
        Class beanClazz = bean.getClass();
        Class componentClass = component.getClass();
        String beanSetterMethodName = null;
        String componentGetterMethodName = null;
        String tmp = null;
        JTextField textField = null;
        
        for (Method beanSetterMethod : beanReflectionFacade.getPublicSetterMethods()) {
            
            beanSetterMethodName = beanSetterMethod.getName();
            
            for (Method panelMethod : componentReflectionFacade.getPublicGetterMethods()) {
                
                componentGetterMethodName = panelMethod.getName();
                tmp = beanSetterMethodName.replaceAll("get", "") + "TextField";
                
                if (componentGetterMethodName.indexOf(tmp) > 0) {
                    
                    logger.finest("Applying value from " + 
                            componentGetterMethodName +
                            " -> " +
                            beanSetterMethodName);
                    
                    try {
                        textField = (JTextField) panelMethod.invoke(component);
                        beanSetterMethod.invoke(bean, textField.getText());
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                    
                }
                
            }
            
        }
        
        beanClazz = null;
        componentClass = null;
        beanSetterMethodName = null;
        tmp = null;
        textField = null;
        
    }
    
}
