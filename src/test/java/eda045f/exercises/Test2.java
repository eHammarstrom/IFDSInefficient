package eda045f.exercises;

import java.util.ArrayList;

public class Test2 {
	public static void main (String[] args) {
        Test2 t = new Test2();
        ArrayList<String> al = new ArrayList<String>();
        if(t.condition()) {
	        al.contains("1");
	        al.add("1");
	        al.remove("1");
	        t.test1("MAIN", al);
	        al.add("1");
        }
	}
    void test3(ArrayList<String> l) {
        test1("TEST3", l);
    }
    void test1(String test, ArrayList<String> l) {
        l.contains(test);
    }
    
    boolean condition() { return false; }
}
