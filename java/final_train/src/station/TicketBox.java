package station;

import client.Client;
import main.Main;

public class TicketBox {
	
	public void ticketing(Client c){
		c.matchedTime = System.currentTimeMillis();
		c.ticketingTimeSpent();
		System.out.println("Ticket open@@@@@@");
		Main.ticS.ticketFinish(c);
		Main.traS.enqueue(c);
	}
	

}
