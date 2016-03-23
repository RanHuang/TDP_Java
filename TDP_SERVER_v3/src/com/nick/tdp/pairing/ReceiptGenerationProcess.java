package com.nick.tdp.pairing;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import com.mysql.fabric.xmlrpc.Client;
import com.nick.tdp.register.RegistrationClient;
import com.nick.tdp.register.TDPConstants;
import com.nick.tdp.security.AESCoder;
import com.nick.tdp.security.BackendServerKey;
import com.nick.tdp.security.DeviceReceipt;
import com.nick.tdp.security.ECDHCurve;
import com.nick.tdp.security.HashFunction;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

/**
 * This class mainly does the calculations during the D2D Receipt Generation. 
 * @author Nick
 * 2016-03-21
 */

public class ReceiptGenerationProcess{

	private static final double ALPHA=0.5; /*To calculate credibility { c = (alpha*S^2 + (1-alpha)d^2)*w } according to formula 3. */
	private static final double BETA = 0.5;/* To calculate QoS { q = (beta*pre_q + (1-beta)*q. }. */
	private static final double THETA=0.6;
	
	private double hat_qos;
	private double qos;
	
	private double hat_cre;
	private double cre;
	
	private int r;
	
	private String self_ID;
	private int effective_num_ofCH;
	private String[] contact_history_ID = new String[TDPConstants.MAX_NUM_OF_DEVICES];
	private int[] contact_history_positive = new int[TDPConstants.MAX_NUM_OF_DEVICES];
	private int[] contact_history_total = new int[TDPConstants.MAX_NUM_OF_DEVICES];
	private double[] contact_history = new double[TDPConstants.MAX_NUM_OF_DEVICES];
	
	private String pair_ID;
	private String[] pair_contact_history_ID;
	private int[] pair_contact_history_positive;
	private int[] pair_contact_history_total;
	private double[] pair_contact_history;
	
	public ReceiptGenerationProcess(){
		initialize();
	}
	
	private void initialize(){
		hat_cre = 0;
		hat_qos = 0;
		r = -1;
		
		effective_num_ofCH = 0;
	}
	/**
	 * Initialize the data of contact history.
	 * Get them from the data base.
	 */
	public void initializeSelfCHdata(double pre_qos, double pre_cre, String id, String ids, String ch_positive, String ch_total){	
		hat_qos = pre_qos;
		hat_cre = pre_cre;
		self_ID = id;
		
		String[] temp_ch_ids = ids.split(",");
		effective_num_ofCH = temp_ch_ids.length;
//		System.out.println("num of CH: " + effective_num_ofCH);
//		System.out.println("IDs: " + Arrays.toString(temp_ch_ids));
		
		int[] temp_ch_positive = stringToIntArray(ch_positive);
		int[] temp_ch_total = stringToIntArray(ch_total);
		
		for(int i=0; i<effective_num_ofCH; i++){
			contact_history_ID[i] = temp_ch_ids[i].trim();
			contact_history_positive[i] = temp_ch_positive[i];
			contact_history_total[i] = temp_ch_total[i];
			contact_history[i] = (double) contact_history_positive[i]/contact_history_total[i] ;
		}
		
//		PrintAllParemetersInfo();
	}
	
