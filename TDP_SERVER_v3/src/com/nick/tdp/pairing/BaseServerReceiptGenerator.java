package com.nick.tdp.pairing;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import com.nick.tdp.security.ECDHCurve;
import com.nick.tdp.security.HashFunction;

public class BaseServerReceiptGenerator {
	/**
	 * Receipt = (ID, r, R, d, P, t);
	 */
	private String _ID;
	private BigInteger _rand;
	private ECPoint _RandEcPoint;/* R = r*G */
	private BigInteger _descr;
	private ECPoint _PublicKey;
	private int _TrustValue;
	
	private BigInteger _baseServerPrivateKey;
	
	public BaseServerReceiptGenerator(BigInteger bsPrivateKey_, String deviceID_, ECPoint devicePublicKey_){
		_baseServerPrivateKey = bsPrivateKey_;
		_ID = deviceID_;
		_PublicKey = devicePublicKey_;
	}
	
	protected int setTrustValue(){
		return 10; /*Default trust value*/
	}
	
	public void generateReceipt()throws Exception{
		_TrustValue = setTrustValue();
		/*Generate r, R*/
		ECDHCurve ecdhCurve = new ECDHCurve();
		_rand = ecdhCurve.generatePrivateKeyBigInteger();
		_RandEcPoint = ecdhCurve.generatePublicKeyEcPoint(_rand);
		
		/**
		 * d = r + x * h0(ID || R || P) mod q (Error!)  --> d = r + x * h0(ID || R || P)
		 */
		String concatString = Hex.toHexString(_ID.getBytes()) 
							+ Hex.toHexString(_RandEcPoint.getEncoded(true)) 
							+ Hex.toHexString(_PublicKey.getEncoded(true));
		
		BigInteger xBSPrivateKey = _baseServerPrivateKey;
		
		_descr = xBSPrivateKey.multiply(HashFunction.hashZero(concatString));
		_descr = _descr.add(_rand);
	}
	
	public String getID(){
		return _ID;
	}
	
	public BigInteger get_rand(){
		return _rand;
	}
	
	public ECPoint getRandomEcPoint(){
		return _RandEcPoint;
	}
	
	public BigInteger get_desc(){
		return _descr;
	}
	
	public ECPoint getPublicKey(){
		return _PublicKey;
	}
	
	public int getTrustValue(){
		return _TrustValue;
	}
	
	public void printReceiptInfo(){
		int radix = 16;
		System.out.println("**** Receipt Infomation ****");
		System.out.println("ID: " + _ID);
		System.out.println("r : " + _rand.toString(radix));
		System.out.println("R: " + Hex.toHexString(_RandEcPoint.getEncoded(true)));
		System.out.println("d : " + _descr.toString(radix));
		System.out.println("P : " + Hex.toHexString(_PublicKey.getEncoded(true)));
		System.out.println("t : " + String.valueOf(_TrustValue));		
		System.out.println("\n");
	}	
}
