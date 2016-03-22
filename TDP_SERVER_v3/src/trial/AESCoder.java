package trial;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * AES Security Code Component
 * @author NickHuang
 * @email xjhznick@gmail.com
 * @version 1.0
 */
public abstract class AESCoder {
	public static final String KEY_ALGORITHM = "AES";
	/**
	 * Encryption&Decryption Algorithm/Operating Mode/Padding Pattern
	 * PKCS5PADDING padding pattern supported by Java 7 
	 * PKS7PADDING padding pattern supported by Bouncy Castle
	 */
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5PADDING";
	
	/**
	 * Encryption
	 * @param data plain-text
	 * @param key secret-key
	 * @return byte[] cipher-text
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] key, byte[] data) throws Exception {
	
		SecureRandom secureRandom = new SecureRandom(key);
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128, secureRandom); /* 128-bit Secret Key*/
		SecretKey secretKey = keyGenerator.generateKey();
		
		/**
		 * Instantiation
		 * if PKCS7PADDING use, then
		 * Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		/*Initiation, Encryption Mode*/
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		/* Encrypt plain-text and return cipher-text */
		cipher.update(data);
		return cipher.doFinal();
	}
	
	/**
	 * Decryption
	 * @param data CipherText
	 * @param key SecretKey
	 * @return byte[] PlainText
	 * @throws Exception
	 * 
	 * It seems that this does not work well in Android platform.
	 */
	public static byte[] decrypt(byte[] key, byte[] data) throws Exception {
	
		SecureRandom secureRandom = new SecureRandom(key);
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128, secureRandom);
		SecretKey secretKey = keyGenerator.generateKey();
	
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		/*Initiation, Decryption Mode*/
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		/* Decrypt cipher-text and return plain-text */
		cipher.update(data);
		return cipher.doFinal();
	}
	
}