	private void PrintAllParemetersInfo(){
		System.out.println("\n***********************All Parameters Info****************************"
				+ "\n\tpreQos: " + hat_qos + "\tQos: " + qos
				+ "\n\tpreCre: " + hat_cre + "\tCre: " + cre
				+ "\n\tNum of CH: " + effective_num_ofCH
				+ "\n\tCH-IDs: " + Arrays.toString(contact_history_ID)
				+ "\n\tCH-Positive: " + Arrays.toString(contact_history_positive)
				+ "\n\tCH-Total: " + Arrays.toString(contact_history_total)
				+ "\n\tCH: " + Arrays.toString(contact_history)
				+"");
	}
	/**
	 * All the parameters are from the paired device by socket.
	 * @param id   ID of paired device.
	 * @param pairIDs  e.g: "xxxx,yyyy,zzzz"
	 * @param ch_positive e.g: "[a, b, c, e, f]" -- integer array to string
	 * @param ch_total    e.g: "[m, n, o, p, q]" -- integer array to string
	 */
	public void initializePairCHdata(String id, String pairIDs, String ch_positive, String ch_total){
		pair_ID = id;
		pair_contact_history_ID = pairIDs.split(",");
		pair_contact_history_positive = stringToIntArray(ch_positive);
		pair_contact_history_total = stringToIntArray(ch_total);
		pair_contact_history = new double[pair_contact_history_ID.length];
		for(int i=0; i<pair_contact_history_ID.length; i++){
			pair_contact_history_ID[i] = pair_contact_history_ID[i].trim();
			pair_contact_history[i] = (double)pair_contact_history_positive[i] / pair_contact_history_total[i];
		}
		
		PrintPairCHInfo();
	}
	private void PrintPairCHInfo(){
		System.out.println("\n******************Paired Device Contact History Info********************"
				+ "\n\tNum of CH: " + pair_contact_history_ID.length
				+ "\n\tCH-IDs: " + Arrays.toString(pair_contact_history_ID)
				+ "\n\tCH-Positive: " + Arrays.toString(pair_contact_history_positive)
				+ "\n\tCH-Total: " + Arrays.toString(pair_contact_history_total)
				+ "\n\tCH: " + Arrays.toString(pair_contact_history)
				+"");
	}
	
	private int[] stringToIntArray(String strIntArray_){
		/**
		 * e.g: [13, 12, 11, 10, 9, 8, 7, 6, 5, 4]
		 * 1. remove the first and last characters('[',']')
		 * 2. fetch every string of an Integer according to the delimiter(',')
		 * 3. convert the string to integer
		 */
		String tempstr = strIntArray_.substring(1, strIntArray_.length()-1);
		
		String[] outstr = tempstr.split(",");
		
		int[] outInt = new int[outstr.length];
		for(int i=0; i<outstr.length; i++){
			outInt[i] = Integer.parseInt(outstr[i].trim());
		}
		
		return outInt;
	}
	
