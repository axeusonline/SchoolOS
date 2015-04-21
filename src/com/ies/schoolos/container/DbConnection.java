package com.ies.schoolos.container;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;

public class DbConnection{
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	
	public static final String USERNAME = "root";
	
	//public static final String PATH = "jdbc:mysql://localhost:3306/schoolos?characterEncoding=UTF-8&";
	public static final String PATH = "jdbc:mysql://localhost:3306/test?characterEncoding=UTF-8";
	
	//public static final String PASSWORD = "!IeSP@ssw0RD?";
	public static final String PASSWORD = "";
	
	private static JDBCConnectionPool connectionPool;
	
	public static JDBCConnectionPool getConnection() throws SQLException { 
		if(connectionPool == null)
			connectionPool =  new SimpleJDBCConnectionPool(DRIVER,PATH,USERNAME, PASSWORD,2,5);
        return connectionPool;  
    }
}
