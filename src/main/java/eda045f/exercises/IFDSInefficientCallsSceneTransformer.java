package eda045f.exercises;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

import org.javatuples.Pair;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import soot.Body;
import soot.Local;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.JimpleIFDSSolver;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

public class IFDSInefficientCallsSceneTransformer extends SceneTransformer {
	/**
	 * Iterate over all statements in the loaded Scene (i.e., the program and all
	 * dependencies)
	 *
	 * @param tester
	 */
	public static void printAll(Function<Stmt, String> tester) {
		for (SootClass cl : Scene.v().getClasses()) {
			System.out.println(cl);
			for (SootMethod m : cl.getMethods()) {
				if (m.hasActiveBody()) {
					System.out.println("  " + m);
					Body body = m.getActiveBody();
					for (Unit unit : body.getUnits()) {
						// final Unit unit = ub.getUnit();
						final String label = tester.apply((Stmt) unit);
						if (label != null) {
							System.out.println(
									"  " + label + " " + m.toString() + " " + unit.getJavaSourceStartColumnNumber());
						}
					}
				}
			}
		}
	}

	private void explore(JimpleBasedInterproceduralCFG cfg, SootMethod m, SootMethod parent, Set<SootMethod> visited) {

		Stack<Pair<SootMethod, SootMethod>> s = new Stack<Pair<SootMethod, SootMethod>>();
		s.push(new Pair<SootMethod, SootMethod>(m, parent));
		while (!s.isEmpty()) {
			Pair<SootMethod, SootMethod> p = s.pop();
			if (p.getValue1() != null)
				System.out.println(p.getValue1().getSignature() + " ------> " + p.getValue0().getSignature());
			else
				System.out.println(p.getValue0().getSignature());
			p.getValue0().getActiveBody().getUnits().forEach(u -> {
				cfg.getCalleesOfCallAt(u).forEach(cm -> {
					if (!cm.getName().equals("<clinit>")) {
						if (!visited.contains(cm)) {
							visited.add(cm);
							s.push(new Pair<SootMethod, SootMethod>(cm, p.getValue0()));
						}
					}
				});
			});
		}

	}

	@Override
	protected void internalTransform(String phase_name, Map<String, String> options) {
		// Here is how you can get a SootMethod by name:
		// This call will raise an ex
//        SootMethod m = Scene.v().getMethod("<java.util.Collection: boolean contains(java.lang.Object)>");

		SimpleCallGraph cfg = new SimpleCallGraph();
		explore(cfg, Scene.v().getMethod("<eda045f.exercises.Test1: void main(java.lang.String[])>"), null, new HashSet<>());

		IFDSTabulationProblem<Unit, Pair<Local, Set<InvokeExpr>>, SootMethod, InterproceduralCFG<Unit, SootMethod>> problem = new IFDSInefficientCalls(cfg);
////        
		JimpleIFDSSolver<Pair<Local, Set<InvokeExpr>>, InterproceduralCFG<Unit, SootMethod>> solver = new JimpleIFDSSolver<>(problem);
		System.out.println("Solve Begin");
		solver.solve();
		System.out.println("Solve End");
		solver.dumpResults();
//		solver.printStats();
//		printAll(new Function<Stmt, String>() {
//			@Override
//			public String apply(Stmt stmt) {
//				return null;
//			}
//		});
	}
}
