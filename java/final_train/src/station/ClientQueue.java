package station;

import java.util.ArrayList;

import client.Client;

public class ClientQueue {
	protected ArrayList<Client> clientQ = new ArrayList<Client>();
	
	public ClientQueue getCq(){
		return this;
	};
	
	public void enqueue(Client c){
		clientQ.add(c);
		c.enqueueTime = System.currentTimeMillis(); 
	}
	
	Client dequeue(){
		if(!clientQ.isEmpty()){
			Client finishC = clientQ.remove(0);
			finishC.dequeueTime = System.currentTimeMillis();
			return finishC;
		} else {
			return null;
		}
	}

	public int size() {	
		return clientQ.size();
	}
}