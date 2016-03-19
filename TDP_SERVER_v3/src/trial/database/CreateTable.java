package trial.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


/*
 
 */
public class CreateTable {
	
	private DBConnection _dbConnection= new DBConnection();
	private Connection _connection = null;
	private Statement _statement = null; 	
	
	public CreateTable(){
		_dbConnection = new DBConnection();
	}
	/**
	 * Create Registration Receipt Table
	 * Table fields: SerialNumber  PrivateKey  PublicKey   r   d   R   ServerPublicKey  TrustValue
	 * 				     ID	 		  x			  Pub	   r   d   bR		sPub			tv
	 * @return
	 */
	public boolean createRegistrationReceiptTable(){
		String pSQL = "CREATE TABLE " + DBProfile.TableName_RegistrationReceipt + " ("
				+ "ID	CHAR(200)	NOT NULL,"
				+ "x 	CHAR(200),"
				+ "Pub	CHAR(250),"
				+ "r	CHAR(250),"
				+ "d 	char(250),"
				+ "bR	char(250),"
				+ "sPub char(250),"
				+ "tv	INT NOT NULL	default 20"
				+ ")";
		boolean result = true;
		_connection = _dbConnection.getConnection();
		try{
			_statement = _connection.createStatement(); //Statement对象将SQL语句发送到数据库
			_statement.executeUpdate(pSQL); //Execute the SQL command
			
			_statement.close(); //释放Statement连接的数据库及JDBC资源
			_connection.close(); //Close the database connection
		}catch(SQLException e){
			System.err.println("Failed: DB delete table - " + DBProfile.TableName_RegistrationReceipt + "\n\tError Message: " + e.getMessage());
			result = false;
		}
		return result;
	}
	
	/**
	 * This table store the intermediate data of the client. e.g ContactHistoryArray QoS Credibility Rating
	 * Table fields: SerialNumber  ContactHistoryArray QoS Credibility Rating
	 * 					ID				chArr				q	    c		 r
	 * @return
	 */
	public boolean createClientParametersTable(){
		String pSQL = "CREATE TABLE " + DBProfile.TableName_ClientParameters + " ("
				+ "ID	CHAR(200)	NOT NULL,"
				+ "chArr	CHAR(250),"      //??存储整型数组
				+ "q	INTEGER,"
				+ "c	FLOAT,"
				+ "r 	FLOAT"
				+ ")";
		boolean result = true;
		_connection = _dbConnection.getConnection();
		try{
			_statement = _connection.createStatement();
			_statement.executeUpdate(pSQL);
			
			_statement.close();
			_connection.close();
		}catch(SQLException e){
			System.err.println("Failed: DB delete table - " + DBProfile.TableName_ClientParameters + "\n\tError Message: " + e.getMessage());
			result = false;
		}
		return result;
	}
	
	/**
	 * This table store the data of the device to device experiments.
	 * Table fields: Index  SerialNumber  PairingTime	Successful	  Type
	 * 				  NO		ID		    duration     succeed	  type
	 * @return
	 */
	public boolean createDeviceToDevicePairingExperimentTable(){
		String pSQL = "CREATE TABLE " + DBProfile.TableName_DeviceToDevicePairingExperiment + " ("
				+ "NO	INT	auto_increment primary key,"
				+ "ID	CHAR(200),"
				+ "duration	INTEGER,"
				+ "succeed	INT default 0,"
				+ "type 	int	default 0"
				+ ")";
		boolean result = true;
		_connection = _dbConnection.getConnection();
		try{
			_statement = _connection.createStatement();
			_statement.executeUpdate(pSQL);
			
			_statement.close();
			_connection.close();
		}catch(SQLException e){
			System.err.println("Failed: DB delete table - " + DBProfile.TableName_DeviceToDevicePairingExperiment + "\n\tError Message: " + e.getMessage());
			result = false;
		}
		return result;
	}
	
	/**
	 * This table store the data of the device to device experiments.
	 * Table fields: Index  SerialNumber  PairingTime	Successful	  Type
	 * 				  NO		ID		    duration     succeed	  type
	 * @return
	 */
	public boolean createWifiDirectPerformanceTestTable(){
		String pSQL = "CREATE TABLE " + DBProfile.TableName_WifiDirectPerformanceTest + " ("
				+ "NO	INT	auto_increment primary key,"
				+ "ID	CHAR(200),"
				+ "duration	INTEGER,"
				+ "succeed	INT,"
				+ "type 	int"
				+ ")";
		boolean result = true;
		_connection = _dbConnection.getConnection();
		try{
			_statement = _connection.createStatement();
			_statement.executeUpdate(pSQL);
			
			_statement.close();
			_connection.close();
		}catch(SQLException e){
			System.err.println("Failed: DB delete table - " + DBProfile.TableName_WifiDirectPerformanceTest + "\n\tError Message: " + e.getMessage());
			result = false;
		}
		return result;
	}
	
	
	
	public void deleteAllRecords(String tableName_){		
		_connection = _dbConnection.getConnection();		
		String pSQL = "TRUNCATE TABLE " + tableName_;
		try{
			_statement = _connection.createStatement();
			_statement.executeUpdate(pSQL);
			
			_statement.close();
			_connection.close();
		}catch(SQLException e){
			System.err.println("Failed: DB delete all records from table - " + tableName_ + "\n\tError Message: " + e.getMessage());
		}
	}
	
	public void deleteTable(String tableName_){
		_connection = _dbConnection.getConnection();		
		String pSQL = "DROP TABLE " + tableName_;
		try{
			_statement = _connection.createStatement(); 
			_statement.executeUpdate(pSQL); 
			
			_statement.close(); 
			_connection.close();
		}catch(SQLException e){
			System.err.println("Failed: DB delete table - " + tableName_ + "\n\tError Message: " + e.getMessage());
		}
	}
	
	
	/************************************************************************************/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CreateTable createTable = new CreateTable();
		String tableName = DBProfile.TableName_DeviceToDevicePairingExperiment;
		
//		boolean isSuccess = createTable.createRegistrationReceiptTable();
//		boolean isSuccess = createTable.createClientParametersTable();
		boolean isSuccess = createTable.createDeviceToDevicePairingExperimentTable();
		if(isSuccess){
			System.out.println("The table " + tableName +" is created.");
		}else {
			System.out.println("The table " + tableName + " is not created.");
		}
		
//		createTable.deleteAllRecords(tableName);
		
//		createTable.deleteTable(tableName);
	}
	
}
