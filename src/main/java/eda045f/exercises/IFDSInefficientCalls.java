package eda045f.exercises;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;

import heros.DefaultSeeds;
import heros.FlowFunction;
import heros.FlowFunctions;
import heros.InterproceduralCFG;
import heros.flowfunc.Identity;
import soot.EquivalentValue;
import soot.Local;
import soot.NullType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.ide.DefaultJimpleIFDSTabulationProblem;

public class IFDSInefficientCalls
		extends DefaultJimpleIFDSTabulationProblem<Pair<Value, Set<Stmt>>, InterproceduralCFG<Unit, SootMethod>> {

	/**
	 * Cosntructs an IFDS tabulator
	 * 
	 * @param icfg
	 */
	public IFDSInefficientCalls(InterproceduralCFG<Unit, SootMethod> icfg) {
		super(icfg);
		//System.out.println("IFDSInefficientCalls: ");
	}

	@Override
	protected FlowFunctions<Unit, Pair<Value, Set<Stmt>>, SootMethod> createFlowFunctionsFactory() {
		//System.out.println("createFlowFunctionsFactory: ");
		return new IFDSInefficientFlowFunctions();
	}

	@Override
	protected Pair<Value, Set<Stmt>> createZeroValue() {
		//System.out.println("createZeroValue: ");
		return new Pair<Value, Set<Stmt>>(new JimpleLocal("ZERO", NullType.v()), new LinkedHashSet<>());
	}

	/**
	 * 0 holds at the beginning of all method calls. Property associated with a
	 * globally scoped variable holds at the beginning of every method call.
	 * 
	 * zeroValue assures that createZeroValue only called once (singleton pattern).
	 */
	@Override
	public Map<Unit, Set<Pair<Value, Set<Stmt>>>> initialSeeds() {
		//System.out.println("initialSeeds: ");
		return DefaultSeeds.make(Collections.singleton(Scene.v().getMainMethod().getActiveBody().getUnits().getFirst()),
				zeroValue());
	}

	private static final String[] targetInvokations = { "contains", "remove" };

	private static boolean isTargetInvokation(Unit u) {
		if (!(u instanceof InvokeStmt))
			return false;
		InvokeStmt is = (InvokeStmt) u;
		for (int i = 0; i != targetInvokations.length; ++i)
			if (is.getInvokeExpr().getMethod().getName().equals(targetInvokations[i]))
				return true;
		return false;
	}

	private static final String[] targetDefs = { "java.util.ArrayList", "java.util.LinkedList" };
	private static boolean isDef(Unit u) {
		if (!(u instanceof DefinitionStmt))
			return false;
		DefinitionStmt d = (DefinitionStmt) u;
		//System.out.println("Test: " + d.getLeftOp().getType() + " <- " + d.getRightOp().getType());
		for (int i = 0; i != targetDefs.length; ++i)
			if (isListType(d.getRightOp()))
				return true;
		return false;
	}

	private static boolean isListType(Value v) {
		for (int i = 0; i != targetDefs.length; ++i)
			if (targetDefs[i].equals(v.getType().toString()))
				return true;
		return false;
	}

	private static boolean containsEquiv(Collection<Value> l, Value v) {
		for (Value w : l)
			if (w.equivTo(v))
				return true;
		return false;
	}

	private static boolean isParameter(Value v) {
		return v.toString().length() > 10 && v.toString().subSequence(0, 10).equals("@parameter");
	}

	private final class IFDSInefficientFlowFunctions
			implements FlowFunctions<Unit, Pair<Value, Set<Stmt>>, SootMethod> {
		@Override
		public FlowFunction<Pair<Value, Set<Stmt>>> getNormalFlowFunction(Unit curr, Unit succ) {
//			//System.out.println("NormalFlow: " + curr + " -> " + succ);
//			if (!isDef(curr))
//				return Identity.v();
			if(!(curr instanceof DefinitionStmt)) return Identity.v();

//			if(!(curr instanceof DefinitionStmt)) return Identity.v();
			DefinitionStmt d = (DefinitionStmt) curr;

			/**
			 * Have a def. d: [x <- ArrayList / LinkedList]
			 * 
			 * Form FlowFunction f :: D -> { D } f 0 = { (x, {} ) } f y | y == x = {} |
			 * otherwise = {y}
			 */
			return new FlowFunction<Pair<Value, Set<Stmt>>>() {
				@Override
				public Set<Pair<Value, Set<Stmt>>> computeTargets(Pair<Value, Set<Stmt>> s) {
					System.out.println("NORMAL: " + d + ", " + s);
					/**
					 * Steal value in:
					 * 	r1 <- r2
					 * Let s = (v, Dv).
					 * If r2 == v then r2 implies (r1, Dv).
					 */
					if (s.getValue0().equivTo(d.getRightOp())) {
						System.out.println("DO STEAL: " + s.getValue1() + " to " + d.getLeftOp());
						return Collections.singleton(new Pair<Value, Set<Stmt>>(d.getLeftOp(), s.getValue1()));
					}
					
					if(!isDef(curr)) return Collections.singleton(s);
					
					/**
					 * Will have stolen value in:
					 * 	r1 <- r2
					 * If r2 is a param, thus do not want to generate the value again (redundant).
					 */
					if (s == zeroValue() && !isParameter(d.getRightOp())) {
						System.out.println("ArrayList Def: " + s + " at " + curr);
						return Collections
								.singleton(new Pair<Value, Set<Stmt>>((Value) d.getLeftOp(), new HashSet<Stmt>()));
					}
					
					if (d.getLeftOp().equivTo(s.getValue0())) {
							System.out.println("KILL OLD DEF" + s);
							return Collections.emptySet();
					}
					return Collections.singleton(s);
				}
			};
		}

		@Override
		public FlowFunction<Pair<Value, Set<Stmt>>> getCallFlowFunction(Unit callStmt, SootMethod destinationMethod) {
			//System.out.println("CALL: " + destinationMethod);
			Stmt stmt = (Stmt) callStmt;
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			final List<Value> args = invokeExpr.getArgs();
			final List<Local> localArguments = new ArrayList<Local>();
			for (Value value : args) {
				if (value instanceof Local) {
					localArguments.add((Local) value);
				}
			}
			/**
			 * Have invocation: g(x1, x2, ..., xn) args = [x1, x2, ..., xn] Form
			 * FlowFunction f :: D -> { D }
			 * f s | s in args = { s } | otherwise = {}
			 */
			return new FlowFunction<Pair<Value, Set<Stmt>>>() {
				@Override
				public Set<Pair<Value, Set<Stmt>>> computeTargets(Pair<Value, Set<Stmt>> s) {
					if (destinationMethod.getName().equals("<clinit>")
							|| destinationMethod.getSubSignature().equals("void run()"))
						return Collections.emptySet();
					System.out.println("CALL TO: " + destinationMethod + ", FLOW: " + s);
					for (Local localArgument : localArguments) {
						System.out.println("Local: " + localArgument + " is equiv: " + s.getValue0().equivTo(localArgument));
						if (s.getValue0().equivTo(localArgument)) {
							int paramIndex = args.indexOf(s.getValue0());
				            Pair<Value, Set<Stmt>> pair = new Pair<Value, Set<Stmt>>(
				                    new EquivalentValue(
				                        Jimple.v().newParameterRef(destinationMethod.getParameterType(paramIndex), paramIndex)),
				                    s.getValue1());
				            
							System.out.println("GENERATE CALL: " + s + " for " + destinationMethod);
							return Collections.singleton(pair);
						}
					}
					return Collections.emptySet();
				}
			};
		}

		@Override
		public FlowFunction<Pair<Value, Set<Stmt>>> getCallToReturnFlowFunction(Unit callSite, Unit returnSite) {
//			//System.out.println("CallToReturnFlow: " + callSite + " -> " + returnSite);
			Stmt stmt = (Stmt) callSite;
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			Set<Value> args = new HashSet<>(invokeExpr.getArgs());

			return new FlowFunction<Pair<Value, Set<Stmt>>>() {
				@Override
				public Set<Pair<Value, Set<Stmt>>> computeTargets(Pair<Value, Set<Stmt>> s) {
					/**
					 * Call to contains / remove on ArrayList / LinkedList
					 */
					if (isTargetInvokation(callSite)) {
						InstanceInvokeExpr iie = (InstanceInvokeExpr) ((InvokeStmt) callSite).getInvokeExpr();

						/**
						 * Let s = (v, Dv). r.contains(..) s.t. r != v
						 */
						if (!iie.getBase().equivTo(s.getValue0()))
							return Collections.singleton(s);
						
						System.out.println("ADD NEW SET: " + callSite + ", " + s);
						/**
						 * r.contains(..) s.t. r == v
						 */
						Set<Stmt> snew = new HashSet<>(s.getValue1());
						snew.add((Stmt) callSite);
						return Collections.singleton(new Pair<Value, Set<Stmt>>(s.getValue0(), snew));
					}

					/**
					 * Let s = (v, Dv). Have call g(..., v, ...)
					 * Will generate s with updates on getReturnFlow, thus kill here.
					 * 
					 * NOTE: The returned value does not get equivalent to the local value since do copy of local values
					 * inside target 
					 */
//					if (containsEquiv(args, s.getValue0())) {
//						return Collections.emptySet();
//					}
//
//					/**
//					 * Let s = (v, Dv). Have call x = g(..., v, ...)
//					 */
//					if (stmt instanceof DefinitionStmt) {
//						DefinitionStmt d = (DefinitionStmt) stmt;
//
//						/**
//						 * x == v
//						 */
//						if (d.getLeftOp().equivTo(s.getValue0())) {
//							return Collections.emptySet();
//						}
//					}
					return Collections.singleton(s);
				}
			};
		}

		@Override
		public FlowFunction<Pair<Value, Set<Stmt>>> getReturnFlowFunction(Unit callSite, SootMethod calleeMethod,
				Unit exitStmt, Unit returnSite) {
//			//System.out.println("ReturnFlow: " + "[" + callSite + ", " + calleeMethod + ", " + exitStmt + ", " + returnSite + "]");
			return new FlowFunction<Pair<Value, Set<Stmt>>>() {
				@Override
				public Set<Pair<Value, Set<Stmt>>> computeTargets(Pair<Value, Set<Stmt>> s) {
					System.out.println("RETURN: " + s);
					return Collections.singleton(s);
				}
			};
		}
	}
}
