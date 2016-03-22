package trial;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;

import com.nick.tdp.pairing.DevicePairingProcess;
import com.nick.tdp.security.DeviceReceipt;
import com.nick.tdp.security.ECDHCurve;

public class DevicePairingTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		DevicePairingTest test = new DevicePairingTest();
		
		test.testDevicePairingProcess();
	}
	
	private void testDevicePairingProcess() throws Exception{
		ECDHCurve _ecdhCurve = new ECDHCurve();
		BigInteger _baseServerPrivateKey = new BigInteger("de9ff58a22798adf2b31f33c8ca9324414257c1d2fa9cdbd", 16);
		ECPoint _baseServerPublicKey = _ecdhCurve.generatePublicKeyEcPoint(_baseServerPrivateKey);
		
		String alphaID = "Android-A-001";
		BigInteger alphaPrivateKey = _ecdhCurve.generatePrivateKeyBigInteger();
		ECPoint alphaPublicKeyEcPoint = _ecdhCurve.generatePublicKeyEcPoint(alphaPrivateKey);
		DeviceReceipt alphaBaseServerReceipt = new DeviceReceipt(alphaID, alphaPublicKeyEcPoint);
		alphaBaseServerReceipt.generateReceipt(_baseServerPrivateKey);
		
		String betaID = "Android_B_123";
		BigInteger betaPrivateKey = _ecdhCurve.generatePrivateKeyBigInteger();
		ECPoint betaPublicKey = _ecdhCurve.generatePublicKeyEcPoint(betaPrivateKey);
		DeviceReceipt betaBaseServerReceipt = new DeviceReceipt(betaID, betaPublicKey);
		betaBaseServerReceipt.generateReceipt(_baseServerPrivateKey);
		
		DevicePairingProcess alphaDevicePairing = new DevicePairingProcess(alphaPrivateKey, alphaBaseServerReceipt.get_d(), _baseServerPublicKey);
		DevicePairingProcess betaDevicePairing = new DevicePairingProcess(betaPrivateKey, betaBaseServerReceipt.get_d(), _baseServerPublicKey);
		
		alphaDevicePairing.setPairDeviceReceiptInfo(betaID, betaBaseServerReceipt.getRandEcPoint(), betaBaseServerReceipt.getPublicKey());
		betaDevicePairing.setPairDeviceReceiptInfo(alphaID, alphaBaseServerReceipt.getRandEcPoint(), alphaBaseServerReceipt.getPublicKey());
		
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
	}

}
