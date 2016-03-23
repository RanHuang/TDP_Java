package com.nick.tdp.register;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONException;
import org.json.JSONObject;
import com.nick.tdp.database.RegistrationDatabase;
import com.nick.tdp.database.RegistrationReceipt;
import com.nick.tdp.security.BackendServerKey;


/*
 * @author NickHuang
 * @mail xjhznick@gmail.com
 * The main process:
 * 	1. Generate the main key (x, Ppub), the private key and public key of the back end server.
 * 	2. Get the device's ID and Public Key through the socket, then generate (d, R)
 * 	3. Return ( x, Pub, r, d, R, msterPub, tv) to the the registered device.
 * 	4. Store the Device's authenticated credential (ID, x, Pub, r, d, R, msterPub, tv) by database system.
 */
public class RegistrationServer implements Runnable {
	private static final String TAG = "Backend Server Service";

	private final ServerSocket _serverSocket;

	/**
	 * Generate the server key. And listen to the port for incoming client.
	 */
	public RegistrationServer() {
		BackendServerKey.getInstance();
		System.out.println(
				"***************************************************************************************************\n"
						+ "\t==== Back End Server Infomation ====\n" + "\tPrivate: "
						+ BackendServerKey.OBJ_SERVER_KEY.getx().toString() + "\n" + "\tPulic  : "
						+ Hex.toHexString(BackendServerKey.OBJ_SERVER_KEY.getPpub().getEncoded(true)) + "\n"
						+ "***************************************************************************************************\n");
		/*Use tempServerSocket because of the _serverSocket is final. */
		ServerSocket tempServerSocket = null; 
		try {
			tempServerSocket = new ServerSocket(TDPConstants.SERVER_PORT_REGISTRATION);
		} catch (IOException e) {
			e.printStackTrace();
		}
		_serverSocket = tempServerSocket;
	}

