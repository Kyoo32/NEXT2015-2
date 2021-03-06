package fileIO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import client.Client;

public class ClientInput {
	FileReader fr = null;
	public BufferedReader br = null;

	public String s = null;
	String[] sArr;
	
	public void openInStream(){
		try{		
			// "ReadFile.txt" 파일을 읽는 FileReader 객체 생성
			// BufferedReader 객체 생성		
			fr = new FileReader("client2.txt");
			br = new BufferedReader(fr);
		} catch(Exception e){	
			e.printStackTrace();	
		}	
	}
	
	public Client readInput(){	
		// ReadFile.txt 에서 한줄씩 읽어서 BufferedRaeder에 저장한다.
		sArr = new String(s).split("%");
		Client c = new Client(Integer.parseInt(sArr[0]), sArr[1], 
				Integer.parseInt(sArr[2]), Integer.parseInt(sArr[3]),
				sArr[4],sArr[5]);
		return c;
	}
	
	public void closeInStream(){
		// BufferedReader FileReader를 닫아준다.
		if(br != null) try{br.close();}catch(IOException e){}
		if(fr != null) try{fr.close();}catch(IOException e){}	
	}
}
