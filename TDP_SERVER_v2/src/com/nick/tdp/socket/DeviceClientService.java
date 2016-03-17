package com.nick.tdp.socket;

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

public class DeviceClientService implements Runnable {

private static final String TAG = "TDP Client Service";
	
	private Socket _socket;
	private InputStream _inputStream;
	private OutputStream _outputStream;
	
	public DeviceClientService(){

	}
	
	public void run() {
		// TODO Auto-generated method stub
		try {
			_socket = new Socket(TDPConstants.SERVER_ADDRESS_REGISTRATION, TDPConstants.SERVER_PORT_REGISTRATION);
			_inputStream = _socket.getInputStream();
			_outputStream = _socket.getOutputStream();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
				jsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_ID, DeviceClient._ID);				
				writeObject(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case TDPConstants.PACKET_TYPE_REGISTRATION_KEYS_OK:
			System.out.println(TAG + ": " + "send packet -- send client's keys.");
			/**
			 * Generate device's public key and private key.
			 * Send (PacketType, DeviceID, PrivateKey, PublicKey)
			 */
			DeviceClient.generateKeys();
			jsonObject = new JSONObject();
			try {
				jsonObject.put(TDPConstants.PACKET_TYPE, TDPConstants.PACKET_TYPE_REGISTRATION_KEYS_OK);
				jsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_ID, DeviceClient._ID);				
				jsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PRIVATE_KEY, DeviceClient._x.toString());
				jsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PUBLIC_KEY, Hex.toHexString(DeviceClient._Ppub.getEncoded(true)));				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
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
			
			System.out.println("Receive a Packet.");
			
			switch (packetType) {
			case TDPConstants.PACKET_TYPE_REGISTRATION_RECEIPT_OK:
				/*
				 * Create a strategy to store the Device Receipt from the Back End Server.
				 * (x, Pub, r, d, R, Ppub_master, t)
				 */
				DeviceClient._x = new BigInteger(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PRIVATE_KEY));
				DeviceClient._Ppub = ECDHCurve.getInstance().decodeBytePoint(Hex.decode(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PUBLIC_KEY)));
				DeviceClient._d = new BigInteger(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_PRIVATE_D));
				DeviceClient._r = new BigInteger(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_PRIVATE_R));
				DeviceClient._Rpub = ECDHCurve.getInstance().decodeBytePoint(Hex.decode(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_PUBLIC_R)));
				DeviceClient._Ppub_master = ECDHCurve.getInstance().decodeBytePoint(Hex.decode(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_SERVER_PUBLIC_KEY)));
				DeviceClient._trustValue = jsonObject.getInt(TDPConstants.PACKET_PAYLOAD_DEVICE_TRUST_VALUE);
				/*
				 * Show the Registration Information.
				 */
				System.out.println("***************************************************************************************************\n"
						+ "\t==== Device Received Receipt ====\n"
						+ "\tID  : " + DeviceClient._ID + "\n"
						+ "\tx   : " + DeviceClient._x.toString() + "\n"
						+ "\tPpub: " + Hex.toHexString(DeviceClient._Ppub.getEncoded(true)) + "\n"
						+ "\tr   : " + DeviceClient._r.toString() + "\n"
						+ "\td   : " + DeviceClient._d.toString() + "\n"
						+ "\tRand: " + Hex.toHexString(DeviceClient._Rpub.getEncoded(true)) + "\n"						
						+ "\tMaster Ppub: " + Hex.toHexString(DeviceClient._Ppub_master.getEncoded(true)) + "\n"
						+ "\tTrust Value: " + String.valueOf(DeviceClient._trustValue) + "\n"
						+ "***************************************************************************************************\n");
				/*Terminate the loop of while-true in function sendPacket(int).*/
				throw new MySocketReadWriteException("End of recving packet.");
				
			case TDPConstants.PACKET_TYPE_REGISTRATION_REQUEST_KEYS:
				sendPacket(TDPConstants.PACKET_TYPE_REGISTRATION_KEYS_OK);
				break;
			default:
				throw new MySocketReadWriteException("Unknown type of received packet.");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cancelSocket();
		}
		
	}
}
