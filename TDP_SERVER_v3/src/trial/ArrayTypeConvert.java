package trial;

import java.util.Arrays;
/**
 * Convert the data type of array.
 * @author Nick
 *
 */
public class ArrayTypeConvert {

	public static void main(String[] args) {
		/*
		 * Integer to String
		 */
		ArrayTypeConvert arrayTypeConvert = new ArrayTypeConvert();
		
		int[] intarr = new int[10];
		double[] douarr = new double[11];
		for(int i=0; i<10; i++){
			intarr[i] = 13 - i;
			douarr[i] = 89.0 + i;
		}
		douarr[10] = 18909.45;
		/**
		 * Integer <--> String
		 */
		String intstr = arrayTypeConvert.IntArrayToString(intarr);
		System.out.println("Integer -> String Array: " + intstr);
		int[] tempInt;
		tempInt = arrayTypeConvert.StringToIntArray(intstr);
		System.out.println("Number of items in String Array: " + tempInt.length);
		System.out.println("String -> Integer Array: " + Arrays.toString(tempInt));
		System.out.println("");
		
		/**
		 * Double <--> String
		 */
		String doublestr = arrayTypeConvert.DoubleArrayToString(douarr);
		System.out.println("Double -> String Array: " + intstr);
		double[] tempDouble;
		tempDouble = arrayTypeConvert.StringToDoubleArray(doublestr);
		System.out.println("Number of items in String Array: " + tempDouble.length);
		System.out.println("String -> Double Array: " + Arrays.toString(tempDouble));
		System.out.println("");
	}
	
	public String IntArrayToString(int[] arrInt_){
		return Arrays.toString(arrInt_);
	}
	public int[] StringToIntArray(String strIntArray_){
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
	
	public String DoubleArrayToString(double[] arrDouble_){
		return Arrays.toString(arrDouble_);
	}
	public double[] StringToDoubleArray(String strDoubleArray_){
		String tempstr = strDoubleArray_.substring(1, strDoubleArray_.length()-1);
		
		String[] outstr = tempstr.split(",");
		
		double[] outDouble = new double[outstr.length];		
		for(int i=0; i<outstr.length; i++){
			outDouble[i] = Double.parseDouble(outstr[i].trim());
		}
		
		return outDouble;
	}
}