	/******************************************************************************/
	public void calculateQCR(int qos_pair){
		/**
		 * Calculate Q
		 */
		/*The rssi of wifi is negative, make is positive to be QoS. QoS(0, 1) */
		int qos_self = -90; //Get a new one
		qos = (qos_self + qos_pair)/2;
		qos = (qos + 100.0)/100;
		
		/**
		 * Calculate C
		 */
		/* calculate H(theda) */
		double Htheta_self = calcuHtheta(contact_history, effective_num_ofCH);		
		double Htheta_pair = calcuHtheta(pair_contact_history, pair_contact_history.length);
		
		/* calculate d according to formulate 5.*/
		double d = 1 - Math.abs(Htheta_self - Htheta_pair);
		
		/**
		 *  Calculate s according to formulate 4.
		 *  1. Get intersection of the two devices' contact history.
		 *  2. calculate s  
		 */
		double[] intersectionOfCH_self = new double[TDPConstants.MAX_NUM_OF_DEVICES];
		double[] intersectionOfCH_pair = new double[TDPConstants.MAX_NUM_OF_DEVICES];
		int num = 0;
		for(int i=0; i<effective_num_ofCH; i++){
			for(int j=0; j<pair_contact_history.length; j++){
				if(contact_history_ID[i].equals(pair_contact_history_ID[j])){
					intersectionOfCH_self[num] = contact_history[i];
					intersectionOfCH_pair[num] = pair_contact_history[j];
					num++;
					break;
				}
			}
		}
//		System.out.println("\n******************Interseciton Contact History********************"
//				+ "\n\tNum of Intersection CH: " + num
//				+ "\n\tIntersection CH: " + Arrays.toString(intersectionOfCH_self)
//				+"\n");
		double s= 0;
		if(num != 0){
			for(int i=0; i<num; i++){
				s += Math.pow(intersectionOfCH_pair[i] - intersectionOfCH_self[i], 2);
			}
			s = 1 - Math.sqrt((double)s/num);
		}	
		
		/* calculate w according to formulate 7.*/
		double w = 0;
		int average_transaction = 31;
		double cw = 0.5;
		int compare = (int)Math.floor(Math.sqrt(1/cw)*average_transaction);
		int pair_index = indexOfDeviceInCH(pair_ID, contact_history_ID);
		if(pair_index < 0){
			w = 1.0;
		}else if (contact_history_total[pair_index] > compare) {
			w = 0.0;
		}else {
			w = -cw * Math.pow(contact_history[pair_index]/average_transaction, 2) + 1;
		}
		
		/* Calculate c(Credibility) according to formulate 3. */
		cre = (ALPHA*s*s + (1-ALPHA)*d*d)*w;
		
		/**
		 * Calculate R
		 */
		double tempBETA = BETA;
		if(effective_num_ofCH == 0 || pair_contact_history.length == 0 || num == 0){
			tempBETA = 1;
		}
		double a = tempBETA*qos*qos + (1-tempBETA)*cre*cre;
		double pre_a = tempBETA*hat_qos*hat_qos + (1-tempBETA)*hat_cre*hat_cre;
		if(a >= pre_a){
			r = 1;
		}else {
			r = -1;
		}
		
		updateContactHistory(r);
		
		PrintQCRCalculationInfo(num, intersectionOfCH_pair);		
	}
	private void PrintQCRCalculationInfo(int num, double[] contact_history_){
		System.out.println("\n**********************QCR Calculation Info************************"
				+ "\n\tNum of Intersection CH: " + num
				+ "\n\tIntersection CH: " + Arrays.toString(contact_history_)
				+ "\n\t QoS: " + qos
				+ "\n\t Cre: " + cre
				+ "\n\t Rat: " + r
				+"");
	}
	/* Calculate H(theda) according to formulate 6.*/
	private double calcuHtheta(double[] contact_history_, int dim_){
		if(dim_ == 0) return 0.0;
		
		double Htheta = 0;
		for(int i=1; i<dim_; i++){
			Htheta += Math.pow(contact_history_[i-1] - contact_history_[i], 2);
		}		
		Htheta += Math.pow(contact_history_[dim_-1] - contact_history_[0], 2);
		
		Htheta = Math.sqrt(Htheta/dim_);
		return Htheta;
	}
	
	
	/******************************************************************************/	
	private void updateContactHistory(int r){
		hat_cre = THETA*cre + (1-THETA)*hat_cre;
		hat_qos = THETA*qos + (1-THETA)*hat_qos;
		
		int index = indexOfDeviceInCH(pair_ID, contact_history_ID);
		if(index < 0){
			/* This is a device encountered the first time. */
			contact_history_ID[effective_num_ofCH] = pair_ID;
			if(r>0){
				contact_history_positive[effective_num_ofCH] = 1;
			}else {
				contact_history_positive[effective_num_ofCH] = 0;
			}
			contact_history_total[effective_num_ofCH] = 1;
			contact_history[effective_num_ofCH] = (double)contact_history_positive[effective_num_ofCH] / contact_history_total[effective_num_ofCH];
			
			effective_num_ofCH++;
		}else{
			/* The contact history of this device exists. */
			if(r > 0) contact_history_positive[index] += 1;
			contact_history_total[index] += 1;
			contact_history[index] = (double)contact_history_positive[index] / contact_history_total[index];
		}
		
		PrintAllParemetersInfo();
		
		/* Store the contact history into the database. */
		//TODO
	}
	
	/**
	 * Get the index of an device's data in the contact history.
	 * Use the index to whether this is a new device, if not get data from the contact history arrays.
	 * @param ID_
	 * @param contact_history_IDs_
	 * @return
	 */
	private int indexOfDeviceInCH(String ID_, String[] contact_history_IDs_){
		int result = -1;
		for(int i=0; i<contact_history_IDs_.length; i++){
			if(ID_.equals(contact_history_IDs_[i])){
				result = i;
				break;
			}
		}
		return result;
	}
	
	/************************************************************************************/
	private byte[] a_enc_r;
	private byte[] a_Snumerator;
	private byte[] a_Sdenominator;
	private byte[] a_T1;
	private byte[] a_T2;
	private byte[] a_T3;
	
