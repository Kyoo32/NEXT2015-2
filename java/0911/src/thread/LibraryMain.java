package thread;

//import java.util.ArrayList;
import java.util.Vector;

class Library{
	public Vector<String> nextShelf = new Vector<String>();
	
	public Library(){
		nextShelf.add("apple");
		nextShelf.add("bpple");
		nextShelf.add("cpple");
	//	nextShelf.add("dpple");
	//	nextShelf.add("epple");
	}
	
	public synchronized String lendBook() throws InterruptedException{
		
		Thread t = Thread.currentThread();
		while(nextShelf.size() == 0){ //v.s. if
			System.out.println("["+t.getName()+"]" + " is waiting.");
			this.wait(); //this is wait
			//return null;
		}
		
		String name = nextShelf.remove(0);
		System.out.println("["+t.getName()+"]" + " lends "+ name);
		return name;
	}
	
	public synchronized void returnBook(String name){
		Thread t = Thread.currentThread();
		System.out.println("["+t.getName()+"]" + " returns "+ name);
		nextShelf.add(name);
		this.notifyAll();//v.s. notify
	}
}
class Student extends Thread{
	public void run(){
		//while( book size is not 0, : polling)
		try {
			String title = LibraryMain.library.lendBook();
			if(title == null){
				System.out.println("[" + this.getName() +"] go back home" );
				return;
			}
			sleep(3000);
			LibraryMain.library.returnBook(title);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
public class LibraryMain {

	public static Library library = new Library();
	public static void main(String[] args) {
		Student s1 = new Student();
		Student s2 = new Student();
		Student s3 = new Student();
		Student s4 = new Student();
		Student s5 = new Student();
		Student s6 = new Student();
		
		s1.start();
		s2.start();
		s3.start();
		s5.start();
		s4.start();
		s6.start();

	}

}
