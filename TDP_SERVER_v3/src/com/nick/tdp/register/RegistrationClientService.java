package com.nick.tdp.register;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import com.nick.tdp.security.ECDHCurve;

public class RegistrationClientService implements Runnable {

private static final String TAG = "TDP Client Service";
	
	private Socket _socket;
	private InputStream _inputStream;
	private OutputStream _outputStream;
	
	public RegistrationClientService(){

	}
	
	public void run() {
		// TODO Auto-generated method stub
		try {
			_socket = new Socket(TDPConstants.SERVER_ADDRESS_REGISTRATION, TDPConstants.SERVER_PORT_REGISTRATION);
			_inputStream = _socket.getInputStream();
			_outputStream = _socket.getOutputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * When the socket connection established, start to send the first message.
		 */		
		sendPacket(TDPConstants.PACKET_TYPE_REGISTRATION_REQUEST_RECEIPT);  
		/*
		 * Receive the feedback from the Server.
		 */
		while(true){
			try {
				ObjectInputStream objectInputStream = new ObjectInputStream(_inputStream);
				Object object = objectInputStream.readObject();
//				System.out.println("Receive a Packet.");
				messageHandler(object);
			}catch(MySocketReadWriteException e){
				System.out.println(e.getMessage());
				cancelSocket();
				break;
			}
			catch (IOException | ClassNotFoundException e) {
				System.err.println(e.getMessage());
				cancelSocket();
				break;
			} catch (Exception e) {
				e.printStackTrace();				
				cancelSocket();
				break;
			}
		}
	}

	protected void cancelSocket(){
		try {
			_inputStream.close();
			_outputStream.close();
			_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private void sendPacket(int packetType_){
//		System.out.println(TAG + ": " + "send packet.");
		JSONObject jsonObject = null;
		switch (packetType_) {
		case TDPConstants.PACKET_TYPE_REGISTRATION_REQUEST_RECEIPT:
			System.out.println(TAG + ": " + "send packet -- request receipt.");
			/**
			 * Send (PacketType, DeviceID)
			 */
			jsonObject = new JSONObject();
			try {
				jsonObject.put(TDPConstants.PACKET_TYPE, TDPConstants.PACKET_TYPE_REGISTRATION_REQUEST_RECEIPT);
				jsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_ID, RegistrationClient._ID);				
				writeObject(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case TDPConstants.PACKET_TYPE_REGISTRATION_KEYS_OK:
			
			/**
			 * Generate device's public key and private key.
			 * Send (PacketType, DeviceID, PrivateKey, PublicKey)
			 */
			RegistrationClient.generateKeys();
			jsonObject = new JSONObject();
			try {
				jsonObject.put(TDPConstants.PACKET_TYPE, TDPConstants.PACKET_TYPE_REGISTRATION_KEYS_OK);
				jsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_ID, RegistrationClient._ID);				
				jsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PRIVATE_KEY, RegistrationClient._x.toString(16));
				jsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PUBLIC_KEY, Hex.toHexString(RegistrationClient._Ppub.getEncoded(true)));				
			} catch (JSONException e) {
				e.printStackTrace();
			}	
			writeObject(jsonObject);
			System.out.println(TAG + ": " + "send packet -- send client's keys.");
			break;
		default:
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
	
	private void messageHandler(Object object_)throws MySocketReadWriteException{
		try {
			JSONObject jsonObject = new JSONObject(object_.toString());
			int packetType = jsonObject.getInt(TDPConstants.PACKET_TYPE);			
			
			switch (packetType) {
			case TDPConstants.PACKET_TYPE_REGISTRATION_RECEIPT_OK:
				System.out.println("Receive a Packet about the Receipt.");
				/*
				 * Create a strategy to store the Device Receipt from the Back End Server.
				 * (x, Pub, r, d, R, Ppub_master, t)
				 */
				RegistrationClient._x = new BigInteger(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PRIVATE_KEY), 16);
				RegistrationClient._Ppub = ECDHCurve.getInstance().decodeBytePoint(Hex.decode(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PUBLIC_KEY)));
				RegistrationClient._d = new BigInteger(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_PRIVATE_D), 16);
				RegistrationClient._r = new BigInteger(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_PRIVATE_R), 16);
				RegistrationClient._Rpub = ECDHCurve.getInstance().decodeBytePoint(Hex.decode(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_PUBLIC_R)));
				RegistrationClient._Ppub_master = ECDHCurve.getInstance().decodeBytePoint(Hex.decode(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_SERVER_PUBLIC_KEY)));
				RegistrationClient._trustValue = jsonObject.getInt(TDPConstants.PACKET_PAYLOAD_DEVICE_TRUST_VALUE);
				/*
				 * Show the Registration Information.
				 */
				System.out.println("***************************************************************************************************\n"
						+ "\t==== Device Received Receipt ====\n"
						+ "\tID  : " + RegistrationClient._ID + "\n"
						+ "\tx   : " + RegistrationClient._x.toString(16) + "\n"
						+ "\tPpub: " + Hex.toHexString(RegistrationClient._Ppub.getEncoded(true)) + "\n"
						+ "\tr   : " + RegistrationClient._r.toString(16) + "\n"
						+ "\td   : " + RegistrationClient._d.toString(16) + "\n"
						+ "\tRand: " + Hex.toHexString(RegistrationClient._Rpub.getEncoded(true)) + "\n"						
						+ "\tMaster Ppub: " + Hex.toHexString(RegistrationClient._Ppub_master.getEncoded(true)) + "\n"
						+ "\tTrust Value: " + String.valueOf(RegistrationClient._trustValue) + "\n"
						+ "***************************************************************************************************\n");
				/*Terminate the loop of while-true in function sendPacket(int).*/
				throw new MySocketReadWriteException("End of recving packet.");
				
			case TDPConstants.PACKET_TYPE_REGISTRATION_REQUEST_KEYS:
				System.out.println("Received packet: Request Keys.");
				sendPacket(TDPConstants.PACKET_TYPE_REGISTRATION_KEYS_OK);
				break;
			default:
				throw new MySocketReadWriteException("Unknown type of received packet.");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			cancelSocket();
		}
		
	}
}
