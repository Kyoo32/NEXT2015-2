import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class A{
	public void m(){
		System.out.println("A.m()");
	}
}

class B extends A{
	public void m(){
		System.out.println("B.m()");
	}
}

public class ShowInfo {
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		/*
		Class c = Integer.class;
		System.out.println(c.getName());
		
		Constructor[] cons = c.getConstructors();
		
		for(Constructor con : cons){
			System.out.println(con);
		}
		
		Method[] meths= c.getMethods();
		for(Method meth : meths){
			System.out.println(meth);
		}
		*/
		
		Class c = Class.forName("B");
		//replacable
		
		Object obj = c.newInstance();
		System.out.println(obj);
	}
}	
