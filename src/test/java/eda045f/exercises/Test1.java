package eda045f.exercises;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Test1 {
	public static void main (String[] args) {
        Test1 t = new Test1();
        ArrayList<String> al = new ArrayList<String>();
        al.contains("1");
        al.add("1");

        /* int z = 1; */
        /* System.out.println(z); */

        al.remove("1");
        /* al.remove("2"); */

        t.test1("MAIN", al);
        al.add("1");
//        t.test2();
//        t.test3(al);
        t.test4(al);
        
//        Set<String> x = new HashSet<>();
//        t.test5(x);
        
        /* int y = x(); */
        /* System.out.println(y); */
	}

    void test3(ArrayList<String> l) {
        test1("TEST3", l);
    }

    void test1(String test, ArrayList<String> l) {
        l.contains(test);
    }

    void test2() {
        ArrayList<String> al = new ArrayList<String>();
        al.contains("2");
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
	
	/**
	 * 
	 */
	void test4(Collection<String> c) {
		c.contains("TEST4");
	}
	
	void test5(Collection<String> c) {
		c.contains("TEST5");
	}
}
