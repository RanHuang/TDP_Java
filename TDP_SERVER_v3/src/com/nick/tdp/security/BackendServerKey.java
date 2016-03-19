package com.nick.tdp.security;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;

public class BackendServerKey {
	
	private static BigInteger _PrivateKeyBigInteger;
	private static ECPoint _PublicKeyEcPoint;
	
	public static BackendServerKey OBJ_SERVER_KEY;
	
	public BackendServerKey(){
		ECDHCurve ecdhCurve = new ECDHCurve();
		
		_PrivateKeyBigInteger = ECCurveParams.SERVER_PRIVATE_KEY;
		_PublicKeyEcPoint = ecdhCurve.generatePublicKeyEcPoint(_PrivateKeyBigInteger);
	}
	
	public static synchronized BackendServerKey getInstance(){
		if(OBJ_SERVER_KEY == null){
			OBJ_SERVER_KEY = new BackendServerKey();
		}
		return OBJ_SERVER_KEY;
	}
	
	public BigInteger getx(){
		return _PrivateKeyBigInteger;
	}
	
	public ECPoint getPpub(){
		return _PublicKeyEcPoint;
	}
}
