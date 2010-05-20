/*
 * Created on 06.04.2005
 *
 */
package com.bensmann.superframe.persistence.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class that holds a ResultSet and corresponding Statement object
 *
 * @author rb
 * @version $Id: MyResult.java,v 1.1 2005/07/19 15:51:40 rb Exp $
 */
public class MyResult {
    
    /**
     * Statement to execute queries
     */
    private Statement stmt;
    
    /**
     * ResultSet
     */
    private ResultSet resultSet;
    
    /**
     * 
     * @param resultSet 
     * @param stmt 
     */
    public MyResult(ResultSet resultSet, Statement stmt) {
        this.resultSet = resultSet;
        this.stmt = stmt;
    }
    
    /**
     * 
     * @return 
     */
    public ResultSet getResultSet() {
        return resultSet;
    }
    
    /**
     * Close MyResult: Call .close() on resultSet and stmt
     */
    public void close() {
        
        try {
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            
        }
        
    }
    
}
