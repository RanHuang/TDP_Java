package com.nick.tdp.securityprocess;

import java.math.BigInteger;
import java.util.Random;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import com.nick.tdp.ecdh.ECDHCurve;
import com.nick.tdp.ecdh.HashFunction;

public class DevicePairingProcess {
	
	private BigInteger _thisPrivateKey;
	private BigInteger _thisBsReceiptdesc;
	
	private ECPoint _bsPublicKey;
	
	private String _pairID;
	private ECPoint _pairRandom;
	private ECPoint _pairPublicKey;
	
	
	private BigInteger _lsBigInteger;
	private ECPoint _UnixEcPoint;
	private byte[] _CatByte;
	private byte[] _FsckByte;
	
	
	private ECPoint _VimEcPoint;
	private ECPoint _VimPairEcPoint;
	private byte[] _Sudobyte;	
	private BigInteger _NiceBigInteger;	
	private byte[] _secretKeyByte;
	
	private ECDHCurve _ecdhCurve;
	
	public DevicePairingProcess(BigInteger thisPrivateKey, BigInteger thisBsRecepitDesc_, ECPoint bsPublicKey){
		
		_thisPrivateKey = thisPrivateKey;
		_thisBsReceiptdesc = thisBsRecepitDesc_;

		_bsPublicKey = bsPublicKey;
		
		_ecdhCurve = new ECDHCurve();
		/**
		 * Ua = la * P(Base Point)
		 */
		_lsBigInteger = _ecdhCurve.generatePrivateKeyBigInteger();
		_UnixEcPoint = _ecdhCurve.generatePublicKeyEcPoint(_lsBigInteger);
	}
	
	public void setPairDeviceReceiptInfo(String pairID_, ECPoint pairRandom_, ECPoint pairPublicKey_){
		_pairID = pairID_;
		_pairRandom = pairRandom_;
		_pairPublicKey = pairPublicKey_;
	}
	
	public ECPoint getUnixEcPoint(){
		return _UnixEcPoint;
	}
	
	protected void calculateVimEcPoint() throws Exception {
		/**
		 * Va = la*h0(IDb || Rb || Pb)*Ppub + la*Rb + la*Pb 
		 *  
		 */
		String concatString = Hex.toHexString(_pairID.getBytes()) 
							+ Hex.toHexString(_pairRandom.getEncoded(true)) 
							+ Hex.toHexString(_pairPublicKey.getEncoded(true));
		
		System.out.println("##### In calculate V #####");
		System.out.println("      " + _pairID + "\n" 
							+ "hashZero: " + concatString);
		System.out.println("Ppub: " + Hex.toHexString(_bsPublicKey.getEncoded(true)) +"\n"
				+ "R: " + Hex.toHexString(_pairRandom.getEncoded(true)) + "\n"
				+ "P: " + Hex.toHexString(_pairPublicKey.getEncoded(true)) + "\n");
		
		_VimEcPoint = _ecdhCurve.multiplyEcPoint(_lsBigInteger, _ecdhCurve.multiplyEcPoint(HashFunction.hashZero(concatString), _bsPublicKey))
						.add(_ecdhCurve.multiplyEcPoint(_lsBigInteger, _pairRandom))
						.add(_ecdhCurve.multiplyEcPoint(_lsBigInteger, _pairPublicKey));			
	}
	
	public void calculateCat(byte[] unixEcPointPairByte_) throws Exception{
		calculateVimEcPoint();
		
		ECPoint unixEcPointPair = ECDHCurve.getInstance().decodeBytePoint(unixEcPointPairByte_);
		/**
		 * Vb = (da + xa)*Ub
		 */
		_VimPairEcPoint = _ecdhCurve.multiplyEcPoint(_thisBsReceiptdesc.add(_thisPrivateKey), unixEcPointPair);
		
		/**
		 * Sa = h1(Va xor Vb)
		 */
		BigInteger vimBigInteger = new BigInteger(_VimEcPoint.getEncoded(true));
		BigInteger vimPairBigInteger = new BigInteger(_VimPairEcPoint.getEncoded(true));
		_Sudobyte = vimBigInteger.xor(vimPairBigInteger).toByteArray();
		
		System.out.println("Sa: " + _Sudobyte);
		
		/**
		 * Ca = Enc(Sa, na)
		 */
		_NiceBigInteger = new BigInteger(32, new Random());
		_CatByte = AESCoder.encrypt(_Sudobyte, _NiceBigInteger.toByteArray());
	}
	
	public byte[] getCat(){
		return _CatByte;
	}
	
	public byte[] getSudo(){
		return _Sudobyte;
	}
	
	public void calculateFsck(byte[] catPair_) throws Exception {
		/**
		 * nb = Dec(Sa, Cb)
		 */
		byte[] nanoByte = AESCoder.decrypt(_Sudobyte, catPair_);
		BigInteger nanoBigInteger = new BigInteger(nanoByte);
		/**
		 * Fa = Enc(Sa, nb + 1)
		 */
		_FsckByte = AESCoder.encrypt(_Sudobyte,nanoBigInteger.add(new BigInteger("1", 10)).toByteArray());
		
	}
	
	public byte[] getFsck(){
		return _FsckByte;
	}
	
	public void calculateSecretKey(byte[] fsckPair_) throws Exception{
		/**
		 *  IF Dec(Sa, Fb) = na +1
		 *  	Kab = Sa
		 */
		byte[] checkByte = AESCoder.decrypt(_Sudobyte, fsckPair_);
		BigInteger checkBigInteger = new BigInteger(checkByte);
		
		if(checkBigInteger.compareTo(_NiceBigInteger.add(new BigInteger("1", 10))) == 0){
			_secretKeyByte = _Sudobyte;
			System.err.println("%#$$^%^^%# OK OK OK OK$#$^#%^#$%&Y#%^#" + "\n" + "Key: " + Hex.toHexString(_secretKeyByte) + "\n");
		}else {			
			_secretKeyByte = null;
			System.err.println("Failed Faild Failed Faild Failed Faild" + "\n" + "Key: NULL" + "\n");
		}
		
		System.out.println("SudoString: " + Hex.toHexString(_Sudobyte));
	}
	
	public byte[] getSecretKey(){
		return _secretKeyByte; 
	}
	
	
	
}
