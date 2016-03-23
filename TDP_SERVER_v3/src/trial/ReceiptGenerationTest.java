package trial;

import java.math.BigInteger;
import java.util.Arrays;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import com.nick.tdp.pairing.DevicePairingProcess;
import com.nick.tdp.pairing.ReceiptGenerationProcess;
import com.nick.tdp.security.BackendServerKey;
import com.nick.tdp.security.DeviceReceipt;
import com.nick.tdp.security.ECDHCurve;

public class ReceiptGenerationTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReceiptGenerationTest test = new ReceiptGenerationTest();
		
		try {
			test.testReceiptGenerationProcess();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void testReceiptGenerationProcess() throws Exception{
		
//		ReceiptGenerationProcess alphaRecGenProc = new ReceiptGenerationProcess();
//		alphaRecGenProc.CalcTest();
		
//		/*
		BigInteger baseServerPrivateKey = BackendServerKey.getInstance().getx();
		ECPoint baseServerPublicKey = BackendServerKey.OBJ_SERVER_KEY.getPpub();
		
		ECDHCurve ecdhCurve = new ECDHCurve();
		String alpha_ID = "Android_239632920-Alpha";
		BigInteger alphaPrivateKey = ecdhCurve.generatePrivateKeyBigInteger();
		ECPoint alphaPublicKey = ecdhCurve.generatePublicKeyEcPoint(alphaPrivateKey);
		DeviceReceipt alphaReceipt = new DeviceReceipt(alpha_ID, alphaPublicKey);
		alphaReceipt.generateReceipt(baseServerPrivateKey);
		String beta_ID = "Android_1475781389-Beta";
//		String pair_ID = "Android_74059307";
		BigInteger betaPrivateKey = ecdhCurve.generatePrivateKeyBigInteger();
		ECPoint betaPublicKey = ecdhCurve.generatePublicKeyEcPoint(betaPrivateKey);
		DeviceReceipt betaReceipt = new DeviceReceipt(beta_ID, betaPublicKey);
		betaReceipt.generateReceipt(baseServerPrivateKey);
		
		DevicePairingProcess alphaDevicePairing = new DevicePairingProcess(alphaPrivateKey, alphaReceipt.get_d(), baseServerPublicKey);
		DevicePairingProcess betaDevicePairing = new DevicePairingProcess(betaPrivateKey, betaReceipt.get_d(), baseServerPublicKey);		
		alphaDevicePairing.setPairDeviceReceiptInfo(beta_ID, betaReceipt.getRandEcPoint(), betaReceipt.getPublicKey());
		betaDevicePairing.setPairDeviceReceiptInfo(alpha_ID, alphaReceipt.getRandEcPoint(), alphaReceipt.getPublicKey());		
		byte[] Ua = alphaDevicePairing.getUnixEcPoint().getEncoded(true);
		byte[] Ub = betaDevicePairing.getUnixEcPoint().getEncoded(true);		
		alphaDevicePairing.calculateCat(Ub);
		betaDevicePairing.calculateCat(Ua);		
		byte[] Ca = alphaDevicePairing.getCat();
		byte[] Cb = betaDevicePairing.getCat();		
		alphaDevicePairing.calculateFsck(Cb);
		betaDevicePairing.calculateFsck(Ca);		
		byte[] Fa = alphaDevicePairing.getFsck();
		byte[] Fb = betaDevicePairing.getFsck();		
		alphaDevicePairing.calculateSecretKey(Fb);
		betaDevicePairing.calculateSecretKey(Fa);
		
		
		ReceiptGenerationProcess alphaRecGenProc = new ReceiptGenerationProcess();
		ReceiptGenerationProcess betaRecGenProc = new ReceiptGenerationProcess();
//		*/
		/**
		 * Test the calculation of Q, C, R			
		 */
//		/*
		double pre_q = 0.4;
		double pre_c = 0.6;
		String[] alpha_IDs = {"Android_1319752751", "Android_1864635234", "Android_1934807146", "Android_1475781389", "Android_219157648"};
		String strAlphaIDs = alpha_IDs[0];
		for(int i=1; i<alpha_IDs.length; i++){
			strAlphaIDs += ", " + alpha_IDs[i];
		}
		int[] alpha_ch_positive = {3, 2, 7, 4, 6};
		int[] alpha_ch_total = {7, 4, 8, 5, 6};
		
		String[] beta_IDs = {"Android_1864635234", "Android_131485814", "Android_1792883326", "Android_1934807146" , "Android_681387016"};
		String strBetaIDs = beta_IDs[0];
		for(int i=1; i<beta_IDs.length; i++){
			strBetaIDs += ", " + beta_IDs[i];
		}		
		int[] beta_ch_positive = {3, 4, 3, 2, 1};
		int[] beta_ch_total = {4, 4, 4, 3, 3};
//		*/
		/**
		 * 坑爹的ID，这里的selfID和pairID对计算过程产生影响(在计算hash-0的时候)，对应的ID必须和设备注册时用于产生d的ID相同。
		 */
		alphaRecGenProc.initializeSelfCHdata(pre_q, pre_c, alpha_ID, strAlphaIDs, Arrays.toString(alpha_ch_positive), Arrays.toString(alpha_ch_total));
		alphaRecGenProc.initializePairCHdata(beta_ID, strBetaIDs, Arrays.toString(beta_ch_positive), Arrays.toString(beta_ch_total));		
		alphaRecGenProc.calculateQCR(-80);
		
		betaRecGenProc.initializeSelfCHdata(pre_q-0.2, pre_c+0.1, beta_ID, strBetaIDs, Arrays.toString(beta_ch_positive), Arrays.toString(beta_ch_total));
		betaRecGenProc.initializePairCHdata(alpha_ID, strAlphaIDs, Arrays.toString(alpha_ch_positive), Arrays.toString(alpha_ch_total));
		betaRecGenProc.calculateQCR(-80);
		
		alphaRecGenProc.setClientSelfKeys(alphaPrivateKey,alphaReceipt.get_d());
		alphaRecGenProc.setClientPairKeys(betaPublicKey,  betaReceipt.getRandEcPoint());
		alphaRecGenProc.calcSignature();
		
		betaRecGenProc.setClientSelfKeys(betaPrivateKey, betaReceipt.get_d());
		betaRecGenProc.setClientPairKeys(alphaPublicKey,  alphaReceipt.getRandEcPoint());
		betaRecGenProc.calcSignature();
		
		alphaRecGenProc.set_PairSignature(betaRecGenProc.get_er(), betaRecGenProc.get_Snumerator(), betaRecGenProc.get_Sdenominator(), betaRecGenProc.get_T(1), betaRecGenProc.get_T(2), betaRecGenProc.get_T(3));
		betaRecGenProc.set_PairSignature(alphaRecGenProc.get_er(), alphaRecGenProc.get_Snumerator(), alphaRecGenProc.get_Sdenominator(), alphaRecGenProc.get_T(1), alphaRecGenProc.get_T(2), alphaRecGenProc.get_T(3));
		alphaRecGenProc.calcReceipt();
		betaRecGenProc.calcReceipt();
		
//		System.out.println("\nxa: " + Hex.toHexString(alphaPrivateKey.toByteArray())
//						 + "\nPa: " + Hex.toHexString(alphaPublicKey.getEncoded(true))
//						 + "\nxb: " + Hex.toHexString(betaPrivateKey.toByteArray())
//						 + "\nPb: " + Hex.toHexString(betaPublicKey.getEncoded(true))
//		                 + "");
//		*/
		/**
		 * Test function of indexOfDeviceInCH()
		 */
//		String[] strAndroidID = {"Android_1319752751", "Android_1864635234", "Android_239632920", "Android_131485814",
//				 "Android_1792883326", "Android_1934807146", "Android_1475781389", "Android_74059307",
//				 "Android_219157648", "Android_681387016"};
//		String id = "Android_131485814"; /* 3 */
//		System.out.println("Index of '" + id + "' is " + receiptGenerationProcess.indexOfDeviceInCH(id, strAndroidID));
//		String notId = "hello";
//		System.out.println("Index of '" + notId + "' is " + receiptGenerationProcess.indexOfDeviceInCH(notId, strAndroidID));
	}
}