	/**
	 * server socket listen for the connect request, then open a new thread to
	 * handle the connection
	 */
	public void run() {
		Socket socket = null;
		while (true) {
			try {
				socket = _serverSocket.accept();
				System.out.println("\n\t^^^A client connected.^^^");
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			/* Start a new thread to communicate with each client. */
			if (socket != null) {
				new ClientSocketHandle(socket).start();
			}
		}
	}

	private class ClientSocketHandle extends Thread {

		private final Socket _clientSocket;
		private final InputStream _inputStream;
		private final OutputStream _outputStream;

		public ClientSocketHandle(Socket socket_) {
			_clientSocket = socket_;

			InputStream tempInputStream = null;
			OutputStream tempOutputStream = null;
			try {
				tempInputStream = _clientSocket.getInputStream();
				tempOutputStream = _clientSocket.getOutputStream();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			_inputStream = tempInputStream;
			_outputStream = tempOutputStream;
		}

		@Override
		public void run() {
			while (true) {
				try {
					ObjectInputStream objectInputStream = new ObjectInputStream(_inputStream);
					Object object = objectInputStream.readObject();
					messageHandler(object);
				} catch (MySocketReadWriteException e) {
					System.err.println(e.getMessage());
					cancelSocket();
					break;
				} catch (JSONException| ClassNotFoundException | IOException e) {
					e.printStackTrace();
					cancelSocket();
					break;
				}
			}
		}

		private void writeObject(JSONObject jsonObject) {
			try {
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(_outputStream);
				objectOutputStream.writeObject(jsonObject.toString());
				objectOutputStream.flush();
			} catch (IOException exception) {
				System.err.println("Connection Object OutputStream Exception.");
				exception.printStackTrace();
				cancelSocket();
			}
		}

		protected void messageHandler(Object object_) throws MySocketReadWriteException, JSONException {
			JSONObject readJsonObject = new JSONObject(object_.toString());
			/* Get packet type and device's ID. */
			int packetType = readJsonObject.getInt(TDPConstants.PACKET_TYPE);
			String deviceId = readJsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_ID);
			System.out.println("Receivde packet & Device ID: " + deviceId);

			RegistrationReceipt registrationReceipt = null;
			JSONObject writeJsonObject = null;
			switch (packetType) {
			case TDPConstants.PACKET_TYPE_REGISTRATION_REQUEST_RECEIPT:
				/*
				 * Get the registration receipt from the data base by device's
				 * ID. If the receipt exists, send it to the device. Otherwise,
				 * generate the receipt, then insert it into the data base and
				 * send it to the device.
				 */

				/**
				 * Get the registration receipt from the data base by device's
				 * ID. (x, Pub, r, d, R, masterPpub, trustValue)
				 */
				registrationReceipt = RegistrationDatabase.getReceiptByID(deviceId);
				if (registrationReceipt == null) {
					/* Request the client's keys to generate a new receipt. */
					System.out.println(TAG + ": " + "Packet: request client's keys(Private&Public).");
					writeJsonObject = new JSONObject();
					writeJsonObject.put(TDPConstants.PACKET_TYPE, TDPConstants.PACKET_TYPE_REGISTRATION_REQUEST_KEYS);
					writeObject(writeJsonObject);
				} else {
					/*
					 * Get the receipt from the database and send it to the client.
					 */
					System.out.println(TAG + ": " + "Packet: get the receipt from database and send it.");
					writeJsonObject = new JSONObject();

					writeJsonObject.put(TDPConstants.PACKET_TYPE, TDPConstants.PACKET_TYPE_REGISTRATION_RECEIPT_OK);
					writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PRIVATE_KEY, registrationReceipt.get_x());
					writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PUBLIC_KEY, registrationReceipt.get_Ppub());
					writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_PRIVATE_D, registrationReceipt.get_d());
					writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_PRIVATE_R, registrationReceipt.get_r());
					writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_PUBLIC_R, registrationReceipt.get_Rpub());
					String writeBackMasterPublicKey = Hex.toHexString(BackendServerKey.OBJ_SERVER_KEY.getPpub().getEncoded(true));
					writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_SERVER_PUBLIC_KEY, writeBackMasterPublicKey);
					writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_DEVICE_TRUST_VALUE, registrationReceipt.getTrustValue());

					writeObject(writeJsonObject);
					System.out.println(
							"***************************************************************************************************");
					throw new MySocketReadWriteException("Registration Receipt Sent.");
				}
				break;
			case TDPConstants.PACKET_TYPE_REGISTRATION_KEYS_OK:
				/**
				 * The receipt is not in the database, then get the client's keys to generate a new one.
				 * 1. Read the info from the client;
				 * 2. Generate a new receipt;
				 * 3. Send the receipt back;
				 * 4. Store the receipt into database.
				 */
				/* Generate a new registration receipt. */
				String privateKeyString = readJsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PRIVATE_KEY);
				String publicKeyString = readJsonObject.getString(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PUBLIC_KEY);				
				registrationReceipt = new RegistrationReceipt(deviceId, publicKeyString);
				registrationReceipt.set_x(privateKeyString);
				try {
					registrationReceipt.generateReceipt(BackendServerKey.OBJ_SERVER_KEY.getx());
				} catch (Exception e) {
					e.printStackTrace();
				}
				/* Send the new generated receipt to the client. */
				writeJsonObject = new JSONObject();

				writeJsonObject.put(TDPConstants.PACKET_TYPE, TDPConstants.PACKET_TYPE_REGISTRATION_RECEIPT_OK);
				writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PRIVATE_KEY, registrationReceipt.get_x());
				writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_DEVICE_PUBLIC_KEY, registrationReceipt.get_Ppub());
				writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_PRIVATE_D, registrationReceipt.get_d());
				writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_PRIVATE_R, registrationReceipt.get_r());
				writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_PUBLIC_R, registrationReceipt.get_Rpub());
				String writeBackMasterPublicKey = Hex.toHexString(BackendServerKey.OBJ_SERVER_KEY.getPpub().getEncoded(true));
				writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_REGISTRATION_SERVER_PUBLIC_KEY, writeBackMasterPublicKey);
				writeJsonObject.put(TDPConstants.PACKET_PAYLOAD_DEVICE_TRUST_VALUE, registrationReceipt.getTrustValue());

				writeObject(writeJsonObject);
				System.out.println("***************************************************************************************************\n"
						+ "\t==== Send Device Received Receipt ====\n"
						+ "\tID  : " + registrationReceipt.getID() + "\n"
						+ "\tx   : " + registrationReceipt.get_x() + "\n"
						+ "\tPpub: " + registrationReceipt.get_Ppub() + "\n"
						+ "\tr   : " + registrationReceipt.get_r() + "\n"
						+ "\td   : " + registrationReceipt.get_d() + "\n"
						+ "\tRand: " + registrationReceipt.get_Rpub() + "\n"						
						+ "\tMaster Ppub: " + writeBackMasterPublicKey + "\n"
						+ "\tTrust Value: " + String.valueOf(registrationReceipt.getTrustValue()) + "\n"
						+ "***************************************************************************************************\n");
				/* Insert the receipt into the database. */
				registrationReceipt.set_x(privateKeyString);
				if(RegistrationDatabase.addRegistrationReceipt(registrationReceipt)){
					System.out.println("\n$$$$Insert a new receipt to the database.$$$$\n");
					throw new MySocketReadWriteException("Registration Receipt Sent.");
				}else{
					throw new MySocketReadWriteException("Registration Receipt Sent, but failed to insert the receipt into the DB.");
				}
			default:
				throw new MySocketReadWriteException("Unknowed type of received packet.");
			}
			/**
			 * Display the Device Registration Information
			 */
		}

		/*
		 * Stop the communication: close the input/output stream and close the
		 * socket.
		 */
		protected void cancelSocket() {
			try {
				_inputStream.close();
				_outputStream.close();
				_clientSocket.close();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}

	}

	/**
	 * Start the server's service.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Thread(new RegistrationServer()).start();
	}

}
