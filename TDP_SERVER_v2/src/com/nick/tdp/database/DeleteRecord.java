package com.nick.tdp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteRecord {

	private DBConnection dbConnection = new DBConnection();
	private Connection _connection = null;
	private PreparedStatement _preparedStatement = null;
	private String pSQL = "delete from receipt where id = ?";
	
	public int deleteRegistrationReceipt(String deviceID_){
		int count = -1;
		
		_connection = dbConnection.getConnection();
		try {
			_preparedStatement = _connection.prepareStatement(pSQL);
			count = _preparedStatement.executeUpdate();			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("delete the recored error: " + e.getMessage());	
		} finally {
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
//		DeleteRecord deleteRecord = new DeleteRecord();
//		int count = deleteRecord.deleteRegistrationReceipt("Android-java.util.Random@7852e922");
//		System.out.println(String.valueOf(count) + " record deleted from the table.");
//	}

}
