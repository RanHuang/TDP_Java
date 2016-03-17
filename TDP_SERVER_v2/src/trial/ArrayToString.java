package trial;

import java.util.Arrays;

public class ArrayToString {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[][] intarr = new int[2][10];
		int[][] tempint = new int[2][10];
		for(int i=0; i<10; i++){
			intarr[0][i] = 13 - i;
			intarr[1][i] = i +95;
		}
		System.out.println(Arrays.toString(tempint[0]));
		String intstr = Arrays.toString(intarr[1]);
//		intstr = intarr[0].toString();
		System.out.println(intstr);
		
		
		String tempstr = intstr.substring(1, intstr.length()-1);
		System.out.println(tempstr);
		
		String[] outstr = tempstr.split(",");
		System.out.println(outstr.length);
		for(int i=0; i<10; i++){
			tempint[1][i] = Integer.parseInt(outstr[i].trim());
		}
		System.out.println(Arrays.toString(tempint[1]));
		
	}
}
