package trial;

import com.nick.tdp.database.DBProfile;
import com.nick.tdp.database.RegistrationDatabase;
import com.nick.tdp.database.RegistrationReceipt;

public class TDPDatabase {

	public static void main(String[] args) {
		
		String tableName = DBProfile.TableName_RegistrationReceipt;
		System.out.println("Table: " + tableName);
//		RegistrationDatabase.deleteTable(tableName);
//		
//		RegistrationDatabase.createRegistrationReceiptTable();
		
		/***************************************************************************************************
		==== Send Device Received Receipt ====
		ID  : Android_1319752751
		x   : f2e256d023c0059db34c4060610fd00be2b032211cc3e3e7
		Ppub: 03fb4b93ad57c15cc74fa7ab0f088037fd34983653
		r   : 24303f63290bfeb04f434c394c0ac1f5fef93ba1887751a1
		d   : f53745a7b538b298bfc48601863afa8595490c5391621ecb05317aa73b775a989cdc757181ca028e5e6500b
		Rand: 03555d44d61a0ff71dd3748fc26d97cff41ccd82d0
		Master Ppub: 02ebfa91c4d14aadbef157a2f3b9875496216e3aea
		Trust Value: 20
		**************************************************************************************************/
		RegistrationReceipt registrationReceipt = new RegistrationReceipt();
		registrationReceipt.setID("Android_1319752745");
		registrationReceipt.set_x("f2e256d023c0059db34c4060610fd00be2b032211cc3e3e7");
		registrationReceipt.set_Ppub("03fb4b93ad57c15cc74fa7ab0f088037fd34983653");
		registrationReceipt.set_r("24303f63290bfeb04f434c394c0ac1f5fef93ba1887751a1");
		registrationReceipt.set_d("f53745a7b538b298bfc48601863afa8595490c5391621ecb05317aa73b775a989cdc757181ca028e5e6500b");
		registrationReceipt.set_Rpub("03555d44d61a0ff71dd3748fc26d97cff41ccd82d0");
		registrationReceipt.setTrustValue(200);
		RegistrationDatabase.addRegistrationReceipt(registrationReceipt);
		
		RegistrationDatabase.closeConnection();
	}
}
