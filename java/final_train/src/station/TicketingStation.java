package station;

import java.util.ArrayList;
import java.util.Date;
import client.Client;

public class TicketingStation implements Station{
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
		try {
			client.tb = match();
			cq.dequeue();
			System.out.println(client.tb);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client.tb.ticketing(client);
		ticketFinish(client);
	}
	
	
	public synchronized TicketBox match() throws InterruptedException{
		
		Thread t = Thread.currentThread();
		while(tbs.size() == 0){ //v.s. if
			t.wait();
			System.out.println("["+t.getName()+"]" + " is waiting.");	
		}
		
		TicketBox tb = tbs.remove(0);
		System.out.println("["+t.getName()+"]" + " uses "+ tb);
		return tb;
	}
	
	public synchronized void ticketFinish(Client c){
		System.out.println("["+c.getName()+"]" + " returns "+ c.tb);
		setDate(c);
		tbs.add(c.tb);
		notifyAll();
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