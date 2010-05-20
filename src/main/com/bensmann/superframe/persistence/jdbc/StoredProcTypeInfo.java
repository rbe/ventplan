/*
 * Created on 10.04.2005
 *
 */
package com.bensmann.superframe.persistence.jdbc;

/**
 * Type information for parameters of stored procedures
 *
 * @author rb
 * @version $Id: StoredProcTypeInfo.java,v 1.1 2005/07/19 15:51:40 rb Exp $
 */
public class StoredProcTypeInfo {

    private int parameterPosition;

    private String parameterType;

    private Object parameterValue;

    /**
     * 
     * @param parameterPosition 
     * @param parameterType 
     * @param parameterValue 
     */
    public StoredProcTypeInfo(int parameterPosition, String parameterType,
            Object parameterValue) {

        this.parameterPosition = parameterPosition;
        this.parameterType = parameterType;
        this.parameterValue = parameterValue;

    }

    /**
     * 
     * @return 
     */
    public int getParameterPosition() {
        return parameterPosition;
    }

    /**
     * 
     * @return 
     */
    public String getParameterType() {
        return parameterType;
    }

    /**
     * 
     * @return 
     */
    public Object getParameterValue() {
        return parameterValue;
    }

}