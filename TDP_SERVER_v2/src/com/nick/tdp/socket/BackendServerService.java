package com.nick.tdp.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import com.nick.tdp.database.InsertRecord;
import com.nick.tdp.database.QueryRecord;
import com.nick.tdp.security.BackendServerKey;
import com.nick.tdp.security.DeviceReceipt;
import com.nick.tdp.security.ECDHCurve;
/*
 * @author NickHuang
 * @mail xjhznick@gmail.com
 * The main process:
 * 	1. Generate the main key (x, Ppub), the private key and public key of the back end server.
 * 	2. Get the device's ID and Public Key through the socket, then generate (d, R)
 * 	3. Return ( x, Pub, r, d, R, msterPub, tv) to the the registered device.
 * 	4. Store the Device's authenticated credential (ID, x, Pub, r, d, R, msterPub, tv) by database system.
 */
public class BackendServerService implements Runnable {
	private static final String TAG= "Backend Server Service";
	
	private BackendServerKey _backendServerKey;
	private HashMap<String, DeviceReceipt> _deviceReceipts; //Store all the registered devices' receipts in RAM.
	
	private final ServerSocket _serverSocket;
	
	/* Information of Registration Receipt
	 * (x, Pub, r, d, R, masterPpub, trustValue)
	 */
	private String _deviceId;
	private String _writeBack_x;
	private String _writeBack_Pub;
	private String _writeBack_r;
	private String _writeBack_d;
	private String _writeBack_R;
	private String _writeBack_masterPub;
	private int _writeBack_tv;
	
	public BackendServerService(){
		_backendServerKey = new BackendServerKey();
		System.out.println("***************************************************************************************************\n"
				+ "\t==== Back End Server Infomation ====\n"				
				+ "\tPrivate: " + _backendServerKey.getx().toString() + "\n"
				+ "\tPulic  : " + Hex.toHexString(_backendServerKey.getPpub().getEncoded(true)) + "\n"
				+ "***************************************************************************************************\n\n");
		_deviceReceipts = new HashMap<String, DeviceReceipt>();
		/*
		 * Use tempServerSocket because of the _serverSocket is final.
		 */
		ServerSocket tempServerSocket = null;
		try{
			tempServerSocket = new ServerSocket(TDPConstants.SERVER_PORT_REGISTRATION);
		}catch(IOException e){
			e.printStackTrace();
		}
		_serverSocket = tempServerSocket;
	}
	
	/*
	 * server socket listen for the connect request, then open a new thread to handle the connection
	 */
	public void run() {
		// TODO Auto-generated method stub
		Socket socket = null;
		while(true){
			try{
				socket = _serverSocket.accept();
				System.out.println("A client connected.");
			}catch(IOException exception){
				exception.printStackTrace();
			}
			/**
			 * Start a new thread to communicate with each client.
			 */
			new ClientSocketHandle(socket).start();
		}
	}
	
	private class ClientSocketHandle extends Thread{
		
		private final Socket _clientSocket;
		private final InputStream _inputStream;
		private final OutputStream _outputStream;
		
		public ClientSocketHandle(Socket socket_){			
			_clientSocket = socket_;
			
			InputStream tempInputStream = null;
			OutputStream tempOutputStream = null;
			try{
				tempInputStream = _clientSocket.getInputStream();
				tempOutputStream = _clientSocket.getOutputStream();
			}catch (IOException exception){
				exception.printStackTrace();
			}
			_inputStream = tempInputStream;
			_outputStream = tempOutputStream;
		}
		
		@Override
		public void run(){
			while(true){
				try {
					ObjectInputStream objectInputStream = new ObjectInputStream(_inputStream);
					Object object = objectInputStream.readObject();
					messageHandler(object);					
				} catch(MySocketReadWriteException e){
					System.out.println(e.getMessage());
					cancelSocket();
					break;
				} catch (ClassNotFoundException e) {
					System.err.println(e.getMessage());
					cancelSocket();
					break;
				} catch (IOException e) {					
					System.err.println("Connection Object InputStream Exception.");
					e.printStackTrace();
					/* Stop the connected thread. */
					cancelSocket();
					break;
				}
			}
		}
		