	private byte[] b_enc_r;
	private byte[] b_Snumerator;
	private byte[] b_Sdenominator;
	private byte[] b_T1;
	private byte[] b_T2;
	private byte[] b_T3;
	
	private KeysForGenRec keysForGenRec = new KeysForGenRec();
	private static final int[] TransactionType = {1, 2, 3};
	
	public void setClientSelfKeys(BigInteger x, BigInteger d){
		keysForGenRec.self_x = x;
		keysForGenRec.self_d = d;
	}
	public void setClientPairKeys(ECPoint pub, ECPoint rPub){
		keysForGenRec.pair_Pub = pub;
		keysForGenRec.pair_R = rPub;
	}
	
	public void calcSignature() throws Exception{
		/**
		 * er_ab = Enc(d_a, r_ab)
		 */
		byte[] key_d = AESCoder.initKey(Hex.toHexString(keysForGenRec.self_d.toByteArray()));
		a_enc_r = AESCoder.encrypt(String.valueOf(r).getBytes(), key_d);
		/**
		 * ha = h2(er_ab||c_ab||q_ab||type_ab)
		 */
		String catStr = Hex.toHexString(a_enc_r) 
					  + Hex.toHexString(String.valueOf(cre).getBytes())
					  + Hex.toHexString(String.valueOf(qos).getBytes())
					  + Hex.toHexString(String.valueOf(TransactionType[1]).getBytes());
		BigInteger ha = HashFunction.hashTwo(catStr);
		System.out.println("hash_a: " + Hex.toHexString(ha.toByteArray()));
		/**
		 * Sa = t0_a/(x_a + d_a)
		 * Sa = Sa_numerator/Sa_denominator
		 */
		BigInteger t0 = new BigInteger(32, new Random());
		BigInteger Sa_numerator = t0;
		BigInteger Sa_denominator = keysForGenRec.self_x.add(keysForGenRec.self_d);	
		
		a_Snumerator = Sa_numerator.toByteArray();
		a_Sdenominator = Sa_denominator.toByteArray();
		/**
		 * T1a = t0*ha*( Rb + h0(b||R_b||P_b)Ppub )
		 * T2a = t0aG
		 * T3a = t0aPb
		 */
		String strHash = Hex.toHexString(pair_ID.getBytes())
					  + Hex.toHexString(keysForGenRec.pair_R.getEncoded(true))
					  + Hex.toHexString(keysForGenRec.pair_Pub.getEncoded(true));
		ECPoint T1 = BackendServerKey.getInstance().getPpub().multiply(HashFunction.hashZero(strHash)).add(keysForGenRec.pair_R).multiply(t0).multiply(ha);
		ECPoint T2 = ECDHCurve.getInstance().getBasePoint().multiply(t0);
		ECPoint T3 = keysForGenRec.pair_Pub.multiply(t0);
		
		a_T1 = T1.getEncoded(true);
		a_T2 = T2.getEncoded(true);
		a_T3 = T3.getEncoded(true);
		
//		PrintSignaturaInfo(self_ID, a_enc_r, a_Snumerator, a_Sdenominator, a_T1, a_T2, a_T3);
	}
	
	private void PrintSignaturaInfo(String id, byte[] er, byte[] Snum, byte[] Sden, byte[] t1, byte[] t2, byte[] t3){
		System.out.println("\n**********************Signature Info************************"
						 + "\n  ID: " + id
						 + "\n  er: " + Hex.toHexString(er)
						 + "\n  Sn: " + Hex.toHexString(Snum)
						 + "\n  Sd: " + Hex.toHexString(Sden)
						 + "\n  t1: " + Hex.toHexString(t1)
						 + "\n  t2: " + Hex.toHexString(t2)
						 + "\n  t3: " + Hex.toHexString(t3)
						 + "");
	}
	
