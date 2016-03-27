package com.nick.tdp.socket;

import java.math.BigInteger;
import java.util.Random;

import org.bouncycastle.math.ec.ECPoint;

import com.nick.tdp.security.ECDHCurve;

public class DeviceClient {

	public static String _ID;
	public static BigInteger _x;
	public static ECPoint _Ppub;
	
	public static ECPoint _Ppub_master;
	public static ECPoint _Rpub;
	public static BigInteger _d;
	public static int _trustValue;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * Generate the Device ID and asymmetrical secret key.
		 */
		_ID = "Android-" + String.valueOf(new Random());
		ECDHCurve ecdhCurve = new ECDHCurve();
		_x = ecdhCurve.generatePrivateKeyBigInteger();
		_Ppub = ecdhCurve.generatePublicKeyEcPoint(_x);
		/*
		 * Start the Client Socket to register in the back-end server.
		 */
		new Thread(new DeviceClientService(TDPConstants.PACKET_TYPE_REGISTRATION)).start();
	}

}
