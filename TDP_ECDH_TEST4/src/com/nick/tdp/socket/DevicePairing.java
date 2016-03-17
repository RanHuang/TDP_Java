package com.nick.tdp.socket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Random;

import org.bouncycastle.math.ec.ECPoint;

import com.nick.tdp.ecdh.ECDHCurve;
import com.nick.tdp.securityprocess.BaseServerReceiptGenerator;
import com.nick.tdp.securityprocess.DevicePairingProcess;

public class DevicePairing implements Runnable {

	private static final String TAG = "Device Pairing";
	private volatile boolean _isRunning;

	private boolean _isServer;

	private ObjectOutputStream _objectOutputStream;
	private ObjectInputStream _objectInputStream;
	private Socket _socket;
	    
	private static long numSentObject = 0;
	    
	private SocketKeyValueObject _outSocketKeyValue;
	private SocketKeyValueObject  _inSocketKeyValue;
	private Object _obj;
	    
	private String _thisDeviceID;
	//For Encryption Negotiation
	    
	private DevicePairingProcess _devicePairingProcess;
	    
	private byte[] _UEcPointPair;
	private byte[] _CbytePair;
	private byte[] _FbytePair;
	
	private ECDHCurve _ecdhCurve;
	private BigInteger _baseServerPrivateKey;
	private ECPoint _baseSeverPublicKey;
	private BigInteger _thisPrivateKey;
	private ECPoint _thisPublicKey;
	private BaseServerReceiptGenerator _thisBaseServerReceipt;
	    
	public DevicePairing(Socket socket_, boolean isServer_){
	    _isRunning = true;	        
	    _socket = socket_; 
	    _isServer = isServer_;
	        
	    _ecdhCurve = new ECDHCurve();
	    initialize();	        
	}
	
