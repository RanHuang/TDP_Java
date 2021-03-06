package trial.database;

public abstract class DBProfile {
	/**
	 * For DataBase
	 */
	public static final String dbDriver = "com.mysql.jdbc.Driver";
	
	public static final String dbURL = "jdbc:mysql://127.0.0.1:4416/";
	
	public static final String dbName = "wifitest_db";
	
	public static final String userName = "root";
	public static final String userPassword = "123456";	
	
	/**
	 * tables' name
	 */
	public static final String TableName_RegistrationReceipt = "RegRec";
	public static final String TableName_ClientParameters = "CliPara";
	public static final String TableName_DeviceToDevicePairingExperiment = "DtoDPerTest";
	public static final String TableName_WifiDirectPerformanceTest = "WifiDirectPerTest";
	public static final String TableName_MobileCrowdsensingData = "SensingData";
	
	
	/**
	 * type of performance test
	 */
	public static final int WifiDirect_ActiveScan = 1;
	public static final int WifiDirect_Autonomous = 2;
	public static final int WifiDirect_ServiceDiscovery = 3;
}
