package station;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;

import client.Client;

public class TicketingStation extends TimerTask implements Station{
	ClientQueue cq;
	public ArrayList<TicketBox> tbs = new ArrayList<TicketBox>();
	
	public TicketingStation(){
		cq = new ClientQueue();
		tbs.add(new TicketBox());
		tbs.add(new TicketBox());
		tbs.add(new TicketBox());
		System.out.println(cq);
	}
	
	public void arrive(Client client) {
		cq.enqueue(client);
	}
	
	public void run(){
		match();	
	}
	
	public void match() {
		if(cq.size() == 0) return; 
		while(tbs.size() == 0){ 
			System.out.println("no ticketbox available");	
		}
		
		Client matchedClient = cq.dequeue();
		matchedClient.tb = tbs.remove(0);
		System.out.println("[" + matchedClient+ "]" + " uses "+ matchedClient.tb);
		matchedClient.tb.ticketing(matchedClient);
		
	}
	
	public void ticketFinish(Client c){
		System.out.println("["+c+"]" + " returns "+ c.tb);
		setDate(c);
		tbs.add(c.tb);
	}

	@Override
	public void calTimeInterval(Object ob) {
		if(ob instanceof Client){
			((Client) ob).ticketWaitInterval = (int) (((Client)ob).dequeueTime - ((Client)ob).enqueueTime);
		}
	}

	@Override
	public void setDate(Object ob) {
		if(ob instanceof Client){
			((Client) ob).ticketFinish = new Date();
		}
	}

}
