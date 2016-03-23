package com.nick.tdp.register;

import java.math.BigInteger;
import java.util.Random;
import org.bouncycastle.math.ec.ECPoint;

import com.nick.tdp.security.ECDHCurve;

public class RegistrationClient {

	public static String _ID;
	public static BigInteger _x;
	public static ECPoint _Ppub;	
	public static BigInteger _r;
	public static BigInteger _d;
	public static ECPoint _Rpub;
	public static ECPoint _Ppub_master;
	public static int _trustValue;	
	
	public static void generateKeys(){
		ECDHCurve ecdhCurve = new ECDHCurve();
		_x = ecdhCurve.generatePrivateKeyBigInteger();
		_Ppub = ecdhCurve.generatePublicKeyEcPoint(_x);
//		if(_x == null | _Ppub == null){
//			System.err.println("failed to genereate the keys");
//		}else {
//			System.out.println(
//					"\tID: " + _ID 
//					+ "\n\tPrivate Key: " + _x.toString(16) 
//					+ "\n\tPublic Key: " + Hex.toHexString(_Ppub.getEncoded(true)) 
//					+ "\n");
//		}
	}	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * Generate the Device ID and asymmetrical secret key.
		 */
		Random random = new Random();
		/*10 Android ID*/
		String[] strAndroidID = {"Android_1319752751", "Android_1864635234", "Android_239632920", "Android_131485814",
								 "Android_1792883326", "Android_1934807146", "Android_1475781389", "Android_74059307",
								 "Android_219157648", "Android_681387016"};
		/*r[0, BOUND), also the number  of devices to be selected. Bound <= 10 */
		int BOUND = 4;
		_ID = strAndroidID[random.nextInt(BOUND)];
				
		/*
		 * Start the Client Socket to register in the back-end server.
		 */
		new Thread(new RegistrationClientService()).start();
	}
	
}
