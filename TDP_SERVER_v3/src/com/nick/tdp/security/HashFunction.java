package com.nick.tdp.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The implementation of three one-way hash functions.
 * @author Nick
 *
 */

public abstract class HashFunction {
	/**
	 * h0: {0, 1}* --> Z*
	 * Use the SHA-1 Algorithm and the length of digest is 160-bit.
	 * @param String data 
	 * @return BigInteger
	 * @throws NoSuchAlgorithmException
	 */
	
	public static BigInteger hashZero(String clear)throws NoSuchAlgorithmException{
		//Initialize MessageDigest with "SHA1"
		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		//Generate the digest message
		messageDigest.update(clear.getBytes());		
		byte[] digest = messageDigest.digest();
//		String digestString = getFormattedText(messageDigest.digest());
		//Convert the digest from bytes to BigInteger
		BigInteger intVal = new BigInteger(digest); 
		
		return intVal;
	}	 	
	
	/**
	 * h1: {0, 1}* --> {0, 1}*
	 * Use the MD5 Algorithm and the length of digest is 160-bit.
	 * @param String data 
	 * @return String
	 * @throws NoSuchAlgorithmException
	 */
	public static String hashOne(String clear) throws NoSuchAlgorithmException{
		//Initialize MessageDigest with "MD5"
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		//
		byte[] digest = messageDigest.digest(clear.getBytes());
		
		return digest.toString();
	}
	
	/**
	 * h2: {0, 1}* --> Z*
	 * The same as hashZero()
	 */
	public static BigInteger hashTwo(String clear)throws NoSuchAlgorithmException{
		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		messageDigest.update(clear.getBytes());		
		byte[] digest = messageDigest.digest();
		BigInteger intVal = new BigInteger(digest); 
		
		return intVal;
	}	 	
}
