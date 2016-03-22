package nick.xjtu.edu.cn;

import org.bouncycastle.jcajce.provider.symmetric.ARC4.Base;

import com.sun.org.apache.xml.internal.security.utils.Base64;
/**
 * AES��ȫ�����������
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
		String inputStr = "���˻��ܣ��򲻿���й��";
		byte[] inputData = inputStr.getBytes();
		System.err.println("����:\t" + inputStr);
		
		//��ʼ����Կ
		byte[] key = AESCoder.initKey();
		System.err.println("��Կ:\t" + Base64.encode(key));
		//����
		inputData = AESCoder.encrypt(inputData, key);
		System.err.println("����:\t" + Base64.encode(inputData));
		//����
		byte[] outputData = AESCoder.decrypt(inputData, key);
		String outputStr = new String(outputData);
		System.err.println("���ܺ������:\t" + outputStr);	
	}
	
	
	public final static void keyTest() throws Exception {
		String keyStr = "0177cda5061afaa706aab9567375f";
		
		System.err.println("��Կ:\t" + keyStr);
		
		String inputStr = "�ºڷ��ҹ��ɱ��Խ��ʱ����ʱ�����Ի�Ϊ�ţ�ͬʱ���֣���Ȯ������";
		byte[] inputData = inputStr.getBytes();
		
//		inputData = Base64.decode("9RN7V+6VJvqZHRdxjCdoLw==");
		//����
		byte[] keyEnc = AESCoder.initKey(keyStr);
		inputData = AESCoder.encrypt(inputData, keyEnc);
		System.err.println("������Կ:\t" + Base64.encode(keyEnc));
		
		System.out.println("����:\t" + Base64.encode(inputData));
		//����
		byte[] keyDec = AESCoder.initKey(keyStr);
		byte[] outputData = AESCoder.decrypt(inputData, keyDec);
//		System.out.println("���ܺ������:\t" + Base64.encode(outputData));	
		String outputStr = new String(outputData);
		System.err.println("������Կ:\t" + Base64.encode(keyDec));
		System.out.println("���ܺ������:\t" + outputStr);	
	}
}
