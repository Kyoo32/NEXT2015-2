package station;

import java.util.TimerTask;

import client.Client;
import main.Main;

public class GatherArrivedClient extends TimerTask implements Runnable {
	ClientQueue goingCq = new ClientQueue();
	
	@Override
	public void run() {
		gatherArrivedClient();
	}

	public void gatherArrivedClient() {
		int many = goingCq.size();
		if(many == 0) return;
		System.out.println(many);
		for(int i=0; i < many; i++){
			Client client = goingCq.index(i);
			System.out.print(client);
			System.out.println("/" + (System.currentTimeMillis() - client.dequeueTime));
			if((System.currentTimeMillis() - client.dequeueTime) >= (long) client.travelTime) {
				Main.traS.setDate(goingCq.dequeue());
				many--;
				i--;
			}
		}
	}
}
