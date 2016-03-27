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

import com.nick.tdp.security.BackendServerKey;
import com.nick.tdp.security.DeviceReceipt;
import com.nick.tdp.security.ECDHCurve;
/*
 * @author NickHuang
 * @mail xjhznick@gmail.com
 * The main process:
 * 	1. Generate the main key (x, Ppub), the private key and public key of the back end server.
 * 	2. Get the device's ID and Public Key through the socket, then generate (d, R)
 * 	3. Return (d, R) and Ppub to the the registered device.
 * 	4. Store the Device's authenticated credential (ID, r, R, d, P, t)¡£
 */
public class BackendServer implements Runnable {
//	private static final String TAG= "Back End Server";
	
	private BackendServerKey _backendServerKey;
	private HashMap<String, DeviceReceipt> _deviceReceipts;
	
	private final ServerSocket _serverSocket;
	
	public BackendServer(){
		_backendServerKey = new BackendServerKey();
		System.out.println("***************************************************************************************************\n"
				+ "==== Back End Server Infomation ====\n"				
				+ "Private: " + _backendServerKey.getx().toString() + "\n"
				+ "Pulic  : " + Hex.toHexString(_backendServerKey.getPpub().getEncoded(true)) + "\n"
				+ "***************************************************************************************************\n");
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
			 * start a new thread to communicate with the client
			 * new InStrem/OutStream
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
			Object object;
				try {
					ObjectInputStream objectInputStream = new ObjectInputStream(_inputStream);
					object = objectInputStream.readObject();
					messageHandler(object);
				} catch (IOException e) {
					//Stop the connected thread
					System.err.println("Connection Object InputStream Exception.");
//					e.printStackTrace();
					cancelSocket();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					cancelSocket();
				}
		}
		
		public void writeObject(JSONObject jsonObject){
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
		
		protected void messageHandler(Object object_){
			try {
				JSONObject jsonObject = new JSONObject(object_.toString());
				int packetType = jsonObject.getInt(TDPConstants.PACKET_TYPE);
				
				switch (packetType) {
				case TDPConstants.PACKET_TYPE_REGISTRATION:
					/*
					 * Get device ID & Public Key
					 */
					String deviceIDString = (String)jsonObject.get(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_ID);
					
					String publicKeyString = (String)jsonObject.get(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PUBLIC_KEY);
					ECPoint publicKeyEcPoint = ECDHCurve.getInstance().decodeBytePoint(Hex.decode(publicKeyString));
					
					DeviceReceipt deviceReceipt = new DeviceReceipt(deviceIDString, publicKeyEcPoint);
					
					try {
						deviceReceipt.generateReceipt(_backendServerKey.getx());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					_deviceReceipts.put(deviceReceipt.getID(), deviceReceipt);					
					
					/**
					 * Return the registered information to the Device
					 * (d, R, Ppub_master, t)
					 */
					jsonObject = new JSONObject();
					jsonObject.put(TDPConstants.PACKET_TYPE, TDPConstants.PACKET_TYPE_REGISTRATION_ACK);
					
					jsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_D, deviceReceipt.get_d().toString());				
					/*
					 * Encode the ECPoint to Hex String by Hex.toHexString(byte[])
					 */
					jsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_R,Hex.toHexString(deviceReceipt.getRandEcPoint().getEncoded(true)) );
					jsonObject.put(TDPConstants.PACKET_PAYLOAD_MASTER_PUBLIC_KEY, Hex.toHexString(_backendServerKey.getPpub().getEncoded(true)));
					
					jsonObject.put(TDPConstants.PACKET_PAYLOAD_DEVICE_TRUST_VALUE, deviceReceipt.getTrustValue());
					
					System.out.println("Send Registration ACK.");
					writeObject(jsonObject);
					/**
					 * Display the Device Registration Information					
					 */
					System.out.println("***************************************************************************************************\n"
							+ "==== Device Registration Infomation ====\n"
							+ "ID  : " + deviceReceipt.getID() + "\n"
							+ "d   : " + deviceReceipt.get_d().toString() + "\n"
							+ "Rand: " + Hex.toHexString(deviceReceipt.getRandEcPoint().getEncoded(true)) + "\n"
							+ "Ppub: " + Hex.toHexString(deviceReceipt.getPublicKey().getEncoded(true)) + "\n"
							+ "Master Ppub: " + Hex.toHexString(_backendServerKey.getPpub().getEncoded(true)) + "\n"
							+ "Trust Value: " + String.valueOf(deviceReceipt.getTrustValue()) + "\n"
							+ "***************************************************************************************************\n");
					System.out.println("#### Current Registered Devices ####");
					for(String deviceID : _deviceReceipts.keySet()){
						System.out.println("Device-ID: " + deviceID);
					}
					System.out.println("***************************************************************************************************");			
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread(new BackendServer()).start();
	}
}
