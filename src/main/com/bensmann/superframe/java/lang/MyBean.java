/*
 * com/bensmann/superframe/java/lang/MyBean.java
 *
 * MyBean.java created on 20. Januar 2007, 15:45 by rb
 *
 * Copyright (C) 2006 Ralf Bensmann, java@bensmann.com
 *
 */

package com.bensmann.superframe.java.lang;

/**
 *
 * @author rb
 * @version 1.0
 */
public class MyBean {
    
    String a;
    transient String b;
    public String c;
    public transient String d;
    
    public MyBean(String a) {
        this.a=a;
    }
    
    public String getA() {
        return a;
    }
    
    /**
     * Creates a new instance of MyBean
     */
    public MyBean() {
    }
    
}
