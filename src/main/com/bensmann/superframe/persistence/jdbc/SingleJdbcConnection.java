/*
 * Created on 06.04.2005
 *
 */
package com.bensmann.superframe.persistence.jdbc;

import com.bensmann.superframe.java.Debug;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * A single JDBC connection: to exactly one database
 *
 * @author rb
 * @version $Id: SingleJdbcConnection.java,v 1.1 2005/07/19 15:51:40 rb Exp $
 */
public class SingleJdbcConnection {
    
    private String jdbcDriver;
    
    private String jdbcHost;
    
    private String jdbcDbname;
    
    private String jdbcUser;
    
    private String jdbcPwd;
    
    private String jdbcUrl;
    
    private boolean dynamic;
    
    private Connection connection;
    
    /**
     *
     * @param jdbcDriver
     * @param jdbcHost
     * @param jdbcDbname
     * @param jdbcUser
     * @param jdbcPwd
     */
    public SingleJdbcConnection(String jdbcDriver, String jdbcHost,
            String jdbcDbname, String jdbcUser, String jdbcPwd) {
        
        this.jdbcDriver = jdbcDriver;
        this.jdbcHost = jdbcHost;
        this.jdbcDbname = jdbcDbname;
        this.jdbcUser = jdbcUser;
        this.jdbcPwd = jdbcPwd;
        
        constructJdbcUrl();
        
    }
    
    /**
     *
     * @param jdbcDriver
     * @param jdbcUrl
     * @param jdbcUser
     * @param jdbcPwd
     */
    public SingleJdbcConnection(String jdbcDriver, String jdbcUrl,
            String jdbcUser, String jdbcPwd) {
        
        this.jdbcDriver = jdbcDriver;
        this.jdbcUrl = jdbcUrl;
        this.jdbcUser = jdbcUser;
        this.jdbcPwd = jdbcPwd;
        
    }
    
    /**
     *
     * @return
     */
    public String getJdbcDbname() {
        return jdbcDbname;
    }
    
    /**
     *
     * @return
     */
    public String getJdbcUrl() {
        return jdbcUrl;
    }
    
    /**
     *
     * @param dynamic
     */
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }
    
    /**
     *
     * @return
     */
    public boolean isDynamic() {
        return dynamic;
    }
    
    /**
     * Construct a JDBC URL depending on type of database and given parameters
     * Save the constructed URL in String this.jdbcUrl and return it
     *
     * @return String Constructed JDBC URL
     */
    private String constructJdbcUrl() {
        
        if (jdbcDriver != null) {
            
            if (jdbcDriver.indexOf("mysql") >= 0) {
                
                jdbcUrl = "jdbc:mysql://" + jdbcHost + "/" + jdbcDbname
                        + "?user=" + jdbcUser
                        + (jdbcPwd != null ? "&password=" + jdbcPwd : "");
                
            } else if (jdbcDriver.indexOf("oracle") >= 0) {
                
                // jdbc:oracle:thin:@MyOracleHost:1521:MyDB
                jdbcUrl = "jdbc:oracle:thin:@" + jdbcHost + ":1521:"
                        + jdbcDbname;
                
            }
            
        }
        
        // Debug
        Debug.log("constructed JDBC URL=" + jdbcUrl);
        
        return jdbcUrl;
        
    }
    
    /**
     * Load JDBC driver class and get a connection from DriverManager if
     * connection object is uninitialized
     *
     */
    public void openConnection() throws ClassNotFoundException, SQLException {
        
        if (connection == null) {
            
            // Debug
            Debug.log("No active JDBC connection. Setting up new connection." +
                    " Using driver: " + jdbcDriver);
            
            // Load JDBC driver
            Class.forName(jdbcDriver);
            
            // Create properties for DriverManager (user and password)
            Properties p = new Properties();
            p.setProperty("user", jdbcUser);
            p.setProperty("password", jdbcPwd);
            
            // Get a connection object from DriverManager
            connection = DriverManager.getConnection(jdbcUrl, p);
            
        }
        
    }
    
    /**
     *
     * @throws java.lang.ClassNotFoundException
     */
    public void checkConnection() throws ClassNotFoundException, SQLException {
        
        Debug.log("connection==" + connection);
        
        if (connection != null) {
            Debug.log("connection.isClosed()==" + connection.isClosed());
            
            if (connection.isClosed()) {
                connection = null;
            }
            
        }
        
        if (connection == null) {
            openConnection();
        }
        
        Debug.log("connection==" + connection + " /.isClosed()==" +
                connection.isClosed());
        
    }
    
    /**
     * Close JDBC database connection
     *
     */
    public void closeConnection() {
        
        // Debug
        Debug.log("Closing database connection");
        
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     *
     * @throws java.sql.SQLException
     */
    public void commit() throws SQLException {
        connection.commit();
    }
    
    /**
     * Executes a SQL query and returns the result set
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public MyResult executeQuery(String sql) throws SQLException {
        
        ResultSet r = null;
        Statement stmt = connection.createStatement();
        
        try {
            stmt.executeQuery(sql);
            r = stmt.getResultSet();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new MyResult(r, stmt);
        
    }
    
    /**
     *
     * @param preparedStatement
     * @throws java.sql.SQLException
     * @return
     */
    public MyResult executeQuery(PreparedStatement preparedStatement)
    throws SQLException {
        
        ResultSet r = null;
        
        preparedStatement.executeQuery();
        r = preparedStatement.getResultSet();
        
        return new MyResult(r, preparedStatement);
        
    }
    
    /**
     * Returns a prepared statement that is initialized with a SQL query
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public PreparedStatement getPreparedStatement(String sql)
    throws SQLException {
        
        Debug.log("connection==" + connection);
        
        return connection.prepareStatement(sql);
        
    }
    
    /**
     * Returns a callable statement that is initialized with a SQL query
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public CallableStatement getCallableStatement(String sql)
    throws SQLException {
        
        return connection.prepareCall(sql);
        
    }
    
}