package com.nick.tdp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RegistrationDatabase {
	
	private static final String TAG = "Database for Registration Recept.";
	
	/**
	 * The parameters for the connection of Database
	 */
	private static String dbDriver = DBProfile.dbDriver;
	private static String connectionURL = DBProfile.dbURL + DBProfile.dbName;
	private static String userName = DBProfile.userName;
	private static String userPassword = DBProfile.userPassword;
	
	/**
	 * JDBC的事务使用Connection对象控制，接口java.sql.Connection提供自动提交和手工提交两种事务模式，
	 * 缺省情况下，新连接使用自动提交模式。
	 */
	private static Connection _connection = null;
	
	/**
	 * Connect to the MySql database(Make sure the database wifiDevicePairing_DB has been created.)
	 * Make sure the service mysqld is running:
	 * 	Start the mysql service before connecting to the database.
	 * 	> net start mysql
	 * 	> net stop mysql
	 * @author Nick
	 */
	static{
		try{
			if (_connection == null) {
				//调用Class.forName()方法加载驱动程序
				Class.forName(dbDriver).newInstance();
				//调用DriverManager对象的getConnection()方法，获得一个Connection对象
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
	
	private RegistrationDatabase(){}
	
	public static boolean addRegistrationReceipt(RegistrationReceipt receipt_){
		if(receipt_ == null){
			System.err.println(TAG + "The null Receipt record cannot be inserted.");
			return false;
		}
		
		String pSQL = "insert into " + DBProfile.TableName_RegistrationReceipt + " values('"
				+ receipt_.getID() + "','"
				+ receipt_.get_x() + "','"
				+ receipt_.get_Ppub() + "','"
				+ receipt_.get_r() + "','"
				+ receipt_.get_d() + "','"
				+ receipt_.get_Rpub() + "',"
				+ receipt_.getTrustValue() + ");";				
		return insertRecord(pSQL);
	}
	
	public static int updateRegistrationReceipt(RegistrationReceipt receipt_){
		String pSQL = "update " + DBProfile.TableName_RegistrationReceipt + " set "
				+ "ID='" + receipt_.getID() + "',"
				+ "x='" + receipt_.get_x() + "',"
				+ "Pub='" + receipt_.get_Ppub() + "',"
				+ "r='" + receipt_.get_r() + "',"
				+ "d='"+ receipt_.get_d() + "',"
				+ "bR='"+ receipt_.get_Rpub() + "',"
				+ "tv="+ receipt_.getTrustValue();		
		return updateRecord(pSQL);
	}
	
	public static RegistrationReceipt getReceiptByID(String ID_){
		String pSQL = "select * from " + DBProfile.TableName_RegistrationReceipt + " where id='" + ID_ + "'";
		RegistrationReceipt receipt = null;
		ResultSet resultSet = null;
		try {
			Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			resultSet = statement.executeQuery(pSQL);
			
			if(resultSet.next()){
				receipt = new RegistrationReceipt();
				receipt.setID(resultSet.getString("ID").trim());
				receipt.set_x(resultSet.getString("x").trim());
				receipt.set_Ppub(resultSet.getString("Pub").trim());
				receipt.set_r(resultSet.getString("r").trim());
				receipt.set_d(resultSet.getString("d").trim());
				receipt.set_Rpub(resultSet.getString("bR").trim());
				receipt.setTrustValue(resultSet.getInt("tv"));
			}
			
			resultSet.close();
			statement.close();
		}catch(SQLException e){
			System.out.println("Query the receipt record error: " + e.getMessage());
		}
		
		return receipt;
	}

	private static boolean insertRecord(String sql_){
		boolean result = false;
//		System.out.println("SQL Statement: " + sql_);
		try {
			Statement statement = _connection.createStatement();
			statement.execute(sql_);
			statement.close();
			result = true;
		} catch (SQLException e) {
			System.out.println("Insert the recored error: " + e.getMessage());
		}
		return result;
	}
	
	private static int updateRecord(String sql_){
		int result = 0;
		try {
			Statement statement = _connection.createStatement();
			result = statement.executeUpdate(sql_);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Create Registration Receipt Table
	 * Table fields: SerialNumber  PrivateKey  PublicKey   r   d   R  TrustValue
	 * 				     ID	 		  x			  Pub	   r   d   bR    tv
	 * @return
	 */
	public static boolean createRegistrationReceiptTable(){
		String pSQL = "CREATE TABLE " + DBProfile.TableName_RegistrationReceipt + " ("
				+ "ID	CHAR(200)	NOT NULL,"
				+ "x 	CHAR(200),"
				+ "Pub	CHAR(250),"
				+ "r	CHAR(250),"
				+ "d 	char(250),"
				+ "bR	char(250),"
				+ "tv	INT NOT NULL	default 20"
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
	
	
	public static void deleteAllRecords(String tableName_){			
		String pSQL = "TRUNCATE TABLE " + tableName_;
		try{
			Statement statement = _connection.createStatement();
			statement.executeUpdate(pSQL);
			statement.close();
		}catch(SQLException e){
			System.err.println("Failed: DB delete all records from table - " + tableName_ + "\n\tError Message: " + e.getMessage());
		}
	}
	
	public static void deleteTable(String tableName_){	
		String pSQL = "DROP TABLE " + tableName_;
		try{
			Statement statement = _connection.createStatement();
			statement.executeUpdate(pSQL);
			statement.close();
		}catch(SQLException e){
			System.err.println("Failed: DB delete table - " + tableName_ + "\n\tError Message: " + e.getMessage());
		}
	}
}
