package trial;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;

import com.nick.tdp.pairing.BaseServerReceiptGenerator;
import com.nick.tdp.pairing.DevicePairingProcess;
import com.nick.tdp.security.ECDHCurve;

public class DevicePairingTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ECDHCurve _ecdhCurve = new ECDHCurve();
		BigInteger _baseServerPrivateKey = new BigInteger("de9ff58a22798adf2b31f33c8ca9324414257c1d2fa9cdbd", 16);
		ECPoint _baseServerPublicKey = _ecdhCurve.generatePublicKeyEcPoint(_baseServerPrivateKey);
		
		String alphaID = "Android-A-001";
		BigInteger alphaPrivateKey = _ecdhCurve.generatePrivateKeyBigInteger();
		ECPoint alphaPublicKeyEcPoint = _ecdhCurve.generatePublicKeyEcPoint(alphaPrivateKey);
		BaseServerReceiptGenerator alphaBaseServerReceipt = new BaseServerReceiptGenerator(_baseServerPrivateKey, alphaID, alphaPublicKeyEcPoint);
		alphaBaseServerReceipt.generateReceipt();
		
		String betaID = "Android_B_123";
		BigInteger betaPrivateKey = _ecdhCurve.generatePrivateKeyBigInteger();
		ECPoint betaPublicKey = _ecdhCurve.generatePublicKeyEcPoint(betaPrivateKey);
		BaseServerReceiptGenerator betaBaseServerReceipt = new BaseServerReceiptGenerator(_baseServerPrivateKey, betaID, betaPublicKey);
		betaBaseServerReceipt.generateReceipt();
		
		DevicePairingProcess alphaDevicePairing = new DevicePairingProcess(alphaPrivateKey, alphaBaseServerReceipt.get_desc(), _baseServerPublicKey);
		DevicePairingProcess betaDevicePairing = new DevicePairingProcess(betaPrivateKey, betaBaseServerReceipt.get_desc(), _baseServerPublicKey);
		
		alphaDevicePairing.setPairDeviceReceiptInfo(betaID, betaBaseServerReceipt.getRandomEcPoint(), betaBaseServerReceipt.getPublicKey());
		betaDevicePairing.setPairDeviceReceiptInfo(alphaID, alphaBaseServerReceipt.getRandomEcPoint(), alphaBaseServerReceipt.getPublicKey());
		
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
