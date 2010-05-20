/*
 * SqlQuery.java
 *
 * Created on 22. Juni 2005, 00:07
 *
 */
package com.bensmann.superframe.persistence.jdbc;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ralf Bensmann
 * @version $Id: SqlQuery.java,v 1.1 2005/07/19 15:51:40 rb Exp $
 */
public class SqlQuery {
    
    /**
     * Name of query (see waf.xml)
     */
    private String name;
    
    /**
     * The query itself
     */
    private String query;
    
    /**
     * Connection to datasource
     */
    private SingleJdbcConnection sjc;
    
    /**
     * Map for parameters: position -> variable name
     */
    private Map<Integer, String> parameters = new HashMap<Integer, String>();
    
    /**
     * A prepared statement for using bind variables (speed up performance)
     */
    private PreparedStatement preparedStatement;
    
    /**
     * Creates a new instance of SqlQuery
     */
    public SqlQuery(SingleJdbcConnection sjc, String name, String query) {
        this.sjc = sjc;
        this.name = name;
        this.query = query;
    }
    
    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     *
     * @return
     */
    public String getQuery() {
        return query;
    }
    
    /**
     *
     * @param sjc
     * @throws java.sql.SQLException
     * @return
     */
    public PreparedStatement getPreparedStatement()
    throws ClassNotFoundException, SQLException {
        
        sjc.checkConnection();
        preparedStatement = sjc.getPreparedStatement(query);
        
        return preparedStatement;
        
    }
    
    /**
     *
     * @param position
     * @param variable
     */
    public void addPreparedStatementParameter(int position, String variable) {
        parameters.put(position, variable);
    }
    
    /**
     *
     * @return
     */
    public boolean hasPreparedStatementParameters() {
        return parameters.size() > 0;
    }
    
    /**
     *
     * @return
     */
    public int getPreparedStatementParameterCount() {
        return parameters.size();
    }
    
    /**
     *
     * @param position
     * @return
     */
    public String getPreparedStatementParameterAtPosition(int position) {
        return parameters.get(position);
    }
    
    /**
     *
     * @param position
     * @param value
     * @throws java.sql.SQLException
     */
    public void setPreparedStatementParameter(int position, String value)
    throws SQLException {
        
        preparedStatement.setString(position, value);
        
    }
    
    /**
     *
     * @param preparedStatement
     * @throws java.sql.SQLException
     * @return
     */
    public MyResult executeQuery() throws SQLException {
        
        ResultSet r = null;
        
        preparedStatement.executeQuery();
        r = preparedStatement.getResultSet();
        
        return new MyResult(r, preparedStatement);
        
    }
    
}
