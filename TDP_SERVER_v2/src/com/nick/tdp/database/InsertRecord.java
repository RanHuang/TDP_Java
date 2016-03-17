package com.nick.tdp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertRecord {
	
	private DBConnection dbConnection = new DBConnection();
	private Connection _connection = null;
	private PreparedStatement _preparedStatement = null;
	private String pSQL = "insert into receipt values(?,?,?,?,?)";

	public int addRegistrationReceipt(String deviceID_, String privateKeyR_, String privateKeyD_, String publicKey_, int trustValue_){
		int count = 0;
		_connection = dbConnection.getConnection();
		try{
			//PreparedStatement对象将参数化的SQL语句发送到数据库
			_preparedStatement = _connection.prepareStatement(pSQL); 
			/**
			 * Insert only one record.
			 */
			_preparedStatement.setString(1, deviceID_);
			_preparedStatement.setString(2, privateKeyR_);
			_preparedStatement.setString(3, privateKeyD_);
			_preparedStatement.setString(4, publicKey_);
			_preparedStatement.setInt(5, trustValue_);
			count = _preparedStatement.executeUpdate();
			/**
			 * Insert two or more records one time.
			 */
//			_preparedStatement.clearBatch();
//			//set values
//			_preparedStatement.addBatch();
//			//set values
//			_preparedStatement.addBatch();
//			_preparedStatement.executeBatch();
		}catch(SQLException exception){
			System.out.println("Insert the recored error: " + exception.getMessage());			
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InsertRecord insertRecord = new InsertRecord();
		int count = insertRecord.addRegistrationReceipt("Android1873649042", 
														"5739904867794505967235191454644719104181685473093185648041", 
														"1840427900427953752849863098567575463724685599385771110654732905960799753911619203380422781694838991596874",
														"add public key here",
														20);
		System.out.println(String.valueOf(count) + " record inserted into the table.");
	}

}
