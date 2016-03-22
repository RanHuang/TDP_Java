package com.nick.tdp.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * AES��ȫ�������--ʹ��ָ���ַ���������Կ
 * @author NickHuang
 * @email xjhznick@gmail.com
 * @version 2.0
 */
public abstract class AESCoder {
	public static final String KEY_ALGORITHM = "AES";
	/**
	 * �ӽ����㷨/����ģʽ/��䷽ʽ
	 * Java 7 ֧�� PKCS5PADDING��䷽ʽ
	 * Bouncy Castle֧��PKS7PADDING��䷽ʽ
	 */
//	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5PADDING";
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7PADDING";
	
	/**
	 * ������Կ��
	 * @return byte[] ��������Կ
	 * @throws Exception
	 */
	public static byte[] initKey() throws Exception {
		//����BouncyCastleProvider֧��
		Security.addProvider(new BouncyCastleProvider());
		//ʵ����
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		//����AES��Կ���ȣ�128, 192,256
		kg.init(128);
		//����������Կ
		SecretKey secretKey = kg.generateKey();
		//��ö����Ʊ�����ʽ��Կ
		return secretKey.getEncoded();
	}
	
	/**
	 * ��Կ������ ʹ���Զ�����Կ
	 * @param strKey �Զ�����Կ�ַ���, HexString
	 * @return
	 * @throws Exception
	 */
	public static byte[] initKey(String strKey) throws Exception {
		//����BouncyCastleProvider֧��
		Security.addProvider(new BouncyCastleProvider());
		//ʵ����
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		//����AES��Կ���ȣ�128, 192,256
//		kg.init(128, new SecureRandom(strKey.getBytes()));
		kg.init(new SecureRandom(strKey.getBytes()));
		//����������Կ
		SecretKey secretKey = kg.generateKey();
		//��ö����Ʊ�����ʽ��Կ
		return secretKey.getEncoded();
	}
	/**
	 * ת����Կ
	 * @param key ��������Կ
	 * @return key ��Կ
	 * @throws Exception
	 */
//	private static Key toKey(byte[] key) throws Exception {
//		//ʵ����DES��Կ����
//		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
//		return secretKey;
//	}
	
	/**
	 * ����
	 * @param data ����
	 * @param key ��Կ
	 * @return byte[] ����
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
		/**
		 * ʵ����
		 * ��ʹ��PKCS7PADDING��䷽ʽ���������·�ʽʵ�֣�
		 * Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
//		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		//��ʼ��������Ϊ����ģʽ
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		//ִ�н��ܲ�����������
		return cipher.doFinal(data);
	}
	
	/**
	 * ����
	 * @param data ����
	 * @param key ��Կ
	 * @return byte[] ����
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
		/**
		 * ʵ����
		 * ��ʹ��PKCS7PADDING��䷽ʽ���������·�ʽʵ�֣�
		 * Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
//		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		//��ʼ��������Ϊ����ģʽ
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		
		//ִ�м��ܲ�����������
		return cipher.doFinal(data);
	}
	
}
