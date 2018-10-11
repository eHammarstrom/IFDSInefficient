package eda045f.exercises;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;

import heros.DefaultSeeds;
import heros.FlowFunction;
import heros.FlowFunctions;
import heros.InterproceduralCFG;
import heros.flowfunc.Identity;
import soot.Local;
import soot.NullType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.ide.DefaultJimpleIFDSTabulationProblem;

public class IFDSInefficientCalls
		extends DefaultJimpleIFDSTabulationProblem<Pair<Local, Set<InvokeExpr>>, InterproceduralCFG<Unit, SootMethod>> {
	/**
	 * Cosntructs an IFDS tabulator
	 * 
	 * @param icfg
	 */
	public IFDSInefficientCalls(InterproceduralCFG<Unit, SootMethod> icfg) {
		super(icfg);
		System.out.println("IFDSInefficientCalls: ");
	}

	@Override
	protected FlowFunctions<Unit, Pair<Local, Set<InvokeExpr>>, SootMethod> createFlowFunctionsFactory() {
		System.out.println("createFlowFunctionsFactory: ");
		return new IFDSInefficientFlowFunctions();
	}

	@Override
	protected Pair<Local, Set<InvokeExpr>> createZeroValue() {
		System.out.println("createZeroValue: ");
		return new Pair<Local, Set<InvokeExpr>>(new JimpleLocal("ZERO", NullType.v()), new LinkedHashSet<>());
	}

	/**
	 * 0 holds at the beginning of all method calls.
	 * Property associated with a globally scoped variable holds at the beginning of every method call.
	 * 
	 * zeroValue assures that createZeroValue only called once (singleton pattern).
	 */
	@Override
	public Map<Unit, Set<Pair<Local, Set<InvokeExpr>>>> initialSeeds() {
		System.out.println("initialSeeds: ");
		return DefaultSeeds.make(Collections.singleton(Scene.v().getMainMethod().getActiveBody().getUnits().getFirst()), zeroValue());
	}
	
	private static final String[] targetInvokations = {
		"<java.util.ArrayList: boolean remove(java.lang.Object)>"
	   ,"<java.util.ArrayList: boolean contains(java.lang.Object)>"
	   ,"<java.util.LinkedList: boolean remove(java.lang.Object)>"
	   ,"<java.util.LinkedList: boolean contains(java.lang.Object)>"
	};
	private static boolean isTargetInvokation(Unit u) {
		if(!(u instanceof InvokeStmt)) return false;
		InvokeStmt is = (InvokeStmt)u;
		System.out.println("INVOKE: " + is.getInvokeExpr().getMethod().getSignature());
		for(int i = 0; i != targetInvokations.length; ++i) 
			if(is.getInvokeExpr().getMethod().getSignature().equals(targetInvokations[i])) return true;
		return false;
	}
	
	private static final String[] targetDefs = {
			"java.util.ArrayList"
		   ,"java.util.LinkedList"
    };
	private static boolean isDef(Unit u) {
		if(!(u instanceof DefinitionStmt)) return false;
		DefinitionStmt d = (DefinitionStmt)u;
		for(int i = 0; i != targetDefs.length; ++i)
			if(targetDefs[i].equals(d.getLeftOp().getType().toString()))return true;
		return false;
	}

	private final class IFDSInefficientFlowFunctions implements FlowFunctions<Unit, Pair<Local, Set<InvokeExpr>>, SootMethod> {
		@Override
		public FlowFunction<Pair<Local, Set<InvokeExpr>>> getNormalFlowFunction(Unit curr, Unit succ) {
			System.out.println("NormalFlow: " + curr + " -> " + succ);
			if(!isDef(curr)) return Identity.v();
			
			/**
			 * Will not be an invocation statement here (all invocations in getCallFlow).
			 */
			if(!(curr instanceof DefinitionStmt)) return Identity.v();
			DefinitionStmt d = (DefinitionStmt) curr;

			/**
			 * Have a def. 
			 * 	d: [x <- ArrayList / LinkedList]
			 * 
			 * Form FlowFunction f :: D -> { D }
			 * f 0 = { (x, {d} ) }
			 * f y  
			 * 	   | y == x    = {}
			 *     | otherwise = {y}
			 */
			return new FlowFunction<Pair<Local,Set<InvokeExpr>>>() {
				@Override
				public Set<Pair<Local, Set<InvokeExpr>>> computeTargets(Pair<Local, Set<InvokeExpr>> s) {
					System.out.println("Flow Func");
					if(s == zeroValue())
						return Collections.singleton(new Pair<Local, Set<InvokeExpr>>((Local)d.getLeftOp(), new LinkedHashSet<InvokeExpr>()));
					if(d.getLeftOp().equivTo(s.getValue0())) return Collections.emptySet();
					return Collections.singleton(s);
				}
			};
		}
		
		@Override
		public FlowFunction<Pair<Local, Set<InvokeExpr>>> getCallFlowFunction(Unit callStmt, SootMethod destinationMethod) {
			System.out.println("CallFlow: " + callStmt + " -> " + destinationMethod.getBytecodeParms());
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			if(!isTargetInvokation(callStmt)) return Identity.v();
			System.out.println("GOT TARGET INVOCATION");
//			InvokeStmt is = (InvokeStmt) callStmt;
//			
//			return new FlowFunction<Pair<Local,Set<InvokeExpr>>>() {
//				@Override
//				public Set<Pair<Local, Set<InvokeExpr>>> computeTargets(Pair<Local, Set<InvokeExpr>> s) {
//					System.out.println("Flow Func");
//					if(s == zeroValue())
//						return Collections.singleton(new Pair<Local, Set<InvokeExpr>>((Local)d.getLeftOp(), new LinkedHashSet<InvokeExpr>()));
//					if(d.getLeftOp().equivTo(s.getValue0())) return Collections.emptySet();
//					return Collections.singleton(s);
//				}
//			};
			return Identity.v();
		}

		@Override
		public FlowFunction<Pair<Local, Set<InvokeExpr>>> getCallToReturnFlowFunction(Unit callSite, Unit returnSite) {
			System.out.println("CallToReturnFlow: " + returnSite + " -> " + callSite);
			return Identity.v();
		}

		@Override
		public FlowFunction<Pair<Local, Set<InvokeExpr>>> getReturnFlowFunction(Unit callSite, SootMethod calleeMethod, Unit exitStmt,
				Unit returnSite) {
			System.out.println("ReturnFlow: " + "[" + callSite + ", " + calleeMethod + ", " + exitStmt + ", " + returnSite + "]");
			return Identity.v();
		}
	}
}
