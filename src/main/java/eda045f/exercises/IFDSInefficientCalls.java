package eda045f.exercises;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import heros.DefaultSeeds;
import heros.FlowFunction;
import heros.FlowFunctions;
import heros.InterproceduralCFG;
import heros.flowfunc.Identity;
import heros.flowfunc.KillAll;
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
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.ide.DefaultJimpleIFDSTabulationProblem;

public class IFDSInefficientCalls
		extends DefaultJimpleIFDSTabulationProblem<Value, InterproceduralCFG<Unit, SootMethod>> {
	/**
	 * The IFDS alg. is used to propagate what instances of any Collection is of concrete type
	 * ArrayList or LinkedList.
	 * When such a concrete type is used to invoke ''remove'' or ''contains'', the invocation
	 * statement is added to the set ''foundStmts''.
	 */
	private Set<Stmt> foundStmts = new HashSet<Stmt>();
	public IFDSInefficientCalls(InterproceduralCFG<Unit, SootMethod> icfg) {
		super(icfg);
	}

	@Override
	protected FlowFunctions<Unit, Value, SootMethod> createFlowFunctionsFactory() {
		return new IFDSInefficientFlowFunctions();
	}

	@Override
	protected Value createZeroValue() {
		return new JimpleLocal("ZERO", NullType.v());
	}

	/**
	 * 0 holds at the beginning of all method calls. Property associated with a
	 * globally scoped variable holds at the beginning of every method call.
	 * zeroValue assures that createZeroValue only called once (singleton pattern).
	 */
	@Override
	public Map<Unit, Set<Value>> initialSeeds() {
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
	private static boolean isConcreteDef(Unit u) {
		if (!(u instanceof DefinitionStmt))
			return false;
		DefinitionStmt d = (DefinitionStmt) u;
		for (int i = 0; i != targetDefs.length; ++i)
			if (isConcreteListType(d.getRightOp()))
				return true;
		return false;
	}

	private static boolean isConcreteListType(Value v) {
		for (int i = 0; i != targetDefs.length; ++i)
			if (targetDefs[i].equals(v.getType().toString()))
				return true;
		return false;
	}

	private static boolean isParameter(Value v) {
		return v.toString().length() > 10 && v.toString().subSequence(0, 10).equals("@parameter");
	}

	private final class IFDSInefficientFlowFunctions
			implements FlowFunctions<Unit, Value, SootMethod> {
		@Override
		public FlowFunction<Value> getNormalFlowFunction(Unit curr, Unit succ) {
			if(!(curr instanceof DefinitionStmt)) return Identity.v();
			DefinitionStmt d = (DefinitionStmt) curr;
			
			return new FlowFunction<Value>() {
				@Override
				public Set<Value> computeTargets(Value s) {
					/**
					 * Propagate type value from concrete to abstract: r1 <- r2 = s
					 * Let s = (v, Dv).
					 * If r2 == v then r2 implies (r1, Dv).
					 * This enables a more abstract type to be inferred to 
					 */
					if (s.equivTo(d.getRightOp())) {
						LinkedHashSet<Value> lhs = new LinkedHashSet<>();
						lhs.add(d.getLeftOp());
						lhs.add(s);
						return lhs;
					}
					
					if(!isConcreteDef(curr)) return Collections.singleton(s);
					
					/**
					 * Introduces a new Concrete Type.
					 * Will have already propagated the information if RHS is a parameter.
					 */
					if (s == zeroValue() && !isParameter(d.getRightOp())) {
						System.out.println("NEW CONCRETE FACT: " + d.getLeftOp());
						return Collections.singleton(d.getLeftOp());
					}
					return Collections.singleton(s);
				}
			};
		}

		@Override
		public FlowFunction<Value> getCallFlowFunction(Unit callStmt, SootMethod destinationMethod) {
			//System.out.println("CALL: " + destinationMethod);
			Stmt stmt = (Stmt) callStmt;
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			final List<Value> args = invokeExpr.getArgs();
			final List<Local> localArguments = new ArrayList<Local>();
			for (Value value : args) 
				if (value instanceof Local) 
					localArguments.add((Local) value);
			/**
			 * Have invocation: g(x1, x2, ..., xn) args = [x1, x2, ..., xn] Form
			 * FlowFunction f :: D -> { D }
			 * f s | s in args = { s } | otherwise = {}
			 */
			return new FlowFunction<Value>() {
				@Override
				public Set<Value> computeTargets(Value s) {
					if (destinationMethod.getName().equals("<clinit>")
							|| destinationMethod.getSubSignature().equals("void run()"))
						return Collections.emptySet();
					for (Local la : localArguments) {
						if (s.equivTo(la)) {
							int paramIndex = args.indexOf(s);
							
							/**
							 * Argument propagates to the @parameter_x in the destinationMethod.
							 * When use the param on the RHS of an assignment, the fact that it is
							 * a concrete type gets propagated.
							 */
				            Value v = new EquivalentValue(Jimple.v().newParameterRef(
				            		destinationMethod.getParameterType(paramIndex), paramIndex));
							return Collections.singleton(v);
						}
					}
					return Collections.emptySet();
				}
			};
		}

		@Override
		public FlowFunction<Value> getCallToReturnFlowFunction(Unit callSite, Unit returnSite) {
			return new FlowFunction<Value>() {
				@Override
				public Set<Value> computeTargets(Value s) {
					/**
					 * Call to contains / remove
					 */
					if (isTargetInvokation(callSite)) {
						InstanceInvokeExpr iie = (InstanceInvokeExpr) ((InvokeStmt) callSite).getInvokeExpr();
						/**
						 * r.contains(..) s.t. r == v
						 */
						if (iie.getBase().equivTo(s))
							foundStmts.add((Stmt)callSite);
					}
					return Collections.singleton(s);
				}
			};
		}

		@Override
		public FlowFunction<Value> getReturnFlowFunction(Unit callSite, SootMethod calleeMethod,
				Unit exitStmt, Unit returnSite) {
			if(exitStmt instanceof ReturnVoidStmt) return KillAll.v();
			if(!(callSite instanceof DefinitionStmt)) return KillAll.v();
			DefinitionStmt d = (DefinitionStmt) callSite;
			
			return new FlowFunction<Value>() {
				@Override
				public Set<Value> computeTargets(Value s) {
					if(!(exitStmt instanceof ReturnStmt)) return Collections.emptySet();
					ReturnStmt ret = (ReturnStmt) exitStmt;
					if(ret.getOp().equivTo(s)) return Collections.singleton(d.getLeftOp());
					return Collections.emptySet();
				}
			};
		}
	}
	
	public void printResult() {
		foundStmts.forEach(s -> System.out.println(s + " at " + s.getJavaSourceStartLineNumber()));
	}
}
