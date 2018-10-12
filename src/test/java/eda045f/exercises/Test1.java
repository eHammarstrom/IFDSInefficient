package eda045f.exercises;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Test1 {
	public static void main (String[] args) {
        Test1 t = new Test1();
        ArrayList<String> al = new ArrayList<String>();
        al.contains("1");
        al.add("1");
        al.remove("1");
        t.test1("MAIN", al);
        al.add("1");
        t.test4(al);
        Set<String> x = new HashSet<>();
        t.test5(x);
        Collection<String> xs = new ArrayList<>();
        t.test6(xs);
        t.test7(al);
        List<String> ys = new LinkedList<>();
        t.test8(ys);
        t.test9(ys);
        t.test2();
        
        Collection<String> c = t.coll();
        c.contains("COLL_MAIN");
        
        Collection<String> c_set = t.coll2();
        c_set.contains("COLL_MAIN SET");
	}
    void test3(ArrayList<String> l) {
        test1("TEST3", l);
    }
    void test1(String test, ArrayList<String> l) {
        l.contains(test);
    }
    void test2() {
        ArrayList<String> al = new ArrayList<String>();
        al.contains("TEST2");
    }
	void test4(Collection<String> c) {
		c.contains("TEST4");
	}
	
	void test5(Collection<String> c) {
		c.contains("TEST5");
	}
	
	void test6(Collection<String> c) {
		c.contains("TEST6");
	}
	
	void test7(List<String> c) {
		c.contains("TEST7");
	}
	
	void test8(Collection<String> c) {
		c.contains("TEST8");
	}
	
	void test9(Collection<String> c) {
		c.contains("TEST9");
		test10(c);
	}
	
	void test10(Collection<String> c) {
		c.contains("TEST10");
	}
	
	Collection<String> coll() {
		Collection<String> a = new ArrayList<>();
		a.remove("COLL_COLL");
		return a;
	}
	
	Collection<String> coll2() {
		Collection<String> a = new ArrayList<>();
		a.remove("COLL_COLL22");
		a = new HashSet<>();
		return a;
	}
}
