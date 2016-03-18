package com.nick.tdp.database;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import com.nick.tdp.security.ECDHCurve;
import com.nick.tdp.security.HashFunction;

public class RegistrationReceipt {
	private static final int DefaultTrustValue = 20;
	/**
	 * Private Key -- BigInteger
	 * 	BigInteger --> String: _bigInt_PriKey.toString(16)
	 *  String --> BigInteger: new BigInteger(str_PriKey, 16)
	 * Public Key --  ECPoint
	 * 	ECPoint --> String: Hex.toHexString(_ecP_Ppub.getEncoded(true))
	 * 	String --> ECPoint: ECDHCurve.getInstance().decodeBytePoint(Hex.decode(_str_Ppub))
	 */
	
	/**
	 * All the fields are the same as those in the database;
	 * (ID, x, Pub, r, d, R, t)
	 */
	private String _str_ID;
	private String _str_x;
	private String _str_Ppub;
	private String _str_r;
	private String _str_d;
	private String _str_Rpub;
	private int _trustValue = DefaultTrustValue;
	
	public RegistrationReceipt(String ID_, String Ppub_){
		_str_ID = ID_;
		_str_Ppub = Ppub_;
	}
	
	public RegistrationReceipt(){

	}
	
	public void setID(String id_){
		_str_ID = id_;
	}
	public String getID(){
		return _str_ID;
	}
	
	public void set_x(String strPriKey_){
		_str_x = strPriKey_;
	}
	public String get_x(){
		return _str_x;
	}
	
	public void set_Ppub(String strPubKey_){
		_str_Ppub = strPubKey_;
	}	
	public String get_Ppub(){
		return _str_Ppub;
	}

	public void set_r(String strPrikey_){
		_str_r = strPrikey_;
	}
	public String get_r(){
		return _str_r;
	}
	
	public void set_Rpub(String strPubkey_){
		_str_Rpub = strPubkey_;
	}
	public String get_Rpub(){
		return _str_Rpub;
	}
	
	public void set_d(String strPriKey_){
		_str_d = strPriKey_;
	}
	public String get_d(){
		return _str_d;
	}
	
	
	
	public void setTrustValue(int trustValue_){
		_trustValue = trustValue_;
	}	
	public int getTrustValue(){
		return _trustValue;
	}	
	
	public void generateReceipt(BigInteger backendServerPrivateKey_) throws Exception{
		/*Generate r, R*/
		ECDHCurve ecdhCurve = new ECDHCurve();
		BigInteger rand = ecdhCurve.generatePrivateKeyBigInteger();
		ECPoint RandEcPoint = ecdhCurve.generatePublicKeyEcPoint(rand); /* R = r*G */
		ECPoint Ppub = ECDHCurve.getInstance().decodeBytePoint(Hex.decode(_str_Ppub));
		/**
		 * d = r + x * h0(ID || R || P) mod q (Error!)  --> d = r + x * h0(ID || R || P)
		 */
		String concatString = Hex.toHexString(_str_ID.getBytes()) 
							+ Hex.toHexString(RandEcPoint.getEncoded(true)) 
							+ Hex.toHexString(Ppub.getEncoded(true));
		
		BigInteger descr = backendServerPrivateKey_.multiply(HashFunction.hashZero(concatString));
		descr = descr.add(rand);
		
		_str_r = rand.toString(16);
		_str_Rpub = Hex.toHexString(RandEcPoint.getEncoded(true));		
		_str_d = descr.toString(16);
	}
}
