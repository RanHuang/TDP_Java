package com.nick.tdp.security;

import java.math.BigInteger;

/*
 *  Device's authenticated credential:
 *  	(ID, r, R, d, P, t)
 */

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

public class DeviceReceipt {
	
	private static final int DefaultTrustValue = 20;
	
	private String _ID;
	private BigInteger _rand;
	private ECPoint _RandEcPoint; /* R = r*G */
	private BigInteger _descr;
	private ECPoint _Ppub;
	private int _trustValue = DefaultTrustValue;
	
	public DeviceReceipt(String ID_, ECPoint Ppub_){
		_ID = ID_;
		_Ppub = Ppub_;
	}
	
	public DeviceReceipt(){

	}
	
	public void setID(String id_){
		_ID = id_;
	}
	public String getID(){
		return _ID;
	}
	
	public void setPublicKey(ECPoint publicKey_){
		_Ppub = publicKey_;
	}	
	public ECPoint getPublicKey(){
		return _Ppub;
	}
	
	public void generateReceipt(BigInteger backendServerPrivateKey_) throws Exception{
		/*Generate r, R*/
		ECDHCurve ecdhCurve = new ECDHCurve();
		_rand = ecdhCurve.generatePrivateKeyBigInteger();
		_RandEcPoint = ecdhCurve.generatePublicKeyEcPoint(_rand);
		
		/**
		 * d = r + x * h0(ID || R || P) mod q (Error!)  --> d = r + x * h0(ID || R || P)
		 */
		String concatString = Hex.toHexString(_ID.getBytes()) 
							+ Hex.toHexString(_RandEcPoint.getEncoded(true)) 
							+ Hex.toHexString(_Ppub.getEncoded(true));
		
		_descr = backendServerPrivateKey_.multiply(HashFunction.hashZero(concatString));
		_descr = _descr.add(_rand);
	}
	
	public void setTrustValue(int trustValue_){
		_trustValue = trustValue_;
	}	
	
	public int getTrustValue(){
		return _trustValue;
	}
	
	public BigInteger get_r(){
		return _rand;
	}
	
	public ECPoint getRandEcPoint(){
		return _RandEcPoint;
	}
	
	public BigInteger get_d(){
		return _descr;
	}
}
