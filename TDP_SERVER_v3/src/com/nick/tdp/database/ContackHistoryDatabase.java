package com.nick.tdp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ContackHistoryDatabase {
	private static final String TAG = "Database for Contact History.";
	
	/**
	 * The parameters for the connection of Database
	 */
	private static String dbDriver;
	private static String connectionURL;
	private static String userName;
	private static String userPassword;
	private static String tableName;
	
	private static Connection _connection = null;
	
	static{
		dbDriver = DBProfile.dbDriver;
		connectionURL = DBProfile.dbURL + DBProfile.dbName;
		userName = DBProfile.userName;
		userPassword = DBProfile.userPassword;
		tableName = DBProfile.TableName_ContactHistory;
		
		try{
			if (_connection == null) {				
				Class.forName(dbDriver).newInstance();				
				_connection = DriverManager.getConnection(connectionURL, userName, userPassword);			
			}		
		}catch(InstantiationException | ClassNotFoundException | IllegalAccessException e){
			System.err.println("Database Driver failure: " + e.getMessage());
		}catch(SQLException e){
			System.err.println("Database Connection failure: " + e.getMessage());
		}
	}
	
	public static void closeConnection(){		
		try{
			if(_connection != null){
				_connection.close();  //Close the database connection
			}									
		}catch (SQLException e){
			System.err.println("Close Database failure");
		}		
	}
	
	/**
	 * Create Contact History Table
	 * Table fields: SerialNumber  qos  cre  IDs  ch_postive  ch_total   
	 * 				     ID	 		q	 c	 ids   chP          chT
	 * q,c double
	 * ID, ids, chP, chT String
	 * @return
	 */
	public static boolean createTable(){
		String pSQL = "CREATE TABLE " + tableName + " ("
				+ "ID	CHAR(200)	NOT NULL,"
				+ "q 	DOUBLE,"
				+ "c	DOUBLE,"
				+ "ids	TEXT,"
				+ "chP 	TEXT,"
				+ "chT	TEXT"
				+ ")";
		boolean result = true;
		try{
			Statement statement = _connection.createStatement(); //Statement对象将SQL语句发送到数据库
			statement.executeUpdate(pSQL); //Execute the SQL command	
			statement.close(); //释放Statement连接的数据库及JDBC资源
		}catch(SQLException e){
			System.err.println("Failed: DB delete table - " + DBProfile.TableName_RegistrationReceipt + "\n\tError Message: " + e.getMessage());
			result = false;
		}
		return result;
	}
	
	
	public static void deleteAllRecords(){			
		String pSQL = "TRUNCATE TABLE " + tableName;
		try{
			Statement statement = _connection.createStatement();
			statement.executeUpdate(pSQL);
			statement.close();
		}catch(SQLException e){
			System.err.println("Failed: DB delete all records from table - " + tableName + "\n\tError Message: " + e.getMessage());
		}
	}
	
	public static void deleteTable(){	
		String pSQL = "DROP TABLE " + tableName;
		try{
			Statement statement = _connection.createStatement();
			statement.executeUpdate(pSQL);
			statement.close();
		}catch(SQLException e){
			System.err.println("Failed: DB delete table - " + tableName + "\n\tError Message: " + e.getMessage());
		}
	}
}
