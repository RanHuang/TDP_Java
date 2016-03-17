package trial;

import java.util.Random;

public class AndroidID {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Random random = new Random();
		System.out.println(String.valueOf(random.nextInt(Integer.MAX_VALUE)) + "\n");
		
		//r[0, BOUND)
		int BOUND = 9;
		int r;
		//Generate the Android devices' ID.
		String[] strAndroidId = new String[BOUND + 1];
		for(int i=0; i<10; i++){
			strAndroidId[i] = "Android_" + String.valueOf(random.nextInt(Integer.MAX_VALUE));
			System.out.println(String.valueOf(random.nextInt(BOUND)) + ": " + strAndroidId[i]);
		}
		System.out.println("\n\n");
		
		String[] strAndroidID = {"Android_1319752751", "Android_1864635234", "Android_239632920", "Android_131485814",
				 "Android_1792883326", "Android_1934807146", "Android_1475781389", "Android_74059307",
				 "Android_219157648", "Android_681387016"};
		BOUND = 2;
		for(int i=0; i<10; i++){
			r = random.nextInt(BOUND);
			String _ID = strAndroidID[r];
			System.out.println(String.valueOf(r) + ": " + _ID);
		}
	}

}
