/*
 * Created on Feb 18, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import javax.swing.JPanel;

import org.ucam.ned.teamalpha.algorithms.GraphAlgorithm;
import org.ucam.ned.teamalpha.algorithms.Algorithm;
import org.ucam.ned.teamalpha.algorithms.VectorAlgorithm;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.queues.AnimatorQueue;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class StaticAlgorithmCatalog extends AlgorithmCatalog {
	private static final String[] algorithms = { "Dijkstra", "InsertionSort", "Kruskal", "QuickSort", "RadixSort" };
	
	private static final int GRAPH = 0;
	private static final int VECTOR = 1;
	
	public class AvailableAlgorithm extends AlgorithmCatalog.AvailableAlgorithm {
		private int type;
		private String name;
		private String desc;
		
		private AvailableAlgorithm(int type, String name, String desc) {
			this.type = type;
			this.name = name;
			this.desc = desc;
		}
		
		public Algorithm getAlgorithm(JPanel panel) {
			switch (type) {
				case GRAPH:
					return new GraphAlgorithm;
					break;
				case VECTOR:
					break;
				default:
					break;
			}
			return null;
		}

		public Animator getAnimator(JPanel panel) {
			
		}

		public String getDescription() {
			return desc;
		}

		public String getName() {
			return name;
		}

		public AnimatorQueue getQueue(Animator anim) {
			
			// TODO Auto-generated method stub
			return null;
		}
}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.shell.AlgorithmCatalog#getAvailableAlgorithms()
	 */
	public AlgorithmCatalog.AvailableAlgorithm[] getAvailableAlgorithms() {
		// TODO Auto-generated method stub
		return null;
	}

}
