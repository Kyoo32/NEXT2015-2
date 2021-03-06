package station;

import java.util.Date;
import java.util.TimerTask;

import client.Client;
import client.ClientQueue;
import main.Main;

public class TrainStation extends TimerTask implements Station {
	// 열차 출발, 고객 열차 대기 정보 관
	ClientQueue cq;
	
	public TrainStation(){
		cq = new ClientQueue();
	}
	
	void enqueue(Client c){
		cq.enqueue(c);
		c.trainWaitingTimeSpent();	
	}
	
	
	@Override
	public void calTimeInterval(Object ob) {
		if(ob instanceof Client){
			((Client) ob).trainWaitInterval = (int) (((Client)ob).dequeueTime - ((Client)ob).enqueueTime);
		}
	}

	@Override
	public void setDate(Object ob) {
		if(ob instanceof Client){
			((Client) ob).arrivalStation = new Date();
		}
	}

	@Override
	public void run() {
		if(cq.size() == 0)
			return;
		System.out.println("train departs");
		trainDepart();	
	}


	private synchronized void trainDepart() {
		int many = cq.size();
		if(cq.size() == 0)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		for(int i=0; i<many; i++){
			Client goC = cq.dequeue();
			calTimeInterval(goC);
			Main.gac.goingCq.enqueue(goC);
			//System.out.println(goC);	
		}	
	}
	
}
