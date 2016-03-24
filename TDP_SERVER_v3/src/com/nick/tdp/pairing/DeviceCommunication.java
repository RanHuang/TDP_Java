package com.nick.tdp.pairing;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import org.bouncycastle.math.ec.ECPoint;

import com.nick.tdp.register.TDPConstants;
import com.nick.tdp.security.BackendServerKey;
import com.nick.tdp.security.DeviceReceipt;
import com.nick.tdp.security.ECDHCurve;
/**
 * There are two processes in this communication between two devices.
 * The two processed are Device Pairing process and D2D Receipt Generation process. 
 * @author Nick
 *
 */
public class DeviceCommunication implements Runnable {

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
	 
	/**
	 * For the Device Pairing Process
	 */
	private String _thisDeviceID;
	    
	private DevicePairingProcess _devicePairingProcess;
	    
	private byte[] _UEcPointPair;
	private byte[] _CbytePair;
	private byte[] _FbytePair;
	
	private ECDHCurve _ecdhCurve;
	private BigInteger _baseServerPrivateKey;
	private ECPoint _baseSeverPublicKey;
	private BigInteger _thisPrivateKey;
	private ECPoint _thisPublicKey;
	private DeviceReceipt _thisDeviceReceipt;

	/**
	 * For the Receipt Generation Process
	 */
	private ReceiptGenerationProcess _receiptGenerationProcess;
	private int QoS_WiFi;
	    
	public DeviceCommunication(Socket socket_, boolean isServer_){
	    _isRunning = true;	        
	    _socket = socket_; 
	    _isServer = isServer_;
	        
	    _ecdhCurve = new ECDHCurve();
	    QoS_WiFi = 0;
	    initialize();		    
	}
	
