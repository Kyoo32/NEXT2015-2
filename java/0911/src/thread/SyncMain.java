package thread;

class Bank{
	private int money = 10000;
	
	public void setMoney(int money) {
		this.money = money;
	}
	public int getMoney(){
		return money;
	}
	public void deposite(int save){
		int m = this.getMoney();
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		
		this.setMoney( m + save);
	}
	public void withdraw(int minus){
		int m = this.getMoney();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		
		this.setMoney( m - minus);
	}
}
class Park extends Thread{
	public void run(){
		SyncMain.myBank.deposite(3000);
		System.out.println("Deposite(3000): " + SyncMain.myBank.getMoney());
	}
}
class ParkWife extends Thread{
	public void run(){
		SyncMain.myBank.withdraw(1000);
		System.out.println("Withdraw(1000): " + SyncMain.myBank.getMoney());
	}
}

public class SyncMain{
	public static Bank myBank = new Bank();
	public static void main(String[] args) {
		Park p = new Park();
		ParkWife pw = new ParkWife();
		
		System.out.println("Begin: " + myBank.getMoney());
		
		p.start();
	
		pw.start();

	}

}
