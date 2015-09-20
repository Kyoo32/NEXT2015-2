package main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import client.Client;
import fileIO.ClientInput;
import fileIO.ClientOutput;
import station.GatherArrivedClient;
import station.TicketingStation;
import station.TrainStation;

public class Main {
	public static TicketingStation ticS = new TicketingStation();
	public static TrainStation traS = new TrainStation();
	public static GatherArrivedClient gac = new GatherArrivedClient();
	
	
	public static void main(String[] args) {
		ArrayList<Client> clientlist = new ArrayList<Client>();
		int clientCount = 0;
		
		ClientInput CIn = new ClientInput();
		ClientOutput COut = new ClientOutput();
		
		Timer ticketTimer = new Timer();
		ticketTimer.schedule(ticS, 1000, 1000);
		
		Timer trainTimer = new Timer();
		trainTimer.schedule(traS, 3000, 3000);
		
		Timer gatheringTimer = new Timer();
		gatheringTimer.schedule(gac, 4000, 1000);
		
		COut.openOutStream();
		CIn.openInStream();
		
		
		try {
			
			while((CIn.s = CIn.br.readLine())!=null){
				System.out.println("does it");
				System.out.println(CIn.s);
				Client c = CIn.readInput();
				clientCount++;
				clientlist.add(c);
				c.run();
			}
				
			Thread t = Thread.currentThread();
			try {
				Thread.sleep(200000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for(int i=0; i<clientCount; i++){
				COut.writeOutput(clientlist.get(i));
			}
			
			System.out.println("The end~~!");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CIn.closeInStream();
		COut.closeOutStream();
		
	}



	
}
