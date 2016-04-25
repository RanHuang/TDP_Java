package trial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class ReadLogText {

	private static String strFileName = "test.txt";
	private static String strLogFileName = "AudioNoise_Location_Log.txt";
	
	private static ArrayList<Recorder> recordList = new ArrayList<Recorder>();

	public static void main(String[] args) {
		try {
			writeTxtFile("Hello", new File(strFileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		dealLogTxtFile(strLogFileName);
	}

	public static void dealLogTxtFile(String strFileName) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // ���ڰ�װInputStreamReader,��ߴ������ܡ���ΪBufferedReader�л���ģ���InputStreamReaderû��
		try {
			String str = "";
			fis = new FileInputStream(strFileName);// FileInputStream
			// ���ļ�ϵͳ�е�ĳ���ļ��л�ȡ�ֽ�
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
						
			double longitude=0, latitude = 0, noiseLevel=0;
			while ((str = br.readLine()) != null) {
				if(str.indexOf(":") > 0){		
					String[] strArrays = str.split(":");
					if(strArrays.length > 1){
						if(strArrays[0].equals("Longitude")){
							longitude = Double.valueOf(strArrays[1].trim());
						}else if (strArrays[0].equals("Latitude")) {
							latitude = Double.valueOf(strArrays[1].trim());
						}else if (strArrays[0].equals("NoiseLevel")) {
							noiseLevel = Double.valueOf(strArrays[1].trim());
							
							Recorder recorder = new Recorder(longitude, latitude, noiseLevel);
							recordList.add(recorder);
						}
					}
				}
			}
			
			System.out.println("Number of Record: " + recordList.size());
			System.out.println("Longitude: " + recordList.get(0).longitude);
			System.out.println("Latitude: " + recordList.get(0).latitude);
			System.out.println("NoiseLevel: " + recordList.get(0).noiseLevel);
		} catch (FileNotFoundException e) {
			System.out.println("�Ҳ���ָ���ļ�");
		} catch (IOException e) {
			System.out.println("��ȡ�ļ�ʧ��");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				// �رյ�ʱ����ð����Ⱥ�˳��ر���󿪵��ȹر������ȹ�s,�ٹ�n,����m
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean writeTxtFile(String content, File fileName) throws Exception {
		RandomAccessFile mm = null;
		boolean flag = false;
		FileOutputStream o = null;
		try {
			o = new FileOutputStream(fileName);
			o.write(content.getBytes("GBK"));
			o.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mm != null) {
				mm.close();
			}
		}
		return flag;
	}
}

class Recorder {
	public double longitude;
	public double latitude;
	public double noiseLevel;
	
	public Recorder(double longitude, double latitude, double noiseLevel){
		this.longitude = longitude;
		this.latitude = latitude;
		this.noiseLevel = noiseLevel;
	}
}
