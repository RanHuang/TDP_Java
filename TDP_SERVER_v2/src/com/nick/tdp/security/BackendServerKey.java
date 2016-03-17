package com.nick.tdp.security;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;

public class BackendServerKey {
	
	private BigInteger _PrivateKeyBigInteger;
	private ECPoint _PublicKeyEcPoint;
	
	public BackendServerKey(){
		ECDHCurve ecdhCurve = new ECDHCurve();
//		_PrivateKeyBigInteger = ecdhCurve.generatePrivateKeyBigInteger();
//		_PrivateKeyBigInteger = new BigInteger("de9ff58a22798adf2b31f33c8ca9324414257c1d2fa9cdbd", 16);
		
		_PrivateKeyBigInteger = ECCurveParams.SERVER_PRIVATE_KEY;
		_PublicKeyEcPoint = ecdhCurve.generatePublicKeyEcPoint(_PrivateKeyBigInteger);
	}
	
	public BigInteger getx(){
		return _PrivateKeyBigInteger;
	}
	
	public ECPoint getPpub(){
		return _PublicKeyEcPoint;
	}
}
