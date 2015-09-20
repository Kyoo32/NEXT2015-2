package client;

import java.util.Date;

import station.TicketBox;
import main.Main;

public class Client {
	public int id;
	public String name;
	
	public long newTime;
	public int visitTimeSpent;
	public long matchedTime;
	public int ticketingInterval;
	public int ticketWaitInterval;
	
	public int trainWaitInterval;
	public int travelTime;
	
	String departSName;
	String destSName;
	
	public long enqueueTime;
	public long dequeueTime;
	
	public Date ticketFinish;
	public Date arrivalStation;
	public TicketBox tb;
	
	public Client(int id, String name, int visitTimeSpent, int ticketingInterval, String departSName, String destSName){
		this.id = id;
		this.name = name;
		this.newTime = System.currentTimeMillis();
		this.visitTimeSpent = visitTimeSpent * 1000;
		this.ticketingInterval = ticketingInterval;
		this.departSName = departSName;
		this.destSName = destSName;
		this.travelTime = calTravelTime() * 1000;
		
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
		System.out.println(this);
		visitTimeSpent();
		Main.ticS.arrive(this);
	}

	private void visitTimeSpent() {
		System.out.println("in" + (System.currentTimeMillis() - newTime ));
		System.out.println("how" +(long)visitTimeSpent);
		while((System.currentTimeMillis() - newTime ) < (long)visitTimeSpent);
			//System.out.println("visiting");		
	}

	public void ticketingTimeSpent() {
		while((System.currentTimeMillis() - matchedTime ) < (long)ticketingInterval);
		
		
	}
	public void trainWaitingTimeSpent() {
		System.out.println(this + "start to wait train");
		
	}
	
	public void trainGoing(){
		
		Main.traS.setDate(this);
	}
	
	
}
