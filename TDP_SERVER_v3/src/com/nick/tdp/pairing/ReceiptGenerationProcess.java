package com.nick.tdp.pairing;

import com.nick.tdp.register.TDPConstants;

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
		hat_cre = 0;
		hat_qos = 0;
	}
	
	/**
	 * Initialize the data of contact history.
	 * Get them from the data base.
	 */
	private void initializeSelfCHdata(){		
		
		effective_num_ofCH = 5;
		for(int i=0; i<effective_num_ofCH; i++){
			contact_history[i] = (double)contact_history_positive[i] / contact_history_total[i];
		}
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
		for(int i=0; i<pair_contact_history_ID.length; i++){
			pair_contact_history_ID[i] = pair_contact_history_ID[i].trim();
			pair_contact_history[i] = (double)pair_contact_history_positive[i] / pair_contact_history_total[i];
		}		
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
	
	public void calculateQos(int rssi_wifi){
		
		/*The rssi of wifi is negative, make is positive to be QoS. QoS(0, 1) */
		qos = rssi_wifi+200.0;//TODO
		
	}
	/******************************************************************************/
	public void calculateCredibility(){	
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
		double s= 0;
		//if num==0
		for(int i=0; i<num; i++){
			s += Math.pow(intersectionOfCH_pair[i] - intersectionOfCH_self[i], 2);
		}
		s = 1 - Math.sqrt(s/num);		
		
		/* calculate w according to formulate 7.*/
		double w = 0;
		
		
		/* Calculate c(Credibility) according to formulate 3. */
		cre = (ALPHA*s*s + (1-ALPHA)*d*d)*w;
	}
	/* Calculate H(theda) according to formulate 6.*/
	private double calcuHtheta(double[] contact_history_, int dim_){
		double Htheta = 0;
		for(int i=1; i<dim_; i++){
			Htheta += Math.pow(contact_history_[i-1] - contact_history_[i], 2);
		}		
		Htheta += Math.pow(contact_history_[dim_-1] - contact_history_[0], 2);
		
		Htheta = Math.sqrt(Htheta/dim_);
		return Htheta;
	}
	
	/******************************************************************************/
	public int calculateRating(){
		//if none item
		double a = BETA*qos*qos + (1-BETA)*cre*cre;
		double pre_a = BETA*hat_qos*hat_qos + (1-BETA)*hat_cre*hat_cre;
		if(a >= pre_a){
			return 1;
		}else {
			return -1;
		}
	}
	/******************************************************************************/	
	public void updateContactHistory(int r){
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
}
