package eda045f.exercises;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.ide.DefaultJimpleIFDSTabulationProblem;
import soot.jimple.toolkits.ide.JimpleIFDSSolver;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;

public class Stub2 {

	public static final boolean DEBUG = true;

	public static void main(String[] args) {
		for (int i = 0; i != args.length; i++)
			System.out.println("Arg: " + args[i]);
		if (args.length < 2) {
			System.out.println("Usage: java " + Stub2.class.getCanonicalName() + " <classpath> <main-classes>");
			System.exit(1);
		}

		configure(args[0]);
		registerEntryPoints(args[1].split(":"));

		PackManager.v().getPack("wjtp")
				.add(new Transform("wjtp.myanalysis", new IFDSInefficientCallsSceneTransformer()));
		PackManager.v().runPacks();
	}

	public static void registerEntryPoints(String[] entrypoints) {
		for (int i = 0; i != entrypoints.length; i++)
			System.out.println("Entry: " + entrypoints[i]);

		List<SootMethod> entry_points = new ArrayList<>();
		if (entrypoints.length == 0) {
			throw new RuntimeException("No entry points specified");
		}
		if (DEBUG) {
			System.out.println("Entry points (" + entrypoints.length + "):");
		}
		for (String entrypoint : entrypoints) {
			SootClass c = Scene.v().forceResolve(entrypoint, SootClass.BODIES);
			c.setApplicationClass();
			Scene.v().loadNecessaryClasses();
			SootMethod method = c.getMethodByName("main");
			entry_points.add(method);
			if (DEBUG) {
				System.out.println("  " + method);
			}
		}
		Scene.v().setEntryPoints(entry_points);
		Scene.v().getEntryPoints().forEach(sm -> System.out.println("Set Entry: " + sm.getSignature()));
	}

	/**
	 * Configures Soot for whole-program analysis of a given classpath
	 *
	 * @param classpath
	 */
	public static void configure(String classpath) {
		System.out.println("Classpath: " + classpath);
		Options.v().set_verbose(false);
		Options.v().set_keep_line_number(true);
		Options.v().set_src_prec(Options.src_prec_class);
		Options.v().set_soot_classpath(classpath);
		Options.v().set_prepend_classpath(true);

		Options.v().set_output_format(Options.output_format_none); // or _xml?
		PhaseOptions.v().setPhaseOption("bb", "off");
		PhaseOptions.v().setPhaseOption("bop", "off");
		PhaseOptions.v().setPhaseOption("db", "off");
		PhaseOptions.v().setPhaseOption("gb", "off");
		// PhaseOptions.v().setPhaseOption("cg", "off");

		Options.v().set_whole_program(true); // whole-program analysis

		System.out.println("Soot CP: " + Scene.v().getSootClassPath());

		setAdvancedCallgraph(true);
	}

	/**
	 * Switches from CHA to RTA or VTA callgraph analysis
	 *
	 * By default, Soot uses Class Hierarchy Analysis (CHA) to build call graphs.
	 * This method enables Rapid Type Analysis (RTA) or Variable Type Analysis
	 * (VTA).
	 *
	 * @param vta
	 */
	public static void setAdvancedCallgraph(boolean vta) {
		// Enable SPARK framework
		PhaseOptions.v().setPhaseOption("cg.cha", "enabled:false");
		PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");
		PhaseOptions.v().setPhaseOption("cg.spark", "on-fly-cg:false"); // disabled Spark's advanced analysis
		PhaseOptions.v().setPhaseOption("cg.spark", (vta ? "vta" : "rta") + ":true");
	}
}
