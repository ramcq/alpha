/*
 * Created on Feb 18, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.ucam.ned.teamalpha.algorithms.Algorithm;
import org.ucam.ned.teamalpha.animators.GraphAnimator;
import org.ucam.ned.teamalpha.animators.VectorAnimator;
import org.ucam.ned.teamalpha.queues.AnimatorQueue;
import org.ucam.ned.teamalpha.queues.GraphQueue;
import org.ucam.ned.teamalpha.queues.VectorQueue;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class StaticAlgorithmCatalog extends AlgorithmCatalog {
	private static final String[] algorithms = { "Dijkstra", "Kruskal", "BiDirBubbleSort", "InsertionSort", "QuickSort" };
	
	private static final int GRAPH = 1;
	private static final int VECTOR = 2;
	
	public class AvailableAlgorithm extends AlgorithmCatalog.AvailableAlgorithm {
		private int type;
		private Class klass;
		private String name;
		private String desc;
		
		private AvailableAlgorithm(int type, Class klass, String name, String desc) {
			this.type = type;
			this.klass = klass;
			this.name = name;
			this.desc = desc;
		}
		
		public ShellPanel getInputPanel() {
			switch (type) {
			case GRAPH:
				return new GraphInputPanel();
			case VECTOR:
				return new VectorInputPanel();
			default:
				return null;
			}
		}

		public Algorithm getAlgorithm(Object[] args) {
			Algorithm ret = null;
			
			try {
				Class[] argtypes = new Class[args.length];
				
				for (int i=0; i < args.length; i++) {
					argtypes[i] = args[i].getClass();
				}
				
				Constructor cons = klass.getConstructor(argtypes);
				ret = (Algorithm) cons.newInstance(args);
			} catch (Exception e) {
				System.err.println("failed to construct algorithm: "+ e);
			}
			
			return ret;
		}

		public ShellAnimator getAnimator() {
			switch (type) {
			case GRAPH:
				return new ShellGraphAnimator();
			case VECTOR:
				return new ShellVectorAnimator();
			default:
				return null;
			}
		}

		public String toString() {
			return name;
		}
		
		public String getDescription() {
			return desc;
		}

		public String getType() {
			switch (type) {
			case GRAPH:
				return "Graph";
			case VECTOR:
				return "Vector";
			default:
				return null;
			}
		}
		
		public AnimatorQueue getQueue(ShellAnimator anim) {
			switch (type) {
			case GRAPH:
				return new GraphQueue((GraphAnimator) anim);
			case VECTOR:
				return new VectorQueue((VectorAnimator) anim);
			default:
				return null;
			}
		}
	}

	public AlgorithmCatalog.AvailableAlgorithm[] getAvailableAlgorithms() {
		AvailableAlgorithm[] available = new AvailableAlgorithm[algorithms.length];
		Class[] emptyclass = {};
		Object[] emptyobject = {};
		
		for (int i=0; i<algorithms.length; i++) {
			try {
				Class algo = Class.forName("org.ucam.ned.teamalpha.algorithms." + algorithms[i]);
				Class supr = algo.getSuperclass();
				Class graph = Class.forName("org.ucam.ned.teamalpha.algorithms.GraphAlgorithm");
				Class vector = Class.forName("org.ucam.ned.teamalpha.algorithms.VectorAlgorithm");

				int type = 0;
				if (supr == graph) {
					type = GRAPH;
				} else if (supr == vector) {
					type = VECTOR;
				}
				
				Method getName = algo.getMethod("getName", emptyclass);
				Method getDescription = algo.getMethod("getDescription", emptyclass);
			
				String name = (String) getName.invoke(null, emptyobject);
				String desc = (String) getDescription.invoke(null, emptyobject);
			
				available[i] = new AvailableAlgorithm(type, algo, name, desc);
			} catch (Exception e) {
				System.err.println("failed to get algorithm " + algorithms[i] + ": " + e);
			}
		}

		return available;
	}
	
	public static void main(String[] args) {
		AlgorithmCatalog a = new StaticAlgorithmCatalog();
		AlgorithmCatalog.AvailableAlgorithm[] as = a.getAvailableAlgorithms();
		for (int i = 0; i < as.length; i++) {
			if (as[i] != null)
				System.out.println(as[i].getType() + ": " + as[i] + ": " + as[i].getDescription());
		}
	}
}