package com.nick.tdp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryRecord {

	private DBConnection dbConnection = new DBConnection();
	private Connection _connection = null;
	private PreparedStatement _preparedStatement = null;
	private String pSQL = "select * from receipt where id = ?";
	
	private String _deviceId = null;
	private String _privateKeyR = null;
	private String _privateKeyD = null;
	private String _publickKey = null;
	private int _trustValue = 0;
	
//	public QueryRecord(String deviceId_){
//		QueryReceiptByID(deviceId_);
//	}
	
	public boolean queryReceiptByID(String deviceId_){
		boolean done = false;
		_connection = dbConnection.getConnection();
		ResultSet resultSet;
		try {
			_preparedStatement = _connection.prepareStatement(pSQL);
			_preparedStatement.setString(1, deviceId_);
			resultSet = _preparedStatement.executeQuery();
			while (resultSet.next()) {
				System.out.println("Record is available.");
				done = true;
				_deviceId = deviceId_;
				_privateKeyR = resultSet.getString(2);
				_privateKeyD = resultSet.getString(3);
				_publickKey = resultSet.getString(4);
				_trustValue = resultSet.getInt(5);
				System.out.println(
						"-------------------------------------------------------------------------"
						+ "\nDevice ID: " + resultSet.getString(1)
						+ "\nr: " + resultSet.getString(2) 
						+ "\nd: " + resultSet.getString(3) 					
						+ "\nP: " + resultSet.getString(4)
						+ "\nt: " + resultSet.getInt(5)
						+"\n-------------------------------------------------------------------------\n");				
			}
			
			/**
			 * The order to close the JDBC: first ResultSet, second Statement, third Connection
			 */
			resultSet.close();
			_preparedStatement.close();
			_connection.close();
		} catch (SQLException e) {
			// TODO: handle exception
			done = false;
			System.out.println("Update the trustValue error: " + e.getMessage());			
		}
		
		return done;
	}
	
	public String getDeviceId(){
		return _deviceId;
	}
	
	public String getPrivateKeyR(){		
		return _privateKeyR;
	}
	
	public String getPrivateKeyD(){		
		return _privateKeyD;
	}
	
	public String getPublicKey(){
		return _publickKey;
	}
	
	public int getTrustValue(){
		return _trustValue;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String deviceId = "Android1873649042";
		QueryRecord queryRecord = new QueryRecord();
		if(queryRecord.queryReceiptByID(deviceId)){
			System.out.println(
					"-------------------------------------------------------------------------"
					+ "\nDevice ID: " + queryRecord.getDeviceId()
					+ "\nr: " + queryRecord.getPrivateKeyR()
					+ "\nd: " + queryRecord.getPrivateKeyD() 
					+ "\nt: " + queryRecord.getTrustValue()
					+"\n-------------------------------------------------------------------------\n");					
			int trustValue = queryRecord.getTrustValue();
			System.out.println("TrustVale: " + String.valueOf(trustValue));
		}else {
			System.err.println("Query the record failed.");
		}		
	}

}