		private void sendPacket(int packetType_){
			JSONObject jsonObject = null;
			switch (packetType_) {
			case TDPConstants.PACKET_TYPE_REGISTRATION_RECEIPT_OK:
				/*
				 * Send the Registration Receipt to the client.
				 * Send(x, Pub, r, d, R, Ppub_master, t)
				 */
				
				break;
			case TDPConstants.PACKET_TYPE_REGISTRATION_REQUEST_KEYS:
				System.out.println(TAG + ": " + "send packet -- request client's keys(Private&Public).");
				/**
				 * Send (PacketType)
				 */
				jsonObject = new JSONObject();
				try {
					jsonObject.put(TDPConstants.PACKET_TYPE, TDPConstants.PACKET_TYPE_REGISTRATION_REQUEST_KEYS);			
					writeObject(jsonObject);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default:
				System.err.println("Unknowed type of received packet.");
				break;
			}
		}
		
		private void writeObject(JSONObject jsonObject){
			try{
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(_outputStream);
				objectOutputStream.writeObject(jsonObject.toString());
				objectOutputStream.flush();
			}catch (IOException exception){
				System.err.println("Connection Object OutputStream Exception.");
				exception.printStackTrace();
				cancelSocket();
			}
		}
		
		protected void messageHandler(Object object_) throws MySocketReadWriteException{
			try {
				JSONObject readJsonObject = new JSONObject(object_.toString());
				/* Get packet type and device's ID. */
				int packetType = readJsonObject.getInt(TDPConstants.PACKET_TYPE);
				_deviceId = readJsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_ID);
				System.out.println("Receivde packet & Device ID: " + _deviceId);
				
				switch (packetType) {
				case TDPConstants.PACKET_TYPE_REGISTRATION_REQUEST_RECEIPT:									
					/*
					 * Get the registration receipt from the data base by device's ID.
					 * If the receipt exists, send it to the device.
					 * Otherwise, generate the receipt, then insert it into the data base and send it to the device.
					 */
					
					/**
					 * Get the registration receipt from the data base by device's ID.
					 * (x, Pub, r, d, R, masterPpub, trustValue)
					 */
					
					
					JSONObject writeJsonObject = new JSONObject();
					String writeBackDeviceId = null;
					String writeBackPrivateKeyR = null;
					String writeBackPirvateKeyD = null;
					String writeBackMasterPublicKey = Hex.toHexString(_backendServerKey.getPpub().getEncoded(true));
					int writeBackTrustValue = -1;
					
					writeJsonObject.put(TDPConstants.PACKET_TYPE, TDPConstants.PACKET_TYPE_REGISTRATION_ACK);
					
					QueryRecord queryReceipt = new QueryRecord();
					if(queryReceipt.queryReceiptByID(deviceIDString)){
						/**
						 * get the registration receipt from the database
						 */
						System.out.println("Query the record success.");
						writeBackDeviceId = queryReceipt.getDeviceId();
						writeBackPrivateKeyR = queryReceipt.getPrivateKeyR();
						writeBackPirvateKeyD = queryReceipt.getPrivateKeyD();
						writeBackTrustValue = queryReceipt.getTrustValue();
					}else {
						/**
						 * Generate a new registration receipt.
						 */
						String publicKeyString = (String)readJsonObject.get(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PUBLIC_KEY);
						ECPoint publicKeyEcPoint = ECDHCurve.getInstance().decodeBytePoint(Hex.decode(publicKeyString));
						
						DeviceReceipt deviceReceipt = new DeviceReceipt(deviceIDString, publicKeyEcPoint);
						
						try {
							deviceReceipt.generateReceipt(_backendServerKey.getx());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						_deviceReceipts.put(deviceReceipt.getID(), deviceReceipt);
						
						writeBackDeviceId = deviceReceipt.getID();
						writeBackPirvateKeyD =deviceReceipt.get_d().toString();
						writeBackPrivateKeyR = Hex.toHexString(deviceReceipt.getRandEcPoint().getEncoded(true));
						writeBackTrustValue = deviceReceipt.getTrustValue();
						/**
						 * Insert a new receipt record into the database.
						 */
						InsertRecord insertRecordReceipt = new InsertRecord();
						int count = insertRecordReceipt.addRegistrationReceipt(writeBackDeviceId, writeBackPrivateKeyR, writeBackPirvateKeyD, publicKeyString, writeBackTrustValue);						
						System.out.println(String.valueOf(count) + " record inserted into the table.");
						System.out.println(  "\tID: " + writeBackDeviceId
											+"\n\tr:  " + writeBackPrivateKeyR
											+"\n\td:  " + writeBackPirvateKeyD
											+"\n\tpk: " + publicKeyString
											+"\n\tt:  " + writeBackTrustValue);
						/**
						 * Display the Device Registration Information					
						 */
//						System.out.println("***************************************************************************************************\n"
//								+ "==== Device Registration Infomation ====\n"
//								+ "ID  : " + deviceReceipt.getID() + "\n"
//								+ "r   : " + deviceReceipt.get_r().toString() + "\n"
//								+ "d   : " + deviceReceipt.get_d().toString() + "\n"
//								+ "Ppub: " + Hex.toHexString(deviceReceipt.getPublicKey().getEncoded(true)) + "\n"
//								+ "Trust Value: " + String.valueOf(deviceReceipt.getTrustValue()) + "\n"
//								+ "Rand: " + Hex.toHexString(deviceReceipt.getRandEcPoint().getEncoded(true)) + "\n"							
//								+ "Master Ppub: " + Hex.toHexString(_backendServerKey.getPpub().getEncoded(true)) + "\n"							
//								+ "***************************************************************************************************\n");
//						System.out.println("***************************************************************************************************\n"
//								+ "==== Device Registration Infomation ====\n"
//								+ "ID  : " + deviceReceipt.getID().length() + "\n"
//								+ "r   : " + deviceReceipt.get_r().toString().length() + "\n"
//								+ "d   : " + deviceReceipt.get_d().toString().length() + "\n"
//								+ "Ppub: " + Hex.toHexString(deviceReceipt.getPublicKey().getEncoded(true)).length() + "\n"
//								+ "Trust Value: " + String.valueOf(deviceReceipt.getTrustValue()) + "\n"
//								+ "Rand: " + Hex.toHexString(deviceReceipt.getRandEcPoint().getEncoded(true)) + "\n"							
//								+ "Master Ppub: " + Hex.toHexString(_backendServerKey.getPpub().getEncoded(true)) + "\n"							
//								+ "***************************************************************************************************\n");
						System.out.println("#### Current Registered Devices ####");
						for(String deviceID : _deviceReceipts.keySet()){
							System.out.println("Device-ID: " + deviceID);
						}						
					}
																																						
					System.out.println("Send Registration ACK.");
					System.out.println("***************************************************************************************************\n");						
					writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_D, writeBackPirvateKeyD);				
					/*
					 * Encode the ECPoint to Hex String by Hex.toHexString(byte[])
					 */
					writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_R,writeBackPrivateKeyR);
					writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_MASTER_PUBLIC_KEY, writeBackMasterPublicKey);
					
					writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_DEVICE_TRUST_VALUE, writeBackTrustValue);					
					writeObject(writeJsonObject);										
								
					break;
				default:
					break;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				cancelSocket();
				e.printStackTrace();				
			}
			
			/*Stop the socket connection*/
			cancelSocket();
		}		
		
		/*Stop the communication: close the input/output stream and close the socket*/
		protected void cancelSocket(){
			try{
				_inputStream.close();
				_outputStream.close();
				_clientSocket.close();
			}catch(IOException exception){
				exception.printStackTrace();
			}
		}
	}
	/**
	 * Start the server's service.
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread(new BackendServerService()).start();
	}

}