	public byte[] get_er(){
		return a_enc_r;
	}
	public byte[] get_Snumerator(){
		return a_Snumerator;
	}
	public byte[] get_Sdenominator(){
		return a_Sdenominator;
	}
	public byte[] get_T(int n) throws Exception{
		switch(n){
			case 1:
				return a_T1;
			case 2:
				return a_T2;
			case 3:
				return a_T3;
			default:				
				throw new Exception("return Tn error(1 <= n <= 3): " + n);
		}
	}
	/**
	 * Calculation Receipt
	 */
	public void set_PairSignature(byte[] er, byte[] Snum, byte[] Sden, byte[] t1, byte[] t2, byte[] t3){
		b_enc_r = er;
		b_Snumerator = Snum;
		b_Sdenominator = Sden;
		b_T1 = t1;
		b_T2 = t2;
		b_T3 = t3;
		
//		PrintSignaturaInfo(pair_ID, b_enc_r, b_Snumerator, b_Sdenominator, b_T1, b_T2, b_T3);
	}
	
	public boolean calcReceipt() throws NoSuchAlgorithmException{
		boolean result = false;
		/**
		 * hb = h2(er_ba||c_ab||q_ab||type_ab)
		 */
		String catStr = Hex.toHexString(b_enc_r)
				 	  + Hex.toHexString(String.valueOf(cre).getBytes())
				 	  + Hex.toHexString(String.valueOf(qos).getBytes())
				 	  + Hex.toHexString(String.valueOf(TransactionType[1]).getBytes());
		BigInteger hb = HashFunction.hashTwo(catStr);		
		System.out.println("hash_b: " + Hex.toHexString(hb.toByteArray()));
		
		ECPoint T1b = ECDHCurve.getInstance().decodeBytePoint(b_T1);
		ECPoint T2b = ECDHCurve.OBJ_ECDH_CUVE.decodeBytePoint(b_T2);
		ECPoint T3b = ECDHCurve.OBJ_ECDH_CUVE.decodeBytePoint(b_T3);
		/**
		 * Right = T1b*Sb_denominator
		 * Left = d_a*hb*Sb_numerator*( Pb + Rb + h0(b||R_b||P_b)Ppub )
		 */
		ECPoint Right = T1b.multiply(new BigInteger(b_Sdenominator));
		System.out.println("b_Sden: " + Hex.toHexString(new BigInteger(b_Sdenominator).toByteArray()));
		String strHash = Hex.toHexString(pair_ID.getBytes())
				  + Hex.toHexString(keysForGenRec.pair_R.getEncoded(true))
				  + Hex.toHexString(keysForGenRec.pair_Pub.getEncoded(true));
		ECPoint Left = BackendServerKey.getInstance().getPpub().multiply(HashFunction.hashZero(strHash)).add(keysForGenRec.pair_Pub).add(keysForGenRec.pair_R)
				.multiply(keysForGenRec.self_d).multiply(hb).multiply(new BigInteger(b_Snumerator));

		ECPoint T2 = ECDHCurve.getInstance().decodeBytePoint(a_T2);
		System.out.println("\n***********Calc Receipt**************"
				 + "\n  ID: " + self_ID
				 + "\n  Left:  " + Hex.toHexString(Left.getEncoded(true))
				 + "\n  Right: " + Hex.toHexString(Right.getEncoded(true))
				 + "\n  xa*T2b:  " + Hex.toHexString(T2b.multiply(keysForGenRec.self_x).getEncoded(true))
				 + "\n  T3:    " + Hex.toHexString(T3b.getEncoded(true))
				 + "");
		
		if(Left.equals(Right) && T2b.multiply(keysForGenRec.self_x).equals(T3b)){
			result = true;
			System.out.println("\n$%%^^*&$#$@ Receipt is generated. $%%^^*&$#$@");
			
		}else {
			System.err.println("\n$%%&$#$  Generate Receipt failed. *&$#$@");	
		}
		return result;
	}	
	