	protected void initialize(){
		_baseServerPrivateKey = BackendServerKey.getInstance().getx();
		_baseSeverPublicKey = BackendServerKey.OBJ_SERVER_KEY.getPpub();
		
		Random random = new Random();
		_thisDeviceID = "Android-" + String.valueOf(random.nextInt(Integer.MAX_VALUE));
		_thisPrivateKey = _ecdhCurve.generatePrivateKeyBigInteger();
		_thisPublicKey = _ecdhCurve.generatePublicKeyEcPoint(_thisPrivateKey);
		_thisDeviceReceipt = new DeviceReceipt(_thisDeviceID, _thisPublicKey);
		try {
			_thisDeviceReceipt.generateReceipt(_baseServerPrivateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		_devicePairingProcess = new DevicePairingProcess(_thisPrivateKey, _thisDeviceReceipt.get_d(), _baseSeverPublicKey);
		
		_receiptGenerationProcess = new ReceiptGenerationProcess(_thisDeviceID);
		_receiptGenerationProcess.setClientSelfKeys(_thisPrivateKey, _thisDeviceReceipt.get_d());
		/**
		 * For Calculating QCR, while the CH is not none.
		 */
		double pre_q = 0.4;
		double pre_c = 0.6;
		String[] alpha_IDs = {"Android_1319752751", "Android_1864635234", "Android_1934807146", "Android_1475781389", "Android_219157648"};
		String strAlphaIDs = alpha_IDs[0];
		for(int i=1; i<alpha_IDs.length; i++){
			strAlphaIDs += ", " + alpha_IDs[i];
		}
		int[] alpha_ch_positive = {3, 2, 7, 4, 6};
		int[] alpha_ch_total = {7, 4, 8, 5, 7};
		
		String[] beta_IDs = {"Android_1864635234", "Android_131485814", "Android_1792883326", "Android_1934807146" , "Android_681387016"};
		String strBetaIDs = beta_IDs[0];
		for(int i=1; i<beta_IDs.length; i++){
			strBetaIDs += ", " + beta_IDs[i];
		}		
		int[] beta_ch_positive = {3, 4, 3, 2, 1};
		int[] beta_ch_total = {4, 5, 4, 3, 3};
		
		//Get the QoS and set
//		QoS_WiFi = -90;		
//		_receiptGenerationProcess.setQoS(QoS_WiFi);
		if(_isServer) {
			_receiptGenerationProcess.setSelfCHdata(pre_q, pre_c, strAlphaIDs, Arrays.toString(alpha_ch_positive), Arrays.toString(alpha_ch_total));
			QoS_WiFi = -80;		
			_receiptGenerationProcess.setQoS(QoS_WiFi);
		}else {
			_receiptGenerationProcess.setSelfCHdata(pre_q+0.2, pre_c-0.1, strBetaIDs, Arrays.toString(beta_ch_positive), Arrays.toString(beta_ch_total));
			QoS_WiFi = -90;		
			_receiptGenerationProcess.setQoS(QoS_WiFi);
		}	
	}
	@Override
	public void run() {
		System.out.println(TAG + ":" + "Start Socket Object Test. 1");
		
		try {
			_objectOutputStream = new ObjectOutputStream(_socket.getOutputStream());					
			_objectInputStream = new ObjectInputStream(new BufferedInputStream(_socket.getInputStream()));
			System.out.println(TAG + ":" + "Start Encryption Controller. 2");
		} catch (IOException e) {	
			System.err.println(TAG + ":" + "Exception - " + e.toString());			
			e.printStackTrace();
		}
		

        if(!_isServer){ //The start of Encryption Logic
        	_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.MESSAGE_BEGIN, "^%&$Start Encryption Process");
        	writeObject(_outSocketKeyValue);
        }

        while (_isRunning){        	
            try {	
            	
            	System.out.println(TAG + ":" + "Wait for a message.");
            	_obj = _objectInputStream.readObject();
            	_inSocketKeyValue = (SocketKeyValueObject)_obj;
            	processing(_inSocketKeyValue);
            	
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}     
        }
        
        try {
        	_objectInputStream.close();
        	_objectOutputStream.close();
		    _socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void writeObject(SocketKeyValueObject socketKeyValue){
		numSentObject++;
		socketKeyValue.set_index(numSentObject);
		try {
			_objectOutputStream.writeObject(socketKeyValue);
			_objectOutputStream.flush();
			System.out.println(TAG + ":" + "Send Message-" + socketKeyValue.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	/**
	 * The main logic of device pairing process.
	 */
	
	private void processing(SocketKeyValueObject socketKeyValue) throws Exception{
		System.out.println(TAG + "-" + "Received Message: ");
		socketKeyValue.printBaseInfo();
		
		String stringKey = socketKeyValue.get_key();
		
		if(stringKey.equals(TDPConstants.MESSAGE_BEGIN)){
			if(_isServer){
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.MESSAGE_BEGIN, "$%^&Server Socket start$%^&");
				
			}else{
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.TDP_PAIR_RECEIPT_INFO, "Device Base Server Receipt Info");
				_outSocketKeyValue.setReceiptInfo(_thisDeviceID, _thisDeviceReceipt.getRandEcPoint().getEncoded(true), _thisPublicKey.getEncoded(true));										
			}
        }else if (stringKey.equals(TDPConstants.TDP_PAIR_RECEIPT_INFO)) {
        	String pairID = socketKeyValue.get_pairDeviceID();
        	byte[] pairRandomByte = socketKeyValue.get_pairDeviceRandomByte();
        	byte[] pairPublicKeyByte = socketKeyValue.get_pairDevicePublicKeyByte();
        	ECPoint pairRandomEcPoint = _ecdhCurve.decodeBytePoint(pairRandomByte);
        	ECPoint pairPublicKeyEcPoint = _ecdhCurve.decodeBytePoint(pairPublicKeyByte);
        	
			_devicePairingProcess.setPairDeviceReceiptInfo(pairID, pairRandomEcPoint, pairPublicKeyEcPoint);
			
			_receiptGenerationProcess.setClientPairKeys(pairPublicKeyEcPoint, pairRandomEcPoint);
			_receiptGenerationProcess.setPairID(pairID);
     	
			if(_isServer){								
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.TDP_PAIR_RECEIPT_INFO, "Device Base Server Receipt Info");
				_outSocketKeyValue.setReceiptInfo(_thisDeviceID, _thisDeviceReceipt.getRandEcPoint().getEncoded(true), _thisPublicKey.getEncoded(true));							
			}else{
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.TDP_PAIR_U, "Device Data U");
				_outSocketKeyValue.set_Ubyte(_devicePairingProcess.getUnixEcPoint().getEncoded(true));
			}
		}else if(stringKey.equals(TDPConstants.TDP_PAIR_U)){
			_UEcPointPair = socketKeyValue.get_Ubyte();
			_devicePairingProcess.calculateCat(_UEcPointPair);

			if(_isServer){
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.TDP_PAIR_U, "Device Data U");
				_outSocketKeyValue.set_Ubyte(_devicePairingProcess.getUnixEcPoint().getEncoded(true));
			}else {
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.TDP_PAIR_C, "Device Data C");
				_outSocketKeyValue.set_Cbyte(_devicePairingProcess.getCat());
			}
		}else if (stringKey.equals(TDPConstants.TDP_PAIR_C)) {
			_CbytePair = socketKeyValue.get_Cbyte();			
			_devicePairingProcess.calculateFsck(_CbytePair);
			
			if(_isServer){
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.TDP_PAIR_C, "Device Data C");
				_outSocketKeyValue.set_Cbyte(_devicePairingProcess.getCat());				
			}else {
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.TDP_PAIR_F, "Device Data F");
				_outSocketKeyValue.set_Fbyte(_devicePairingProcess.getFsck());				
			}			
		}else if(stringKey.equals(TDPConstants.TDP_PAIR_F)){
			_FbytePair = socketKeyValue.get_Fbyte();
			_devicePairingProcess.calculateSecretKey(_FbytePair);
			//Get the QoS and set
//			QoS_WiFi = -90;
//			_receiptGenerationProcess.setQoS(QoS_WiFi);
			if(_isServer){
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.TDP_PAIR_F, "Device Data F");
				_outSocketKeyValue.set_Fbyte(_devicePairingProcess.getFsck());
			}else {
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.TDP_GEN_RECP_CH, "Device's Contact History");	
				_outSocketKeyValue.set_ContactHistory(QoS_WiFi, _receiptGenerationProcess.get_CHIDs(), _receiptGenerationProcess.get_CHPositive(), _receiptGenerationProcess.get_CHTotal());
			}
		}else if (stringKey.equals(TDPConstants.TDP_GEN_RECP_CH)) {
			int pairQoS = socketKeyValue.get_Qos();
			String pairCHIDs = socketKeyValue.get_CHIDs();
			String pairCHPositive = socketKeyValue.get_CHPositive();
			String pairCHTotal = socketKeyValue.get_CHTotal();
						
			if(_isServer){
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.TDP_GEN_RECP_CH, "Device's Contact History");
				_outSocketKeyValue.set_ContactHistory(QoS_WiFi, _receiptGenerationProcess.get_CHIDs(), _receiptGenerationProcess.get_CHPositive(), _receiptGenerationProcess.get_CHTotal());
				/* Send the Contact History before calculate the Signature. */
				_receiptGenerationProcess.setPairCHdata(pairCHIDs, pairCHPositive, pairCHTotal);
				_receiptGenerationProcess.calculateQCR(pairQoS);
				_receiptGenerationProcess.calcSignature();
			}else {
				_receiptGenerationProcess.setPairCHdata(pairCHIDs, pairCHPositive, pairCHTotal);
				_receiptGenerationProcess.calculateQCR(pairQoS);
				_receiptGenerationProcess.calcSignature();
				/* Calculate the Signature then send it. */
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.TDP_GEN_RECP_SIG, "Device's Signature");
				_outSocketKeyValue.set_Signature(_receiptGenerationProcess.get_er(), _receiptGenerationProcess.get_Snumerator(), _receiptGenerationProcess.get_Sdenominator(),
												_receiptGenerationProcess.get_T(1), _receiptGenerationProcess.get_T(2), _receiptGenerationProcess.get_T(3));
			}
		}else if (stringKey.equals(TDPConstants.TDP_GEN_RECP_SIG)) {
			byte[] er = socketKeyValue.get_er();
			byte[] snum = socketKeyValue.get_Snum();
			byte[] sden = socketKeyValue.get_Sden();
			byte[] t1 = socketKeyValue.get_T1();
			byte[] t2 = socketKeyValue.get_T2();
			byte[] t3 = socketKeyValue.get_T3();
			
			_receiptGenerationProcess.setPairSignature(er, snum, sden, t1, t2, t3);
			_receiptGenerationProcess.calcReceipt();
			if(_isServer){
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.TDP_GEN_RECP_SIG, "Device's Signature");
				_outSocketKeyValue.set_Signature(_receiptGenerationProcess.get_er(), _receiptGenerationProcess.get_Snumerator(), _receiptGenerationProcess.get_Sdenominator(),
												_receiptGenerationProcess.get_T(1), _receiptGenerationProcess.get_T(2), _receiptGenerationProcess.get_T(3));
			}else {
				_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.MESSAGE_END, "$%^& Server Socket End $%^&");
				_isRunning = false;
			}
		}else if(stringKey.equals(TDPConstants.MESSAGE_END)){
        	_isRunning = false;
        	return;
        }else if(stringKey.equals(TDPConstants.MESSAGE_UNKNOWN)) {
        	_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.MESSAGE_END, "$Data transmission is over.");
			writeObject(_outSocketKeyValue);
			_isRunning = false;
        }else {
        	_outSocketKeyValue = new SocketKeyValueObject(TDPConstants.MESSAGE_UNKNOWN, "Do not know what to do. Start from the begining.");
        	writeObject(_outSocketKeyValue);
        }
		
		writeObject(_outSocketKeyValue);		
	}

}
