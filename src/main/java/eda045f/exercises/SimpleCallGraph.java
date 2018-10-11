package eda045f.exercises;

import java.util.Collection;
import java.util.stream.Collectors;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

public class SimpleCallGraph extends JimpleBasedInterproceduralCFG {
	private String[] targetClasses = {"eda045f.exercises.Test1", "java.util.ArrayList", "java.util.LinkedList"};
	public SimpleCallGraph() {
		super(false,false);
	}

	@Override
	public Collection<SootMethod> getCalleesOfCallAt(Unit u) {
		return super.getCalleesOfCallAt(u).stream().filter(sm -> {
			for(int i = 0; i != targetClasses.length; ++i)
				if(sm.getDeclaringClass().getName().equals(targetClasses[i]))
					return true;
			return false;
		}).collect(Collectors.toList());
//		return super.getCalleesOfCallAt(u);
	}
}
