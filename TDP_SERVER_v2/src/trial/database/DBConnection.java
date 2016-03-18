package trial.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Start the mysql service before connecting to the database.
 * > net start mysql
 * > net stop mysql
 * @author Nick
 *
 */
public class DBConnection {
	
	/**
	 * JDBC������ʹ��Connection������ƣ��ӿ�java.sql.Connection�ṩ�Զ��ύ���ֹ��ύ��������ģʽ��
	 * ȱʡ����£�������ʹ���Զ��ύģʽ��
	 */
	private Connection _connection = null;
	
	/**
	 * The parameters for the connection of Database
	 */
	private static String dbDriver = DBProfile.dbDriver;
	private static String connectionURL = DBProfile.dbURL + DBProfile.dbName;
	private static String userName = DBProfile.userName;
	private static String userPassword = DBProfile.userPassword;
	
	/**
	 * Connect to the MySql database(Make sure the database wifiDevicePairing_DB has been created.)
	 * @return
	 */
	public Connection getConnection() {
		try{
			//����Class.forName()����������������
			Class.forName(dbDriver).newInstance();
			//����DriverManager�����getConnection()���������һ��Connection����
			_connection = DriverManager.getConnection(connectionURL, userName, userPassword);
		}catch(InstantiationException | ClassNotFoundException | IllegalAccessException e){
			System.err.println("Database Driver failure: " + e.getMessage());
		}catch(SQLException e){
			System.err.println("Database Connection failure: " + e.getMessage());
		} 
		
		return _connection;
	}
	
	public void closeConnection(){
		if(_connection != null){
			try{
				_connection.close();
//				System.out.println("Close Database success!");
			}catch (SQLException e){
				System.err.println("Close Database failure");
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBConnection dbConnection = new DBConnection();
		dbConnection.getConnection();
		dbConnection.closeConnection();
	}

}
