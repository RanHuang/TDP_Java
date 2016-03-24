package com.nick.tdp.pairing;

import java.io.Serializable;

public class SocketKeyValueObject implements Serializable {
	
	private static final long serialVersionUID = 20160324L;
	
	private long _index;
	private String _key;
	private String _payload;
	/**
	 * 1st round, get the pair device's (ID, R, P) 
	 */
	private String _pairID;
	private byte[] _pairRandomEcPoint;
	private byte[] _pairPublicKey;
	/**
	 * 2nd round, get the pair device's U
	 */
	private byte[] _pairU;
	/**
	 * 3rd round, get the pair device's C
	 */
	private byte[] _pairC;
	/**
	 * 4th round, get get pair device's F
	 */
	private byte[] _pairF;
	
	/**
	 * 5th round, get the pair device's(ID, QoS, Contact History)
	 * 	(Qos, CH-IDs, CH-positive, CH-total), GET the ID in the 1st round.
	 */
	private int _genRecPairQos;
	private String _genRecPairCHIDs;
	private String _genRecPairCHPositive; /* sum of positive contact evaluation.*/
	private String _genRecPairCHTotal; /* Device contacted total times.*/
	
	/**
	 * 6th round, get the pair device's signature
	 * (er, Snum, Sden, T1, T2, T3)
	 */
	private byte[] _genRec_er;
	private byte[] _genRecSnum;
	private byte[] _genRecSden;
	private byte[] _genRecT1;
	private byte[] _genRecT2;
	private byte[] _genRecT3;
	
	public SocketKeyValueObject(String key_, String payload_){
		_index = 0;
		_key = key_;
		_payload = payload_;
		
		_pairID = null;
		_pairRandomEcPoint = null;
		_pairPublicKey = null;
		_pairU = null;
		_pairC = null;
		_pairF = null;
		
		_genRecPairQos = 0;
		_genRecPairCHIDs = null;
		_genRecPairCHPositive = null;
		_genRecPairCHTotal = null;
		_genRec_er = null;
		_genRecSnum = null;
		_genRecSden = null;
		_genRecT1 = null;
		_genRecT2 = null;
		_genRecT3 = null;
	}
	
	public void printBaseInfo(){
		System.out.println("\n  Index: " + String.valueOf(_index)
							+ "\n  Key: " + _key
							+ "\n  Payload: " + _payload
							+ "");
	}
	
	public long get_index(){
		return _index;
	}
	
	public void set_index(long numSentObject){
		_index = numSentObject;
	}
	
	public String get_key(){
		return _key;
	}
	
	public void set_key(String key_){
		_key = key_;
	}
	
	public String get_payload(){
		return _payload;
	}
	
	public void set_payload(String payload_){
		_payload = payload_;
	}
	
	/**
	 * 1st round
	 */
	public void setReceiptInfo(String ID_, byte[] randomEcPoint_, byte[] publicKey_){
		_pairID = ID_;
		_pairRandomEcPoint = randomEcPoint_;
		_pairPublicKey = publicKey_;
	}
	
	public String get_pairDeviceID(){
		return _pairID;
	}
	
	public byte[] get_pairDeviceRandomByte(){
		return _pairRandomEcPoint;
	}
	
	public byte[] get_pairDevicePublicKeyByte(){
		return _pairPublicKey;
	}
	/**
	 * 2nd round
	 */
	public void set_Ubyte(byte[] unix_){
		_pairU = unix_;
	}	
	
	public byte[] get_Ubyte(){
		return _pairU;
	}
	/**
	 * 3rd round
	 */
	public void set_Cbyte(byte[] cat_){
		_pairC = cat_;
	}	
	
	public byte[] get_Cbyte(){
		return _pairC;
	}
	/**
	 * 4th round
	 */
	public void set_Fbyte(byte[] fsdisk_){
		_pairF = fsdisk_;
	}	
	
	public byte[] get_Fbyte(){
		return _pairF;
	}
	/**
	 * 5th round
	 */
	public void set_ContactHistory(int qos_, String chIds_, String chPos_, String chTot_){
		_genRecPairQos = qos_;
		_genRecPairCHIDs = chIds_;
		_genRecPairCHPositive = chPos_;
		_genRecPairCHTotal = chTot_;
	}
	
	public int get_Qos(){
		return _genRecPairQos;
	}
	
	public String get_CHIDs(){
		return _genRecPairCHIDs;
	}
	
	public String get_CHPositive(){
		return _genRecPairCHPositive;
	}
	
	public String get_CHTotal(){
		return _genRecPairCHTotal;
	}
	
	/**
	 * 6th round
	 */
	public void set_Signature(byte[] er_, byte[] Sunm_, byte[] Sden_, byte[] T1_, byte[] T2_, byte[] T3_){
		_genRec_er = er_;
		_genRecSnum = Sunm_;
		_genRecSden = Sden_;
		_genRecT1 = T1_;
		_genRecT2 = T2_;
		_genRecT3 = T3_;
	}
	
	public byte[] get_er(){
		return _genRec_er;
	}
	
	public byte[] get_Snum(){
		return _genRecSnum;
	}
	
	public byte[] get_Sden(){
		return _genRecSden;
	}
	
	public byte[] get_T1(){
		return _genRecT1;
	}
	
	public byte[] get_T2(){
		return _genRecT2;
	}
	
	public byte[] get_T3(){
		return _genRecT3;
	}
}