	public void CalcTest() throws Exception {
		BigInteger baseServerPrivateKey = BackendServerKey.getInstance().getx();
//		ECPoint baseServerPublicKey = BackendServerKey.OBJ_SERVER_KEY.getPpub();
		ECDHCurve ecdhCurve = new ECDHCurve();
		String ID = "Android_239632920";
		BigInteger x = ecdhCurve.generatePrivateKeyBigInteger();
		ECPoint pub = ecdhCurve.generatePublicKeyEcPoint(x);
		DeviceReceipt receipt = new DeviceReceipt(ID, pub);
		receipt.generateReceipt(baseServerPrivateKey);
		/**
		 * er_ab = Enc(d_a, r_ab)
		 */
		byte[] key_d = AESCoder.initKey(Hex.toHexString(receipt.get_d().toByteArray()));
		a_enc_r = AESCoder.encrypt(String.valueOf(1).getBytes(), key_d);
		/**
		 * ha = h2(er_ab||c_ab||q_ab||type_ab)
		 */
		String catStr = Hex.toHexString(a_enc_r) + Hex.toHexString(String.valueOf(0.5).getBytes())
				+ Hex.toHexString(String.valueOf(0.4).getBytes())
				+ Hex.toHexString(String.valueOf(1).getBytes());
		BigInteger ha = HashFunction.hashTwo(catStr);
		System.out.println("hash_a: " + Hex.toHexString(ha.toByteArray()));
		/**
		 * Sa = t0_a/(x_a + d_a) Sa = Sa_numerator/Sa_denominator
		 */
		BigInteger t0 = new BigInteger("12345678910");
		BigInteger Sa_numerator = t0;
		BigInteger Sa_denominator = x.add(receipt.get_d());
		/**
		 * T1a = t0*ha*( Rb + h0(b||R_b||P_b)Ppub )
		 */
		String strHash = Hex.toHexString(ID.getBytes()) + Hex.toHexString(receipt.getRandEcPoint().getEncoded(true))
				+ Hex.toHexString(pub.getEncoded(true));
		ECPoint T1 = BackendServerKey.getInstance().getPpub().multiply(HashFunction.hashZero(strHash))
				.add(receipt.getRandEcPoint()).multiply(t0).multiply(ha);
		ECPoint T2 = ECDHCurve.getInstance().getBasePoint().multiply(t0);
		ECPoint T3 = pub.multiply(t0);

		String catStrB = Hex.toHexString(a_enc_r) + Hex.toHexString(String.valueOf(0.5).getBytes())
				+ Hex.toHexString(String.valueOf(0.4).getBytes())
				+ Hex.toHexString(String.valueOf(1).getBytes());
		BigInteger hb = HashFunction.hashTwo(catStrB);

//		System.out.println("hash_b: " + Hex.toHexString(hb.toByteArray()));

		/**
		 * Right = T1b*Sb_denominator Left = d_a*hb*Sb_numerator*( Pb + Rb +
		 * h0(b||R_b||P_b)Ppub )
		 */

		ECPoint Right = T1.multiply(Sa_denominator);

		String strHashB = Hex.toHexString(ID.getBytes()) + Hex.toHexString(receipt.getRandEcPoint().getEncoded(true))
				+ Hex.toHexString(pub.getEncoded(true));
		ECPoint Left = BackendServerKey.getInstance().getPpub().multiply(HashFunction.hashZero(strHashB)).add(pub)
				.add(receipt.getRandEcPoint()).multiply(receipt.get_d()).multiply(hb).multiply(Sa_numerator);

		System.out.println("\n***********Calc Receipt**************" 
				+ "\n  ID: " + ID 
				+ "\n  Left:  "
				+ Hex.toHexString(Left.getEncoded(true)) 
				+ "\n  Right: " + Hex.toHexString(Right.getEncoded(true))
				+ "\n  x*T2:  " + Hex.toHexString(T2.multiply(x).getEncoded(true)) 
				+ "\n  T3:    "
				+ Hex.toHexString(T3.getEncoded(true)) 
				+ "");
		if (Left.equals(Right) && T2.multiply(x).equals(T3)) {
			System.out.println("\n$%%^^*&$#$@ Receipt is generated. $%%^^*&$#$@");
		} else {
			System.err.println("\n$%%&$#$  Generate Receipt failed. *&$#$@");
		}

	}

	private class KeysForGenRec{
		public BigInteger self_x;
		public BigInteger self_d;
		
		public ECPoint pair_Pub;
		public ECPoint pair_R;
	}

}

