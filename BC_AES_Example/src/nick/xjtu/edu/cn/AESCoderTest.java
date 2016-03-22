package nick.xjtu.edu.cn;

import org.bouncycastle.jcajce.provider.symmetric.ARC4.Base;

import com.sun.org.apache.xml.internal.security.utils.Base64;
/**
 * AES安全编码组件测试
 * @author Nick
 * @version 1.0
 * @throws Exception
 */
public class AESCoderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
//			test();
			keyTest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final static void test() throws Exception {
		String inputStr = "此乃机密，万不可外泄。";
		byte[] inputData = inputStr.getBytes();
		System.err.println("明文:\t" + inputStr);
		
		//初始化密钥
		byte[] key = AESCoder.initKey();
		System.err.println("密钥:\t" + Base64.encode(key));
		//加密
		inputData = AESCoder.encrypt(inputData, key);
		System.err.println("密文:\t" + Base64.encode(inputData));
		//解密
		byte[] outputData = AESCoder.decrypt(inputData, key);
		String outputStr = new String(outputData);
		System.err.println("解密后的明文:\t" + outputStr);	
	}
	
	
	public final static void keyTest() throws Exception {
		String keyStr = "0177cda5061afaa706aab9567375f";
		
		System.err.println("密钥:\t" + keyStr);
		
		String inputStr = "月黑风高夜，杀人越货时。丑时三刻以火为号，同时动手，鸡犬不留。";
		byte[] inputData = inputStr.getBytes();
		
//		inputData = Base64.decode("9RN7V+6VJvqZHRdxjCdoLw==");
		//加密
		byte[] keyEnc = AESCoder.initKey(keyStr);
		inputData = AESCoder.encrypt(inputData, keyEnc);
		System.err.println("加密密钥:\t" + Base64.encode(keyEnc));
		
		System.out.println("密文:\t" + Base64.encode(inputData));
		//解密
		byte[] keyDec = AESCoder.initKey(keyStr);
		byte[] outputData = AESCoder.decrypt(inputData, keyDec);
//		System.out.println("解密后的明文:\t" + Base64.encode(outputData));	
		String outputStr = new String(outputData);
		System.err.println("解密密钥:\t" + Base64.encode(keyDec));
		System.out.println("解密后的明文:\t" + outputStr);	
	}
}
