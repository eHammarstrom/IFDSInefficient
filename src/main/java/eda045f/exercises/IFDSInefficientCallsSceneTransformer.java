package eda045f.exercises;

import java.util.Map;
import java.util.function.Function;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import soot.Body;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.JimpleIFDSSolver;

public class IFDSInefficientCallsSceneTransformer extends SceneTransformer {
	/**
	 * Iterate over all statements in the loaded Scene (i.e., the program and all
	 * dependencies)
	 * @param tester
	 */
	public static void printAll(Function<Stmt, String> tester) {
		for (SootClass cl : Scene.v().getClasses()) {
			for (SootMethod m : cl.getMethods()) {
				if (m.hasActiveBody()) {
					Body body = m.getActiveBody();
					for (Unit unit : body.getUnits()) {
						final String label = tester.apply((Stmt) unit);
						if (label != null)
							System.out.println(label + " " + m.toString() + " " + unit.getJavaSourceStartLineNumber());
					}
				}
			}
		}
	}

	@Override
	protected void internalTransform(String phase_name, Map<String, String> options) {
		SimpleCallGraph cfg = new SimpleCallGraph();
		for(SootClass sc : Scene.v().getApplicationClasses()) {
			if(Stub2.DEBUG) 
				System.out.println("ENTRY: " + sc.getMethodByName("main"));
			
			/**
			 * Tells soot what class to use as the main entry point
			 */
			Scene.v().setMainClass(sc);

			/**
			 * Construct Tabulation Problem
			 */
			IFDSTabulationProblem<Unit, Value, SootMethod, InterproceduralCFG<Unit, SootMethod>> problem = new IFDSInefficientCalls(cfg);

			/**
			 * Construct Solver
			 */
			JimpleIFDSSolver<Value, InterproceduralCFG<Unit, SootMethod>> solver = new JimpleIFDSSolver<>(problem);
			if(Stub2.DEBUG)
				System.out.println("Solve Begin");
			solver.solve();
			if(Stub2.DEBUG) {
				System.out.println("Solve End");
		        System.out.println("Propagation Count: " + solver.propagationCount);
		        solver.dumpResults();
			}
			
			/**
			 * Print results of solver
			 */
			printAll(new Function<Stmt, String>() {
				@Override
				public String apply(Stmt stmt) {
					if(((IFDSInefficientCalls)problem).getFoundStmts().contains(stmt)) {
						return "IFDSLIST " + ((InvokeStmt)stmt).getInvokeExpr().getMethod().getSignature() + " ";
					}
					else
					{
						return null;
					}
				}
			});
		}
	}
}
