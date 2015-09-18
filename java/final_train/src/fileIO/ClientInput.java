package fileIO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import client.Client;

public class ClientInput {
	FileReader fr = null;
	BufferedReader br = null;
	ClientOutput COut = null;
	
	public void openInStream(ClientOutput COut){
		try{		
			// "ReadFile.txt" 파일을 읽는 FileReader 객체 생성
			// BufferedReader 객체 생성		
			fr = new FileReader("client2.txt");
			br = new BufferedReader(fr);
			this.COut = COut;
		} catch(Exception e){	
			e.printStackTrace();	
		}
		
	}
	
	public synchronized void readInput(){
		
		String s = null;
		String[] sArr;
		
		// ReadFile.txt 에서 한줄씩 읽어서 BufferedRaeder에 저장한다.
		try {
			while((s=br.readLine())!=null){		
				sArr = new String(s).split("%");
				Client c = new Client(Integer.parseInt(sArr[0]), sArr[1], 
						Integer.parseInt(sArr[2]), Integer.parseInt(sArr[3]),
						sArr[4],sArr[5]);
				c.start();
				c.join();
				System.out.println("finish!!!!!!!!!!");
				COut.writeOutput(c);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public void closeInStream(){
		// BufferedReader FileReader를 닫아준다.
		if(br != null) try{br.close();}catch(IOException e){}
		if(fr != null) try{fr.close();}catch(IOException e){}	
	}
}
