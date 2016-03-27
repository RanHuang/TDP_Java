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
	
	private int _packetType;
	
	public DeviceClientService(int type_){
		_packetType = type_;
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
		 * When the socket connection established, start to send message.
		 */
		sendPacket();  
		/*
		 * Receive the feedback from the Server.
		 */
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(_inputStream);
			Object object = objectInputStream.readObject();
			System.out.println("Receive a Packet.");
			receivePacket(object);
		} catch (IOException e) {
			e.printStackTrace();
			cancelSocket();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			cancelSocket();
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
	
	private void sendPacket(){
		System.out.println(TAG + ": " + "send packet.");
		switch (_packetType) {
		case TDPConstants.PACKET_TYPE_REGISTRATION:
			/*
			 * Get the Device ID and Public Key, then send to the Back End Server for registration.
			 */
			JSONObject jsonObject = new JSONObject();
			try {
				/*
				 * Send device ID & Public Key.
				 */
				jsonObject.put(TDPConstants.PACKET_TYPE, TDPConstants.PACKET_TYPE_REGISTRATION);
				jsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_ID, DeviceClient._ID);
				jsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PUBLIC_KEY, Hex.toHexString(DeviceClient._Ppub.getEncoded(true)));
				
				writeObject(jsonObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
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
	
	private void receivePacket(Object object_){
		try {
			JSONObject jsonObject = new JSONObject(object_.toString());
			int packetType = jsonObject.getInt(TDPConstants.PACKET_TYPE);
			
			switch (packetType) {
			case TDPConstants.PACKET_TYPE_REGISTRATION_ACK:
				/*
				 * Create a strategy to store the Device Receipt from the Back End Server.
				 * (d, R, Ppub_master, t)
				 */
				DeviceClient._d = new BigInteger(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_D));
				DeviceClient._Rpub = ECDHCurve.getInstance().decodeBytePoint(Hex.decode(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_R)));
				DeviceClient._Ppub_master = ECDHCurve.getInstance().decodeBytePoint(Hex.decode(jsonObject.getString(TDPConstants.PACKET_PAYLOAD_MASTER_PUBLIC_KEY)));
				DeviceClient._trustValue = jsonObject.getInt(TDPConstants.PACKET_PAYLOAD_DEVICE_TRUST_VALUE);
				/*
				 * Show the Registration Information.
				 */
				System.out.println("***************************************************************************************************\n"
						+ "==== Device Registration Infomation ====\n"
						+ "ID  : " + DeviceClient._ID + "\n"
						+ "d   : " + DeviceClient._d.toString() + "\n"
						+ "Rand: " + Hex.toHexString(DeviceClient._Rpub.getEncoded(true)) + "\n"
						+ "Ppub: " + Hex.toHexString(DeviceClient._Ppub.getEncoded(true)) + "\n"
						+ "Master Ppub: " + Hex.toHexString(DeviceClient._Ppub_master.getEncoded(true)) + "\n"
						+ "Trust Value: " + String.valueOf(DeviceClient._trustValue) + "\n"
						+ "***************************************************************************************************\n");
				break;

			default:
				break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cancelSocket();
		}
		
	}
}