	protected void initialize(){
		_baseServerPrivateKey = new BigInteger("de9ff58a22798adf2b31f33c8ca9324414257c1d2fa9cdbd", 16);
		_baseSeverPublicKey = _ecdhCurve.generatePublicKeyEcPoint(_baseServerPrivateKey);
		
		_thisDeviceID = "Android-" + String.valueOf(new Random());
		_thisPrivateKey = _ecdhCurve.generatePrivateKeyBigInteger();
		_thisPublicKey = _ecdhCurve.generatePublicKeyEcPoint(_thisPrivateKey);
		_thisBaseServerReceipt = new BaseServerReceiptGenerator(_baseServerPrivateKey, _thisDeviceID, _thisPublicKey);
		try {
			_thisBaseServerReceipt.generateReceipt();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		_devicePairingProcess = new DevicePairingProcess(_thisPrivateKey, _thisBaseServerReceipt.get_desc(), _baseSeverPublicKey);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.err.println(TAG + ":" + "Start Socket Object Test. 1");
		
		try {
			_objectOutputStream = new ObjectOutputStream(_socket.getOutputStream());					
			_objectInputStream = new ObjectInputStream(new BufferedInputStream(_socket.getInputStream()));
			System.err.println(TAG + ":" + "Start Encryption Controller. 2");
		} catch (IOException e) {
			// TODO Auto-generated catch block		
			System.err.println(TAG + ":" + "Exception - " + e.toString());			
			e.printStackTrace();
		}
		

        if(!_isServer){ //The start of Encryption Logic
        	_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.MESSAGE_BEGIN, "^%&$Start Encryption Process");
        	writeObject(_outSocketKeyValue);
        }

        while (_isRunning){
        	
            try {	
            	
            	System.err.println(TAG + ":" + "Wait for a message.");
            	_obj = _objectInputStream.readObject();
            	_inSocketKeyValue = (SocketKeyValueObject)_obj;
            	processing(_inSocketKeyValue);
            	
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     

        }
        
        try {
        	_objectInputStream.close();
        	_objectOutputStream.close();
		    _socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void writeObject(SocketKeyValueObject socketKeyValue){
		numSentObject++;
		socketKeyValue.set_index(numSentObject);
		try {
			_objectOutputStream.writeObject(socketKeyValue);
			_objectOutputStream.flush();
			System.err.println(TAG + ":" + "Send Message-" + socketKeyValue.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	/**
	 * The main logic of device pairing process.
	 */
	
	private void processing(SocketKeyValueObject socketKeyValue) throws Exception{
		System.err.println(TAG + "-" + "Received Message: ");
		socketKeyValue.printBaseInfo();
		
		String stringKey = socketKeyValue.get_key();
		
		if(stringKey.equals(KeyConstant.MESSAGE_BEGIN)){
			if(_isServer){
				_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.MESSAGE_BEGIN, "$%^&Socket start$%^&");
				
			}else{
				_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.TDP_PAIR_RECEIPT_INFO, "Pair Device Base Server Receipt Info");
				_outSocketKeyValue.setReceiptInfo(_thisDeviceID, _thisBaseServerReceipt.getRandomEcPoint().getEncoded(true), _thisPublicKey.getEncoded(true));										
			}
        }else if (stringKey.equals(KeyConstant.TDP_PAIR_RECEIPT_INFO)) {
        	String pairID = socketKeyValue.get_pairDeviceID();
        	byte[] pairRandomByte = socketKeyValue.get_pairDeviceRandomByte();
        	byte[] pairPublicKeyByte = socketKeyValue.get_pairDevicePublicKeyByte();
        	ECPoint pairRandomEcPoint = _ecdhCurve.decodeBytePoint(pairRandomByte);
        	ECPoint pairPublicKeyEcPoint = _ecdhCurve.decodeBytePoint(pairPublicKeyByte);
        	
			_devicePairingProcess.setPairDeviceReceiptInfo(pairID, pairRandomEcPoint, pairPublicKeyEcPoint);
     	
			if(_isServer){								
				_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.TDP_PAIR_RECEIPT_INFO, "Pair Device Base Server Receipt Info");
				_outSocketKeyValue.setReceiptInfo(_thisDeviceID, _thisBaseServerReceipt.getRandomEcPoint().getEncoded(true), _thisPublicKey.getEncoded(true));							
			}else{
				_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.TDP_PAIR_U, "Pair Device Data U");
				_outSocketKeyValue.set_Ubyte(_devicePairingProcess.getUnixEcPoint().getEncoded(true));
			}
		}else if(stringKey.equals(KeyConstant.TDP_PAIR_U)){
			_UEcPointPair = socketKeyValue.get_Ubyte();
			_devicePairingProcess.calculateCat(_UEcPointPair);

			if(_isServer){
				_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.TDP_PAIR_U, "Pair Device Data U");
				_outSocketKeyValue.set_Ubyte(_devicePairingProcess.getUnixEcPoint().getEncoded(true));
			}else {
				_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.TDP_PAIR_C, "Pair Device Data C");
				_outSocketKeyValue.set_Cbyte(_devicePairingProcess.getCat());
			}
		}else if (stringKey.equals(KeyConstant.TDP_PAIR_C)) {
			_CbytePair = socketKeyValue.get_Cbyte();			
			_devicePairingProcess.calculateFsck(_CbytePair);
			
			if(_isServer){
				_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.TDP_PAIR_C, "Pair Device Data C");
				_outSocketKeyValue.set_Cbyte(_devicePairingProcess.getCat());				
			}else {
				_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.TDP_PAIR_F, "Pair Device Data F");
				_outSocketKeyValue.set_Fbyte(_devicePairingProcess.getFsck());				
			}			
		}else if(stringKey.equals(KeyConstant.TDP_PAIR_F)){
			_FbytePair = socketKeyValue.get_Fbyte();
			_devicePairingProcess.calculateSecretKey(_FbytePair);
			
			if(_isServer){
				_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.TDP_PAIR_F, "Pair Device Data F");
				_outSocketKeyValue.set_Fbyte(_devicePairingProcess.getFsck());
			}else {
				_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.MESSAGE_END, "$%^&Socket start$%^&");
				_isRunning = false;
			}
		}
		else if(stringKey.equals(KeyConstant.MESSAGE_END)){
        	_isRunning = false;
        	return;
        }else if(stringKey.equals(KeyConstant.MESSAGE_UNKNOWN)) {
        	_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.MESSAGE_END, "$Data transmission is over.");
			writeObject(_outSocketKeyValue);
			_isRunning = false;
        }else {
        	_outSocketKeyValue = new SocketKeyValueObject(KeyConstant.MESSAGE_UNKNOWN, "Do not know what to do. Start from the begining.");
        	writeObject(_outSocketKeyValue);
        }
		
		writeObject(_outSocketKeyValue);
		
	}

}
