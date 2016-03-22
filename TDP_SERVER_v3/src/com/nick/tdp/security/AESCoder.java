package com.nick.tdp.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * AES安全编码组件--使用指定字符串生成秘钥
 * @author NickHuang
 * @email xjhznick@gmail.com
 * @version 2.0
 */
public abstract class AESCoder {
	public static final String KEY_ALGORITHM = "AES";
	/**
	 * 加解密算法/工作模式/填充方式
	 * Java 7 支持 PKCS5PADDING填充方式
	 * Bouncy Castle支持PKS7PADDING填充方式
	 */
//	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5PADDING";
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7PADDING";
	
	/**
	 * 生成密钥、
	 * @return byte[] 二进制密钥
	 * @throws Exception
	 */
	public static byte[] initKey() throws Exception {
		//加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());
		//实例化
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		//设置AES密钥长度：128, 192,256
		kg.init(128);
		//生成秘密密钥
		SecretKey secretKey = kg.generateKey();
		//获得二进制编码形式密钥
		return secretKey.getEncoded();
	}
	
	/**
	 * 密钥生成器 使用自定义密钥
	 * @param strKey 自定义密钥字符串, HexString
	 * @return
	 * @throws Exception
	 */
	public static byte[] initKey(String strKey) throws Exception {
		//加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());
		//实例化
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		//设置AES密钥长度：128, 192,256
//		kg.init(128, new SecureRandom(strKey.getBytes()));
		kg.init(new SecureRandom(strKey.getBytes()));
		//生成秘密密钥
		SecretKey secretKey = kg.generateKey();
		//获得二进制编码形式密钥
		return secretKey.getEncoded();
	}
	/**
	 * 转换密钥
	 * @param key 二进制密钥
	 * @return key 密钥
	 * @throws Exception
	 */
//	private static Key toKey(byte[] key) throws Exception {
//		//实例化DES密钥材料
//		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
//		return secretKey;
//	}
	
	/**
	 * 解密
	 * @param data 密文
	 * @param key 密钥
	 * @return byte[] 明文
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
		/**
		 * 实例化
		 * 若使用PKCS7PADDING填充方式，按照如下方式实现：
		 * Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
//		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		//初始化，设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		//执行解密操作，并返回
		return cipher.doFinal(data);
	}
	
	/**
	 * 加密
	 * @param data 明文
	 * @param key 密钥
	 * @return byte[] 密文
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
		/**
		 * 实例化
		 * 若使用PKCS7PADDING填充方式，按照如下方式实现：
		 * Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
//		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		//初始化，设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		
		//执行加密操作，并返回
		return cipher.doFinal(data);
	}
	
}
