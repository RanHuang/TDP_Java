package com.nick.tdp.ecdh;

import java.math.BigInteger;
import java.util.Random;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;


public class ECDHCurve {
	public static final String TAG = "ecc curve";
	
	private static ECPoint _GEcPoint;
	private static ECCurve.Fp _fpEccCurve;
	
	public static ECDHCurve OBJ_ECDH_CUVE;
	
	public ECDHCurve(){
		/**
		 * @param p, a, b
		 */
		_fpEccCurve = new ECCurve.Fp(ECCurveParams.ECC_p, ECCurveParams.ECC_a, ECCurveParams.ECC_b);
		_GEcPoint = _fpEccCurve.decodePoint(Hex.decode(ECCurveParams.ECC_G_CODE));
	}
	
	public static synchronized ECDHCurve getInstance(){
		if(OBJ_ECDH_CUVE == null){
			OBJ_ECDH_CUVE = new ECDHCurve();
		}
		return OBJ_ECDH_CUVE;
	}
	
	public static byte[] getEncodedPoint(ECPoint point_){
		return point_.getEncoded(true);
	}
	
	public ECPoint decodeBytePoint(byte[] encodedPoint_){
		ECPoint ecPoint = _fpEccCurve.decodePoint(encodedPoint_);
		return ecPoint;
	}
	
	public ECPoint getBasePoint(){
		return _GEcPoint;
	}
	
	public ECPoint generatePublicKeyEcPoint(BigInteger eccPrivateKey_){
		return _GEcPoint.multiply(eccPrivateKey_);
	}
	
	public ECPoint multiplyEcPoint(BigInteger bigInteger_, ECPoint ecPoint_){
		return ecPoint_.multiply(bigInteger_);
	}
	
	public BigInteger generatePrivateKeyBigInteger(){
		BigInteger privateKey;
		do{
			privateKey = new BigInteger(ECCurveParams.ECC_Bit_Length, new Random(System.nanoTime()));
		}while(privateKey.compareTo(BigInteger.ZERO)<=0 || privateKey.compareTo(ECCurveParams.ECC_n)>=0);
		return privateKey;
	}
	
	public void printECCurveInfo(){
		System.out.println("\n ****Info of the ECC Curve****");
		int radix = 16;
		System.out.println("p: " + _fpEccCurve.getQ().toString(radix));
		System.out.println("a: " + _fpEccCurve.getA().toBigInteger().toString(radix));
		System.out.println("b: " + _fpEccCurve.getB().toBigInteger().toString(radix));
		System.out.println("G: " + Hex.toHexString(_GEcPoint.getEncoded(true)));
//		System.out.println("n: " + _fpEccCurve.getOrder().toString(radix));
//		System.out.println("h: " + _fpEccCurve.getCofactor().toString(radix));
		System.out.println("ECMultiplier: " + _fpEccCurve.getMultiplier().toString());
		System.out.println("\n");
	}
	
}
