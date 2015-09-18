package client;

import java.util.Date;

import station.TicketBox;
import main.Main;

public class Client extends Thread {
	public int id;
	public String name;
	
	public int visitTime;
	public int ticketingInterval;
	String departSName;
	String destSName;
	public int ticketWaitInterval;
	public int trainWaitInterval;
	public int travelTime;
	
	public long enqueueTime;
	public long dequeueTime;
	//?train running time spend?
	public Date ticketFinish;
	public Date arrivalStation;
	public TicketBox tb;
	
	public Client(int id, String name, int visitTime, int ticketingInterval, String departSName, String destSName){
		this.id = id;
		this.name = name;
		this.visitTime = visitTime;
		this.ticketingInterval = ticketingInterval;
		this.departSName = departSName;
		this.destSName = destSName;
		this.travelTime = calTravelTime();
	}
	
	private int calTravelTime() {
		String des = destSName;
		switch(departSName){
		case "Seoul" : if(des =="Chuncheon") return 16;
						else if(des == "Wonju") return 22;
						else if(des == "Gyeongju") return 44;
						else if(des == "Deajeon") return 29;
						else if(des == "Ansan") return 20;
						else return 41; 
		case "Chuncheon" : if(des =="Seoul") return 16;
						else if(des == "Wonju") return 28;
						else if(des == "Gyeongju") return 31;
						else if(des == "Deajeon") return 45;
						else if(des == "Ansan") return 36;
						else return 49;
		case "Wonju" : if(des =="Seoul") return 22;
						else if(des == "Chuncheon") return 28;
						else if(des == "Gyeongju") return 32;
						else if(des == "Deajeon") return 23;
						else if(des == "Ansan") return 42;
						else return 35;
		case "Gyeongju" : if(des =="Seoul") return 44;
						else if(des == "Wonju") return 32;
						else if(des == "Chuncheon") return 31;
						else if(des == "Deajeon") return 15;
						else if(des == "Ansan") return 43;
						else return 18;
		case "Deajeon" : if(des =="Seoul") return 29;
						else if(des == "Wonju") return 23;
						else if(des == "Gyeongju") return 15;
						else if(des == "Chuncheon") return 45;
						else if(des == "Ansan") return 35;
						else return 12;
		case "Ansan" : 	if(des =="Seoul") return 20;
						else if(des == "Wonju") return 42;
						else if(des == "Gyeongju") return 43;
						else if(des == "Deajeon") return 35;
						else if(des == "Chuncheon") return 36;
						else return 25;
		default : 		if(des =="Seoul") return 41;
						else if(des == "Wonju") return 53;
						else if(des == "Gyeongju") return 18;
						else if(des == "Deajeon") return 12;
						else if(des == "Ansan") return 25;
						else return 49;
		}
	}

	public void run(){
		visitTimeSpent();
		Main.ticS.arrive(this);
	}

	private void visitTimeSpent() {
		try {
			sleep(visitTime * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void ticketingTimeSpent() {
		try {
			sleep(ticketingInterval* 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	public synchronized void trainWaitingTimeSpent() {
		
		try {
			System.out.println(this + "start to wait");
			wait();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				
	}
	
	public void trainGoing(){
		try {
			sleep(travelTime *1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Main.traS.setDate(this);
	}
	
	
}