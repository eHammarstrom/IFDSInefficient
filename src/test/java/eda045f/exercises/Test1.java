package eda045f.exercises;

import java.util.ArrayList;

public class Test1 {
	public static void main (String[] args) {
        ArrayList<String> al = new ArrayList<String>();
        al.contains("1");
        al.add("1");
        al.remove("1");
        
        int y = x();
        System.out.println(y);
	}
	
//	public ArrayList<String> getList() {
//		return new ArrayList<>();
//	}
//	
//	public HashSet<String> getSet() {
//		return new HashSet<String>();
//	}
//	
//	void indirection() {
//		Collection<String> c = getList();
//		System.out.println(c.contains("2"));
//	}
	static int x() { return 5; };
	
	
}