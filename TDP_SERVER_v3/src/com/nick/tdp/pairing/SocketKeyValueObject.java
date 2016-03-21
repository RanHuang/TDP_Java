package com.nick.tdp.pairing;

import java.io.Serializable;

public class SocketKeyValueObject implements Serializable {
	
	private static final long serialVersionUID = 20150703L;
	
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
	 * 5th round, get the pair device's(ID, Index, Contact History)
	 */
	private int _genRecIndex;
	private int _genRecQos;
	private String _genRecContactHistoryIDs;
	private String _genRecContactHistoryA; /* sum of positive contact evaluation.*/
	private String _genRecContactHistoryB; /* Device contacted times.*/
	
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
		
		_genRecIndex = -1;
		_genRecQos = 0;
		_genRecContactHistoryIDs = null;
		_genRecContactHistoryA = null;
		_genRecContactHistoryB = null;
	}
	
	public void printBaseInfo(){
		System.out.println("Index: " + String.valueOf(_index));
		System.out.println("Key: " + _key);
		System.out.println("Payload: " + _payload);
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
	public void set_genRecIndex(int index_){
		_genRecIndex = index_;
	}	
	public int get_genRecIndex(){
		return _genRecIndex;
	}
	
	public void set_genRecQos(int qos_){
		_genRecQos = qos_;
	}
	public int get_genRecQos(){
		return _genRecQos;
	}
	
	public void set_genRecContactHistoryIDs(String contactHistoryIds_){
		_genRecContactHistoryIDs = contactHistoryIds_;
	}
	public String get_genRecContactHistoryIDs(){
		return _genRecContactHistoryIDs;
	}
	
	public void set_genRecContactHistoryA(String contactHistoryA_){
		_genRecContactHistoryA = contactHistoryA_;
	}
	public String get_genRecContactHistoryA(){
		return _genRecContactHistoryA;
	}
	
	public void set_genRecContactHistoryB(String contactHistoryB_){
		_genRecContactHistoryB = contactHistoryB_;
	}
	public String get_genRecContactHistoryB(){
		return _genRecContactHistoryB;
	}
}
