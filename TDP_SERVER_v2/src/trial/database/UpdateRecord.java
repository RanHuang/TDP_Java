package trial.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateRecord {

	private DBConnection dbConnection = new DBConnection();
	private Connection _connection = null;
	private PreparedStatement _preparedStatement = null;
	
	
	
	public int updateTrustValue(String deviceID_, int trustValue_){
		String pSQL = "update receipt set t=? where id = ?";
		int count = -1;
		_connection = dbConnection.getConnection();		
		try {
			_preparedStatement = _connection.prepareStatement(pSQL); 
			_preparedStatement.setString(2, deviceID_);
			_preparedStatement.setInt(1, trustValue_);
			count = _preparedStatement.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("update the record error: " + e.getMessage());
		}finally {
			try {
				_preparedStatement.close();
				_connection.close();
			} catch (SQLException e) {
				// TODO: handle exception
				System.out.println("Close the database error: " + e.getMessage());
			}
		}
		return count;
	}
	
	public int updatePrivateKeyR(String deviceID_, String privateKeyR_){
		String pSQL = "update receipt set r=? where id = ?";
		int count = -1;
		_connection = dbConnection.getConnection();		
		try {
			_preparedStatement = _connection.prepareStatement(pSQL); 
			_preparedStatement.setString(2, deviceID_);
			_preparedStatement.setString(1, privateKeyR_);
			count = _preparedStatement.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("update the record error: " + e.getMessage());
		}finally {
			try {
				_preparedStatement.close();
				_connection.close();
			} catch (SQLException e) {
				// TODO: handle exception
				System.out.println("Close the database error: " + e.getMessage());
			}
		}
		return count;
	}
	
	public int updatePrivateKeyD(String deviceID_, String privateKeyD_){
		String pSQL = "update receipt set d=? where id = ?";
		int count = -1;
		_connection = dbConnection.getConnection();		
		try {
			_preparedStatement = _connection.prepareStatement(pSQL); 
			_preparedStatement.setString(2, deviceID_);
			_preparedStatement.setString(1, privateKeyD_);
			count = _preparedStatement.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("update the record error: " + e.getMessage());
		}finally {
			try {
				_preparedStatement.close();
				_connection.close();
			} catch (SQLException e) {
				// TODO: handle exception
				System.out.println("Close the database error: " + e.getMessage());
			}
		}
		return count;
	}
	
	public int updatePublicKey(String deviceID_, String publicKey_){
		String pSQL = "update receipt set p=? where id = ?";
		int count = -1;
		_connection = dbConnection.getConnection();		
		try {
			_preparedStatement = _connection.prepareStatement(pSQL); 
			_preparedStatement.setString(2, deviceID_);
			_preparedStatement.setString(1, publicKey_);
			count = _preparedStatement.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("update the record error: " + e.getMessage());
		}finally {
			try {
				_preparedStatement.close();
				_connection.close();
			} catch (SQLException e) {
				// TODO: handle exception
				System.out.println("Close the database error: " + e.getMessage());
			}
		}
		return count;
	}
	
	public int updateReceipt(String deviceID_, String privateKeyR_, String privateKeyD_, String publicKey_, int trustValue_){
		String pSQL = "update receipt set r=?, d=?, p=?, t=? where id=?";
		int count = -1;
		_connection = dbConnection.getConnection();		
		try {
			_preparedStatement = _connection.prepareStatement(pSQL); 
			_preparedStatement.setString(4, deviceID_);
			_preparedStatement.setString(1, privateKeyR_);
			_preparedStatement.setString(2, privateKeyD_);
			_preparedStatement.setString(3, publicKey_);
			_preparedStatement.setInt(4, trustValue_);
			count = _preparedStatement.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("update the record error: " + e.getMessage());
		}finally {
			try {
				_preparedStatement.close();
				_connection.close();
			} catch (SQLException e) {
				// TODO: handle exception
				System.out.println("Close the database error: " + e.getMessage());
			}
		}
		return count;
	}
	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		String deviceId = "Android-java.util.Random@7852e922";
//		UpdateRecord updateRecord = new UpdateRecord();
////		int count = updateRecord.updateTrustValue(deviceId, 23);
//		int count = updateRecord.updateReceipt(deviceId, "1234404aefjal", "daljfoadifjwelf", "290759847105", 50);
//		System.out.println(String.valueOf(count) + "record(s) updated.");
//	}

}
