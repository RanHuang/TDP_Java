package trial;

import java.util.Arrays;

import com.nick.tdp.pairing.ReceiptGenerationProcess;

public class ReceiptGenerationTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReceiptGenerationTest test = new ReceiptGenerationTest();
		
		test.testReceiptGenerationProcess();
	}

	private void testReceiptGenerationProcess(){
		ReceiptGenerationProcess recGenProc = new ReceiptGenerationProcess();
		/**
		 * Test the calculation of Q, C, R			
		 */
		double pre_q = 0.4;
		double pre_c = 0.6;
		String[] my_IDs = {"Android_1319752751", "Android_1864635234", "Android_1934807146", "Android_1475781389", "Android_219157648"};
		String strMyIDs = my_IDs[0];
		for(int i=1; i<my_IDs.length; i++){
			strMyIDs += ", " + my_IDs[i];
		}
		int[] my_ch_positive = {3, 2, 7, 4, 6};
		int[] my_ch_total = {7, 4, 8, 5, 6};
		recGenProc.initializeSelfCHdata(pre_q, pre_c, strMyIDs, Arrays.toString(my_ch_positive), Arrays.toString(my_ch_total));
		String[] pair_IDs = {"Android_1864635234", "Android_131485814", "Android_1792883326", "Android_1934807146" , "Android_681387016"};
		String strPairIDs = pair_IDs[0];
		for(int i=1; i<pair_IDs.length; i++){
			strPairIDs += ", " + pair_IDs[i];
		}
//		String pair_ID = "Android_1475781389";
		String pair_ID = "Android_74059307";
		int[] pair_ch_positive = {3, 4, 3, 2, 1};
		int[] pair_ch_total = {4, 4, 4, 3, 3};
		recGenProc.initializePairCHdata(pair_ID, strPairIDs, Arrays.toString(pair_ch_positive), Arrays.toString(pair_ch_total));
		
		recGenProc.calculateQCR(-80);
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
